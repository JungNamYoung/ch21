package haru.transaction;

import java.util.logging.Logger;

import org.apache.ibatis.session.SqlSession;

import haru.logger.LoggerManager;

public class SqlSessionTxHandler implements TxHandler {
  private SqlSession sqlSession;
  static Logger logger = LoggerManager.getLogger(SqlSessionTxHandler.class.getSimpleName());

  public SqlSessionTxHandler(SqlSession sqlSession) {
    this.sqlSession = sqlSession;
  }

  @Override
  public void begin() {
    try {
      logger.info("[transaction] start");
    } catch (Exception e) {
      throw new RuntimeException("트랜잭션 시작 실패", e);
    }
  }

  @Override
  public void commit() {
    try {
      this.sqlSession.commit();
      logger.info("[transaction] commit");
    } catch (Exception e) {
      throw new RuntimeException("트랜잭션 커밋 실패", e);
    }
  }

  @Override
  public void rollback() {
    try {
      this.sqlSession.rollback();
      logger.info("[transaction] rollback");
    } catch (Exception e) {
      throw new RuntimeException("트랜잭션 롤백 실패", e);
    }
  }

  @Override
  public void close() {

    this.sqlSession.close();
    logger.info("트랜잭션 종료");

  }
}