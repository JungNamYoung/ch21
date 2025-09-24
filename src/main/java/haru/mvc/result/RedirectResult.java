package haru.mvc.result;

import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

public record RedirectResult(String location, int status) implements MiniResponse {
  public RedirectResult(String location) {
    this(location, HttpServletResponse.SC_FOUND);
  }

  @Override
  public Map<String, String> headers() {
    return Map.of("Location", location);
  }
}