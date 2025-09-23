package haru.transaction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.sf.cglib.proxy.Enhancer;
import haru.annotation.aop.Transactional;
import haru.logger.LoggerManager;

public class TransactionalProxyRegister {

  List<TransactionalMetadata> transactionalMetadataList = new ArrayList<>();

  static Logger logger = LoggerManager.getLogger(TransactionalProxyRegister.class.getSimpleName());

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

    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(bean.getClass());
    try {
      enhancer.setCallback(new TxMethod(bean, txHandler));
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    Object result = enhancer.create();

    return result;
  }
}
