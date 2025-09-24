package haru.mvc.result;

import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

public record NoContentResult() implements MiniResponse {
  @Override
  public int status() {
    return HttpServletResponse.SC_NO_CONTENT;
  }

  @Override
  public Map<String, String> headers() {
    return Map.of();
  }
}
