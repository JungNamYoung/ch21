package haru.mvc.argument;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.mvc.model.Model;
import haru.support.TypeConverter;

public class CommandObjectArgumentResolver implements ArgumentResolver {

  @Override
  public boolean supports(Parameter p) {
    Class<?> t = p.getType();
    return !TypeConverter.isSimpleType(t);
  }

  @Override
  public Object resolve(Parameter p, MiniHttpServletRequest request, MiniHttpServletResponse response, Model model) throws Exception {
    Class<?> t = p.getType();
    Object target = t.getDeclaredConstructor().newInstance();

    for (Field f : t.getDeclaredFields()) {
      f.setAccessible(true);
      String name = f.getName();
      String raw = request.getParameter(name);
      if (raw != null) {
        Object v = TypeConverter.convert(raw, f.getType());
        f.set(target, v);
      }
    }
    return target;
  }
}
