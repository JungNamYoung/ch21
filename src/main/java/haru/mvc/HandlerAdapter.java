package haru.mvc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import haru.constants.Define;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.logging.MiniLogger;
import haru.mvc.argument.ArgumentResolver;
import haru.mvc.model.Model;
import haru.mvc.model.ModelMap;
import haru.mvc.result.BodyWriter;
import haru.mvc.result.JsonResult;
import haru.mvc.result.MiniResponse;
import haru.mvc.result.NoContentResult;
import haru.mvc.result.RedirectResult;
import haru.mvc.result.TextResult;
import haru.mvc.result.ViewResult;
import haru.servlet.view.MiniRequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

public class HandlerAdapter {
  private static final Logger logger = MiniLogger.getLogger(HandlerAdapter.class.getSimpleName());
  private final ObjectMapper objectMapper;
  private final List<ArgumentResolver> argumentResolvers;
  private final List<BodyWriter> bodyWriters;

  public HandlerAdapter(ObjectMapper objectMapper, List<ArgumentResolver> argumentResolvers, List<BodyWriter> bodyWriters) {
    this.objectMapper = objectMapper;
    this.argumentResolvers = argumentResolvers;
    this.bodyWriters = bodyWriters;
  }

  public void handle(HandlerMapping mapping, MiniHttpServletRequest req, MiniHttpServletResponse res) {
    Model model = new ModelMap();
    try {
      Object controller = mapping.getBeanDefinition().getProxyInstance() != null ? mapping.getBeanDefinition().getProxyInstance() : mapping.getBeanDefinition().getTargetBean();

      Method method = mapping.getMethod();
      Object[] args = resolveMethodArguments(method, req, res, model);

      Object ret = method.invoke(controller, args);
      MiniResponse response = adaptReturnValue(ret, model, req);

      writeResponse(response, req, res);
    } catch (InvocationTargetException ite) {
      handleException(ite.getTargetException(), res);
    } catch (Exception ex) {
      handleException(ex, res);
    } finally {
      try {
        if (!res.isCommitted())
          res.flushBuffer();
      } catch (Exception ignore) {
      }
    }
  }

  private Object[] resolveMethodArguments(Method method, MiniHttpServletRequest req, MiniHttpServletResponse res, Model model) {
    Parameter[] params = method.getParameters();
    Object[] args = new Object[params.length];
    for (int i = 0; i < params.length; i++) {
      boolean resolved = false;
      for (ArgumentResolver r : argumentResolvers) {
        if (r.supports(params[i])) {
          try {
            args[i] = r.resolve(params[i], req, res, model);
          } catch (Exception e) {
            e.printStackTrace();
          }
          resolved = true;
          break;
        }
      }
      if (!resolved)
        throw new IllegalArgumentException("Cannot resolve argument: " + params[i]);
    }
    return args;
  }

  private MiniResponse adaptReturnValue(Object ret, Model model, MiniHttpServletRequest req) {
    if (ret == null) {
      Object json = model.getAttribute(Define.JSON);
      return (json != null) ? new JsonResult(json) : new NoContentResult();
    }
    if (ret instanceof MiniResponse mr)
      return mr;
    if (ret instanceof String viewName) {
      if (viewName.startsWith("redirect:")) {
        String location = viewName.substring("redirect:".length());
        location = resolveRedirectLocation(location, req.getContextPath());
        return new RedirectResult(location);
      }
      return new ViewResult(viewName, model);
    }
    return new JsonResult(ret);
  }

  private String resolveRedirectLocation(String location, String contextPath) {
    String normalized = location.strip();
    String lower = normalized.toLowerCase();
    if (lower.startsWith(Define.HTTP)) {
      return normalized;
    }

    if (!contextPath.equals(Define.SLASH)) {
      if (normalized.startsWith(Define.SLASH)) {
        if (!normalized.startsWith(contextPath)) {
          normalized = contextPath + normalized;
        }
      } else {
        normalized = contextPath + Define.SLASH + normalized;
      }
    } else if (!normalized.startsWith(Define.SLASH)) {
      normalized = Define.SLASH + normalized;
    }

    return normalized;
  }

  private void writeResponse(MiniResponse mr, MiniHttpServletRequest req, MiniHttpServletResponse res) throws IOException {
    res.setStatus(mr.status());
    String contentType = null;
    for (var e : mr.headers().entrySet()) {
      res.setHeader(e.getKey(), e.getValue());
      if ("Content-Type".equalsIgnoreCase(e.getKey())) {
        contentType = e.getValue();
      }
    }

    if (mr instanceof ViewResult vr) {
      String jspPath = Define.WEB_INF_EX + "jsp/" + vr.viewName() + Define.EXT_JSP;
      try {
        ((MiniRequestDispatcher) req.getRequestDispatcher(jspPath)).compileAndExecute(req, res, vr.model().getAttributes());
      } catch (ServletException | IOException e) {
        throw new RuntimeException("Failed to render view: " + vr.viewName(), e);
      }
    } else if (mr instanceof RedirectResult rr) {
      if (!res.isCommitted()) {
        res.setHeader("Location", rr.location());
      }
    } else if (mr instanceof JsonResult jr) {
      writeBody(jr.body(), contentType, res);
    } else if (mr instanceof TextResult tr) {
      writeBody(tr.body(), contentType, res);
    } else if (mr instanceof NoContentResult) {
      // nothing to write
    }
  }

  private void writeBody(Object body, String contentType, MiniHttpServletResponse res) throws IOException {
    for (BodyWriter writer : bodyWriters) {
      if (writer.supports(body, contentType)) {
        writer.write(body, contentType, res);
        return;
      }
    }

    if (contentType == null || contentType.isBlank()) {
      if (body instanceof String strBody) {
        res.setContentType("text/plain; charset=UTF-8");
        res.getWriter().write(strBody);
      } else {
        res.setContentType("application/json; charset=UTF-8");
        res.getWriter().write(objectMapper.writeValueAsString(body));
      }
      return;
    }

    res.setContentType(contentType);
    res.getWriter().write(body.toString());
  }

  private void writeText(String body, MiniHttpServletResponse res) throws IOException {
    res.setContentType("text/plain; charset=UTF-8");
    res.getWriter().write(body);
  }

  private void handleException(Throwable ex, MiniHttpServletResponse res) {
    logger.severe(() -> "Handller error: " + ex.getMessage());
    if (!res.isCommitted()) {
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      try {
        res.setContentType("application/json; charset=UTF-8");
        res.getWriter().write("{\"error\":\"internal_server_error\"}");
      } catch (Exception ext) {
      }
    }
  }
}