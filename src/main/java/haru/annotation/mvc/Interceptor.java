package haru.annotation.mvc;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptor {
  int order() default 0;

  String[] includePatterns() default "/*";

  String[] excludePatterns() default {};
}