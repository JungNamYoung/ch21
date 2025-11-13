package haru.support;

import haru.constants.Define;

public final class PathUtils {

  private PathUtils() {
  }
  
  /*
  사용자가 요청한 requestURI
  
  - ? 제거
  - contextPath 제거
  - 슬래시 정규화
      
  실제 요청 경로를 내부 로직에서 비교 가능하도록 정리
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
  개발자가 작성한 매핑 경로

  - "/" 자동 추가
  - 끝 "/" 제거

  컨트롤러/인터셉터 등록 시 경로 비교 규칙 통일
  */
  public static String normalizeMappingPath(String path) {
    if (path == null || path.isEmpty())
      return Define.SLASH;

    String normalized = path.startsWith(Define.SLASH) ? path : Define.SLASH + path;
    if (normalized.length() > 1 && normalized.endsWith(Define.SLASH))
      normalized = normalized.substring(0, normalized.length() - 1);

    return normalized;
  }
}