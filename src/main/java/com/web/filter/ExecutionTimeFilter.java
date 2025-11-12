package com.web.filter;

import java.io.IOException;
import java.util.logging.Logger;

import haru.annotation.web.Filter;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.logging.MiniLogger;
import haru.servlet.filter.FilterChain;
import haru.servlet.filter.MiniFilter;
import jakarta.servlet.ServletException;

@Filter(order=1, urlPatterns="/*")
public class ExecutionTimeFilter implements MiniFilter {

  private static Logger logger = MiniLogger.getLogger(ExecutionTimeFilter.class.getSimpleName());
  
  @Override
  public void doFilter(MiniHttpServletRequest miniRequest, MiniHttpServletResponse miniResponse, FilterChain filterChain) throws IOException, ServletException {
    String requestURI = miniRequest.getRequestURI();

      long startTime = System.currentTimeMillis();
      
      logger.info(requestURI + " 요청 시작");
      
      filterChain.doFilter(miniRequest, miniResponse);
      
      long endTime = System.currentTimeMillis();
      
      logger.info(requestURI + " 처리 완료, 실행 시간: " + (endTime - startTime) + "ms");
  }
}
