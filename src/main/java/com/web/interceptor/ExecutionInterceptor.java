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
public class ExecutionInterceptor implements HandlerInterceptor {

  private static final String START_TIME = "startTime";

  private static final Logger logger = MiniLogger.getLogger(ExecutionInterceptor.class.getSimpleName());

  @Override
  public boolean preHandle(MiniHttpServletRequest req, MiniHttpServletResponse resp, Object h) {
    long startTime = System.currentTimeMillis();
    req.setAttribute(START_TIME, startTime);
    logger.info("[interceptor] " + req.getRequestURI());
    
    return true;
  }

  @Override
  public void afterCompletion(MiniHttpServletRequest req, MiniHttpServletResponse resp, Object h, Exception ex) {
    long startTime = (Long) req.getAttribute(START_TIME);
    long endTime = System.currentTimeMillis();
    logger.info("[interceptor] " + req.getRequestURI() + ", " + (endTime - startTime) + "ms");
  }
}
