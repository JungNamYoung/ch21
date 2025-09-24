package haru.mvc.model;

import java.util.HashMap;
import java.util.Map;

public class ModelMap implements Model {

  private final Map<String, Object> model = new HashMap<>();

  @Override
  public void addAttribute(String key, Object value) {
    this.model.put(key, value);
  }

  @Override
  public Object getAttribute(String key) {
    return this.model.get(key);
  }

  @Override
  public Map<String, Object> getAttributes() {
    return this.model;
  }
}