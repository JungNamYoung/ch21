package haru.mvc.result;

import java.io.IOException;

import haru.http.MiniHttpServletResponse;

public class BytesBodyWriter implements BodyWriter {
  @Override
  public boolean supports(Object body, String contentType) {
    return body instanceof byte[];
  }

  @Override
  public void write(Object body, String contentType, MiniHttpServletResponse res) throws IOException {
    if (contentType == null || contentType.isBlank()) {
      contentType = "application/octet-stream";
    }
    res.setContentType(contentType);
    res.getOutputStream().write((byte[]) body);
  }
}