package haru.mvc.interceptor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class InterceptorRegistry {
  private final List<InterceptorRegistration> registrations = new ArrayList<>();
  private final String contextPath;

  public InterceptorRegistry(String contextPath) {
    this.contextPath = contextPath == null ? "" : contextPath;
  }

  public void register(HandlerInterceptor itc, int order, String[] includes, String[] excludes) {
    registrations.add(new InterceptorRegistration(itc, order, includes, excludes));
    registrations.sort(Comparator.comparingInt(r -> r.order));
  }

  public List<HandlerInterceptor> resolveChain(String requestURI) {
    final String path = normalizePath(requestURI, contextPath);
    return registrations.stream().filter(r -> matchesAny(r.includes, path) && !matchesAny(r.excludes, path)).map(r -> r.interceptor).collect(Collectors.toList());
  }

  private static boolean matchesAny(List<String> patterns, String path) {
    for (String raw : patterns) {
      if (raw == null || raw.isBlank())
        continue;
      final String pat = ensureLeadingSlashForPathPattern(raw.trim());
      if (matches(pat, path))
        return true;
    }
    return false;
  }

  static boolean matches(String pattern, String path) {
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
      return path.equals(prefix) || path.startsWith(prefix + "/");
    }
    return path.equals(pattern);
  }

  static String normalizePath(String requestURI, String contextPath) {
    String uri = requestURI;
    int q = uri.indexOf('?');
    if (q >= 0)
      uri = uri.substring(0, q);
    if (!contextPath.isEmpty() && uri.startsWith(contextPath)) {
      uri = uri.substring(contextPath.length());
    }
    if (uri.isEmpty())
      uri = "/";
    return uri.replaceAll("/{2,}", "/");
  }

  static String ensureLeadingSlashForPathPattern(String p) {
    if (p.startsWith("*."))
      return p;
    if (!p.startsWith("/"))
      return "/" + p;
    return p;
  }
}
