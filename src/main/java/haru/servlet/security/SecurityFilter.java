package haru.servlet.security;

import java.io.IOException;

import haru.constants.Define;
import haru.http.MiniHttpServletResponse;
import jakarta.servlet.http.HttpServletResponse;

public class SecurityFilter {

  public static boolean isRestricted(String requestUrl, MiniHttpServletResponse miniHttpServletResponse) {

    if (requestUrl.contains(Define.WEB_INF_EX)) {
      try {
        miniHttpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        miniHttpServletResponse.getWriter().write("403 Forbidden: Access to WEB-INF is not allowed.");
        miniHttpServletResponse.flushBuffer();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      return true;
    }

    return false;
  }
}