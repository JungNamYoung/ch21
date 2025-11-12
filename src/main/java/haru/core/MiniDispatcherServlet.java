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

import haru.annotation.mvc.Controller;
import haru.annotation.mvc.RequestMapping;
import haru.constants.Define;
import haru.core.bootstrap.MiniServletContainer;
import haru.core.context.BeanDefinition;
import haru.core.context.MiniApplicationContext;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.logging.MiniLogger;
import com.fasterxml.jackson.databind.ObjectMapper;

import haru.mvc.HandlerAdapter;
import haru.mvc.HandlerMapping;
import haru.mvc.core.DispatcherServlet;
import haru.mvc.interceptor.ExecutionTimeInterceptor;
import haru.mvc.interceptor.MiniInterceptor;
import haru.mvc.interceptor.InterceptorChain;
import haru.mvc.view.HtmlResponseHandler;
import haru.mvc.view.JspResponseHandler;
import haru.mvc.view.ResponseHandler;
import haru.servlet.resource.MiniResourceHandler;
import haru.servlet.resource.WelcomeFileResolver;
import haru.servlet.security.SecurityFilter;
import haru.mvc.argument.ArgumentResolver;
import haru.mvc.argument.CommandObjectArgumentResolver;
import haru.mvc.argument.ModelArgumentResolver;
import haru.mvc.argument.RequestParamArgumentResolver;
import haru.mvc.argument.ServletArgumentResolver;
import haru.mvc.result.BodyWriter;
import haru.mvc.result.BytesBodyWriter;
import haru.mvc.result.JsonBodyWriter;
import haru.mvc.result.TextBodyWriter;

public class MiniDispatcherServlet implements DispatcherServlet {

  private final Map<String, HandlerMapping> handlerMappings = new ConcurrentHashMap<>();
  private final MiniApplicationContext appContext = new MiniApplicationContext();
  private final List<MiniInterceptor> interceptors = List.of(new ExecutionTimeInterceptor());
  private final HandlerAdapter handlerAdapter;
  private static final Logger logger = MiniLogger.getLogger(MiniDispatcherServlet.class.getSimpleName());

  public MiniDispatcherServlet(String basePackage) {
    handlerAdapter = createHandlerAdapter();
    try {
      logger.info(() -> "basePackage : " + basePackage);
      appContext.initializeContext(basePackage);
      registerHandlerMappings();
    } catch (Exception e) {
      throw new RuntimeException("컨트롤러 등록 실패", e);
    }
  }

  private void registerHandlerMappings() {
    for (BeanDefinition beanDef : appContext.getBeans()) {
      Class<?> type = beanDef.getTargetBean().getClass();
      if (type.isAnnotationPresent(Controller.class)) {
        registerControllerMethods(type, beanDef);
      }
    }
  }

  private void registerControllerMethods(Class<?> type, BeanDefinition beanDef) {
    for (Method method : type.getDeclaredMethods()) {
      if (!method.isAnnotationPresent(RequestMapping.class))
        continue;

      RequestMapping rm = method.getAnnotation(RequestMapping.class);
      for (String raw : rm.value()) {
        String path = normalizePath(raw);
        HandlerMapping mapping = new HandlerMapping(path, method, beanDef);

        HandlerMapping prev = handlerMappings.putIfAbsent(path, mapping);
        if (prev != null) {
          logger.warning(() -> String.format("[Duplicate Mapping] %s => %s::%s (existing: %s::%s)", path, beanDef.getTargetBean().getClass().getSimpleName(), method.getName(), prev.getBeanDefinition().getTargetBean().getClass().getSimpleName(), prev.getMethod().getName()));
        } else {
          logger.info(() -> String.format("[RequestMapping] %s - %s::%s", path, beanDef.getTargetBean().getClass().getSimpleName(), method.getName()));
        }
      }
    }
  }

  private String normalizePath(String path) {
    if (path == null || path.isEmpty())
      return "/";
    String p = path.startsWith(Define.SLASH) ? path : Define.SLASH + path;
    if (p.length() > 1 && p.endsWith(Define.SLASH))
      p = p.substring(0, p.length() - 1);
    return p;
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

  @Override
  public void service(MiniHttpServletRequest req, MiniHttpServletResponse res) {
    String requestUrl = req.getRequestURI();
    String welcomeFile = WelcomeFileResolver.resolve(requestUrl);
    
    if (welcomeFile != null) {
      req.setRequestURI(welcomeFile);
      requestUrl = welcomeFile;
    }

    final String contextPath = MiniServletContainer.getContextPath();
    final String logRequestUrl = requestUrl;
    logger.info(() -> "requestUrl : " + logRequestUrl);

    if (SecurityFilter.isRestricted(requestUrl, res))
      return;

    InterceptorChain chain = new InterceptorChain(interceptors);

    try {
      chain.preHandle(req, res);

      if (handleByStaticHandlers(req, res))
        return;

      String requestUri = resolveRequestUri(requestUrl, contextPath);
      requestUri = normalizePath(requestUri);

      HandlerMapping mapping = findHandlerMapping(requestUri);
      if (mapping == null) {
        ResponseHandler.handleNotFound(res, requestUri);
        return;
      }
      handlerAdapter.handle(mapping, req, res);
    } finally {
      chain.postHandle(req, res);
    }
  }

  private HandlerAdapter createHandlerAdapter() {
    ObjectMapper objectMapper = new ObjectMapper();
    List<ArgumentResolver> argumentResolvers = List.of(new ServletArgumentResolver(), new ModelArgumentResolver(), new RequestParamArgumentResolver(), new CommandObjectArgumentResolver());
    List<BodyWriter> bodyWriters = List.of(new JsonBodyWriter(objectMapper), new TextBodyWriter(), new BytesBodyWriter());
    return new HandlerAdapter(objectMapper, argumentResolvers, bodyWriters);
  }
}