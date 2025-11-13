package haru.mvc.interceptor;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;

public interface MiniInterceptor {
  default void preHandle(MiniHttpServletRequest req, MiniHttpServletResponse resp) {};
  default void postHandle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse resp) {};
  default void afterCompletion(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse resp) {};
//  
//  void preHandle(MiniHttpServletRequest req, MiniHttpServletResponse resp) ;
//  void postHandle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse resp) ;
//  void afterCompletion(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse resp) ;
  
}