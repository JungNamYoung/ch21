package haru.kitten;

import java.io.IOException;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import jakarta.servlet.ServletException;

public interface DispatcherServletBase {
  public void service(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) throws ServletException, IOException;
}
