package haru.mvc.result;

import java.io.IOException;

import haru.http.MiniHttpServletResponse;

public class BytesBodyWriter implements BodyWriter {
  @Override
  public boolean supports(Object body, String contentType) {
    return body instanceof byte[];
  }

  @Override
  public void write(Object body, String contentType, MiniHttpServletResponse response) throws IOException {
    if (contentType == null || contentType.isBlank()) {
      contentType = "application/octet-stream";
    }
    response.setContentType(contentType);
    response.getOutputStream().write((byte[]) body);
  }
}