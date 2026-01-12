package haru.core.context;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import haru.constants.Define;
import haru.logging.MiniLogger;

public class MiniAnnotationScanner {

  private final String basePackageName;   // 예 : "app.web"
  private final String baseResourcePath;  // 예 : "app/web"
  private final ClassLoader classLoader;
  private static final Logger logger = MiniLogger.getLogger(MiniAnnotationScanner.class.getSimpleName());

  private final Map<Class<? extends Annotation>, Set<Class<?>>> cache = new ConcurrentHashMap<>();

  public MiniAnnotationScanner(String basePackage) {
    if (basePackage == null || basePackage.isBlank()) {
      throw new IllegalArgumentException("basePackage must not be null/blank");
    }
    this.basePackageName = basePackage;
    this.baseResourcePath = basePackage.replace('.', '/');
    this.classLoader = Thread.currentThread().getContextClassLoader();
  }

  public Set<Class<?>> findTypesAnnotatedWith(Class<? extends Annotation> annotation) {

    Set<Class<?>> cached = cache.get(annotation);
    if (cached != null)
      return cached;

    Set<Class<?>> annotated = new HashSet<>();

    try {
      Set<String> classNames = scanAllClassesInPackage();

      for (String className : classNames) {

        // 클래스 로딩 시도
        Class<?> clazz = loadClassSafelyPlain(className);

        // 로딩 실패 시 건너뜁니다
        if (clazz == null) {
          continue;
        }

        // 어노테이션 존재 + 구체 클래스인지 확인합니다
        if (clazz.isAnnotationPresent(annotation) && isConcreteClass(clazz)) {
          annotated.add(clazz);
        }
      }

    } catch (IOException e) {
      logger.warning(() -> "I/O error while scanning package: " + e.getMessage());
    }

    Set<Class<?>> unmodifiable = Collections.unmodifiableSet(annotated);
    cache.put(annotation, unmodifiable);
    return unmodifiable;
  }

  private Class<?> loadClassSafelyPlain(String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      logger.fine(() -> "Class not found: " + className);
      return null;
    } catch (LinkageError e) {
      logger.warning(() -> "Failed to load class: " + className);
      return null;
    }
  }

  private Set<String> scanAllClassesInPackage() throws IOException {
    Set<String> result = new HashSet<>();
    Enumeration<URL> resources = classLoader.getResources(baseResourcePath);

    while (resources.hasMoreElements()) {
      URL url = resources.nextElement();
      String protocol = url.getProtocol();

      if (Define.FILE.equalsIgnoreCase(protocol)) {
        result.addAll(scanFileProtocolEasy(url));
      } else if (Define.JAR.equalsIgnoreCase(protocol)) {
        result.addAll(scanJarProtocol(url));
      } else {
        logger.warning(() -> "Unsupported protocol: " + protocol + " (" + url + ")");
      }
    }
    return result;
  }

  private Set<String> scanFileProtocolEasy(URL resourceUrl) {

    Set<String> classNames = new HashSet<>();

    try {
      Path root = Paths.get(resourceUrl.toURI());

      if (!Files.isDirectory(root)) {
        logger.warning(() -> "Not a directory: " + root);
        return classNames;
      }

      scanDirectoryRecursively(root, root, classNames);
    } catch (Exception e) {
      logger.warning(() -> "Scan failed: " + e.getMessage());
    }
    return classNames;
  }

  private void scanDirectoryRecursively(Path currentDir, Path rootDir, Set<String> classNames) throws IOException {

    // 현재 디렉토리 안의 모든 파일/폴더 목록을 가져옵니다
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDir)) {

      for (Path path : stream) {
        // 폴더라면 → 다시 그 안으로 들어갑니다
        if (Files.isDirectory(path)) {
          scanDirectoryRecursively(path, rootDir, classNames);
          continue;
        }

        // 파일이 아니면 무시합니다
        if (!Files.isRegularFile(path)) {
          continue;
        }

        // .class 파일만 대상입니다
        if (!path.toString().endsWith(".class")) {
          continue;
        }

        // 내부 클래스($ 포함)는 제외합니다
        if (path.getFileName().toString().contains("$")) {
          continue;
        }

        // 파일 경로 → 클래스 이름 변환
        String className = toClassNameFromFile(path, rootDir, basePackageName);
        classNames.add(className);
      }
    }
  }

  private Set<String> scanJarProtocol(URL jarUrl) {
    Set<String> classNames = new HashSet<>();
    try {
      JarURLConnection conn = (JarURLConnection) jarUrl.openConnection();
      try (JarFile jarFile = conn.getJarFile()) {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          String name = entry.getName();
          if (entry.isDirectory())
            continue;
          if (!name.startsWith(baseResourcePath + "/"))
            continue;
          if (!name.endsWith(".class"))
            continue;
          if (name.contains("$"))
            continue;

          classNames.add(toClassNameFromJarEntry(name));
        }
      }
    } catch (IOException ioe) {
      logger.warning(() -> "Failed to read JAR: " + ioe.getMessage());
    }
    return classNames;
  }

  private String toClassNameFromFile(Path classFile, Path root, String basePackage) {
    Path relative = root.relativize(classFile);
    String withoutExt = relative.toString().replaceAll("\\.class$", "");
    String dotted = withoutExt.replace(File.separatorChar, '.');
    return basePackage + (dotted.isEmpty() ? "" : "." + dotted);
  }

  private String toClassNameFromJarEntry(String entryName) {
    String withoutExt = entryName.substring(0, entryName.length() - ".class".length());
    return withoutExt.replace('/', '.');
  }

  private Optional<Class<?>> loadClassSafely(String className) {
    try {
      return Optional.of(classLoader.loadClass(className));
    } catch (ClassNotFoundException | LinkageError e) {
      logger.fine(() -> "Skip loading class: " + className + " (" + e.getClass().getSimpleName() + ")");
      return Optional.empty();
    }
  }

  private boolean isConcreteClass(Class<?> clazz) {
    int m = clazz.getModifiers();
    return !clazz.isInterface() && !Modifier.isAbstract(m) && !clazz.isAnnotation();
  }
}
