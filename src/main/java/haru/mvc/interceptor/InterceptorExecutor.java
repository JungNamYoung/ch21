
package haru.mvc.interceptor;

import java.util.List;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;

public final class InterceptorExecutor {
  private final List<HandlerInterceptor> chain;
  private int lastPreHandledIndex = -1;

  public InterceptorExecutor(List<HandlerInterceptor> chain) {
    this.chain = chain == null ? List.of() : List.copyOf(chain);
  }

  public boolean applyPreHandle(MiniHttpServletRequest request, MiniHttpServletResponse response, Object handler) throws Exception {
    for (int i = 0; i < chain.size(); i++) {
      if (!chain.get(i).preHandle(request, response, handler)) {
        lastPreHandledIndex = i - 1;
        triggerAfterCompletion(request, response, handler, null);
        return false;
      }
      lastPreHandledIndex = i;
    }
    return true;
  }

  public void applyPostHandle(MiniHttpServletRequest request, MiniHttpServletResponse response, Object handler, Object modelAndView) throws Exception {
    for (int i = lastPreHandledIndex; i >= 0; i--) {
      chain.get(i).postHandle(request, response, handler, modelAndView);
    }
  }

  public void triggerAfterCompletion(MiniHttpServletRequest request, MiniHttpServletResponse response, Object handler, Exception ex) {
    for (int i = lastPreHandledIndex; i >= 0; i--) {
      try {
        chain.get(i).afterCompletion(request, response, handler, ex);
      } catch (Exception ignore) {
      }
    }
  }
}