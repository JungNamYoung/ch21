package haru.filter;

import java.io.IOException;

import haru.kitten.MiniHttpServletRequest;
import haru.kitten.MiniHttpServletResponse;
import jakarta.servlet.ServletException;

public interface Filter {
  void doFilter(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, FilterChain chain) throws IOException, ServletException;
}