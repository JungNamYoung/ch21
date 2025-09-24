package haru.mvc.result;

import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

public record TextResult(String body, int status, Map<String, String> headers) implements MiniResponse {
  public TextResult(String body) {
    this(body, HttpServletResponse.SC_OK, Map.of("Content-Type", "text/plain; charset=UTF-8"));
  }
}
