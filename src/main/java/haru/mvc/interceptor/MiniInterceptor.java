package haru.mvc.interceptor;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;

public interface MiniInterceptor {
  void preHandle(MiniHttpServletRequest req, MiniHttpServletResponse resp);
  void postHandle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse resp);
  void afterCompletion(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse resp);
}