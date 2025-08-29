package haru.interceptor;

import java.util.List;

import haru.kitten.MiniHttpServletRequest;
import haru.kitten.MiniHttpServletResponse;

public class InterceptorChain {
  private final List<Interceptor> interceptors;

  public InterceptorChain(List<Interceptor> interceptors) {
    this.interceptors = interceptors;
  }

  public void preHandle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {
    for (Interceptor interceptor : interceptors) {
      interceptor.preHandle(miniHttpServletRequest, miniHttpServletResponse);
    }
  }

  public void postHandle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {
    for (Interceptor interceptor : interceptors) {
      interceptor.postHandle(miniHttpServletRequest, miniHttpServletResponse);
    }
  }

  public void afterCompletion(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {
    for (Interceptor interceptor : interceptors) {
      interceptor.afterCompletion(miniHttpServletRequest, miniHttpServletResponse);
    }
  }
}

