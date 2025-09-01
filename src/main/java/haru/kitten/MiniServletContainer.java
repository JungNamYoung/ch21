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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpServer;

import haru.define.Haru;
import haru.util.TokenEx;
import haru.util.UtilExt;
import jakarta.servlet.ServletException;

public class MiniServletContainer {

  private static MiniServletContext miniServletContext;

  public static MiniServletContext getMiniWebApplicationContext() {
    return miniServletContext;
  }

  public static String getContextPath() {
    return Haru.CONTEXT_PATH;
  }

  public static String getRealPath(String requestedResource) {

    if (requestedResource.startsWith(getContextPath())) {
      requestedResource = requestedResource.substring(getContextPath().length());
    }

    String filePath = MiniServletContext.getWebAppRoot() + requestedResource;

    return filePath;
  }

  public static void main(String[] args) throws IOException, ServletException {

    HttpServer server = HttpServer.create(new InetSocketAddress(Haru.PORT), 0);

    String str = String.format("\nMiniServletContainer started on port: %d with context path: %s", Haru.PORT, MiniServletContainer.getContextPath());
    System.out.println(str);

    TokenEx tokenEx = new TokenEx(UtilExt.getClassPath(Haru.CONFIG_HARU));
    String webRoot = Paths.get("").toAbsolutePath().resolve(tokenEx.get(Haru.ROOT_PATH)).toString();

    miniServletContext = new MiniServletContext(webRoot);

    MiniDispatcherServlet miniDispatcherServlet = new MiniDispatcherServlet(tokenEx.get(Haru.KEY_SCAN_PACKAGE).toString());

    server.createContext(MiniServletContainer.getContextPath(), new MiniDispatcherHandler(miniServletContext, miniDispatcherServlet));

    server.setExecutor(null);
    server.start();

  }
}