package haru.servlet.security;

import java.io.IOException;

import haru.constants.Define;
import haru.http.MiniHttpServletResponse;
import jakarta.servlet.http.HttpServletResponse;

public class SecurityFilter {

  public static boolean isRestricted(String requestUrl, MiniHttpServletResponse resp) {

    if (requestUrl.contains(Define.WEB_INF_EX)) {
      try {
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        resp.getWriter().write("403 Forbidden: Access to WEB-INF is not allowed.");
        resp.flushBuffer();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      return true;
    }

    return false;
  }
}