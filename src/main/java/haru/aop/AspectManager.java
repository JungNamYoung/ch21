package haru.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import haru.annotation.aop.After;
import haru.annotation.aop.Around;
import haru.annotation.aop.Before;
import haru.constants.Define;
import haru.logging.LoggerManager;
import net.sf.cglib.proxy.Enhancer;

public class AspectManager {
  
  List<Object> aspectHolderList = new ArrayList<>();
  
  private static final Logger logger = LoggerManager.getLogger(AspectManager.class.getSimpleName());

  public List<Object> findBeanAspect(Object bean) {

    String className = bean.getClass().getName();
    Boolean flag = false;

    List<Object> results = new ArrayList<>();

    for (Object aspectInstance : aspectHolderList) {

      Method[] methods = aspectInstance.getClass().getDeclaredMethods();

      for (Method method : methods) {

        Before before = method.getAnnotation(Before.class);
        if (before != null)
          flag = matchValue(className, before.value());

        Around around = method.getAnnotation(Around.class);
        if (around != null)
          flag = matchValue(className, around.value());

        After after = method.getAnnotation(After.class);
        if (after != null)
          flag = matchValue(className, after.value());
      }

      if (flag) {
        results.add(aspectInstance);
      }
    }

    return results;
  }

  private Boolean matchValue(String className, String annotationValue) {

    if (annotationValue.indexOf(className) > Define.INDEX_MINUS_1) {
      return true;
    }

    return false;
  }

  public void registerAspectBeans(Set<Class<?>> aspectClasses) {
    
    for (Class<?> aspectClass : aspectClasses) {

      Object aspectInstance = null;

      try {
        aspectInstance = aspectClass.getDeclaredConstructor().newInstance();       
        aspectHolderList.add(aspectInstance);

      } catch (Exception ex) {
        ex.printStackTrace();
      }

      logger.info("[aspect] create : " + aspectClass.getSimpleName());
    }
  }

  public Object makeProxyInstance(Object originalBean, List<Object> aspectBeans) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(originalBean.getClass());
    enhancer.setCallback(new AopMethod(originalBean, aspectBeans));

    Object result = enhancer.create();
    return result;
  }
}
