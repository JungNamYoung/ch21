package haru.kitten;

import java.util.logging.Logger;

import haru.logger.LoggerManager;

public class BeanDefinition {
  private final String beanName;
  private final Object targetBean;
  private final Object proxyInstance;
  
  Logger logger = LoggerManager.getLogger(this.getClass().getSimpleName());

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
