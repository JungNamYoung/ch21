package haru.support;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class CglibProxyFactory {

  public static Object createProxy(Object target, MethodInterceptor interceptor) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(target.getClass());
    enhancer.setCallback(interceptor);
    return enhancer.create();
  }
}