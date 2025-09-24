package haru.servlet.filter;

import java.io.IOException;
import java.util.logging.Logger;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.logging.LoggerManager;
import jakarta.servlet.ServletException;

public class IdParameterFilter implements Filter {

  private static final Logger logger = LoggerManager.getLogger(IdParameterFilter.class.getSimpleName());

  @Override
  public void doFilter(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, FilterChain filterChain) throws IOException, ServletException {

    String id = miniHttpServletRequest.getParameter("id");

    logger.info("[filter] #id - " + id);

    filterChain.doFilter(miniHttpServletRequest, miniHttpServletResponse);
  }
}