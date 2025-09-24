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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

import haru.config.MiniServletOutputStream;
import haru.define.Define;
import haru.kitten.MiniServletContainer;
import haru.logging.LoggerManager;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class MiniHttpServletResponse implements HttpServletResponse {

  static Logger logger = LoggerManager.getLogger(MiniHttpServletResponse.class.getSimpleName());

  private final HttpExchange exchange;
  private OutputStream outputStream;
  private PrintWriter writer;
  private int statusCode = HttpServletResponse.SC_OK;
  private String contentType = Define.TEXT_PLAIN;
  private boolean headersSent = false;
  private long contentLength = 0;
  private boolean contentLengthSet = false;

  public MiniHttpServletResponse(HttpExchange exchange) {
    this.exchange = exchange;
  }

  @Override
  public void setStatus(int sc) {

    checkSendHeader();

    this.statusCode = sc;
  }

  @Override
  public void setContentType(String type) {

    checkSendHeader();

    this.contentType = type;
  }

  @Override
  public PrintWriter getWriter() {
    if (writer == null) {
      initOutputStream();
      writer = new PrintWriter(outputStream);
    }
    return writer;
  }

  @Override
  public void flushBuffer() throws IOException {

    sendHeaders();

    try {
      if (writer != null) {
        writer.flush();
        writer.close();
      } else if (outputStream != null) {
        outputStream.flush();
        outputStream.close();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initOutputStream() {
    if (outputStream == null) {
      sendHeaders();
      outputStream = exchange.getResponseBody();
    }
  }

  public void sendHeaders() {
    if (!headersSent) {
      try {
        exchange.getResponseHeaders().set(Define.CONTENT_TYPE, contentType);
        if (contentLengthSet) {
          exchange.sendResponseHeaders(statusCode, contentLength);
        } else {
          exchange.getResponseHeaders().remove(Define.CONTENT_LENGTH);
          exchange.sendResponseHeaders(statusCode, 0);
        }

        logger.info("send - Response - Headers");

        headersSent = true;
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  @Override
  public String getCharacterEncoding() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    initOutputStream();
    return new MiniServletOutputStream(outputStream);
  }

  @Override
  public void setCharacterEncoding(String charset) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setContentLength(int len) {
    throw new RuntimeException("setContentLengthLong() 사용하여라");
  }

  @Override
  public void setContentLengthLong(long len) {

    checkSendHeader();

    contentLength = len;
    contentLengthSet = true;

    exchange.getResponseHeaders().set(Define.CONTENT_LENGTH, String.valueOf(len));
  }

  @Override
  public void setBufferSize(int size) {
    // TODO Auto-generated method stub

  }

  @Override
  public int getBufferSize() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void resetBuffer() {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isCommitted() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void reset() {
    // TODO Auto-generated method stub

  }

  @Override
  public void setLocale(Locale loc) {
    // TODO Auto-generated method stub

  }

  @Override
  public Locale getLocale() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addCookie(Cookie cookie) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean containsHeader(String name) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String encodeURL(String url) {
    if (url == null) {
      return null;
    }

    String contextPath = MiniServletContainer.getContextPath();

    if (!url.startsWith(Define.HTTP)) {
      if (Define.SLASH.equals(contextPath)) {
        if (!url.startsWith(Define.SLASH)) {
          url = Define.SLASH + url;
        }
      } else if (!url.startsWith(contextPath)) {
        url = contextPath + url;
      }
    }

    return url;
  }

  @Override
  public String encodeRedirectURL(String url) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void sendError(int sc, String msg) throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public void sendError(int sc) throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public void sendRedirect(String location) throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public void setDateHeader(String name, long date) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addDateHeader(String name, long date) {
    // TODO Auto-generated method stub

  }

  private void checkSendHeader() {
    if (headersSent)
      throw new RuntimeException("Headers have already been sent, cannot modify them.");
  }

  @Override
  public void setHeader(String name, String value) {

    checkSendHeader();

    exchange.getResponseHeaders().set(name, value);
  }

  @Override
  public void addHeader(String name, String value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setIntHeader(String name, int value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addIntHeader(String name, int value) {
    // TODO Auto-generated method stub

  }

  @Override
  public int getStatus() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getHeader(String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<String> getHeaders(String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<String> getHeaderNames() {
    // TODO Auto-generated method stub
    return null;
  }
}
