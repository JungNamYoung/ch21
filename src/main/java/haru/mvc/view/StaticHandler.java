package haru.mvc.view;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;

public interface StaticHandler {
  boolean supports(MiniHttpServletRequest request);
  void handle(MiniHttpServletRequest request, MiniHttpServletResponse response) throws Exception;

  default String name() {
    return this.getClass().getSimpleName();
  }
}