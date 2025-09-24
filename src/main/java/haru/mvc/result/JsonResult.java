package haru.mvc.result;

import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

public record JsonResult(Object body, int status, Map<String, String> headers) implements MiniResponse {
  public JsonResult(Object body) {
    this(body, HttpServletResponse.SC_OK, Map.of("Content-Type", "application/json; charset=UTF-8"));
  }
}
