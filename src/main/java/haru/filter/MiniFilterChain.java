package haru.filter;

import java.io.IOException;
import java.util.List;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.kitten.DispatcherServlet;
import jakarta.servlet.ServletException;

public class MiniFilterChain implements FilterChain {
  private List<Filter> filters;
  private int currentPosition = 0;
  private DispatcherServlet dispatcherServlet;

  public MiniFilterChain(List<Filter> filters, DispatcherServlet dispatcherServlet) {
    this.filters = filters;
    this.dispatcherServlet = dispatcherServlet;
  }

  @Override
  public void doFilter(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) throws IOException, ServletException {
    if (currentPosition < filters.size()) {
      Filter nextFilter = filters.get(currentPosition++);
      nextFilter.doFilter(miniHttpServletRequest, miniHttpServletResponse, this);
    } else {
      dispatcherServlet.service(miniHttpServletRequest, miniHttpServletResponse);
    }
  }
}