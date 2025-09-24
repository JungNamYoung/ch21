package haru.mvc.result;

import java.util.Map;

public sealed interface MiniResponse permits ViewResult, JsonResult, TextResult, NoContentResult, RedirectResult {
  int status();
  Map<String, String> headers();
}
