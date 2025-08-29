package haru.config;

import java.util.Enumeration;
import java.util.Hashtable;

//import javax.servlet.http.HttpSessionContext;

import haru.define.Haru;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

public class MiniHttpSession implements HttpSession {
  private String id;
  private Hashtable<String, Object> attributes = new Hashtable<>();
  private ServletContext context;
  private long creationTime;
  private long lastAccessedTime;
  private boolean isValid;
  private int maxSecondActive = Haru.SESSION_ACTIVE_SECOND;
//	private int maxSecondActive = 10;

  public MiniHttpSession(ServletContext context) {
//		this.id = UUID.randomUUID().toString();
    this.id = "kitten-" + System.currentTimeMillis();
    this.context = context;
    this.creationTime = System.currentTimeMillis();
    this.lastAccessedTime = this.creationTime;
    this.isValid = true;
  }

  @Override
  public long getCreationTime() {
    return creationTime;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public long getLastAccessedTime() {
    return lastAccessedTime;
  }

  @Override
  public ServletContext getServletContext() {
    return context;
  }

  @Override
  public void setAttribute(String name, Object value) {
    attributes.put(name, value);
  }

  @Override
  public Object getAttribute(String name) {
    return attributes.get(name);
  }

  @Override
  public void removeAttribute(String name) {
    attributes.remove(name);
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return attributes.keys();
  }

  @Override
  public void invalidate() {
    isValid = false;
    attributes.clear();
  }

  public boolean isValid() {
    if (!isValid) {
      return false;
    }
    long now = System.currentTimeMillis();
    return (now - lastAccessedTime) < (maxSecondActive * 1000);
  }

  public void updateLastAccessedTime() {
    this.lastAccessedTime = System.currentTimeMillis();
  }

  // ------------------------------------------------------------------

  @Override
  public void setMaxInactiveInterval(int interval) {
    // TODO Auto-generated method stub

  }

  @Override
  public int getMaxInactiveInterval() {
    // TODO Auto-generated method stub
    return 0;
  }
//
//  @Override
//  public HttpSessionContext getSessionContext() {
//    // TODO Auto-generated method stub
//    return null;
//  }
//
//  @Override
//  public Object getValue(String name) {
//    // TODO Auto-generated method stub
//    return null;
//  }
//
//  @Override
//  public String[] getValueNames() {
//    // TODO Auto-generated method stub
//    return null;
//  }
//
//  @Override
//  public void putValue(String name, Object value) {
//    // TODO Auto-generated method stub
//
//  }
//
//  @Override
//  public void removeValue(String name) {
//    // TODO Auto-generated method stub
//
//  }

  @Override
  public boolean isNew() {
    // TODO Auto-generated method stub
    return false;
  }

//	@Override
//	public boolean isNew() {
//		return (System.currentTimeMillis() - creationTime) < 5000;
//	}
}