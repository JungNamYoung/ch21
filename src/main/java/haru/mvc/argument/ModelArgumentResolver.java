package haru.mvc.argument;

import java.lang.reflect.Parameter;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.mvc.model.Model;

public class ModelArgumentResolver implements ArgumentResolver {
  @Override
  public boolean supports(Parameter parameter) {
    return Model.class.isAssignableFrom(parameter.getType());
  }

  @Override
  public Object resolve(Parameter parameter, MiniHttpServletRequest req, MiniHttpServletResponse resp, Model model) {
    return model;
  }
}