package haru.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import haru.annotation.web.Filter;

public class FilterRegistry {
  private static final List<FilterEntry> filters = new ArrayList<>();

  public static void register(Class<?> clazz) {
    if (clazz.isAnnotationPresent(Filter.class)) {
      Filter annotation = clazz.getAnnotation(Filter.class);
      try {
        haru.servlet.filter.MiniFilter filter = (haru.servlet.filter.MiniFilter) clazz.getDeclaredConstructor().newInstance();
        filters.add(new FilterEntry(annotation.order(), annotation.urlPatterns(), filter));
        filters.sort(Comparator.comparingInt(FilterEntry::order));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static List<haru.servlet.filter.MiniFilter> getFiltersFor(String requestURI) {
    List<haru.servlet.filter.MiniFilter> matched = new ArrayList<>();
    for (FilterEntry entry : filters) {
      for (String pattern : entry.urlPatterns) {
//        if (matches(pattern, requestURI)) {
        if(matchesAny(pattern, requestURI, "haru")) {
          matched.add(entry.filter);
        }
      }
    }
    return Collections.unmodifiableList(matched);
  }

//  private static boolean matches(String pattern, String uri) {
//    if (pattern.equals("/*"))
//      return true;
//    if (pattern.endsWith("/*")) {
//      String str = pattern.substring(0, pattern.length() - 2);
//      return uri.startsWith(pattern.substring(0, pattern.length() - 2));
//    }
//    return uri.equals(pattern);
//  }

  private record FilterEntry(int order, String[] urlPatterns, haru.servlet.filter.MiniFilter filter) {
  }

  public static boolean matchesAny(String urlPatterns, String requestURI, String contextPath) {
    if (urlPatterns == null || urlPatterns.isEmpty())
      return false;
    String path = normalizePath(requestURI, contextPath);

    for (String raw : urlPatterns.split("[,\\s]+")) {
      if (raw.isEmpty())
        continue;
      String pattern = ensureLeadingSlashForPathPattern(raw.trim());
      if (matches(pattern, path))
        return true;
    }
    return false;
  }

  private static String normalizePath(String requestURI, String contextPath) {
    String uri = requestURI;
    int q = uri.indexOf('?');
    if (q >= 0)
      uri = uri.substring(0, q);

    if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
      uri = uri.substring(contextPath.length());
    }

    if (uri.isEmpty())
      uri = "/";

    uri = uri.replaceAll("/{2,}", "/");
    return uri;
  }

  private static String ensureLeadingSlashForPathPattern(String p) {
    if (p.startsWith("*."))
      return p;
    if (!p.startsWith("/"))
      return "/" + p;
    return p;
  }

  private static boolean matches(String pattern, String path) {
    if ("/*".equals(pattern))
      return true;

    if (!pattern.contains("*")) {
      return path.equals(pattern);
    }

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