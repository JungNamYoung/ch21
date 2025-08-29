package haru.transaction;
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
    Object result = null;
    txHandler.begin();
    try {
      result = method.invoke(originalBean, args);
      txHandler.commit();
    } catch (Throwable ex) {
      txHandler.rollback();
      ex.printStackTrace();
    } finally {
      // by JNY
      // 지우지 말것
//			miniTxHandler.close();
    }

    return result;
  }
}