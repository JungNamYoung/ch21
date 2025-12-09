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
  public boolean preHandle(MiniHttpServletRequest request, MiniHttpServletResponse response, Object h) {
    long startTime = System.currentTimeMillis();
    request.setAttribute(START_TIME, startTime);
    logger.info("[interceptor] " + request.getRequestURI());
    
    return true;
  }

  @Override
  public void afterCompletion(MiniHttpServletRequest request, MiniHttpServletResponse response, Object h, Exception ex) {
    long startTime = (Long) request.getAttribute(START_TIME);
    long endTime = System.currentTimeMillis();
    logger.info("[interceptor] " + request.getRequestURI() + ", " + (endTime - startTime) + "ms");
  }
}
