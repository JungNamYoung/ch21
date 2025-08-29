package haru.kitten;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class MiniAnnotationScanner {
  private final String basePackage;
  private final ClassLoader classLoader;

  public MiniAnnotationScanner(String basePackage) {
    this.basePackage = basePackage.replace('.', '/');
    this.classLoader = Thread.currentThread().getContextClassLoader();
  }

  public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
    Set<Class<?>> annotatedClasses = new HashSet<>();
    try {
      Set<Class<?>> allClasses = getAllClassesInPackage();
      for (Class<?> clazz : allClasses) {
        if (clazz.isAnnotationPresent(annotation)) {
          annotatedClasses.add(clazz);
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return annotatedClasses;
  }

  private Set<Class<?>> getAllClassesInPackage() throws IOException, ClassNotFoundException {
    Set<Class<?>> classes = new HashSet<>();
    Enumeration<URL> resources = classLoader.getResources(basePackage);

    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();
      File directory = new File(resource.getFile());

      if (directory.exists() && directory.isDirectory()) {
        classes.addAll(findClasses(directory, basePackage.replace('/', '.')));
      }
      else if (resource.getFile().contains(".jar")) {
        throw new UnsupportedOperationException("jar 파일은 현재 지원하지 않습니다.");
      }
    }
    return classes;
  }

  private Set<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
    Set<Class<?>> classes = new HashSet<>();
    File[] files = directory.listFiles();
    if (files == null)
      return classes;

    for (File file : files) {
      if (file.isDirectory()) {
        classes.addAll(findClasses(file, packageName + "." + file.getName()));
      } else if (file.getName().endsWith(".class")) {
        String className = packageName + '.' + file.getName().replace(".class", "");
        Class<?> clazz = Class.forName(className);
        if (!Modifier.isAbstract(clazz.getModifiers())) {
          classes.add(clazz);
        }
      }
    }
    return classes;
  }
}

