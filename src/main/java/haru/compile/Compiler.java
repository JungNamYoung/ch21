package haru.compile;

public interface Compiler {
  public void compileJavaFile(String filePath, String outputDir, String classPath);
}
