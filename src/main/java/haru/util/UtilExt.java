package haru.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.net.URISyntaxException;
import java.net.URI;

import haru.define.Define;
import haru.define.Haru;

public class UtilExt {

  // getClassPath("config/info.txt")
  public static String getClassPath(String path) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    URL resource = classLoader.getResource(path);

    System.out.println("path getClassPath : " + path);

    if (resource != null) {
      try {
        String path1 = resource.getPath();

        System.out.println("dir resource : " + path1);

        return path1;
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    } else {
      throw new RuntimeException("에러");
    }

    return null;
  }

  public static Path getConfFile(String relative) throws IOException {
    String appHome = System.getProperty(Haru.JVM_APP_HOME, Paths.get(".").toAbsolutePath().normalize().toString());
    Path confPath = Paths.get(appHome, "conf").resolve(relative.replace("\\", Define.SLASH));
    if (Files.exists(confPath))
      return confPath;

    String cpPath = relative.replace("\\", Define.SLASH);
    if (cpPath.startsWith(Define.SLASH))
      cpPath = cpPath.substring(1);
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try (InputStream in = cl.getResourceAsStream(cpPath)) {
      if (in == null)
        throw new FileNotFoundException("conf에도 없고 classpath에도 없음: " + relative);
      Files.createDirectories(confPath.getParent());
      Files.copy(in, confPath, StandardCopyOption.REPLACE_EXISTING);
    }
    return confPath;
  }

  public static URI getResourceUri(String resourcePath) {

    if (resourcePath.startsWith(Define.SLASH))
      resourcePath = resourcePath.substring(1);

    URL url = Thread.currentThread().getContextClassLoader().getResource(resourcePath);

    if (url == null)
      throw new RuntimeException("리소스를 찾을 수 없습니다: " + resourcePath);
    try {
      return url.toURI();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  // public static String loadTextSmart(String resourcePath) throws IOException {
  public static String loadTextSmart(String resourcePath) {

    URI uri = getResourceUri(resourcePath);

//    System.out.println("resource uri : " + uri);

    try {
      if ("file".equals(uri.getScheme())) {
        return Files.readString(Paths.get(uri), StandardCharsets.UTF_8);
      } else if ("jar".equals(uri.getScheme())) {
				try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
						resourcePath.startsWith(Define.SLASH) ? resourcePath.substring(1) : resourcePath)) {
          if (in == null)
            throw new FileNotFoundException(resourcePath);
          return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
      } else {
        try (InputStream in = uri.toURL().openStream()) {
          return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return "";
  }

  public static int safeInt(String str, int defaultValue) {
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }

  public static String resolveWebRoot(TokenEx tokenEx) {
    String webappProp = System.getProperty(Haru.JVM_WEB_APP_DIR);
    if (webappProp != null && !webappProp.isBlank()) {
      Path p = Paths.get(webappProp).toAbsolutePath().normalize();
      if (Files.isDirectory(p))
        return p.toString();
    }

    try {
      Object v = tokenEx != null ? tokenEx.get(Haru.ROOT_PATH) : null;
      if (v != null) {
        String cfg = String.valueOf(v).trim();
        if (!cfg.isEmpty()) {
          Path p = Paths.get(cfg);
          if (!p.isAbsolute()) {
						String appHome = System.getProperty(Haru.JVM_APP_HOME,
								Paths.get("").toAbsolutePath().normalize().toString());
            p = Paths.get(appHome).resolve(cfg);
          }
          p = p.toAbsolutePath().normalize();
          if (Files.isDirectory(p))
            return p.toString();
        }
      }
    } catch (Throwable ignore) {
    }

    String appHome = System.getProperty(Haru.JVM_APP_HOME, Paths.get("").toAbsolutePath().normalize().toString());
    Path prod = Paths.get(appHome, "webapp").toAbsolutePath().normalize();
    if (Files.isDirectory(prod))
      return prod.toString();

    Path dev = Paths.get(appHome, "src", "main", "webapp").toAbsolutePath().normalize();
    return dev.toString();
  }

	public static void closeWindow(Map<String, Object> result, String title) {
		try {
			String os = System.getProperty("os.name").toLowerCase();

			if (os.contains("win")) {

				String cmd = String.format("taskkill /f /fi \"WINDOWTITLE eq %s\"", title);
				// String cmd = "taskkill /f /fi \"WINDOWTITLE eq 양방향 수어*\"";
				// String cmd = "taskkill /f /fi \"WINDOWTITLE eq 양방향 수어 동시 통역 서비스 - chrome\"";
				Process process = Runtime.getRuntime().exec(cmd);
				process.waitFor();

				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "MS949"));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				reader.close();

				result.put("close", "true");
				result.put("message", "Windows에서 크롬이 종료되었습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("close", "false");
			result.put("message", "창 종료 중 오류가 발생했습니다.");
		}

//		model.addAttribute(Define.JSON, result);
	}
}
