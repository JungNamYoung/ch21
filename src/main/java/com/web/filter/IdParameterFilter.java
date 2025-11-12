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


@Filter(order=2, urlPatterns="/api/*")
public class IdParameterFilter implements MiniFilter {

  private static final Logger logger = MiniLogger.getLogger(IdParameterFilter.class.getSimpleName());

  @Override
  public void doFilter(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, FilterChain filterChain) throws IOException, ServletException {

    String id = miniHttpServletRequest.getParameter("id");

    logger.info("[filter] #id - " + id);

    filterChain.doFilter(miniHttpServletRequest, miniHttpServletResponse);
  }
}