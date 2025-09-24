package haru.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

class TransactionalSqlSession {
  private String sessionId;
  private String transactionManagerName;
  private SqlSessionFactory sqlSessionFactory;
  private SqlSession sqlSession;

  TransactionalSqlSession(String sessionId, String transactionManagerName, SqlSessionFactory sqlSessionFactory, SqlSession sqlSession) {
    this.sessionId = sessionId;
    this.transactionManagerName = transactionManagerName;
    this.sqlSessionFactory = sqlSessionFactory;
    this.sqlSession = sqlSession;
  }

  public String getSessionId() {
    return sessionId;
  }

  public String getTransactionManagerName() {
    return transactionManagerName;
  }

  public SqlSessionFactory getSqlSessionFactory() {
    return sqlSessionFactory;
  }

  public SqlSession getSqlSession() {
    return sqlSession;
  }

}