package haru.mvc.core;

import java.io.IOException;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import jakarta.servlet.ServletException;

public interface DispatcherServletBase {
  public void service(MiniHttpServletRequest req, MiniHttpServletResponse resp) throws ServletException, IOException;
}
