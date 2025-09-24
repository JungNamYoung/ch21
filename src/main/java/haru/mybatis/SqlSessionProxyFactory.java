package haru.mybatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public final class SqlSessionProxyFactory {

  private SqlSessionProxyFactory() {
  }

  public static SqlSession create(SqlSessionFactory sqlSessionFactory) {
    InvocationHandler handler = new SqlSessionInvocationHandler(sqlSessionFactory);
    return (SqlSession) Proxy.newProxyInstance(SqlSession.class.getClassLoader(), new Class[] { SqlSession.class }, handler);
  }

  private static class SqlSessionInvocationHandler implements InvocationHandler {
    private final SqlSessionFactory sqlSessionFactory;

    SqlSessionInvocationHandler(SqlSessionFactory sqlSessionFactory) {
      this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(this, args);
      }

      SqlSession sqlSession = SqlSessionContext.getSqlSession();
      boolean managed = sqlSession != null;

      if (!managed) {
        sqlSession = sqlSessionFactory.openSession(true);
      }

      try {
        return method.invoke(sqlSession, args);
      } catch (InvocationTargetException ex) {
        throw ex.getCause();
      } finally {
        if (!managed) {
          sqlSession.close();
        }
      }
    }

    @Override
    public String toString() {
      return "SqlSessionProxy(" + sqlSessionFactory + ")";
    }
  }
}
