package haru.compile;

import java.util.Map;

class MemoryClassLoader extends ClassLoader {
  private final Map<String, byte[]> compiledClasses;

  protected MemoryClassLoader(Map<String, byte[]> compiledClasses) {
    this.compiledClasses = compiledClasses;
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    byte[] bytecode = compiledClasses.get(name);
    if (bytecode == null) {
      throw new ClassNotFoundException(name);
    }
    return defineClass(name, bytecode, 0, bytecode.length);
  }
}
