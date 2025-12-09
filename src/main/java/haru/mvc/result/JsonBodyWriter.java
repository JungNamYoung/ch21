package haru.mvc.result;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import haru.http.MiniHttpServletResponse;

public class JsonBodyWriter implements BodyWriter {

  private final ObjectMapper objectMapper;

  public JsonBodyWriter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public boolean supports(Object body, String contentType) {
    return contentType != null && contentType.toLowerCase().startsWith("application/json");
  }

  @Override
  public void write(Object body, String contentType, MiniHttpServletResponse response) throws IOException {
    response.setContentType(contentType);
    response.getWriter().write(objectMapper.writeValueAsString(body));
  }
}