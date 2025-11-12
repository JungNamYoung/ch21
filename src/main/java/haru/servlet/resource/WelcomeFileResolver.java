package haru.servlet.resource;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import haru.constants.Define;
import haru.constants.Haru;
import haru.core.bootstrap.MiniServletContainer;
import haru.logging.MiniLogger;
import haru.support.LineEx;
import haru.support.TokenEx;
import haru.support.UtilExt;

public final class WelcomeFileResolver {

  private static final Logger logger = MiniLogger.getLogger(WelcomeFileResolver.class.getSimpleName());
  private static final List<String> DEFAULT_WELCOME_FILES = List.of("index.html", "index.jsp", "index.htm", "default.html");
  private static final List<String> WELCOME_FILES = loadWelcomeFiles();

  private WelcomeFileResolver() {
  }

  private static List<String> loadWelcomeFiles() {
    try {
      String text = UtilExt.loadTextSmart(Haru.CONFIG_SERVLET);
      if (text == null) {
        text = Define.STR_BLANK;
      }
      
//    List<String> files = LineEx.toEffectiveLines(text).stream().map(String::trim).filter(line -> !line.isEmpty()).collect(Collectors.toList());
      TokenEx tokenEx = new TokenEx(Define.STR_BLANK, UtilExt.loadTextSmart(Haru.CONFIG_SERVLET));
      
      List<String> files = parseFiles(tokenEx.get("servlet.welcome.files"));
      if (files.isEmpty()) {
        return DEFAULT_WELCOME_FILES;
      }
      return List.copyOf(files);
    } catch (Exception ex) {
      logger.warning(() -> "Failed to load welcome file list: " + ex.getMessage());
      return DEFAULT_WELCOME_FILES;
    }
  }

  private static List<String> parseFiles(String files) {
    String raw = files.trim();

    if (raw.isEmpty())
      return null;

    return Arrays.stream(raw.split("[,;\\s]+")).map(String::trim).filter(s -> !s.isEmpty()).distinct().toList();
  }

  public static String resolve(String requestUrl) {
    if (requestUrl == null || requestUrl.isEmpty()) {
      return null;
    }

    String baseUrl = requestUrl.endsWith(Define.SLASH) ? requestUrl : requestUrl + Define.SLASH;

    File directory = new File(MiniServletContainer.getRealPath(baseUrl));
    if (!directory.exists() || !directory.isDirectory()) {
      return null;
    }

    for (String welcomeFile : WELCOME_FILES) {
      String candidateUrl = baseUrl + welcomeFile;
      File candidate = new File(MiniServletContainer.getRealPath(candidateUrl));
      if (candidate.isFile()) {
        return candidateUrl;
      }
    }

    logger.fine(() -> "No welcome file found for " + requestUrl);
    return null;
  }
}