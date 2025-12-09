package haru.mvc.view;

import java.util.logging.Logger;

import haru.constants.Define;
import haru.http.MiniHttpServletResponse;
import haru.logging.MiniLogger;
import jakarta.servlet.http.HttpServletResponse;

public class ResponseHandler {

  private static final Logger logger = MiniLogger.getLogger(ResponseHandler.class.getSimpleName());

  public static void handleNotFound(MiniHttpServletResponse response, String requestUri) {
    try {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write(Define.NOT_FOUND_404);
      response.flushBuffer();
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    logger.info(requestUri + Define.SPACE + Define.NOT_FOUND);
  }
}
