package haru.servlet.security;

import java.io.IOException;

import haru.constants.Define;
import haru.http.MiniHttpServletResponse;
import jakarta.servlet.http.HttpServletResponse;

public class SecurityFilter {

  public static boolean isRestricted(String requestUrl, MiniHttpServletResponse response) {

    if (requestUrl.contains(Define.WEB_INF_EX)) {
      try {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("403 Forbidden: Access to WEB-INF is not allowed.");
        response.flushBuffer();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      return true;
    }

    return false;
  }
}