package haru.compile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import javax.tools.SimpleJavaFileObject;

class MemoryByteCodeFile extends SimpleJavaFileObject {
  private final String className;
  private final Map<String, byte[]> compiledClasses;

  protected MemoryByteCodeFile(String className, Map<String, byte[]> compiledClasses) {
    super(URI.create("bytes:///" + className), Kind.CLASS);
    this.className = className;
    this.compiledClasses = compiledClasses;
  }

  @Override
  public OutputStream openOutputStream() {
    return new ByteArrayOutputStream() {
      @Override
      public void close() throws IOException {
        compiledClasses.put(className, toByteArray());
        super.close();
      }
    };
  }
}
