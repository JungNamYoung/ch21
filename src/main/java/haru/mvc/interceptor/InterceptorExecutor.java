
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

  public boolean applyPreHandle(MiniHttpServletRequest req, MiniHttpServletResponse res, Object handler) throws Exception {
    for (int i = 0; i < chain.size(); i++) {
      if (!chain.get(i).preHandle(req, res, handler)) {
        lastPreHandledIndex = i - 1;
        triggerAfterCompletion(req, res, handler, null);
        return false;
      }
      lastPreHandledIndex = i;
    }
    return true;
  }

  public void applyPostHandle(MiniHttpServletRequest req, MiniHttpServletResponse res, Object handler, Object modelAndView) throws Exception {
    for (int i = lastPreHandledIndex; i >= 0; i--) {
      chain.get(i).postHandle(req, res, handler, modelAndView);
    }
  }

  public void triggerAfterCompletion(MiniHttpServletRequest req, MiniHttpServletResponse res, Object handler, Exception ex) {
    for (int i = lastPreHandledIndex; i >= 0; i--) {
      try {
        chain.get(i).afterCompletion(req, res, handler, ex);
      } catch (Exception ignore) {
      }
    }
  }
}