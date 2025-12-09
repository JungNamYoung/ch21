package haru.mvc.result;

import java.io.IOException;

import haru.http.MiniHttpServletResponse;

public class TextBodyWriter implements BodyWriter {
  @Override
  public boolean supports(Object body, String contentType) {
    return contentType != null && contentType.toLowerCase().startsWith("text/");
  }

  @Override
  public void write(Object body, String contentType, MiniHttpServletResponse response) throws IOException {
    response.setContentType(contentType);
    response.getWriter().write(body == null ? "" : body.toString());
  }
}