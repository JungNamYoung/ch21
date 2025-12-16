package haru.compile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import haru.logging.MiniLogger;

public class DynamicCompiler implements Compiler {
  
  private static final Logger logger = MiniLogger.getLogger(DynamicCompiler.class.getSimpleName()); 
  
  public void compileJavaFile(String filePath, String outputDir, String classPath) {

    JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
    if (javaCompiler == null) {
      throw new IllegalStateException("시스템 Java 컴파일러를 사용할 수 없습니다. JDK로 실행 중인지 확인하세요.");
    }

    try {
      
      File sourceFile = new File(filePath);
      File outputDirectory = new File(outputDir);
      
      if (!outputDirectory.exists()) {
        outputDirectory.mkdirs();
      }

      StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
      // 클래스 출력 위치 설정 (-d)
      fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(outputDirectory));

      Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(sourceFile));
      // 컴파일 옵션에 classpath 명시
      List<String> options = Arrays.asList("-classpath", classPath);

      JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileManager, null, options, null, compilationUnits);

      boolean success = task.call();
      fileManager.close();

      if (!success) {
        throw new RuntimeException("컴파일에 실패했습니다.");
      }

      logger.info("소스 컴파일 성공 : " + outputDir + "에 .class이 저장됨");

    } catch (IOException e) {
      throw new RuntimeException("입출력 오류: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new RuntimeException("클래스 로딩 오류: " + e.getMessage(), e);
    }
  }
}