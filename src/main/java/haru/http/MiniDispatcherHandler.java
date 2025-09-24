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
package haru.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import haru.constants.Define;
import haru.core.MiniDispatcherServlet;
import haru.core.bootstrap.MiniServletContainer;
import haru.http.session.MiniSessionManager;
import haru.logging.LoggerManager;
import haru.servlet.MiniServletContext;
import haru.servlet.filter.Filter;
import haru.servlet.filter.FilterChain;
import haru.servlet.filter.IdParameterFilter;
import haru.servlet.filter.MiniFilterChain;
import haru.servlet.filter.SnParameterFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

public class MiniDispatcherHandler implements HttpHandler {

  MiniServletContext miniServletContext;
  MiniDispatcherServlet miniDispatcherServlet;

  Logger logger = LoggerManager.getLogger(this.getClass().getSimpleName());

  public MiniDispatcherHandler(MiniServletContext miniServletContext, MiniDispatcherServlet miniDispatcherServlet) {
    this.miniServletContext = miniServletContext;
    this.miniDispatcherServlet = miniDispatcherServlet;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    System.out.println(Define.STR_BLANK);
    logger.info("HTTP server received");

    String requestPath = exchange.getRequestURI().getPath();
    
    if (Define.FAVICON.equals(requestPath)) {
      logger.info("Ignoring " + Define.FAVICON + " request");
      exchange.sendResponseHeaders(HttpServletResponse.SC_NO_CONTENT, -1);
      return;
    }

    Headers headers = exchange.getRequestHeaders();
    String sessionId = getSessionIdFromCookie(headers);
    MiniHttpSession miniHttpSession = MiniSessionManager.getSession(sessionId);

    if (miniHttpSession == null) {
      miniHttpSession = MiniSessionManager.createSession(MiniServletContainer.getMiniWebApplicationContext());
      exchange.getResponseHeaders().set(Define.SET_COOKIE, Define.JSESSIONID + miniHttpSession.getId() + "; Path=/; HttpOnly");
    }

    MiniHttpServletRequest miniHttpServletRequest = new MiniHttpServletRequest(exchange);
    MiniHttpServletResponse miniHttpServletResponse = new MiniHttpServletResponse(exchange);

    miniHttpServletRequest.setSession(miniHttpSession);

    List<Filter> filters = new ArrayList<>();
    filters.add(new SnParameterFilter());
    filters.add(new IdParameterFilter());
//    filters.add(new ExecutionTimeFilter());

    FilterChain filterChain = new MiniFilterChain(filters, miniDispatcherServlet);

    try {
      filterChain.doFilter(miniHttpServletRequest, miniHttpServletResponse);
    } catch (ServletException e) {
      e.printStackTrace();
    }
  }

  private String getSessionIdFromCookie(Headers headers) {
    List<String> cookies = headers.get(Define.COOKIE);
    if (cookies != null) {
      for (String cookie : cookies) {
        if (cookie.startsWith(Define.JSESSIONID)) {
          return cookie.split(Define.EQUAL)[1];
        }
      }
    }
    return null;
  }
}