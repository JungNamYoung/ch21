package haru.servlet.filter;

import java.io.IOException;
import java.util.logging.Logger;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.logging.LoggerManager;
import jakarta.servlet.ServletException;

public class SnParameterFilter implements Filter {

  private static final Logger logger = LoggerManager.getLogger(SnParameterFilter.class.getSimpleName());

  @Override
  public void doFilter(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, FilterChain filterChain) throws IOException, ServletException {

    String sn = miniHttpServletRequest.getParameter("sn");

    logger.info("[filter] #sn - " + sn);

    filterChain.doFilter(miniHttpServletRequest, miniHttpServletResponse);
  }
}