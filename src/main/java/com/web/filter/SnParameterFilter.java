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
public class SnParameterFilter implements MiniFilter {

  private static final Logger logger = MiniLogger.getLogger(SnParameterFilter.class.getSimpleName());

  @Override
  public void doFilter(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, FilterChain filterChain) throws IOException, ServletException {

    String sn = miniHttpServletRequest.getParameter("sn");

    logger.info("[filter] #sn - " + sn);

    filterChain.doFilter(miniHttpServletRequest, miniHttpServletResponse);
  }
}