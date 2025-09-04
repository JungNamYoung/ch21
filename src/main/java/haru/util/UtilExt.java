package haru.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.net.URISyntaxException;
import java.net.URI;

import haru.define.Define;

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
		String appHome = System.getProperty("app.home", Paths.get(".").toAbsolutePath().normalize().toString());
		Path confPath = Paths.get(appHome, "conf").resolve(relative.replace("\\", "/"));
		if (Files.exists(confPath))
			return confPath;

		String cpPath = relative.replace("\\", "/");
		if (cpPath.startsWith("/"))
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
		
		if (resourcePath.startsWith("/"))
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

	public static String loadTextSmart(String resourcePath) throws IOException {
		
		URI uri = getResourceUri(resourcePath);
		
		System.out.println("resource uri : " + uri);
		
		if ("file".equals(uri.getScheme())) {
			return Files.readString(Paths.get(uri), StandardCharsets.UTF_8);
		} else if ("jar".equals(uri.getScheme())) {
			try (InputStream in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath)) {
				if (in == null)
					throw new FileNotFoundException(resourcePath);
				return new String(in.readAllBytes(), StandardCharsets.UTF_8);
			}
		} else {
			try (InputStream in = uri.toURL().openStream()) {
				return new String(in.readAllBytes(), StandardCharsets.UTF_8);
			}
		}
	}

  public static int safeInt(String str, int defaultValue) {
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }

	public static String resolveWebRoot(TokenEx tokenEx) {
		String webappProp = System.getProperty("webapp.dir");
		if (webappProp != null && !webappProp.isBlank()) {
			Path p = Paths.get(webappProp).toAbsolutePath().normalize();
			if (Files.isDirectory(p))
				return p.toString();
		}

		try {
			Object v = tokenEx != null ? tokenEx.get("ROOT_PATH") : null;
			if (v != null) {
				String cfg = String.valueOf(v).trim();
				if (!cfg.isEmpty()) {
					Path p = Paths.get(cfg);
					if (!p.isAbsolute()) {
						String appHome = System.getProperty("app.home",
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

		String appHome = System.getProperty("app.home", Paths.get("").toAbsolutePath().normalize().toString());
		Path prod = Paths.get(appHome, "webapp").toAbsolutePath().normalize();
		if (Files.isDirectory(prod))
			return prod.toString();

		Path dev = Paths.get(appHome, "src", "main", "webapp").toAbsolutePath().normalize();
		return dev.toString();
	}
}
