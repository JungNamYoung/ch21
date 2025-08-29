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

public class HandlerMapping {
  private String requestPpath;
  private Method method;
  private Object aop;
  private BeanDefinition beanDefinition;

  public HandlerMapping(String pathRequest, Method method, BeanDefinition beanDefinition) {
    this.requestPpath = pathRequest;
    this.method = method;
    this.beanDefinition = beanDefinition;
  }

  public String getPathRequest() {
    return requestPpath;
  }

  public void setPathRequest(String pathRequest) {
    this.requestPpath = pathRequest;
  }

  public Method getMethod() {
    return method;
  }

  public BeanDefinition getBeanDefinition() {
    return beanDefinition;
  }

}