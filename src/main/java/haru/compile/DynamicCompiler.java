package haru.compile;

import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import haru.constants.Define;

public class DynamicCompiler {
  public void make(String str) {
    String className = "HelloWorld";
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("public class HelloWorld {" + Define.ENTER_EX);
    stringBuilder.append("  public void greet() {" + Define.ENTER_EX);
    stringBuilder.append("    System.out.println(\"Hello, " + str + "\");" + Define.ENTER_EX);
    stringBuilder.append("  }" + Define.ENTER_EX);
    stringBuilder.append("}" + Define.ENTER_EX);
    String sourceCode = stringBuilder.toString();

    System.out.println(sourceCode);

    JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager standardJavaFileManager = javaCompiler.getStandardFileManager(null, null, null);
    MemoryJavaFileManager memoryJavaFileManager = new MemoryJavaFileManager(standardJavaFileManager);

    JavaFileObject javaFileObject = new MemoryJavaFileObject(className, sourceCode);
    List<JavaFileObject> compilationUnits = Arrays.asList(javaFileObject);

    JavaCompiler.CompilationTask compilationTask = javaCompiler.getTask(null, memoryJavaFileManager, null, null, null, compilationUnits);

    if (!compilationTask.call()) {
      throw new RuntimeException("Compilation failed");
    }

    MemoryClassLoader memoryClassLoader = new MemoryClassLoader(memoryJavaFileManager.getCompiledClasses());

    try {
      Class<?> compiledClass = memoryClassLoader.loadClass(className);

      Object instance = compiledClass.getDeclaredConstructor().newInstance();

      compiledClass.getMethod("greet").invoke(instance);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }
}
