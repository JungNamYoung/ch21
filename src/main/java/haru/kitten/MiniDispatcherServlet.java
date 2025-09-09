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
package haru.kitten;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import haru.annotation.mvc.Controller;
import haru.annotation.mvc.RequestMapping;
import haru.define.Define;
import haru.interceptor.ExecutionTimeInterceptor;
import haru.interceptor.Interceptor;
import haru.interceptor.InterceptorChain;
import haru.logger.LoggerManager;
import haru.security.SecurityFilter;

public class MiniDispatcherServlet implements DispatcherServlet {

  private List<HandlerMapping> handlerMappings = new ArrayList<>();
  private MiniApplicationContext miniApplicationContext = new MiniApplicationContext();
  private List<Interceptor> interceptors = new ArrayList<>();
  Logger logger = LoggerManager.getLogger(this.getClass().getSimpleName());

  public MiniDispatcherServlet(String scanPackage) {
    try {

      miniApplicationContext.initializeContext(scanPackage);
//      miniApplicationContext.injectDependencies();

      requestMapping();

      interceptors.add(new ExecutionTimeInterceptor());

    } catch (Exception e) {
      throw new RuntimeException("컨트롤러 등록 실패", e);
    }
  }

  private void requestMapping() {
    for (BeanDefinition beanDefinition : miniApplicationContext.getBeans()) {

      Class<?> tClass = beanDefinition.getTargetBean().getClass();

      if (tClass.isAnnotationPresent(Controller.class)) {

        requestMappingSub(tClass, beanDefinition);
      }
    }
  }

  private void requestMappingSub(Class<?> tClass, BeanDefinition beanDefinition) {

    Method[] methods = tClass.getDeclaredMethods();

    for (Method method : methods) {

      if (method.isAnnotationPresent(RequestMapping.class)) {

        RequestMapping handlerMapping = method.getAnnotation(RequestMapping.class);

        for (String pathRequest : handlerMapping.value()) {
          handlerMappings.add(new HandlerMapping(pathRequest, method, beanDefinition));
          logger.info("[RequestMapping] " + pathRequest + " - " + beanDefinition.getTargetBean().getClass().getSimpleName() + "::" + method.getName());
        }
      }
    }
  }

  private HandlerMapping findHandlerMapping(String pathRequest) {

    for (HandlerMapping handlerMapping : handlerMappings) {
      if (handlerMapping.getPathRequest().equals(pathRequest))
        return handlerMapping;
    }

    return null;
  }

  @Override
  public void service(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {

    String requestUrl = miniHttpServletRequest.getRequestURI();
    String contextPath = MiniServletContainer.getContextPath();
    
    logger.info("requestUrl : " + requestUrl);

    if (SecurityFilter.isRestricted(requestUrl, miniHttpServletResponse))
      return;

    InterceptorChain interceptorChain = new InterceptorChain(interceptors);
    interceptorChain.preHandle(miniHttpServletRequest, miniHttpServletResponse);

    if (HtmlResponseHandler.handle(requestUrl, miniHttpServletResponse)) {
      logger.info("HtmlResponseHandler.handle()");
    } else if (MiniResourceHandler.handle(miniHttpServletRequest, miniHttpServletResponse)) {
      logger.info("MiniResourceHandler.handle()");
    } else {
      String requestUri = requestUrl;
      if (!Define.SLASH.equals(contextPath) && requestUrl.startsWith(contextPath)) {
        requestUri = requestUrl.substring(contextPath.length());
      }

      HandlerMapping handlerMapping = findHandlerMapping(requestUri);

      if (handlerMapping == null) {
        ResponseHandler.handleNotFound(miniHttpServletResponse, requestUri);
      } else
        HandlerExecutor.execute(handlerMapping, miniHttpServletRequest, miniHttpServletResponse);
    }

    interceptorChain.postHandle(miniHttpServletRequest, miniHttpServletResponse);
  }
}
