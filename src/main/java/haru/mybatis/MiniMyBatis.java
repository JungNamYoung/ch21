package haru.mybatis;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import haru.define.Define;
import haru.define.Haru;
import haru.transaction.TxHandler;
import haru.transaction.SqlSessionTxHandler;

public class MiniMyBatis {

  private List<TransactionalSqlSession> transactionalSqlSessions = new ArrayList<>();
  private Map<String, TxHandler> txHandlers = new HashMap<>();

  public void initSessionFactory() {
    try {
      loadSqlSessionFactory("sqlSessionUser", "txUser", Haru.ROOT_PACKAGE + "/dbconfig/mybatis-config.xml");
    } catch (Exception e) {
      throw new RuntimeException("MyBatis 설정 초기화 실패", e);
    }
  }

  public TxHandler findMiniTxHandler(String txName) {
    return txHandlers.get(txName);
  }

  private void loadSqlSessionFactory(String sessionId, String transactionManagerName, String dbConfig) {
    try {
      InputStream inputStream = Resources.getResourceAsStream(dbConfig);
      SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
      SqlSession sqlSessionProxy = SqlSessionProxyFactory.create(factory);

      TransactionalSqlSession transactionalSqlSession = new TransactionalSqlSession(sessionId, transactionManagerName, factory, sqlSessionProxy);

      transactionalSqlSessions.add(transactionalSqlSession);

      txHandlers.put(transactionManagerName, new SqlSessionTxHandler(factory));

    } catch (Exception ex) {
      throw new RuntimeException("SqlSessionFactory 로드 실패", ex);
    }
  }

  public SqlSession getSqlSessionBySessionId(String sessionId) {

    for (TransactionalSqlSession transactionalSqlSession : transactionalSqlSessions) {
      if (transactionalSqlSession.getSessionId().equals(sessionId))
        return transactionalSqlSession.getSqlSession();
    }

    throw new RuntimeException(Define.NOT_APPLICABLE);
  }

  public SqlSession getSqlSessionByType(Class<?> type) {

    for (TransactionalSqlSession transactionalSqlSession : transactionalSqlSessions) {
      if (type.isAssignableFrom(transactionalSqlSession.getSqlSession().getClass()))
        return transactionalSqlSession.getSqlSession();
    }

    throw new RuntimeException(Define.NOT_APPLICABLE);
  }
}