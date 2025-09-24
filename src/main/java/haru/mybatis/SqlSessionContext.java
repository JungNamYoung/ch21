package haru.mybatis;

import org.apache.ibatis.session.SqlSession;

public final class SqlSessionContext {

  private static final ThreadLocal<SqlSession> SQL_SESSION_HOLDER = new ThreadLocal<>();

  private SqlSessionContext() {
  }

  public static SqlSession getSqlSession() {
    return SQL_SESSION_HOLDER.get();
  }

  public static void bind(SqlSession sqlSession) {
    SQL_SESSION_HOLDER.set(sqlSession);
  }

  public static void close() {
    SqlSession sqlSession = SQL_SESSION_HOLDER.get();
    try {
      if (sqlSession != null) {
        sqlSession.close();
      }
    } finally {
      SQL_SESSION_HOLDER.remove();
    }
  }
}