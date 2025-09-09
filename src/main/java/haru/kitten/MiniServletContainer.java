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

import haru.define.Define;
import haru.define.Haru;
import haru.util.TokenEx;
import haru.util.UtilExt;
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

    if (requestedResource.startsWith(getContextPath())) {
      requestedResource = requestedResource.substring(getContextPath().length());
    }

    String filePath = MiniServletContext.getWebAppRoot() + requestedResource;

    return filePath;
  }

  public static void main(String[] args) throws IOException, ServletException {

		TokenEx tokenEx = new TokenEx(Define.STR_BLANK, UtilExt.loadTextSmart(Haru.CONFIG_HARU));
		
		int port = Integer.parseInt(tokenEx.get(Haru.PORT));
		
		contextPath = tokenEx.get(Haru.CONTEXT_PATH);
		
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

		System.out.printf("%nMiniServletContainer started on port: %d with context path: %s%n", port, contextPath);
		System.out.println("user.dir : " + Paths.get("").toAbsolutePath());

		String webRoot = UtilExt.resolveWebRoot(tokenEx);

		System.out.println("webRoot : " + webRoot);

    miniServletContext = new MiniServletContext(webRoot);

		MiniDispatcherServlet miniDispatcherServlet = new MiniDispatcherServlet(
				tokenEx.get(Haru.KEY_SCAN_PACKAGE).toString());

		server.createContext(contextPath,	new MiniDispatcherHandler(miniServletContext, miniDispatcherServlet));

    server.setExecutor(null);
    server.start();

  }
}