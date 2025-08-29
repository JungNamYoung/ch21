package haru.compile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
  private final Map<String, byte[]> compiledClasses = new ConcurrentHashMap<>();

  protected MemoryJavaFileManager(StandardJavaFileManager fileManager) {
    super(fileManager);
  }

  @Override
  public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
    return new MemoryByteCodeFile(className, compiledClasses);
  }

  public Map<String, byte[]> getCompiledClasses() {
    return compiledClasses;
  }
}
