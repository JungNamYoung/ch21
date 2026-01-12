package com.web.filter;

import java.io.IOException;
import java.util.logging.Logger;

import haru.annotation.web.Filter;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.logging.MiniLogger;
import haru.servlet.filter.MiniFilterChain;
import haru.servlet.filter.MiniFilter;
import jakarta.servlet.ServletException;

@Filter(order = 1, urlPatterns = "/*")
public class ExecutionTimeFilter implements MiniFilter {

  private static final Logger logger = MiniLogger.getLogger(ExecutionTimeFilter.class.getSimpleName());

  @Override
  public void doFilter(MiniHttpServletRequest request, MiniHttpServletResponse response, MiniFilterChain filterChain) throws IOException, ServletException {
    
    String requestURI = request.getRequestURI();

    logger.info(requestURI + " 요청 시작");
    long startTime = System.currentTimeMillis();

    filterChain.doFilter(request, response);

    long endTime = System.currentTimeMillis();
    logger.info(requestURI + " 처리 완료, 실행 시간: " + (endTime - startTime) + "ms");
  }
}
