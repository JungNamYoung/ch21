package haru.interceptor;

import java.util.logging.Logger;

import haru.kitten.MiniHttpServletRequest;
import haru.kitten.MiniHttpServletResponse;
import haru.logger.LoggerManager;

public class ExecutionTimeInterceptor implements Interceptor {

  private static final String START_TIME = "startTime";

  Logger logger = LoggerManager.getLogger(this.getClass().getSimpleName());

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
