/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) [2018년] [SamuelSky]
 */

package haru.kitten;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.jasper.JspC;
import org.apache.jasper.TrimSpacesOption;

import haru.config.MiniServletConfig;
import haru.define.Define;
import haru.logger.LoggerManager;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MiniRequestDispatcher implements RequestDispatcher {
  private String webAppRoot;
  private String relativePath;
  private String jspPath;
  private MiniServletContext miniServletContext;
  static Logger logger = LoggerManager.getLogger(MiniRequestDispatcher.class.getSimpleName());

  public MiniRequestDispatcher(String webAppRoot, String relativePath) {
    this.webAppRoot = webAppRoot;
    this.relativePath = relativePath;

    miniServletContext = MiniServletContainer.getMiniWebApplicationContext();
  }

  public void compileAndExecute(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, Map<String, Object> param) throws ServletException, IOException {

    String realJspPath = webAppRoot + relativePath;
    Path jspFilePath = Paths.get(realJspPath);

    logger.info("realJspPath : " + realJspPath);
    logger.info("webAppRoot : " + webAppRoot);
    
    File outputDir = new File(webAppRoot + "/output/compiledJspServlets");
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }

    String jspFileName = new File(jspFilePath.toString()).getName();
    String servletClassName = jspFileName.replace(Define.EXT_JSP, "_jsp");

    String packagePath = "org.apache.jsp";

    compileJspToServlet(jspFilePath.toString(), outputDir.getAbsolutePath(), packagePath);

    Path javaFile = Paths.get(outputDir.getAbsolutePath() + "/" + packagePath.replace(".", "/") + "/" + servletClassName + ".java");
    compileJavaFile(javaFile);

    String classPath = webAppRoot + "/output/compiledJspServlets";

    executeServlet(classPath, miniHttpServletRequest, miniHttpServletResponse, packagePath + "." + servletClassName, param);

  }

  private void executeServlet(String classPath, MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, String servletClassName, Map<String, Object> param) throws ServletException, IOException {
    try {
      File file = new File(classPath);
      URI uri = file.toURI();
      URL url = uri.toURL();

      ClassLoader classLoader = new URLClassLoader(new URL[] { url }, ClassLoader.getSystemClassLoader());
      Class<?> servletClass = classLoader.loadClass(servletClassName);
      HttpServlet jspServletInstance = (HttpServlet) servletClass.getDeclaredConstructor().newInstance();

      MiniHttpSession miniHttpSession = new MiniHttpSession(miniServletContext);

      MiniHttServletRequestWrapper miniHttpServletRequestWrapper = new MiniHttServletRequestWrapper(miniHttpServletRequest, miniServletContext, miniHttpSession);

      MiniServletConfig miniServletConfig = new MiniServletConfig(miniServletContext);

      for (Entry<String, Object> entry : param.entrySet()) {
        miniHttpServletRequestWrapper.setAttribute(entry.getKey(), entry.getValue());
      }

      jspServletInstance.init(miniServletConfig);

      Method jspServiceMethod = servletClass.getDeclaredMethod("_jspService", HttpServletRequest.class, HttpServletResponse.class);
      jspServiceMethod.setAccessible(true);

      jspServiceMethod.invoke(jspServletInstance, miniHttpServletRequestWrapper, miniHttpServletResponse);

    } catch (Exception e) {
      throw new ServletException("Error executing compiled JSP servlet.", e);
    }
  }
