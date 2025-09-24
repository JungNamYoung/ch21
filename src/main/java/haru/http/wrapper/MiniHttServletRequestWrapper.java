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

package haru.http.wrapper;

import haru.core.bootstrap.MiniServletContainer;
import haru.http.MiniHttpSession;
import haru.servlet.MiniServletContext;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

public class MiniHttServletRequestWrapper extends HttpServletRequestWrapper {
  private MiniServletContext miniServletContext;
  private MiniHttpSession miniHttpSession;

  public MiniHttServletRequestWrapper(HttpServletRequest request, MiniServletContext miniServletContext, MiniHttpSession miniHttpSession) {
    super(request);
    this.miniServletContext = miniServletContext;
    this.miniHttpSession = miniHttpSession;
  }

  @Override
  public ServletContext getServletContext() {
    return this.miniServletContext;
  }

  @Override
  public HttpSession getSession() {
    return miniHttpSession;
  }

  @Override
  public HttpSession getSession(boolean create) {
    if (create && miniHttpSession == null) {
      miniHttpSession = new MiniHttpSession(getServletContext());
    }

    return miniHttpSession;
  }

  @Override
  public String getContextPath() {
    return MiniServletContainer.getContextPath();
  }
}
