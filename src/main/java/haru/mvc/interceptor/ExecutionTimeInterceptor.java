package haru.mvc.interceptor;

import java.util.logging.Logger;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.logging.MiniLogger;

public class ExecutionTimeInterceptor implements Interceptor {

  private static final String START_TIME = "startTime";

  private static final Logger logger = MiniLogger.getLogger(ExecutionTimeInterceptor.class.getSimpleName());

  @Override
  public void preHandle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {
    long startTime = System.currentTimeMillis();
    miniHttpServletRequest.setAttribute(START_TIME, startTime);
    logger.info("[interceptor] " + miniHttpServletRequest.getRequestURI());
  }

  @Override
  public void postHandle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {
    long startTime = (Long) miniHttpServletRequest.getAttribute(START_TIME);
    long endTime = System.currentTimeMillis();
    logger.info("[interceptor] " + miniHttpServletRequest.getRequestURI() + ", " + (endTime - startTime) + "ms");
  }

  @Override
  public void afterCompletion(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {
  }
}
