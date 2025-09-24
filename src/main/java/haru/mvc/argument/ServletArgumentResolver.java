package haru.mvc.argument;

import java.lang.reflect.Parameter;

import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.http.MiniHttpSession;
import haru.mvc.model.Model;

public class ServletArgumentResolver implements ArgumentResolver {
  @Override
  public boolean supports(Parameter p) {
    Class<?> t = p.getType();
    return t.equals(MiniHttpServletRequest.class) || t.equals(MiniHttpServletResponse.class) || t.equals(MiniHttpSession.class);
  }

  @Override
  public Object resolve(Parameter p, MiniHttpServletRequest req, MiniHttpServletResponse res, Model model) {
    Class<?> t = p.getType();
    if (t.equals(MiniHttpServletRequest.class))
      return req;
    if (t.equals(MiniHttpServletResponse.class))
      return res;
    if (t.equals(MiniHttpSession.class))
      return req.getSession();
    throw new IllegalStateException("Unsupported servlet arg: " + t);
  }
}