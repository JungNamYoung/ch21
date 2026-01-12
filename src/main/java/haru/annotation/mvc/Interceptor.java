package haru.annotation.mvc;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Interceptor {
  int order() default 0;
  String[] includePatterns() default "/*";
  String[] excludePatterns() default {};
}