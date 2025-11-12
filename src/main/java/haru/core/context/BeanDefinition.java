package haru.core.context;

import java.util.logging.Logger;

import haru.logging.MiniLogger;

public class BeanDefinition {
  private final String beanName;
  private final Object targetBean;
  private final Object proxyInstance;
  
  private static final Logger logger = MiniLogger.getLogger(BeanDefinition.class.getSimpleName());

  public BeanDefinition(String beanName, Object targetBean, Object proxyInstance) {
    this.beanName = beanName;
    this.targetBean = targetBean;
    this.proxyInstance = proxyInstance;
    logger.info("create : " + beanName + " - " + targetBean + " - " + proxyInstance);
  }

 public String getBeanName() {
    return beanName;
  }

  public Object getTargetBean() {
    return targetBean;
  }

  public Object getProxyInstance() {
    return proxyInstance;
  }
}
