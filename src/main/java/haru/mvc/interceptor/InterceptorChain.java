package haru.mvc.interceptor;

import java.util.List;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;

public class InterceptorChain {
  private final List<MiniInterceptor> interceptors;

  public InterceptorChain(List<MiniInterceptor> interceptors) {
    this.interceptors = interceptors;
  }

  public void preHandle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {
    for (MiniInterceptor interceptor : interceptors) {
      interceptor.preHandle(miniHttpServletRequest, miniHttpServletResponse);
    }
  }

  public void postHandle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {
    for (MiniInterceptor interceptor : interceptors) {
      interceptor.postHandle(miniHttpServletRequest, miniHttpServletResponse);
    }
  }

  public void afterCompletion(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {
    for (MiniInterceptor interceptor : interceptors) {
      interceptor.afterCompletion(miniHttpServletRequest, miniHttpServletResponse);
    }
  }
}

