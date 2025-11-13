package haru.mvc.interceptor;

import java.util.logging.Logger;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.logging.MiniLogger;

public class ExecutionTimeInterceptor implements MiniInterceptor {

  private static final String START_TIME = "startTime";

  private static final Logger logger = MiniLogger.getLogger(ExecutionTimeInterceptor.class.getSimpleName());

  @Override
  public void preHandle(MiniHttpServletRequest req, MiniHttpServletResponse resp) {
    long startTime = System.currentTimeMillis();
    req.setAttribute(START_TIME, startTime);
    logger.info("[interceptor] " + req.getRequestURI());
  }

  @Override
  public void postHandle(MiniHttpServletRequest req, MiniHttpServletResponse resp) {
    long startTime = (Long) req.getAttribute(START_TIME);
    long endTime = System.currentTimeMillis();
    logger.info("[interceptor] " + req.getRequestURI() + ", " + (endTime - startTime) + "ms");
  }

  @Override
  public void afterCompletion(MiniHttpServletRequest req, MiniHttpServletResponse resp) {
  }
}
