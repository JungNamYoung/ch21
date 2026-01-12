package haru.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import haru.annotation.web.Filter;
import haru.core.bootstrap.MiniServletContainer;
import haru.servlet.filter.MiniFilter;
import haru.support.RequestPathUtils;

public class FilterRegistry {
  private static final List<FilterEntry> filters = new ArrayList<>();

  public static void register(Class<?> clazz) {
    if (clazz.isAnnotationPresent(Filter.class)) {
      Filter annotation = clazz.getAnnotation(Filter.class);
      try {
        MiniFilter filter = (MiniFilter) clazz.getDeclaredConstructor().newInstance();
        filters.add(new FilterEntry(annotation.order(), annotation.urlPatterns(), filter));
        filters.sort(Comparator.comparingInt(FilterEntry::order));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static List<MiniFilter> getFiltersFor(String requestURI) {
    List<MiniFilter> matched = new ArrayList<>();
    for (FilterEntry entry : filters) {
      for (String pattern : entry.urlPatterns) {
        if(matchesAny(pattern, requestURI, MiniServletContainer.getContextPath())) {
          matched.add(entry.filter);
        }
      }
    }
    return Collections.unmodifiableList(matched);
  }

  private record FilterEntry(int order, String[] urlPatterns, MiniFilter filter) {
  }

  public static boolean matchesAny(String urlPatterns, String requestURI, String contextPath) {
    if (urlPatterns == null || urlPatterns.isEmpty())
      return false;
    String path = RequestPathUtils.normalizeRequestPath(requestURI, contextPath);

    for (String raw : urlPatterns.split("[,\\s]+")) {
      if (raw.isEmpty())
        continue;
      String pattern = RequestPathUtils.ensureLeadingSlashForPathPattern(raw.trim());
      if (RequestPathUtils.matches(pattern, path))
        return true;
    }
    return false;
  }

}