//
//  private void convertJspToServlet(Path jspPath, Path servletJavaPath, String className) throws IOException {
//    List<String> jspLines = Files.readAllLines(jspPath);
//    StringBuilder servletCode = new StringBuilder();
//
//    servletCode.append("import java.io.*;\n");
//    servletCode.append("import javax.servlet.*;\n");
//    servletCode.append("import javax.servlet.http.*;\n");
//    servletCode.append("public class ").append(className).append(" extends HttpServlet { \n");
//    servletCode.append(" protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {\n");
//
//    String contentType = Define.TEXT_HTML;
//
//    StringBuilder bodyContent = new StringBuilder();
//
//    for (String line : jspLines) {
//      line = line.trim();
//
//      if (line.startsWith("<%@") && line.endsWith("%>")) {
//        if (line.contains("contentType=")) {
//          contentType = extractAttributeValue(line, "contentType");
//        }
//      } else if (line.startsWith("<%=") && line.endsWith("%>")) {
//        String expression = line.substring(3, line.length() - 2).trim();
//        bodyContent.append("  out.println(").append(expression).append(");\n");
//      } else if (line.startsWith("<%") && line.endsWith("%>")) {
//        String scriptlet = line.substring(2, line.length() - 2).trim();
//        bodyContent.append("  ").append(scriptlet).append("\n");
//      } else {
//        bodyContent.append("  out.println(\"").append(line.replace("\"", "\\\"")).append("\");\n");
//      }
//    }
//
//    servletCode.append("  resp.setContentType(\"").append(contentType).append("\");\n");
//    servletCode.append("  PrintWriter out = resp.getWriter(); \n");
//
//    servletCode.append(bodyContent.toString());
//
//    servletCode.append(" }\n");
//    servletCode.append("}\n");
//
//    Files.write(servletJavaPath, servletCode.toString().getBytes());
//
//    logger.info(servletCode.toString());
//  }

  private String extractAttributeValue(String directive, String attributeName) {
    int startIndex = directive.indexOf(attributeName + "=\"");
    if (startIndex == -1) {
      return "";
    }

    startIndex += attributeName.length() + 2;
    int endIndex = directive.indexOf("\"", startIndex);
    return directive.substring(startIndex, endIndex);
  }

  private void compileJavaFile(Path javaFilePath) {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    if (compiler == null) {
      throw new RuntimeException("No Java compiler available. Ensure you are running with a JDK.");
    }

    int result = compiler.run(null, null, null, javaFilePath.toString());

    if (result != 0) {
      throw new RuntimeException("Compilation failed  for : " + javaFilePath);
    }
  }

  @Override
  public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
    String realPath = webAppRoot + relativePath;
    File jspFile = new File(realPath);
    if (!jspFile.exists()) {
      throw new ServletException("jsp 파일을 찾을 수 없습니다. " + realPath);
    }

    FileInputStream fis = new FileInputStream(jspFile);

    try {
      byte[] buffer = new byte[1024];
      int len;
      ServletOutputStream out = response.getOutputStream();
      while ((len = fis.read(buffer)) != -1) {
        out.write(buffer, 0, len);
      }
    } finally {
      fis.close();
    }
  }

  public void write(ServletRequest request, ServletResponse response) throws ServletException, IOException {
    String realPath = webAppRoot + relativePath;

    Path real = Paths.get(realPath);

    List<String> lines = Files.readAllLines(real);

    for (String line : lines) {
      response.getWriter().write(line + Define.ENTER_EX);
    }
  }

  @Override
  public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
    throw new UnsupportedOperationException("include는 지원하지 않습니다.");
  }

  private void compileJspToServlet(String jspFilePath, String outputDir, String packagePath) {

      logger.info("jspFilePath : " + jspFilePath);
      logger.info("outputDir : " + outputDir);
      logger.info("packagePath : " + packagePath);
      
    try {
      File file = new File(jspFilePath);
      JspC jspCompiler = new JspC();
      jspCompiler.setUriroot(file.getParent());
      jspCompiler.setJspFiles(file.getName());
      jspCompiler.setOutputDir(outputDir);
      jspCompiler.setPackage(packagePath);
      jspCompiler.setFailOnError(true);
      jspCompiler.setTrimSpaces(TrimSpacesOption.TRUE);
      jspCompiler.execute();
      logger.info("[변환] JSP to Servlet - " + file.getName());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
