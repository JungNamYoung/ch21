package haru.kitten;

public class BeanDefinition {
  private final String beanName;
  private final Object targetBean;
  private final Object proxyInstance;

  public BeanDefinition(String beanName, Object targetBean, Object proxyInstance) {
    this.beanName = beanName;
    this.targetBean = targetBean;
    this.proxyInstance = proxyInstance;
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
