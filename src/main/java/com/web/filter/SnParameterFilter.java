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
public class SnParameterFilter implements MiniFilter {

  private static final Logger logger = MiniLogger.getLogger(SnParameterFilter.class.getSimpleName());

  @Override
  public void doFilter(MiniHttpServletRequest request, MiniHttpServletResponse response, MiniFilterChain filterChain) throws IOException, ServletException {

    String sn = request.getParameter("sn");

    logger.info("[filter] #sn - " + sn);

    filterChain.doFilter(request, response);
  }
}