package haru.filter;

import java.io.IOException;
import java.util.logging.Logger;

import haru.kitten.MiniHttpServletRequest;
import haru.kitten.MiniHttpServletResponse;
import haru.logger.LoggerManager;
import jakarta.servlet.ServletException;

public class SnParameterFilter implements Filter {

  static Logger logger = LoggerManager.getLogger(SnParameterFilter.class.getSimpleName());

  @Override
  public void doFilter(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, FilterChain filterChain) throws IOException, ServletException {

    String sn = miniHttpServletRequest.getParameter("sn");

    logger.info("[filter] #sn - " + sn);

    filterChain.doFilter(miniHttpServletRequest, miniHttpServletResponse);
  }
}