package haru.mvc.model;

import java.util.Map;

public interface Model {
  
  void addAttribute(String key, Object value) ;

  Object getAttribute(String key) ;

  Map<String, Object> getAttributes() ;
}