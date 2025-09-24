package haru.mvc.argument;

import java.lang.reflect.Parameter;

import haru.annotation.mvc.RequestParam;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.mvc.model.Model;
import haru.support.TypeConverter;

public class RequestParamArgumentResolver implements ArgumentResolver {

  @Override
  public boolean supports(Parameter p) {
    return p.isAnnotationPresent(RequestParam.class);
  }

  @Override
  public Object resolve(Parameter p, MiniHttpServletRequest req, MiniHttpServletResponse res, Model model) {
    RequestParam ann = p.getAnnotation(RequestParam.class);
    String name = ann.value();
    if (name == null || name.isEmpty())
      name = p.getName();
    String raw = req.getParameter(name);

    if (raw == null && ann.required()) {
      throw new IllegalArgumentException("Required request parameter '" + name + "' is missing");
    }
    return TypeConverter.convert(raw, p.getType());
  }
}
