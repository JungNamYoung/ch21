package haru.support;

import java.util.Locale;

import haru.constants.Define;

public final class RequestPathUtils {

  private RequestPathUtils() {
  }

  /*
   * 사용자가 요청한 requestURI
   * 
   * - ? 제거 - contextPath 제거 - 슬래시 정규화
   * 
   * 실제 요청 경로를 내부 로직에서 비교 가능하도록 정리
   */
  public static String normalizeRequestPath(String requestURI, String contextPath) {
    if (requestURI == null || requestURI.isEmpty())
      return Define.SLASH;

    String uri = requestURI;
    int q = uri.indexOf('?');
    if (q >= 0)
      uri = uri.substring(0, q);

    String ctxPath = contextPath == null ? "" : contextPath;
    if (!ctxPath.isEmpty() && uri.startsWith(ctxPath))
      uri = uri.substring(ctxPath.length());

    if (uri.isEmpty())
      uri = Define.SLASH;

    return uri.replaceAll("/{2,}", Define.SLASH);
  }

  /*
   * 개발자가 작성한 매핑 경로
   * 
   * - "/" 자동 추가 - 끝 "/" 제거
   * 
   * 컨트롤러/인터셉터 등록 시 경로 비교 규칙 통일
   */
  public static String normalizeMappingPath(String path) {
    if (path == null || path.isEmpty())
      return Define.SLASH;

    String normalized = path.startsWith(Define.SLASH) ? path : Define.SLASH + path;
    if (normalized.length() > 1 && normalized.endsWith(Define.SLASH))
      normalized = normalized.substring(0, normalized.length() - 1);

    return normalized;
  }

  public static String resolveRequestUri(String requestUrl, String contextPath) {
    if (!Define.SLASH.equals(contextPath) && requestUrl.startsWith(contextPath)) {
      return requestUrl.substring(contextPath.length());
    }
    return requestUrl;
  }

  // URI에서 path만 안정적으로 쓰기 위해 최소한의 정규화입니다.
  // (getRequestURI()가 path만 준다는 전제라도, ;jsessionid 같은 케이스를 방어합니다.)
  public static String normalizePathOnly(String requestUri) {
    if (requestUri == null)
      return "";

    int semicolon = requestUri.indexOf(';');
    if (semicolon >= 0) {
      requestUri = requestUri.substring(0, semicolon);
    }
    return requestUri;
  }

  // 마지막 '.' 이후를 확장자로 보고 소문자로 반환합니다.
  // "/a/b.min.js" -> "js"
  // "/a/b" -> null
  public static String getExtensionLower(String path) {
    int slash = path.lastIndexOf('/');
    int dot = path.lastIndexOf('.');
    if (dot < 0 || dot < slash) {
      return null;
    }
    return path.substring(dot + 1).toLowerCase(Locale.ROOT);
  }

  public static String ensureLeadingSlashForPathPattern(String pattern) {
    if (pattern == null || pattern.isEmpty())
      return "/";
    if (pattern.startsWith("*."))
      return pattern;
    if (!pattern.startsWith("/"))
      return "/" + pattern;
    return pattern;
  }

  public static boolean matches(String pattern, String path) {
    if (pattern == null || path == null)
      return false;

    if ("/*".equals(pattern))
      return true;

    if (!pattern.contains("*"))
      return path.equals(pattern);

    if (pattern.startsWith("*.")) {
      String ext = pattern.substring(1);
      return path.endsWith(ext);
    }

    if (pattern.endsWith("/*")) {
      String prefix = pattern.substring(0, pattern.length() - 2);
      if (prefix.isEmpty())
        return true;

      return path.equals(prefix) || path.startsWith(prefix + "/");
    }

    return path.equals(pattern);
  }
}