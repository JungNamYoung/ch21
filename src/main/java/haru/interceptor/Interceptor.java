package haru.interceptor;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;

public interface Interceptor {
  void preHandle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse);
  void postHandle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse);
  void afterCompletion(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse);
}