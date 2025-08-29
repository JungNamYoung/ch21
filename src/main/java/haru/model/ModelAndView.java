package haru.model;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
  private String viewName;
  private Map<String, Object> model = new HashMap<>();

  public ModelAndView(String viewName) {
    this.viewName = viewName;
  }

  public String getViewName() {
    return viewName;
  }

  public void addObject(String key, Object value) {
    model.put(key, value);
  }

  public Map<String, Object> getModel() {
    return model;
  }
}
