package haru.mvc;

import java.util.ArrayList;
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
//        filters.sort(Comparator.comparingInt(FilterEntry::getOrder));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static List<haru.servlet.filter.MiniFilter> getFiltersFor(String requestURI) {
    List<haru.servlet.filter.MiniFilter> matched = new ArrayList<>();
    for (FilterEntry entry : filters) {
      for (String pattern : entry.urlPatterns) {
        if (matches(pattern, requestURI)) {
          matched.add(entry.filter);
        }
      }
    }
    return matched;
  }

  private static boolean matches(String pattern, String uri) {
    if (pattern.equals("/*"))
      return true;
    if (pattern.endsWith("/*"))
      return uri.startsWith(pattern.substring(0, pattern.length() - 2));
    return uri.equals(pattern);
  }

  private record FilterEntry(int order, String[] urlPatterns, haru.servlet.filter.MiniFilter filter) {
  }
}