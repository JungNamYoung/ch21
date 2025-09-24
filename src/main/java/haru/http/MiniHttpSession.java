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

import java.util.Enumeration;
import java.util.Hashtable;

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

  @Override
  public void setMaxInactiveInterval(int interval) {
    // TODO Auto-generated method stub
  }

  @Override
  public int getMaxInactiveInterval() {
    // TODO Auto-generated method stub
    return 0;
  }

//	@Override
//	public HttpSessionContext getSessionContext() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public Object getValue(String name) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String[] getValueNames() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void putValue(String name, Object value) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void removeValue(String name) {
//		// TODO Auto-generated method stub
//
//	}

  @Override
  public boolean isNew() {
    // TODO Auto-generated method stub
    return false;
  }
}