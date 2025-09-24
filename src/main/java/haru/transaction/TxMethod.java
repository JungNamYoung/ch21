package haru.transaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class TxMethod implements MethodInterceptor {

  private Object originalBean;
  private TxHandler txHandler;

  public TxMethod(Object originalBean, TxHandler txHandler) {
    this.originalBean = originalBean;
    this.txHandler = txHandler;
  }

  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    txHandler.begin();
    try {
      Object result = method.invoke(originalBean, args);
      txHandler.commit();
      return result;
    } catch (InvocationTargetException ex) {
      txHandler.rollback();
      throw ex.getCause();
    } catch (Throwable ex) {
      txHandler.rollback();
      throw ex;
    } finally {
      txHandler.close();
    }
  }
}