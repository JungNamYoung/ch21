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
import haru.annotation.mvc.Interceptor;
import haru.annotation.web.Filter;
import haru.aop.AspectManager;
import haru.constants.Define;
import haru.logging.MiniLogger;
import haru.mvc.FilterRegistry;
import haru.mybatis.MiniMyBatis;
import haru.transaction.TransactionalProxyRegister;

public class MiniApplicationContext {
  private List<BeanHolder> beanHolders = new ArrayList<>();
  private TransactionalProxyRegister transactionalProxyRegister = new TransactionalProxyRegister();
  private Map<Class<? extends Annotation>, Set<Class<?>>> annotatedClasses = new HashMap<>();
  private AspectManager aspectManager = new AspectManager();
  MiniMyBatis miniMyBatis = new MiniMyBatis();
  private static final Logger logger = MiniLogger.getLogger(MiniApplicationContext.class.getSimpleName());
  private boolean initialized;

  private static final Map<Class<? extends Annotation>, String> ANNOTATION_TO_MSG_MAP = new HashMap<>();

  static {
    ANNOTATION_TO_MSG_MAP.put(Controller.class, "Controller - ");
    ANNOTATION_TO_MSG_MAP.put(Service.class, "Service - ");
    ANNOTATION_TO_MSG_MAP.put(Repository.class, "Repository - ");
    ANNOTATION_TO_MSG_MAP.put(Interceptor.class, "Interceptor - ");
  }
  
  public void initializeContext(String basePackage) {
    try {
      scanAnnotatedClasses(basePackage);
      registerFilters();

      initializeInfrastructure();

      initializeBeans();
      injectDependencies();
      initialized = true;

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initializeInfrastructure() throws Exception {
    initializePersistence();
    initializeAop();
    initializeTransactionProxies();
  }

  private void initializePersistence() throws Exception {
    miniMyBatis.initSessionFactory();
  }

  private void initializeAop() {
    aspectManager.registerAspectBeans(getAnnotatedClasses(Aspect.class));
  }

  private void initializeTransactionProxies() {
    registerTransactionalFor(Service.class, Repository.class);
  }

  private final void registerTransactionalFor(Class<? extends Annotation>... stereotypes) {
    for (Class<? extends Annotation> stereotype : stereotypes) {
      transactionalProxyRegister.registerTransactionalClasses(getAnnotatedClasses(stereotype));
    }
  }

  private void scanAnnotatedClasses(String basePackage) {
    MiniAnnotationScanner scanner = new MiniAnnotationScanner(basePackage);
    registerAnnotatedClasses(Service.class, scanner);
    registerAnnotatedClasses(Repository.class, scanner);
    registerAnnotatedClasses(Controller.class, scanner);
    registerAnnotatedClasses(Aspect.class, scanner);
    registerAnnotatedClasses(Filter.class, scanner);
    registerAnnotatedClasses(Interceptor.class, scanner);
  }

  private void registerFilters() {
    getAnnotatedClasses(Filter.class).forEach(FilterRegistry::register);
  }

  private void initializeBeans() throws Exception {
    List<Object> beanContainer = new ArrayList<>();

    registerBeans(beanContainer, getAnnotatedClasses(Controller.class));
    registerBeans(beanContainer, getAnnotatedClasses(Service.class));
    registerBeans(beanContainer, getAnnotatedClasses(Repository.class));
    registerBeans(beanContainer, getAnnotatedClasses(Interceptor.class));

    for (Object bean : beanContainer) {
      BeanHolder beanHolder = createInfoBeanWithProxy(bean);
      beanHolders.add(beanHolder);
    }
  }

  private BeanHolder createInfoBeanWithProxy(Object bean) throws Exception {
    String beanName = resolveBeanName(bean.getClass());
    Object proxyInstance = createProxyIfNeeded(bean);
    return new BeanHolder(beanName, bean, proxyInstance);
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

      String annotationTypeMsg = null;
      for (Map.Entry<Class<? extends Annotation>, String> entry : ANNOTATION_TO_MSG_MAP.entrySet()) {
        if (tClass.isAnnotationPresent(entry.getKey())) {
          annotationTypeMsg = entry.getValue();
          break;
        }
      }

      if (annotationTypeMsg == null) {
        throw new RuntimeException(Define.NOT_APPLICABLE);
      }
      String msg = "create : " + annotationTypeMsg;
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

    if (clazz.isAnnotationPresent(Interceptor.class)) {
      String simpleName = clazz.getSimpleName();
      return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    return clazz.getSimpleName();
  }

  private void injectDependencies() throws Exception {

    for (BeanHolder beanHolder : beanHolders) {

      Object bean = beanHolder.getTargetBean();

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
    BeanHolder beanHolder = findBeanByType(field.getType());
    assignDependency(bean, field, beanHolder);
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

    BeanHolder beanHolder = !resourceName.isEmpty() ? findBean(resourceName) : findBeanByType(field.getType());
    assignDependency(bean, field, beanHolder);
  }

  private void assignDependency(Object bean, Field field, BeanHolder beanHolder) {
    if (beanHolder == null) {
      throw new RuntimeException("빈 주입 실패: 빈을 찾을 수 없습니다.");
    }

    Object dependency = beanHolder.getProxyInstance() != null ? beanHolder.getProxyInstance() : beanHolder.getTargetBean();

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

  public List<BeanHolder> getBeans() {
    return beanHolders;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public <T> T getBean(Class<T> type) {
    BeanHolder beanHolder = findBeanByType(type);
    if (beanHolder == null) {
      throw new IllegalArgumentException("빈을 찾을 수 없습니다: " + type.getName());
    }

    Object candidate = beanHolder.getProxyInstance() != null ? beanHolder.getProxyInstance() : beanHolder.getTargetBean();

    return type.cast(candidate);
  }

  public BeanHolder findBean(String beanName) {
    for (BeanHolder beanHolder : beanHolders) {
      if (beanHolder.getBeanName().equals(beanName))
        return beanHolder;
    }

    return null;
  }

  private BeanHolder findBeanByType(Class<?> type) {
    for (BeanHolder beanHolder : beanHolders) {
      Object candidate = beanHolder.getTargetBean();
      if (type.isAssignableFrom(candidate.getClass())) {
        return beanHolder;
      }
    }

    return null;
  }
}