
package haru.mvc.interceptor;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;

public interface HandlerInterceptor {
  default boolean preHandle(MiniHttpServletRequest req, MiniHttpServletResponse res, Object handler) throws Exception {
    return true;
  }

  default void postHandle(MiniHttpServletRequest req, MiniHttpServletResponse res, Object handler, Object modelAndView) throws Exception {
  }

  default void afterCompletion(MiniHttpServletRequest req, MiniHttpServletResponse res, Object handler, Exception ex) throws Exception {
  }
}