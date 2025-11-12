package haru.servlet.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public final class ServletConfig {
  private static final List<String> DEFAULTS = List.of("index.html", "index.jsp", "index.htm", "default.html");

  public static List<String> loadWelcomeFiles(Path appconfigDir) {
    Properties p = new Properties();
    Path servletProps = appconfigDir.resolve("servlet.properties");
    if (Files.exists(servletProps)) {
      try (var in = Files.newInputStream(servletProps)) {
        p.load(in);
      } catch (IOException e) {
      }
    }
    String raw = p.getProperty("welcome.files", "").trim();
    List<String> fromProps = parseList(raw);

    if (fromProps.isEmpty()) {
      Path legacy = appconfigDir.resolve("welcome-files.txt");
      if (Files.exists(legacy)) {
        try {
          var lines = Files.readAllLines(legacy);
          fromProps = lines.stream().map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        } catch (IOException e) {
        }
      }
    }
    return fromProps.isEmpty() ? DEFAULTS : dedupPreserveOrder(fromProps);
  }

  private static List<String> parseList(String raw) {
    if (raw.isEmpty())
      return List.of();
    return Arrays.stream(raw.split("[,;\\s]+")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
  }

  private static List<String> dedupPreserveOrder(List<String> in) {
    LinkedHashSet<String> set = new LinkedHashSet<>(in);
    return new ArrayList<>(set);
  }
}