package haru.core.context;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
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
import java.util.stream.Stream;

import haru.constants.Define;
import haru.logging.LoggerManager;

public class MiniAnnotationScanner {

  private final String basePackageName; // 예 : "app.web"
  private final String baseResourcePath; // 예 : "app/web"
  private final ClassLoader classLoader;
  private static final Logger logger = LoggerManager.getLogger(MiniAnnotationScanner.class.getSimpleName());

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
        loadClassSafely(className).ifPresent(clazz -> {
          if (clazz.isAnnotationPresent(annotation) && isConcreteClass(clazz)) {
            annotated.add(clazz);
          }
        });
      }
    } catch (IOException e) {
      logger.warning(() -> "I/O error while scanning package: " + e.getMessage());
    }

    Set<Class<?>> unmodifiable = Collections.unmodifiableSet(annotated);
    cache.put(annotation, unmodifiable);
    return unmodifiable;
  }

  private Set<String> scanAllClassesInPackage() throws IOException {
    Set<String> result = new HashSet<>();
    Enumeration<URL> resources = classLoader.getResources(baseResourcePath);

    while (resources.hasMoreElements()) {
      URL url = resources.nextElement();
      String protocol = url.getProtocol();

      if (Define.FILE.equalsIgnoreCase(protocol)) {
        result.addAll(scanFileProtocol(url));
      } else if (Define.JAR.equalsIgnoreCase(protocol)) {
        result.addAll(scanJarProtocol(url));
      } else {
        logger.warning(() -> "Unsupported protocol: " + protocol + " (" + url + ")");
      }
    }
    return result;
  }

  private Set<String> scanFileProtocol(URL resourceUrl) {
    Set<String> classNames = new HashSet<>();
    try {
      Path root = Paths.get(resourceUrl.toURI());
      if (!Files.isDirectory(root)) {
        logger.warning(() -> "Not a directory: " + root);
        return classNames;
      }

      try (Stream<Path> paths = Files.walk(root)) {
        paths.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".class")).filter(p -> !p.getFileName().toString().contains("$")).map(p -> toClassNameFromFile(p, root, basePackageName)).forEach(classNames::add);
      }
    } catch (URISyntaxException e) {
      logger.warning(() -> "Invalid URI for resource: " + e.getMessage());
    } catch (IOException ioe) {
      logger.warning(() -> "Failed to walk file tree: " + ioe.getMessage());
    }
    return classNames;
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
