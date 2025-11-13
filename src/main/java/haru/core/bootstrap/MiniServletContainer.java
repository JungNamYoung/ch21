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

package haru.core.bootstrap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpServer;

import haru.annotation.mvc.Interceptor;
import haru.constants.Define;
import haru.constants.Haru;
import haru.core.MiniDispatcherServlet;
import haru.core.context.BeanHolder;
import haru.core.context.MiniApplicationContext;
import haru.http.MiniDispatcherHandler;
import haru.mvc.interceptor.HandlerInterceptor;
import haru.mvc.interceptor.InterceptorRegistry;
import haru.servlet.MiniServletContext;
import haru.support.TokenEx;
import haru.support.UtilExt;
import jakarta.servlet.ServletException;

public class MiniServletContainer {

  private static MiniServletContext miniServletContext;
  static String contextPath;

  public static MiniServletContext getMiniWebApplicationContext() {
    return miniServletContext;
  }

  public static String getContextPath() {
    return contextPath;
  }

  public static String getRealPath(String requestedResource) {

    String ctx = getContextPath();
    if (!Define.SLASH.equals(ctx) && requestedResource.startsWith(ctx)) {
      requestedResource = requestedResource.substring(ctx.length());
    }

    String filePath = miniServletContext.getRealPath(Define.STR_BLANK) + requestedResource;

    return filePath;
  }

  private InterceptorRegistry createInterceptorRegistry(MiniApplicationContext ctx, String contextPath) {
    InterceptorRegistry registry = new InterceptorRegistry(contextPath);

    for (BeanHolder beanHolder : ctx.getBeans()) {
      Class<?> beanType = beanHolder.getTargetBean().getClass();
      if (!beanType.isAnnotationPresent(Interceptor.class))
        continue;
      if (!HandlerInterceptor.class.isAssignableFrom(beanType)) {
        throw new IllegalStateException("@Interceptor는 HandlerInterceptor만 대상입니다: " + beanType.getName());
      }
      Object bean = beanHolder.getProxyInstance() != null ? beanHolder.getProxyInstance() : beanHolder.getTargetBean();
      Interceptor meta = beanType.getAnnotation(Interceptor.class);
      registry.register((HandlerInterceptor) bean, meta.order(), meta.includePatterns(), meta.excludePatterns());
    }

    return registry;
  }

  public static void main(String[] args) throws IOException, ServletException {

    TokenEx tokenHaru = new TokenEx(Define.STR_BLANK, UtilExt.loadTextSmart(Haru.CONFIG_HARU));
    TokenEx tokenServlet = new TokenEx(Define.STR_BLANK, UtilExt.loadTextSmart(Haru.CONFIG_SERVLET));

    int port = Integer.parseInt(tokenHaru.get(Haru.PORT));

    contextPath = tokenHaru.get(Haru.CONTEXT_PATH);
    if (contextPath == null || contextPath.isBlank() || Define.SLASH.equals(contextPath)) {
      contextPath = Define.SLASH;
    } else if (!contextPath.startsWith(Define.SLASH)) {
      contextPath = Define.SLASH + contextPath;
    }

    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

    System.out.printf("%n%n%nMiniServletContainer started on port: %d with context-path: %s%n", port, contextPath);
    System.out.println("user.dir : " + Paths.get("").toAbsolutePath());

    String webAppRoot = UtilExt.resolveWebRoot(tokenServlet);

    System.out.println("webAppRoot : " + webAppRoot);

    miniServletContext = new MiniServletContext(webAppRoot);

    MiniServletContainer container = new MiniServletContainer();
    String basePackage = tokenHaru.get(Haru.KEY_BASE_PACKAGE).toString();
    MiniApplicationContext appContext = new MiniApplicationContext();
    appContext.initializeContext(basePackage);
    InterceptorRegistry interceptorRegistry = container.createInterceptorRegistry(appContext, contextPath);

    MiniDispatcherServlet miniDispatcherServlet = new MiniDispatcherServlet(basePackage, appContext, interceptorRegistry, contextPath);

    server.createContext(contextPath, new MiniDispatcherHandler(miniServletContext, miniDispatcherServlet));

    server.setExecutor(null);
    server.start();

  }
}