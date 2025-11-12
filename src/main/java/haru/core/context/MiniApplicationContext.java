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
package haru.core.context;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.ibatis.session.SqlSession;

import haru.annotation.aop.Aspect;
import haru.annotation.di.Autowired;
import haru.annotation.di.Repository;
import haru.annotation.di.Resource;
import haru.annotation.di.Service;
import haru.annotation.mvc.Controller;
import haru.annotation.web.Filter;
import haru.aop.AspectManager;
import haru.constants.Define;
import haru.logging.LoggerManager;
import haru.mybatis.MiniMyBatis;
import haru.transaction.TransactionalProxyRegister;

public class MiniApplicationContext {
  private List<BeanDefinition> beanDefinitions = new ArrayList<>();
  private TransactionalProxyRegister transactionalProxyRegister = new TransactionalProxyRegister();
  private Map<Class<? extends Annotation>, Set<Class<?>>> annotatedClasses = new HashMap<>();
  private AspectManager aspectManager = new AspectManager();
  MiniMyBatis miniMyBatis = new MiniMyBatis();
  private static final Logger logger = LoggerManager.getLogger(MiniApplicationContext.class.getSimpleName());

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
    registerAnnotatedClasses(Service.class, scanner);
    registerAnnotatedClasses(Repository.class, scanner);
    registerAnnotatedClasses(Controller.class, scanner);
    registerAnnotatedClasses(Aspect.class, scanner);
    registerAnnotatedClasses(Filter.class, scanner);
  }

  private void initTransactionAndAop() throws Exception {
    transactionalProxyRegister.registerTransactionalClasses(getAnnotatedClasses(Service.class));
    transactionalProxyRegister.registerTransactionalClasses(getAnnotatedClasses(Repository.class));
    aspectManager.registerAspectBeans(getAnnotatedClasses(Aspect.class));
    miniMyBatis.initSessionFactory();
  }

  private void initializeBeans() throws Exception {
    List<Object> beanContainer = new ArrayList<>();

    registerBeans(beanContainer, getAnnotatedClasses(Controller.class));
    registerBeans(beanContainer, getAnnotatedClasses(Service.class));
    registerBeans(beanContainer, getAnnotatedClasses(Repository.class));

    for (Object bean : beanContainer) {
      BeanDefinition beanDefinition = createInfoBeanWithProxy(bean);
      beanDefinitions.add(beanDefinition);
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

  private void registerBeans(List<Object> beanContainer, Set<Class<?>> tClasses) {

    for (Class<?> tClass : tClasses) {

      Object bean = null;

      try {
        bean = tClass.getConstructor().newInstance();
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      String msg = "create - ";

      if (tClass.isAnnotationPresent(Controller.class)) {
        msg = msg + "Controller - ";
      } else if (tClass.isAnnotationPresent(Service.class)) {
        msg = msg + "Service - ";
      } else if (tClass.isAnnotationPresent(Repository.class)) {
        msg = msg + "Repository - ";
      } else {
        throw new RuntimeException(Define.NOT_APPLICABLE);
      }

      logger.info(msg + tClass.getSimpleName());

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

    if (clazz.isAnnotationPresent(Repository.class)) {
      Repository repository = clazz.getAnnotation(Repository.class);

      if (!repository.value().isEmpty()) {
        return repository.value();
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
        if ((field.getType() == SqlSession.class) && (field.isAnnotationPresent(Autowired.class) || field.isAnnotationPresent(Resource.class))) {
          injectSqlSession(bean, field);
        } else if (field.isAnnotationPresent(Autowired.class)) {
          injectAutowiredDependency(bean, field);
        } else if (field.isAnnotationPresent(Resource.class)) {
          injectResourceBasedDependency(bean, field);
        }
      }
    }
  }

  private void injectAutowiredDependency(Object bean, Field field) {
    BeanDefinition beanDefinition = findBeanByType(field.getType());
    assignDependency(bean, field, beanDefinition);
  }

  void injectSqlSession(Object bean, Field field) {

    field.setAccessible(true);

    try {
      SqlSession sqlSession = miniMyBatis.getSqlSessionByType(field.getType());

      field.set(bean, sqlSession);

      logger.info("[dependency #1] inject : " + bean.getClass().getSimpleName());

    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  private void injectResourceBasedDependency(Object bean, Field field) {
    Resource resource = field.getAnnotation(Resource.class);
    String resourceName = resource.name();

    BeanDefinition beanDefinition = !resourceName.isEmpty() ? findBean(resourceName) : findBeanByType(field.getType());
    assignDependency(bean, field, beanDefinition);
  }

  private void assignDependency(Object bean, Field field, BeanDefinition beanDefinition) {
    if (beanDefinition == null) {
      throw new RuntimeException("빈 주입 실패: 빈을 찾을 수 없습니다.");
    }

    Object dependency = beanDefinition.getProxyInstance() != null ? beanDefinition.getProxyInstance() : beanDefinition.getTargetBean();

    field.setAccessible(true);

    if (!field.getType().isAssignableFrom(dependency.getClass())) {
      String msg = "주입 불가능 : 필드 타입 " + field.getType() + "은(는) " + dependency.getClass() + "을(를) 할당할 수 없습니다.";
      logger.info("error | " + msg);
    }

    try {
      field.set(bean, dependency);
      logger.info("[dependency] inject : " + bean.getClass().getSimpleName() + " - " + dependency.getClass().getSimpleName());
    } catch (IllegalAccessException e) {
      throw new RuntimeException("의존성 주입 실패", e);
    }
  }

  private void registerAnnotatedClasses(Class<? extends Annotation> annotation, MiniAnnotationScanner scanner) {
    Set<Class<?>> scannedClasses = scanner.findTypesAnnotatedWith(annotation);
    annotatedClasses.put(annotation, scannedClasses != null ? scannedClasses : Collections.emptySet());
  }

  private Set<Class<?>> getAnnotatedClasses(Class<? extends Annotation> annotation) {
    return annotatedClasses.getOrDefault(annotation, Collections.emptySet());
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
}