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

package haru.servlet.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Set;
import java.util.logging.Logger;

import haru.constants.Define;
import haru.core.bootstrap.MiniServletContainer;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.logging.MiniLogger;
import haru.mvc.view.StaticHandler;
import haru.support.RequestPathUtils;
import jakarta.servlet.http.HttpServletResponse;

public class ResourceResponseHandler implements StaticHandler {

  private static final Logger logger = MiniLogger.getLogger(ResourceResponseHandler.class.getSimpleName());

  // “정적 파일”로 인정할 확장자만 허용합니다.
  // 필요에 따라 계속 추가하면 됩니다.
  private static final Set<String> STATIC_EXTS = Set.of("css", "js", "html", "htm", "png", "jpg", "jpeg", "gif", "svg", "webp", "ico", "json", "txt", "xml", "woff", "woff2", "ttf", "otf", "mp4", "webm", "mp3", "wav", "pdf");

  @Override
  public boolean supports(MiniHttpServletRequest request) {

    String uri = RequestPathUtils.normalizePathOnly(request.getRequestURI());

    // 확장자가 없는 경로는 정적 파일로 보지 않습니다.
    String ext = RequestPathUtils.getExtensionLower(uri);
    if (ext == null || !STATIC_EXTS.contains(ext)) {
      return false;
    }

    // 실제 파일이 존재하는지로 최종 판정합니다.
    String filePath = MiniServletContainer.getRealPath(uri);
    File file = new File(filePath);
    return file.exists() && file.isFile();
  }

//  // URI에서 path만 안정적으로 쓰기 위해 최소한의 정규화입니다.
//  // (getRequestURI()가 path만 준다는 전제라도, ;jsessionid 같은 케이스를 방어합니다.)
//  private String normalizePathOnly(String requestUri) {
//    if (requestUri == null)
//      return "";
//
//    int semicolon = requestUri.indexOf(';');
//    if (semicolon >= 0) {
//      requestUri = requestUri.substring(0, semicolon);
//    }
//    return requestUri;
//  }
//
//  // 마지막 '.' 이후를 확장자로 보고 소문자로 반환합니다.
//  // "/a/b.min.js" -> "js"
//  // "/a/b" -> null
//  private String getExtensionLower(String path) {
//    int slash = path.lastIndexOf('/');
//    int dot = path.lastIndexOf('.');
//    if (dot < 0 || dot < slash) {
//      return null;
//    }
//    return path.substring(dot + 1).toLowerCase(Locale.ROOT);
//  }

  @Override
  public void handle(MiniHttpServletRequest request, MiniHttpServletResponse response) throws Exception {
    String requestUri = request.getRequestURI();

    String filePath = MiniServletContainer.getRealPath(requestUri);
    File file = new File(filePath);

    String mimeType = request.getServletContext().getMimeType(file.getName());
    if (mimeType == null) {
      mimeType = getContentType(file.getName());
    }
    response.setContentType(mimeType);

    String rangeHeader = request.getHeader("Range");
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

      response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
      response.setHeader("Accept-Ranges", Define.BYTES);
      response.setContentLengthLong(contentLength);
      response.setHeader("Content-Range", "bytes " + start + Define.DASH + end + Define.SLASH + fileLength);
      response.setHeader(Define.CONNECTION, "keep-alive");
      response.setHeader("keep-Alive", "timeout=20");
      response.sendHeaders();

      try (RandomAccessFile raf = new RandomAccessFile(file, "r"); OutputStream os = response.getOutputStream()) {
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
      response.setContentLengthLong(file.length());
      response.sendHeaders();

      try (FileInputStream fis = new FileInputStream(file); OutputStream os = response.getOutputStream()) {
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