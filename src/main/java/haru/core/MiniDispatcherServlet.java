/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) [2018년] [SamuelSky]
 */

package haru.core;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import haru.annotation.mvc.Controller;
import haru.annotation.mvc.RequestMapping;
import haru.constants.Define;
import haru.core.bootstrap.MiniServletContainer;
import haru.core.context.BeanHolder;
import haru.core.context.MiniApplicationContext;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.logging.MiniLogger;
import haru.mvc.HandlerAdapter;
import haru.mvc.HandlerMapping;
import haru.mvc.argument.ArgumentResolver;
import haru.mvc.argument.CommandObjectArgumentResolver;
import haru.mvc.argument.ModelArgumentResolver;
import haru.mvc.argument.RequestParamArgumentResolver;
import haru.mvc.argument.ServletArgumentResolver;
import haru.mvc.core.DispatcherServlet;
import haru.mvc.interceptor.HandlerInterceptor;
import haru.mvc.interceptor.InterceptorExecutor;
import haru.mvc.interceptor.InterceptorRegistry;
import haru.mvc.result.BodyWriter;
import haru.mvc.result.BytesBodyWriter;
import haru.mvc.result.JsonBodyWriter;
import haru.mvc.result.TextBodyWriter;
import haru.mvc.view.HtmlResponseHandler;
import haru.mvc.view.JspResponseHandler;
import haru.mvc.view.ResponseHandler;
import haru.servlet.resource.MiniResourceHandler;
import haru.servlet.resource.WelcomeFileResolver;
import haru.servlet.security.SecurityFilter;
import haru.support.PathUtils;

public class MiniDispatcherServlet implements DispatcherServlet {

  private final Map<String, HandlerMapping> handlerMappings = new ConcurrentHashMap<>();
  private final MiniApplicationContext appContext;
  private final HandlerAdapter handlerAdapter;
  private static final Logger logger = MiniLogger.getLogger(MiniDispatcherServlet.class.getSimpleName());

  private final InterceptorRegistry interceptorRegistry;
  private final String contextPath;

  public MiniDispatcherServlet(String basePackage) {
    this(basePackage, new MiniApplicationContext(), null, MiniServletContainer.getContextPath());
  }

  public MiniDispatcherServlet(String basePackage, MiniApplicationContext context, InterceptorRegistry registry, String contextPath) {
    this.appContext = context != null ? context : new MiniApplicationContext();
    String resolvedContextPath = contextPath != null ? contextPath : MiniServletContainer.getContextPath();
    this.contextPath = resolvedContextPath != null ? resolvedContextPath : Define.SLASH;
    this.interceptorRegistry = registry != null ? registry : new InterceptorRegistry(this.contextPath);
    handlerAdapter = createHandlerAdapter();
    try {
      logger.info(() -> "[basePackage] " + basePackage);
      if (basePackage != null && !appContext.isInitialized()) {
        appContext.initializeContext(basePackage);
      }
      registerHandlerMappings();
    } catch (Exception e) {
      throw new RuntimeException("컨트롤러 등록 실패", e);
    }
  }

  private void registerHandlerMappings() {
    for (BeanHolder beanDef : appContext.getBeans()) {
      Class<?> type = beanDef.getTargetBean().getClass();
      if (type.isAnnotationPresent(Controller.class)) {
        registerControllerMethods(type, beanDef);
      }
    }
  }

  private void registerControllerMethods(Class<?> type, BeanHolder beanDef) {
    for (Method method : type.getDeclaredMethods()) {
      if (!method.isAnnotationPresent(RequestMapping.class))
        continue;

      RequestMapping rm = method.getAnnotation(RequestMapping.class);
      for (String raw : rm.value()) {
        String path = PathUtils.normalizeMappingPath(raw);
        HandlerMapping mapping = new HandlerMapping(path, method, beanDef);

        HandlerMapping prev = handlerMappings.putIfAbsent(path, mapping);
        if (prev != null) {
          logger.warning(() -> String.format("[Duplicate Mapping] %s => %s::%s (existing: %s::%s)", path, beanDef.getTargetBean().getClass().getSimpleName(), method.getName(), prev.getBeanDefinition().getTargetBean().getClass().getSimpleName(), prev.getMethod().getName()));
        } else {
          //logger.info(() -> String.format("[RequestMapping] %s - %s::%s", path, beanDef.getTargetBean().getClass().getSimpleName(), method.getName()));
          logger.info(() -> String.format("[RequestMapping] %s", path));
        }
      }
    }
  }

