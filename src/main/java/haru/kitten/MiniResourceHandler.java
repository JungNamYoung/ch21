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

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

import haru.define.Define;
import haru.logger.LoggerManager;
import jakarta.servlet.http.HttpServletResponse;

public class MiniResourceHandler {

  static Logger logger = LoggerManager.getLogger(MiniResourceHandler.class.getSimpleName());

  public static boolean handle(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {
    String requestUri = miniHttpServletRequest.getRequestURI();
    String filePath = MiniServletContainer.getRealPath(requestUri);

    File file = new File(filePath);

    if (!file.exists() || file.isDirectory()) {
      return false;
    }

    String mimeType = miniHttpServletRequest.getServletContext().getMimeType(file.getName());
    if (mimeType == null) {
      mimeType = getContentType(file.getName());
    }
    miniHttpServletResponse.setContentType(mimeType);

    String rangeHeader = miniHttpServletRequest.getHeader("Range");
    if (rangeHeader != null) {
      long fileLength = file.length();
      long start = 0;
      long end = fileLength - 1;

      String rangeValue = rangeHeader.replace("bytes=", "").trim();
      String[] ranges = rangeValue.split(Define.DASH);
      try {
        start = Long.parseLong(ranges[0].trim());
        if (ranges.length > 1 && !ranges[1].isEmpty()) {
          end = Long.parseLong(ranges[1].trim());
        }
      } catch (NumberFormatException e) {
        start = 0;
        end = fileLength - 1;
      }
      if (end >= fileLength) {
        end = fileLength - 1;
      }

      long contentLength = end - start + 1;

      miniHttpServletResponse.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
      miniHttpServletResponse.setHeader("Accept-Ranges", Define.BYTES);
      miniHttpServletResponse.setContentLengthLong(contentLength);
      miniHttpServletResponse.setHeader("Content-Range", "bytes " + start + Define.DASH + end + "/" + fileLength);
      miniHttpServletResponse.setHeader(Define.CONNECTION, "keep-alive");
      miniHttpServletResponse.setHeader("keep-Alive", "timeout=20");
      miniHttpServletResponse.sendHeaders();

      try (RandomAccessFile raf = new RandomAccessFile(file, "r"); OutputStream os = miniHttpServletResponse.getOutputStream()) {
        raf.seek(start);
        byte[] buffer = new byte[8192];
        long remaining = contentLength;
        int bytesRead;
        long totalBytesWritten = 0;

        while (remaining > 0 && (bytesRead = raf.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
          try {
            os.write(buffer, 0, bytesRead);
          } catch (Exception ex) {
            ex.printStackTrace();
            break;
          }

          totalBytesWritten += bytesRead;
          remaining -= bytesRead;
        }

        try {
          os.flush();
        } catch (Exception ex) {
          ex.printStackTrace();
        }

        logger.info("totalBytesWritten : contentLength = " + totalBytesWritten + " : " + contentLength);

      } catch (Exception ex) {
        ex.printStackTrace();
      }

    } else {
      miniHttpServletResponse.setContentLengthLong(file.length());
      miniHttpServletResponse.sendHeaders();

      try (FileInputStream fis = new FileInputStream(file); OutputStream os = miniHttpServletResponse.getOutputStream()) {
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
          os.write(buffer, 0, bytesRead);
        }
        os.flush();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return true;
  }

  private static String getContentType(String filePath) {
    if (filePath.endsWith(".css")) {
      return "text/css";
    } else if (filePath.endsWith(".js")) {
      return "application/javascript";
    } else if (filePath.endsWith(Define.EXT_HTML) || filePath.endsWith(Define.EXT_HTM)) {
      return "text/html";
    } else if (filePath.endsWith(".png")) {
      return "image/png";
    } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
      return "image/jpeg";
    } else if (filePath.endsWith(".gif")) {
      return "image/gif";
    } else if (filePath.endsWith(Define.EXT_MP4)) {
      return "video/mp4";
    } else {
      return "application/octet-stream";
    }
  }

}
