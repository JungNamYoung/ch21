/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) [2018년] [SamuelSky]
 */
package haru.kitten;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.ibatis.session.SqlSession;

import haru.annotation.aop.Aspect;
import haru.annotation.di.Autowired;
import haru.annotation.di.Service;
import haru.annotation.mvc.Controller;
import haru.aop.AspectManager;
import haru.define.Define;
import haru.logger.LoggerManager;
import haru.mybatis.MiniMyBatis;
import haru.transaction.TransactionalProxyRegister;

public class MiniApplicationContext {
  private List<BeanDefinition> beanDefinitions = new ArrayList<>();
  private TransactionalProxyRegister transactionalProxyRegister = new TransactionalProxyRegister();
  private Set<Class<?>> annotatedServiceClasses;
  private Set<Class<?>> annotatedControllerClasses;
  private Set<Class<?>> annotatedAspectClasses;
  private AspectManager aspectManager = new AspectManager();
  MiniMyBatis miniMyBatis = new MiniMyBatis();
  Logger logger = LoggerManager.getLogger(this.getClass().getSimpleName());

  public void initializeContext(String basePackage) {
    try {
      scanAnnotatedClasses(basePackage);
      initTransactionAndAop();
      initializeBeans();
      injectDependencies();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void scanAnnotatedClasses(String basePackage) {
    MiniAnnotationScanner scanner = new MiniAnnotationScanner(basePackage);
    annotatedServiceClasses = scanner.getTypesAnnotatedWith(Service.class);
    annotatedControllerClasses = scanner.getTypesAnnotatedWith(Controller.class);
    annotatedAspectClasses = scanner.getTypesAnnotatedWith(Aspect.class);
  }

  private void initTransactionAndAop() throws Exception {
    transactionalProxyRegister.registerTransactionalClasses(annotatedServiceClasses);
    aspectManager.registerAspectBeans(annotatedAspectClasses);
    miniMyBatis.initSessionFactory();
  }

  private void initializeBeans() throws Exception {
    List<Object> beanContainer = new ArrayList<>();

    registerBeans(beanContainer, annotatedControllerClasses);
    registerBeans(beanContainer, annotatedServiceClasses);

    for (Object bean : beanContainer) {
      BeanDefinition beanDefinition = createInfoBeanWithProxy(bean);
      beanDefinitions.add(beanDefinition);
      logProxyCreation(beanDefinition);
    }
  }

  private BeanDefinition createInfoBeanWithProxy(Object bean) throws Exception {
    String beanName = resolveBeanName(bean.getClass());
    Object proxyInstance = createProxyIfNeeded(bean);
    return new BeanDefinition(beanName, bean, proxyInstance);
  }

  private Object createProxyIfNeeded(Object bean) throws Exception {

    String transactionMangerName = transactionalProxyRegister.findTransactionManagerName(bean.getClass());

    if (transactionMangerName != null) {
      return transactionalProxyRegister.createProxyInstance(bean, miniMyBatis.findMiniTxHandler(transactionMangerName));
    }

    List<Object> aspectInstances = aspectManager.findBeanAspect(bean);

    if (aspectInstances.size() > Define.COUNT_0) {
      return aspectManager.makeProxyInstance(bean, aspectInstances);
    }

    return null;
  }

  private void logProxyCreation(BeanDefinition beanDefinition) {
    if (beanDefinition.getProxyInstance() != null) {
      String targetBean = beanDefinition.getTargetBean().getClass().getSimpleName();
      String proxyInstance = beanDefinition.getProxyInstance().getClass().getSimpleName();
      logger.info("[proxy] create | " + targetBean + " | " + proxyInstance);
    }
  }

  // registerBeans()함수는 Controller, Service 어노테이션만 지원
  private void registerBeans(List<Object> beanContainer, Set<Class<?>> tClasses) {

    for (Class<?> tClass : tClasses) {

      Object bean = null;

      try {
        bean = tClass.getConstructor().newInstance();
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      if (tClass.isAnnotationPresent(Controller.class))
        logger.info("[bean] controller - create - " + tClass.getSimpleName());
      else if (tClass.isAnnotationPresent(Service.class))
        logger.info("[bean] service - create - " + tClass.getSimpleName());
      else {
        throw new RuntimeException(Define.NOT_APPLICABLE);
      }

      beanContainer.add(bean);
    }
  }

  private String resolveBeanName(Class<?> clazz) {
    if (clazz.isAnnotationPresent(Service.class)) {
      Service service = clazz.getAnnotation(Service.class);

      if (!service.value().isEmpty()) {
        return service.value();
      }
    }

    if (clazz.isAnnotationPresent(Controller.class)) {
      String simpleName = clazz.getSimpleName();
      return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    return clazz.getSimpleName();
  }

  private void injectDependencies() throws Exception {

    for (BeanDefinition beanDefinition : beanDefinitions) {

      Object bean = beanDefinition.getTargetBean();

      Field[] fields = bean.getClass().getDeclaredFields();

      for (Field field : fields) {
        if ((field.getType() == SqlSession.class) && field.isAnnotationPresent(Autowired.class)) {
          injectSqlSession(bean, field);
        } else if (field.isAnnotationPresent(Autowired.class)) {
          injectFieldDependency(bean, field);
        }
      }
    }
  }

  private void injectFieldDependency(Object bean, Field field) {
    Autowired autowired = field.getAnnotation(Autowired.class);
//    String autowiredName = autowired.name();

    BeanDefinition beanDefinition = null;
//    if (!autowiredName.isEmpty()) {
//      beanDefinition = findBean(autowiredName);
//    } else {
      beanDefinition = findBeanByType(field.getType());
//    }

    if (beanDefinition == null) {
//      String msg = autowiredName.isEmpty() ? field.getType().getSimpleName() : autowiredName;
      throw new RuntimeException("빈 주입 실패 : " + " 빈을 찾을 수 없습니다.");
    }

    Object dependency = beanDefinition.getProxyInstance() != null ? beanDefinition.getProxyInstance() : beanDefinition.getTargetBean();

    field.setAccessible(true);

    if (!field.getType().isAssignableFrom(dependency.getClass())) {
      String msg = "주입 불가능 : 필드 타입 " + field.getType() + "은(는) " + dependency.getClass() + "을(를) 할당할 수 없습니다.";
      logger.info("error | " + msg);
    }

    try {
      field.set(bean, dependency);
      logger.info("[dependency] inject | " + bean.getClass().getSimpleName() + " | " + dependency.getClass().getSimpleName());
    } catch (IllegalAccessException e) {
      throw new RuntimeException("의존성 주입 실패 ", e);
    }
  }

  void injectSqlSession(Object bean, Field field) {

    field.setAccessible(true);

    Autowired autowired = field.getAnnotation(Autowired.class);

    try {

//      SqlSession sqlSession = miniMyBatis.getSqlSessionBySessionId(autowired.name());
      SqlSession sqlSession = miniMyBatis.getSqlSessionByType(field.getType());

      field.set(bean, sqlSession);

      //logger.info("[dependency #1] inject | " + bean.getClass().getSimpleName() + " | " + autowired.name());
      logger.info("[dependency #1] inject | " + bean.getClass().getSimpleName() + " | ");

    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public List<BeanDefinition> getBeans() {
    return beanDefinitions;
  }

  public BeanDefinition findBean(String beanName) {
    for (BeanDefinition beanDefinition : beanDefinitions) {
      if (beanDefinition.getBeanName().equals(beanName))
        return beanDefinition;
    }

    return null;
  }

  private BeanDefinition findBeanByType(Class<?> type) {
    for (BeanDefinition beanDefinition : beanDefinitions) {
      Object candidate = beanDefinition.getTargetBean();
      if (type.isAssignableFrom(candidate.getClass())) {
        return beanDefinition;
      }
    }

    return null;
  }

  public TransactionalProxyRegister getTransactionEx() {
    return transactionalProxyRegister;
  }

  public void setTransactionEx(TransactionalProxyRegister utilTransaction) {
    this.transactionalProxyRegister = utilTransaction;
  }

  public Set<Class<?>> getServiceClasses() {
    return annotatedServiceClasses;
  }

  public void setServiceClasses(Set<Class<?>> serviceClasses) {
    this.annotatedServiceClasses = serviceClasses;
  }

  public Set<Class<?>> getControllerClasses() {
    return annotatedControllerClasses;
  }

  public void setControllerClasses(Set<Class<?>> controllerClasses) {
    this.annotatedControllerClasses = controllerClasses;
  }

  public Set<Class<?>> getAspectClasses() {
    return annotatedAspectClasses;
  }

  public void setAspectClasses(Set<Class<?>> aspectClasses) {
    this.annotatedAspectClasses = aspectClasses;
  }
}

