package haru.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

public class MiniHttServletRequestWrapper extends HttpServletRequestWrapper {
  private MiniServletContext miniServletContext;
  private MiniHttpSession session;
  private String contextPath;

  public MiniHttServletRequestWrapper(HttpServletRequest request, MiniServletContext context, MiniHttpSession session) {
    super(request);
    this.miniServletContext = context;
    this.session = session;
    this.contextPath = context.getContextPath();
  }

  @Override
  public ServletContext getServletContext() {
    return this.miniServletContext;
  }

  @Override
  public HttpSession getSession() {
    return session;
  }

  @Override
  public HttpSession getSession(boolean create) {
    if (create && session == null) {
      session = new MiniHttpSession(getServletContext());
    }

    return session;
  }

  @Override
  public String getContextPath() {
    return contextPath;
  }
}
