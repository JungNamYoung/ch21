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

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;

import haru.define.Define;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.FilterRegistration.Dynamic;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;

public class MiniServletContext implements ServletContext {
  static private String webAppRoot;
  private Hashtable<String, Object> attributes = new Hashtable<>();

  public MiniServletContext(String webAppRoot) {
    this.webAppRoot = webAppRoot;

    attributes.put(InstanceManager.class.getName(), new SimpleInstanceManager());
  }

  @Override
  public String getRealPath(String path) {
    return webAppRoot + path;
  }

  static public String getWebAppRoot() {

    if (webAppRoot == null || webAppRoot.length() == Define.COUNT_0)
      throw new RuntimeException(Define.NOT_APPLICABLE);

    return webAppRoot;
  }

  @Override
  public ServletContext getContext(String uripath) {
    return null;
  }

  @Override
  public int getMajorVersion() {
    return 3;
  }

  @Override
  public int getMinorVersion() {
    return 1;
  }

  @Override
  public int getEffectiveMajorVersion() {
    return 3;
  }

  @Override
  public int getEffectiveMinorVersion() {
    return 1;
  }

  @Override
  public String getMimeType(String file) {
    return null;
  }

  @Override
  public Set<String> getResourcePaths(String path) {
    return null;
  }

  @Override
  public URL getResource(String path) {
    return null;
  }

  @Override
  public InputStream getResourceAsStream(String path) {
    return null;
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String path) {
    return null;
  }

  @Override
  public RequestDispatcher getNamedDispatcher(String name) {
    return null;
  }

//	@Override public Servlet getServlet(String name) { return null;}
//	@Override public Enumeration<Servlet> getServlets() { return Collections.emptyEnumeration();}
//	@Override public Enumeration<String> getServletNames() { return Collections.emptyEnumeration();}
  @Override
  public void log(String msg) {
    System.out.println(msg);
  }

//	@Override public void log(Exception exception, String msg) { System.out.println(msg); exception.printStackTrace();}
  @Override
  public void log(String message, Throwable throwable) {
    System.out.println(message);
    throwable.printStackTrace();
  }

  @Override
  public String getServerInfo() {
    return "MiniServletContainer/1.0";
  }

  @Override
  public String getInitParameter(String name) {
    return null;
  }

  @Override
  public Enumeration<String> getInitParameterNames() {
    return Collections.emptyEnumeration();
  }

  @Override
  public boolean setInitParameter(String name, String value) {
    return false;
  }

  @Override
  public Object getAttribute(String name) {
    return attributes.get(name);
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return attributes.keys();
  }

  @Override
  public void setAttribute(String name, Object object) {

    attributes.put(name, object);
  }

  @Override
  public void removeAttribute(String name) {
    attributes.remove(name);
  }

  @Override
  public String getServletContextName() {
    return "MiniServletContext";
  }

  @Override
  public String getContextPath() {
    return MiniServletContainer.getContextPath();
  }

//	@Override
//	public Dynamic addServlet(String servletName, String className) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Dynamic addServlet(String servletName, Servlet servlet) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public Dynamic addJspFile(String servletName, String jspFile) {
//		// TODO Auto-generated method stub
//		return null;
//	}

  @Override
  public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ServletRegistration getServletRegistration(String servletName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, ? extends ServletRegistration> getServletRegistrations() {
    // TODO Auto-generated method stub
    return null;
  }

//	@Override
//	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
//		// TODO Auto-generated method stub
//		return null;
//	}

  @Override
  public FilterRegistration getFilterRegistration(String filterName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SessionCookieConfig getSessionCookieConfig() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
    // TODO Auto-generated method stub

  }

  @Override
  public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addListener(String className) {
    // TODO Auto-generated method stub

  }

  @Override
  public <T extends EventListener> void addListener(T t) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addListener(Class<? extends EventListener> listenerClass) {
    // TODO Auto-generated method stub

  }

  @Override
  public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public JspConfigDescriptor getJspConfigDescriptor() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ClassLoader getClassLoader() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void declareRoles(String... roleNames) {
    // TODO Auto-generated method stub

  }

  @Override
  public String getVirtualServerName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getSessionTimeout() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setSessionTimeout(int sessionTimeout) {
    // TODO Auto-generated method stub

  }

  @Override
  public String getRequestCharacterEncoding() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setRequestCharacterEncoding(String encoding) {
    // TODO Auto-generated method stub

  }

  @Override
  public String getResponseCharacterEncoding() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setResponseCharacterEncoding(String encoding) {
    // TODO Auto-generated method stub

  }

//  public void setMiniDispatcherServlet(MiniDispatcherServlet miniDispatcherServlet) {
//    this.miniDispatcherServlet = miniDispatcherServlet;
//  }
//
//  public MiniDispatcherServlet getMiniDispatcherServlet() {
//    return miniDispatcherServlet;
//  }

  @Override
  public Dynamic addFilter(String filterName, Filter filter) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName, String className) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public jakarta.servlet.ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public jakarta.servlet.ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Dynamic addFilter(String filterName, String className) {
    // TODO Auto-generated method stub
    return null;
  }
}
