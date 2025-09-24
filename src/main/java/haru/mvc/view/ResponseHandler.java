package haru.mvc.view;

import java.util.logging.Logger;

import haru.constants.Define;
import haru.http.MiniHttpServletResponse;
import haru.logging.LoggerManager;
import jakarta.servlet.http.HttpServletResponse;

public class ResponseHandler {

  private static final Logger logger = LoggerManager.getLogger(ResponseHandler.class.getSimpleName());

  public static void handleNotFound(MiniHttpServletResponse miniHttpServletResponse, String requestUri) {
    try {
      miniHttpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
      miniHttpServletResponse.getWriter().write(Define.NOT_FOUND_404);
      miniHttpServletResponse.flushBuffer();
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    logger.info(requestUri + Define.SPACE + Define.NOT_FOUND);
  }
}
