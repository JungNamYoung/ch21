package haru.servlet.filter;

import java.io.IOException;
import java.util.List;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.mvc.core.DispatcherServletBase;
import jakarta.servlet.ServletException;

// haru이 아닌 순수한 서블릿 기반의 인터셉터 방식
public class MyFilterChain implements FilterChain {
  private List<Filter> filters;
  private int currentPosition = 0;
  private DispatcherServletBase dispatcherServletBase;

  public MyFilterChain(List<Filter> filters, DispatcherServletBase dispatcherServlet) {
    this.filters = filters;
    this.dispatcherServletBase = dispatcherServlet;
  }

  @Override
  public void doFilter(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) throws IOException, ServletException {
    if (currentPosition < filters.size()) {
      Filter nextFilter = filters.get(currentPosition++);
      nextFilter.doFilter(miniHttpServletRequest, miniHttpServletResponse, this);
    } else {
      dispatcherServletBase.service(miniHttpServletRequest, miniHttpServletResponse);
    }
  }
}