package haru.mvc.argument;

import java.lang.reflect.Parameter;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.mvc.model.Model;

public interface ArgumentResolver {
  boolean supports(Parameter parameter);
  Object resolve(Parameter parameter, MiniHttpServletRequest request, MiniHttpServletResponse response, Model model) throws Exception;
}