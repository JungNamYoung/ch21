package haru.mvc.result;

import java.io.IOException;

import haru.http.MiniHttpServletResponse;

public interface BodyWriter {
  boolean supports(Object body, String contentType);
  void write(Object body, String contentType, MiniHttpServletResponse resp) throws IOException;
}