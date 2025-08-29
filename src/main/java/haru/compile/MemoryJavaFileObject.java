package haru.compile;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

class MemoryJavaFileObject extends SimpleJavaFileObject {
  private final String code;

  protected MemoryJavaFileObject(String className, String code) {
    super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
    this.code = code;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
    return code;
  }
}
