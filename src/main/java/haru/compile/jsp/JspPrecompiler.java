package haru.compile.jsp;

import java.io.File;

import org.apache.jasper.JspC;
import org.apache.jasper.TrimSpacesOption;

public class JspPrecompiler {

  public void compile(String jspFilePath, String outputDir, String packagePath, String classpath) {

    System.out.println("jspFilePath = " + jspFilePath);
    System.out.println("outputDir = " + outputDir);
    System.out.println("packagePath = " + packagePath);

    File out = new File(outputDir);
    if (!out.exists())
      out.mkdirs();

    try {
      File file = new File(jspFilePath);
      JspC jspCompiler = new JspC();

      System.out.println("parent = " + file.getParent());

      jspCompiler.setUriroot(file.getParent());
      jspCompiler.setJspFiles(file.getName());
      jspCompiler.setOutputDir(outputDir);
      jspCompiler.setPackage(packagePath);
      jspCompiler.setClassPath(classpath);
      jspCompiler.setFailOnError(true);
      jspCompiler.setTrimSpaces(TrimSpacesOption.TRUE);

      jspCompiler.execute();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}