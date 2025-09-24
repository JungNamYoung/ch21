package haru.servlet.filter;

import java.io.IOException;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import jakarta.servlet.ServletException;

public class ExecutionTimeFilter implements Filter {

  @Override
  public void doFilter(MiniHttpServletRequest miniRequest, MiniHttpServletResponse miniResponse, FilterChain chain) throws IOException, ServletException {
    String requestURI = miniRequest.getRequestURI();

    if (requestURI.equals("/selectUser.do")) {
      long startTime = System.currentTimeMillis();
      System.out.println("[Interceptor : ExecutionTime]" + requestURI + " 요청 시작");
      chain.doFilter(miniRequest, miniResponse);
      long endTime = System.currentTimeMillis();
      System.out.println("[Interceptor : ExecutionTime] " + requestURI + " 처리 완료 . 실행 시간: " + (endTime - startTime) + "ms");
    } else {
      chain.doFilter(miniRequest, miniResponse);
    }
  }
}
