package com.web.filter;

import java.io.IOException;
import java.util.logging.Logger;

import haru.annotation.web.Filter;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.logging.LoggerManager;
//import haru.servlet.filter.Filter;
import haru.servlet.filter.FilterChain;
import jakarta.servlet.ServletException;


@Filter(order=2, urlPatterns="/api/*")
public class IdParameterFilter implements haru.servlet.filter.MiniFilter {

  private static final Logger logger = LoggerManager.getLogger(IdParameterFilter.class.getSimpleName());

  @Override
  public void doFilter(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, FilterChain filterChain) throws IOException, ServletException {

    String id = miniHttpServletRequest.getParameter("id");

    logger.info("[filter] #id - " + id);

    filterChain.doFilter(miniHttpServletRequest, miniHttpServletResponse);
  }
}