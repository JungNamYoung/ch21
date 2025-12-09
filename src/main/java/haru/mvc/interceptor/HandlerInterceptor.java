
package haru.mvc.interceptor;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;

public interface HandlerInterceptor {
  default boolean preHandle(MiniHttpServletRequest request, MiniHttpServletResponse response, Object handler) throws Exception {
    return true;
  }

  default void postHandle(MiniHttpServletRequest request, MiniHttpServletResponse response, Object handler, Object modelAndView) throws Exception {
  }

  default void afterCompletion(MiniHttpServletRequest request, MiniHttpServletResponse response, Object handler, Exception ex) throws Exception {
  }
}