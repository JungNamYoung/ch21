package haru.model;

import java.util.HashMap;
import java.util.Map;

public class Model {
  private Map<String, Object> model = new HashMap<>();

  public void addAttribute(String key, Object value) {
    model.put(key, value);
  }

  public Object getAttribute(String key) {
    return model.get(key);
  }

  public Map<String, Object> getAttributes() {
    return model;
  }
}
