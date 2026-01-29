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
package haru.mvc;

import java.lang.reflect.Method;

import haru.core.context.BeanHolder;

public class HandlerMapping {
  private String requestPath;
  private final String httpMethod; // "GET", "POST" 등
  private Method method;
  private Object aop;
  private BeanHolder beanHolder;

  public HandlerMapping(String pathRequest, String httpMethod, Method method, BeanHolder beanHolder) {
    this.requestPath = pathRequest;
    this.httpMethod = httpMethod;
    this.method = method;
    this.beanHolder = beanHolder;

  }

  public String getPathRequest() {
    return requestPath;
  }

  public void setPathRequest(String pathRequest) {
    this.requestPath = pathRequest;
  }

  public Method getMethod() {
    return method;
  }

  public BeanHolder getBeanDefinition() {
    return beanHolder;
  }

  public String getHttpMethod() {
    return httpMethod;
  }
}