  private String resolveRequestUri(String requestUrl, String contextPath) {
    if (!Define.SLASH.equals(contextPath) && requestUrl.startsWith(contextPath)) {
      return requestUrl.substring(contextPath.length());
    }
    return requestUrl;
  }

  private boolean handleByStaticHandlers(MiniHttpServletRequest req, MiniHttpServletResponse res) {
    String url = req.getRequestURI();
    if (HtmlResponseHandler.handle(url, res)) {
      logger.info("HtmlResponseHandler.handle()");
      return true;
    }
    if (JspResponseHandler.handle(req, res)) {
      logger.info("JspResponseHandler.handle()");
      return true;
    }
    if (MiniResourceHandler.handle(req, res)) {
      logger.info("MiniResourceHandler.handle()");
      return true;
    }
    return false;
  }

  private HandlerMapping findHandlerMapping(String path) {
    return handlerMappings.get(path);
  }

  public void service(MiniHttpServletRequest req, MiniHttpServletResponse res) {
    List<HandlerInterceptor> interceptorChain = interceptorRegistry.resolveChain(req.getRequestURI());
    InterceptorExecutor executor = new InterceptorExecutor(interceptorChain);

    Object handler = null;
    Object modelAndView = null;

    String requestUrl = prepareRequest(req);
    if (SecurityFilter.isRestricted(requestUrl, res))
      return;

    Exception ex = null;
    boolean interceptorPreHandled = false;
    try {
      HandlerMapping mapping = resolveHandler(requestUrl, req, res);
      if (mapping == null) {
        return;
      }

      handler = mapping;

      interceptorPreHandled = executor.applyPreHandle(req, res, handler);
      if (!interceptorPreHandled) {
        return;
      }

      modelAndView = invokeHandler(handler, req, res);

      executor.applyPostHandle(req, res, handler, modelAndView);

      render(modelAndView, req, res);
    } catch (Exception e) {
      ex = e;
    } finally {
      if (interceptorPreHandled) {
        executor.triggerAfterCompletion(req, res, handler, ex);
      }
    }
  }

  private HandlerMapping resolveHandler(String requestUrl, MiniHttpServletRequest req, MiniHttpServletResponse res) {
    if (handleByStaticHandlers(req, res))
      return null;

    String requestUri = resolveRequestUri(requestUrl, contextPath);
    requestUri = PathUtils.normalizeMappingPath(requestUri);

    HandlerMapping mapping = findHandlerMapping(requestUri);
    if (mapping == null) {
      ResponseHandler.handleNotFound(res, requestUri);
      return null;
    }
    return mapping;
  }

  private String prepareRequest(MiniHttpServletRequest req) {
    String requestUrl = req.getRequestURI();
    String welcomeFile = WelcomeFileResolver.resolve(requestUrl);

    if (welcomeFile != null) {
      req.setRequestURI(welcomeFile);
      requestUrl = welcomeFile;
    }

    final String logRequestUrl = requestUrl;
    logger.info(() -> "requestUrl : " + logRequestUrl);
    return requestUrl;
  }

  private Object invokeHandler(Object handler, MiniHttpServletRequest req, MiniHttpServletResponse res) {
    if (!(handler instanceof HandlerMapping)) {
      throw new IllegalArgumentException("지원하지 않는 handler 타입 : " + handler);
    }
    HandlerMapping mapping = (HandlerMapping) handler;
    handlerAdapter.handle(mapping, req, res);
    return null;
  }

  private void render(Object modelAndView, MiniHttpServletRequest req, MiniHttpServletResponse res) {
  }

  private HandlerAdapter createHandlerAdapter() {
    ObjectMapper objectMapper = new ObjectMapper();
    List<ArgumentResolver> argumentResolvers = List.of(new ServletArgumentResolver(), new ModelArgumentResolver(), new RequestParamArgumentResolver(), new CommandObjectArgumentResolver());
    List<BodyWriter> bodyWriters = List.of(new JsonBodyWriter(objectMapper), new TextBodyWriter(), new BytesBodyWriter());
    return new HandlerAdapter(objectMapper, argumentResolvers, bodyWriters);
  }
}