package haru.config;

import java.util.Collections;
import java.util.Enumeration;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;

public class MiniServletConfig implements ServletConfig {

  ServletContext miniServletContext;

  public MiniServletConfig(ServletContext miniServletContext) {
    this.miniServletContext = miniServletContext;
  }

  @Override
  public String getServletName() {
    return "JspServlet";
  }

  @Override
  public ServletContext getServletContext() {
    return miniServletContext;
  }

  @Override
  public String getInitParameter(String name) {
    return null;
  }

  @Override
  public Enumeration<String> getInitParameterNames() {
    return Collections.emptyEnumeration();
  }
};