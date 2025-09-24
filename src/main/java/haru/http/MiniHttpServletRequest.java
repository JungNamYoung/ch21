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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import haru.define.Define;
import haru.kitten.MiniServletContainer;
import haru.servlet.MiniRequestDispatcher;
//import haru.config.MiniWebApplicationContext;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

public class MiniHttpServletRequest implements HttpServletRequest {
  private HttpExchange exchange;
  private BufferedReader reader;
  private final Map<String, String> parameters = new HashMap<>();
  private Map<String, Object> attributes = new HashMap<>();
  private MiniHttpSession session;

  public MiniHttpServletRequest(HttpExchange exchange) {
    this.exchange = exchange;

    String method = exchange.getRequestMethod();
    if (Define.GET.equalsIgnoreCase(method)) {
      parseQueryParameters(exchange.getRequestURI().getQuery());
    } else if (Define.POST.equalsIgnoreCase(method)) {
      parseQueryParameters();
    }
  }

  private void parseQueryParameters() {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
      StringBuilder body = new StringBuilder();
      String line;
      try {
        while ((line = reader.readLine()) != null) {
          body.append(line);
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      parseQueryParameters(body.toString());
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public String getRequestURI() {
    return exchange.getRequestURI().getPath();
  }

  @Override
  public String getMethod() {
    return exchange.getRequestMethod();
  }

  @Override
  public BufferedReader getReader() {
    return reader;
  }

  @Override
  public String getHeader(String name) {
    return exchange.getRequestHeaders().getFirst(name);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    return Collections.enumeration(exchange.getRequestHeaders().keySet());
  }

  private void parseQueryParameters(String query) {
    if (query != null) {
      for (String param : query.split("&")) {
        String[] parts = param.split("=", 2);
        if (parts.length == 2) {
          try {
            parameters.put(URLDecoder.decode(parts[0], Define.UTF8), URLDecoder.decode(parts[1], Define.UTF8));
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        } else if (parts.length == 1) {
          try {
            parameters.put(URLDecoder.decode(parts[0], Define.UTF8), "");
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      }
    }
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String path) {
    return new MiniRequestDispatcher(MiniServletContainer.getMiniWebApplicationContext().getRealPath(Define.STR_BLANK), path);
  }

  @Override
  public Object getAttribute(String name) {
    return attributes.get(name);
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getCharacterEncoding() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
    // TODO Auto-generated method stub

  }

  @Override
  public int getContentLength() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long getContentLengthLong() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getContentType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getParameter(String name) {
    return parameters.get(name);
  }

  @Override
  public Enumeration<String> getParameterNames() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String[] getParameterValues(String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getProtocol() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getScheme() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getServerName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getServerPort() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getRemoteAddr() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getRemoteHost() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setAttribute(String name, Object o) {
    attributes.put(name, o);
  }

  @Override
  public void removeAttribute(String name) {
    attributes.remove(name);
  }

  @Override
  public Locale getLocale() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Enumeration<Locale> getLocales() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isSecure() {
    // TODO Auto-generated method stub
    return false;
  }

//	@Override
//	public RequestDispatcher getRequestDispatcher(String path) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public String getRealPath(String path) {
//		// TODO Auto-generated method stub
//		return null;
//	}

  @Override
  public int getRemotePort() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getLocalName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getLocalAddr() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getLocalPort() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public ServletContext getServletContext() {
    // TODO Auto-generated method stub
//		return null;
    return MiniServletContainer.getMiniWebApplicationContext();
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isAsyncStarted() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isAsyncSupported() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public AsyncContext getAsyncContext() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public DispatcherType getDispatcherType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getAuthType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Cookie[] getCookies() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long getDateHeader(String name) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getIntHeader(String name) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getPathInfo() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getPathTranslated() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getContextPath() {
    return MiniServletContainer.getContextPath();
  }

  @Override
  public String getQueryString() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getRemoteUser() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isUserInRole(String role) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Principal getUserPrincipal() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getRequestedSessionId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StringBuffer getRequestURL() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getServletPath() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HttpSession getSession(boolean create) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HttpSession getSession() {
//    return null;
    return session;
  }

  @Override
  public String changeSessionId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    // TODO Auto-generated method stub
    return false;
  }

//	@Override
//	public boolean isRequestedSessionIdFromUrl() {
//		// TODO Auto-generated method stub
//		return false;
//	}

  @Override
  public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void login(String username, String password) throws ServletException {
    // TODO Auto-generated method stub

  }

  @Override
  public void logout() throws ServletException {
    // TODO Auto-generated method stub

  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Part getPart(String name) throws IOException, ServletException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getRequestId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getProtocolRequestId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ServletConnection getServletConnection() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public void setSession(MiniHttpSession session) {
    this.session = session;
  }
}