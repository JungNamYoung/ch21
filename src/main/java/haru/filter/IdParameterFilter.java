package haru.filter;

import java.io.IOException;
import java.util.logging.Logger;

import haru.kitten.MiniHttpServletRequest;
import haru.kitten.MiniHttpServletResponse;
import haru.logger.LoggerManager;
import jakarta.servlet.ServletException;

public class IdParameterFilter implements Filter {

  Logger logger = LoggerManager.getLogger(getClass().getSimpleName());

  @Override
  public void doFilter(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, FilterChain filterChain) throws IOException, ServletException {

    String id = miniHttpServletRequest.getParameter("id");

    logger.info("[filter] #id - " + id);

    filterChain.doFilter(miniHttpServletRequest, miniHttpServletResponse);
  }
}