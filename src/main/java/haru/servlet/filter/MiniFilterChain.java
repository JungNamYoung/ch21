package haru.servlet.filter;

import java.io.IOException;
import java.util.List;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.mvc.core.DispatcherServlet;
import jakarta.servlet.ServletException;

public class MiniFilterChain implements FilterChain {
  private List<MiniFilter> filters;
  private int currentPosition = 0;
  private DispatcherServlet dispatcherServlet;

  public MiniFilterChain(List<MiniFilter> filters, DispatcherServlet dispatcherServlet) {
    this.filters = filters;
    this.dispatcherServlet = dispatcherServlet;
  }

  @Override
  public void doFilter(MiniHttpServletRequest request, MiniHttpServletResponse response) throws IOException, ServletException {
    if (currentPosition < filters.size()) {
      MiniFilter nextFilter = filters.get(currentPosition++);
      nextFilter.doFilter(request, response, this);
    } else {
      dispatcherServlet.service(request, response);
    }
  }
}