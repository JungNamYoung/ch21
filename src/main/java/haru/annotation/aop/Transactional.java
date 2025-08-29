package haru.annotation.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Transactional {
  String transactionManager() default "defaultTxManager";

  Class<? extends Exception>[] rollbackFor() default { Exception.class };
}