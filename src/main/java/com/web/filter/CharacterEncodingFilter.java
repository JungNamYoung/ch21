package com.web.filter;

import java.io.IOException;

import haru.annotation.web.Filter;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.servlet.filter.MiniFilterChain;
import haru.servlet.filter.MiniFilter;
import jakarta.servlet.ServletException;

@Filter(order = 0, urlPatterns = "/*")
public class CharacterEncodingFilter implements MiniFilter {
  private final String encoding;

  public CharacterEncodingFilter() {
    this("UTF-8");
  }

  public CharacterEncodingFilter(String encoding) {
    this.encoding = encoding;
  }

  @Override
  public void doFilter(MiniHttpServletRequest request, MiniHttpServletResponse response, MiniFilterChain chain) throws IOException, ServletException {

    // 바디/파라미터 파싱 전에 인코딩을 고정하는 것이 핵심입니다.
    if (request.getCharacterEncoding() == null) {
      request.setCharacterEncoding(encoding);
    }
    response.setCharacterEncoding(encoding);
    chain.doFilter(request, response);
  }
}