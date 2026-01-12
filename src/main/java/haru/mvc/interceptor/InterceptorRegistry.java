package haru.mvc.interceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import haru.support.RequestPathUtils;

public final class InterceptorRegistry {
  private final List<InterceptorRegistration> registrations = new ArrayList<>();
  private final String contextPath;

  public InterceptorRegistry(String contextPath) {
    this.contextPath = contextPath == null ? "" : contextPath;
  }

  public void register(HandlerInterceptor itc, int order, String[] includes, String[] excludes) {

    registrations.add(new InterceptorRegistration(itc, order, includes, excludes));

    Comparator<InterceptorRegistration> comparator = new Comparator<InterceptorRegistration>() {
      @Override
      public int compare(InterceptorRegistration r1, InterceptorRegistration r2) {
        if (r1.order < r2.order) {
          return -1;
        } else if (r1.order > r2.order) {
          return 1;
        } else {
          return 0;
        }
      }
    };

    registrations.sort(comparator);
  }

  public List<HandlerInterceptor> resolveChain(String requestURI) {
    final String path = RequestPathUtils.normalizeRequestPath(requestURI, contextPath);

    List<HandlerInterceptor> result = new ArrayList<>();

    for (InterceptorRegistration reg : registrations) {
      boolean includeMatched = false;
      boolean excludeMatched = false;

      for (String inc : reg.includes) {
        if (matchesAny(Collections.singletonList(inc), path)) {
          includeMatched = true;
          break;
        }
      }

      if (!includeMatched) {
        continue;
      }

      for (String exc : reg.excludes) {
        if (matchesAny(Collections.singletonList(exc), path)) {
          excludeMatched = true;
          break;
        }
      }

      if (excludeMatched) {
        continue;
      }

      result.add(reg.interceptor);
    }
    return result;
  }

  private static boolean matchesAny(List<String> patterns, String path) {
    for (String raw : patterns) {
      if (raw == null || raw.isBlank())
        continue;
      final String pat = RequestPathUtils.ensureLeadingSlashForPathPattern(raw.trim());
      if (RequestPathUtils.matches(pat, path))
        return true;
    }
    return false;
  }

}
