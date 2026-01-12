package haru.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

//public class BodyCachingRequestWrapper extends MiniHttpServletRequest {
//
//  private final MiniHttpServletRequest original;
//  private final byte[] cachedBody;
//
//  public BodyCachingRequestWrapper(MiniHttpServletRequest original) throws IOException {
//    this.original = original;
//    this.cachedBody = readAllBytes(original.getInputStream());
//  }
//
//  @Override
//  public InputStream getInputStream() {
//    return new ByteArrayInputStream(cachedBody);
//  }
//
//  public byte[] getCachedBody() {
//    return cachedBody.clone();
//  }
//
//  public String getCachedBodyAsString() {
//    String enc = original.getCharacterEncoding();
//    Charset cs = (enc != null) ? Charset.forName(enc) : Charset.forName("UTF-8");
//    return new String(cachedBody, cs);
//  }
//
//  private static byte[] readAllBytes(InputStream in) throws IOException {
//    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//    byte[] buf = new byte[4096];
//    int r;
//    while ((r = in.read(buf)) != -1) {
//      bos.write(buf, 0, r);
//    }
//    return bos.toByteArray();
//  }
//
//  // 필요 시 original의 다른 동작을 위임(delegate)하도록 추가 확장합니다.
//  // 예: getHeader(), getMethod(), getRequestURI() 등
//}
