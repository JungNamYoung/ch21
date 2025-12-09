package haru.servlet.filter;

import java.io.IOException;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import jakarta.servlet.ServletException;

public interface MiniFilter {
  void doFilter(MiniHttpServletRequest request, MiniHttpServletResponse response, FilterChain chain) throws IOException, ServletException;
}