package haru.transaction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.sf.cglib.proxy.Enhancer;
import haru.annotation.aop.Transactional;
import haru.logging.MiniLogger;
import haru.support.CglibProxyFactory;

public class TransactionalProxyRegister {

  List<TransactionalMetadata> transactionalMetadataList = new ArrayList<>();

  private static final Logger logger = MiniLogger.getLogger(TransactionalProxyRegister.class.getSimpleName());

  public void registerTransactionalClasses(Set<Class<?>> serviceClasses) {
    for (Class<?> serviceClass : serviceClasses) {
      Method[] methods = serviceClass.getDeclaredMethods();

      Transactional transactional = null;

      for (Method method : methods) {
        if (method.isAnnotationPresent(Transactional.class)) {
          transactional = method.getAnnotation(Transactional.class);
        }
      }

      if (transactional != null) {
        TransactionalMetadata transcationalMetadata = new TransactionalMetadata(transactional.transactionManager(), serviceClass);

        transactionalMetadataList.add(transcationalMetadata);

        logger.info("[transaction] scan : " + serviceClass.getSimpleName());
      }

    }
  }

  public String findTransactionManagerName(Class<?> tClass) {

    for (TransactionalMetadata target : transactionalMetadataList) {
      if (target.getServiceClass() == tClass) {
        return target.getTransactionManagerName();
      }
    }

    // 트랜잭션 메타데이터가 등록되지 않은 클래스
    return null;
  }

  public Object createProxyInstance(Object bean, TxHandler txHandler) {
    TxMethod interceptor = null;
    try {
      interceptor = new TxMethod(bean, txHandler);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new RuntimeException("TxMethod 생성 중 예외 발생", ex);
    }

    return CglibProxyFactory.createProxy(bean, interceptor);
  }
}
