package com.web.interceptor;

import java.util.logging.Logger;

import haru.annotation.mvc.Interceptor;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.logging.MiniLogger;
import haru.mvc.interceptor.HandlerInterceptor;

@Interceptor(
    order = 1, 
    includePatterns = "/*"
)
public class LoggingInterceptor implements HandlerInterceptor {
  
  Logger logger = MiniLogger.getLogger(LoggingInterceptor.class.getSimpleName());
  
  long t0;
  
  @Override
  public boolean preHandle(MiniHttpServletRequest req, MiniHttpServletResponse resp, Object h) {
    t0 = System.nanoTime();
    logger.info("[REQ] " + req.getMethod() + " " + req.getRequestURI());
    return true;
  }

  @Override
  public void afterCompletion(MiniHttpServletRequest req, MiniHttpServletResponse resp, Object h, Exception ex) {
    long ms = (System.nanoTime() - t0) / 1_000_000;
    logger.info("[RES] " + resp.getStatus() + " (" + ms + "ms)");
  }
}