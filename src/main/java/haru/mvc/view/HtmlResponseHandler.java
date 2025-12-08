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
package haru.mvc.view;

import java.util.List;

import haru.constants.Define;
import haru.core.bootstrap.MiniServletContainer;
import haru.http.MiniHttpServletResponse;
import haru.support.FileEx;

public class HtmlResponseHandler {

  public static boolean handle(String requestUrl, MiniHttpServletResponse resp) {

    if (requestUrl.endsWith(Define.EXT_HTML) || requestUrl.endsWith(Define.EXT_HTM)) {
      try {
        resp.setContentType(Define.TEXT_HTML);

        String filePath = MiniServletContainer.getRealPath(requestUrl);

        List<String> list = FileEx.readEx(filePath, false);

        for (String str : list)
          resp.getWriter().write(str);

        resp.flushBuffer();

      } catch (Exception ex) {
        ex.printStackTrace();
      }

      return true;
    }

    return false;
  }
}
