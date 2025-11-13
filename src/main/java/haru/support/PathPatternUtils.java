package haru.support;

public final class PathPatternUtils {

  private PathPatternUtils() {
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