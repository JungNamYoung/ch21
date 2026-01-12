package haru.mvc.view;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import haru.constants.Define;
import haru.core.bootstrap.MiniServletContainer;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.logging.MiniLogger;
import haru.servlet.view.MiniRequestDispatcher;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletResponse;

public class JspResponseHandler implements StaticHandler {

  private static final Logger logger = MiniLogger.getLogger(JspResponseHandler.class.getSimpleName());

  @Override
  public boolean supports(MiniHttpServletRequest request) {
    String requestUri = request.getRequestURI();
    if (!requestUri.endsWith(Define.EXT_JSP)) {
      return false;
    }

    String filePath = MiniServletContainer.getRealPath(requestUri);
    File jspFile = new File(filePath);

    return jspFile.exists() && !jspFile.isDirectory();
  }

  @Override
  public void handle(MiniHttpServletRequest request, MiniHttpServletResponse response) throws Exception {
    String requestUri = request.getRequestURI();

    String contextPath = MiniServletContainer.getContextPath();
    String relativePath = requestUri;
    if (!Define.SLASH.equals(contextPath) && requestUri.startsWith(contextPath)) {
      relativePath = requestUri.substring(contextPath.length());
    }

    RequestDispatcher requestDispatcher = request.getRequestDispatcher(relativePath);
    MiniRequestDispatcher miniRequestDispatcher = (MiniRequestDispatcher) requestDispatcher;

    Map<String, Object> model = new HashMap<>();
    response.setStatus(HttpServletResponse.SC_OK);

    miniRequestDispatcher.render(request, response, model);

    response.flushBuffer();
  }
}