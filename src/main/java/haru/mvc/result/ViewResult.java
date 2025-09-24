package haru.mvc.result;

import java.util.Map;

import haru.mvc.model.Model;
import jakarta.servlet.http.HttpServletResponse;

public record ViewResult(String viewName, Model model, int status, Map<String, String> headers) implements MiniResponse {
  public ViewResult(String viewName, Model model) {
    this(viewName, model, HttpServletResponse.SC_OK, Map.of());
  }
}
