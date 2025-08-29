package haru.mybatis;

import org.apache.ibatis.session.SqlSession;

class TransactionalSqlSession {
  private String sessionId;
  private String transactionManagerName;
  private SqlSession sqlSession;

  TransactionalSqlSession(String sessionId, String transactionManagerName, SqlSession sqlSession) {
    this.sessionId = sessionId;
    this.transactionManagerName = transactionManagerName;
    this.sqlSession = sqlSession;
  }

  public String getSessionId() {
    return sessionId;
  }

  public String getTransactionManagerName() {
    return transactionManagerName;
  }

  public SqlSession getSqlSession() {
    return sqlSession;
  }

}