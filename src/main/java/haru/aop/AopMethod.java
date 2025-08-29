package haru.aop;

import java.lang.reflect.Method;
import java.util.List;

import haru.annotation.aop.After;
import haru.annotation.aop.Around;
import haru.annotation.aop.Before;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class AopMethod implements MethodInterceptor {
  Object oringinalBean;
  private final List<Object> aspectBeans;

  public AopMethod(Object oringinalBean, List<Object> aspectBeans) {
    this.oringinalBean = oringinalBean;
    this.aspectBeans = aspectBeans;
  }

  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

    for (Object aspectBean : aspectBeans) {
      invokeBefore(aspectBean, method, oringinalBean, args);
    }

    for (Object aspectBean : aspectBeans) {
      invokeAround(aspectBean, method, oringinalBean, args);
    }

    Object result = method.invoke(oringinalBean, args);

    for (Object aspectBean : aspectBeans) {
      invokeAfter(aspectBean, method, oringinalBean, args);
    }

    return result;
  }

  private void invokeBefore(Object aspectBean, Method method, Object target, Object[] args) {

    Method[] methods = aspectBean.getClass().getDeclaredMethods();

    for (Method tMethod : methods) {
      if (tMethod.isAnnotationPresent(Before.class)) {
        try {
          tMethod.invoke(aspectBean, method, target, args);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  private void invokeAround(Object aspectBean, Method method, Object target, Object[] args) {

    Method[] methods = aspectBean.getClass().getDeclaredMethods();

    for (Method tMethod : methods) {
      if (tMethod.isAnnotationPresent(Around.class)) {
        try {
          tMethod.invoke(aspectBean, method, target, args);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  private void invokeAfter(Object aspectBean, Method method, Object target, Object[] args) {

    Method[] methods = aspectBean.getClass().getDeclaredMethods();

    for (Method tMethod : methods) {
      if (tMethod.isAnnotationPresent(After.class)) {
        try {
          tMethod.invoke(aspectBean, method, target, args);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }
}