package haru.interceptor;

import haru.kitten.MiniHttpServletRequest;
import haru.kitten.MiniHttpServletResponse;

public interface Interceptor {
  void preHandle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse);
  void postHandle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse);
  void afterCompletion(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse);
}