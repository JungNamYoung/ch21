package haru.mvc.interceptor;

import java.util.List;

public final class InterceptorRegistration {
  public final HandlerInterceptor interceptor;
  public final int order;
  public final List<String> includes;
  public final List<String> excludes;

  public InterceptorRegistration(HandlerInterceptor interceptor, int order, String[] includePatterns, String[] excludePatterns) {
    this.interceptor = interceptor;
    this.order = order;
    this.includes = includePatterns == null ? List.of("/*") : List.of(includePatterns);
    this.excludes = excludePatterns == null ? List.of() : List.of(excludePatterns);
  }
}