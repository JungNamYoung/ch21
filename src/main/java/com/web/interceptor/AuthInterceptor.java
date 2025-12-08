package com.web.interceptor;

import haru.annotation.mvc.Interceptor;
import haru.mvc.interceptor.HandlerInterceptor;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;

@Interceptor(
    order = 10, 
    includePatterns = { "/api/*", "/admin/*" }, 
    excludePatterns = { "/api/auth/*", "/static/*", "*.css", "*.js" }
)
public class AuthInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(MiniHttpServletRequest req, MiniHttpServletResponse resp, Object handler) throws Exception {
    if (req.getSession(false) == null || req.getSession(false).getAttribute("user") == null) {
      resp.setStatus(401);
      resp.getWriter().write("{\"ok\":false, \"error\":\"unauthorized\"}");
      return false;
    }
    return true;
  }
}
