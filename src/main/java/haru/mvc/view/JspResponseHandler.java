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

public class JspResponseHandler {

  private static final Logger logger = MiniLogger.getLogger(JspResponseHandler.class.getSimpleName());

  public static boolean handle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {
    String requestUri = miniHttpServletRequest.getRequestURI();

    if (!requestUri.endsWith(Define.EXT_JSP)) {
      return false;
    }

    String filePath = MiniServletContainer.getRealPath(requestUri);
    File jspFile = new File(filePath);

    if (!jspFile.exists() || jspFile.isDirectory()) {
      return false;
    }

    String contextPath = MiniServletContainer.getContextPath();
    String relativePath = requestUri;
    if (!Define.SLASH.equals(contextPath) && requestUri.startsWith(contextPath)) {
      relativePath = requestUri.substring(contextPath.length());
    }

    try {
      
      RequestDispatcher requestDispatcher = miniHttpServletRequest.getRequestDispatcher(relativePath);
      MiniRequestDispatcher miniRequestDispatcher = (MiniRequestDispatcher) requestDispatcher;
      Map<String, Object> model = new HashMap<>();
      miniHttpServletResponse.setStatus(HttpServletResponse.SC_OK);
      
      miniRequestDispatcher.compileAndExecute(miniHttpServletRequest, miniHttpServletResponse, model);
      
      miniHttpServletResponse.flushBuffer();
      
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return true;
  }
}