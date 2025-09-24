package haru.transaction;

import java.util.logging.Logger;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import haru.logger.LoggerManager;
import haru.mybatis.SqlSessionContext;

public class SqlSessionTxHandler implements TxHandler {
  private SqlSessionFactory sqlSessionFactory;
  static Logger logger = LoggerManager.getLogger(SqlSessionTxHandler.class.getSimpleName());

  public SqlSessionTxHandler(SqlSessionFactory sqlSessionFactory) {
    this.sqlSessionFactory = sqlSessionFactory;
  }

  @Override
  public void begin() {
    try {
      if (SqlSessionContext.getSqlSession() != null) {
        throw new IllegalStateException("이미 활성화된 SqlSession이 존재합니다.");
      }
      SqlSession sqlSession = sqlSessionFactory.openSession(false);
      SqlSessionContext.bind(sqlSession);
      logger.info("[transaction] start");
    } catch (Exception e) {
      throw new RuntimeException("트랜잭션 시작 실패", e);
    }
  }

  @Override
  public void commit() {
    try {
      SqlSession sqlSession = SqlSessionContext.getSqlSession();
      if (sqlSession == null) {
        throw new IllegalStateException("커밋할 SqlSession이 존재하지 않습니다.");
      }
      sqlSession.commit();
      logger.info("[transaction] commit");
    } catch (Exception e) {
      throw new RuntimeException("트랜잭션 커밋 실패", e);
    }
  }

  @Override
  public void rollback() {
    try {
      SqlSession sqlSession = SqlSessionContext.getSqlSession();
      if (sqlSession != null) {
        sqlSession.rollback();
      }
      logger.info("[transaction] rollback");
    } catch (Exception e) {
      throw new RuntimeException("트랜잭션 롤백 실패", e);
    }
  }

  @Override
  public void close() {
    try {
      SqlSessionContext.close();
      logger.info("[transaction] close");
    } catch (Exception e) {
      throw new RuntimeException("SqlSession 종료 실패", e);
    }
  }
}