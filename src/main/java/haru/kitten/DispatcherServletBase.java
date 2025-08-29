package haru.kitten;

import java.io.IOException;

import jakarta.servlet.ServletException;

public interface DispatcherServletBase {
  public void service(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) throws ServletException, IOException;
}
