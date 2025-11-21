package haru.mvc.interceptor;

import java.util.List;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;

//public class InterceptorChain {
//  private final List<MiniInterceptor> interceptors;
//
//  public InterceptorChain(List<MiniInterceptor> interceptors) {
//    this.interceptors = interceptors;
//  }
//
//  public void preHandle(MiniHttpServletRequest req, MiniHttpServletResponse resp) {
//    for (MiniInterceptor interceptor : interceptors) {
//      interceptor.preHandle(req, resp);
//    }
//  }
//
//  public void postHandle(MiniHttpServletRequest req, MiniHttpServletResponse resp) {
//    for (MiniInterceptor interceptor : interceptors) {
//      interceptor.postHandle(req, resp);
//    }
//  }
//
//  public void afterCompletion(MiniHttpServletRequest req, MiniHttpServletResponse resp) {
//    for (MiniInterceptor interceptor : interceptors) {
//      interceptor.afterCompletion(req, resp);
//    }
//  }
//}
//
