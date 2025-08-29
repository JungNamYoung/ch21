package haru.filter;

import java.io.IOException;

import haru.kitten.MiniHttpServletRequest;
import haru.kitten.MiniHttpServletResponse;
import jakarta.servlet.ServletException;

public interface FilterChain {
  void doFilter(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) throws IOException, ServletException;
}