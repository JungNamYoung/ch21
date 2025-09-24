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

package haru.servlet.view;

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

import javax.lang.model.SourceVersion;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.jasper.JspC;
import org.apache.jasper.TrimSpacesOption;

import haru.constants.Define;
import haru.core.bootstrap.MiniServletContainer;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.http.MiniHttpSession;
import haru.http.wrapper.MiniHttServletRequestWrapper;
import haru.logging.LoggerManager;
import haru.servlet.MiniServletContext;
import haru.servlet.config.MiniServletConfig;
import haru.support.IdentifierUtil;
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
  private String webInf;
  private MiniServletContext miniServletContext;
  static Logger logger = LoggerManager.getLogger(MiniRequestDispatcher.class.getSimpleName());

  public MiniRequestDispatcher(String webAppRoot, String relativePath) {
    this.webAppRoot = webAppRoot;
    this.webInf = webAppRoot + Define.WEB_INF;
    this.relativePath = relativePath;

    miniServletContext = MiniServletContainer.getMiniWebApplicationContext();
  }

  public void compileAndExecute(MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, Map<String, Object> param) throws ServletException, IOException {

    String jspPath = webAppRoot + relativePath;
    Path jspFilePath = Paths.get(jspPath);

    logger.info("jspPath : " + jspPath);
//    logger.info("webAppRoot : " + webAppRoot);

    File outputDir = new File(webInf + "/output/compiledJspServlets");
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }

    String jspRelative = relativePath.startsWith(Define.SLASH) ? relativePath.substring(1) : relativePath;
    Path relPath = Paths.get(jspRelative).normalize();
    String jspFileName = relPath.getFileName().toString();
    String servletClassName = IdentifierUtil.makeJavaIdentifier(jspFileName.replace(Define.EXT_JSP, "")) + "_jsp";

    String packagePath = "org.apache.jsp";
    Path parent = relPath.getParent();
    if (parent != null) {
      for (Path segment : parent) {
        packagePath += "." + IdentifierUtil.makeJavaIdentifier(segment.toString());
      }
    }

    compileJspToServlet(jspFilePath.toString(), outputDir.getAbsolutePath(), packagePath);

    Path javaFile = Paths.get(outputDir.getAbsolutePath(), packagePath.replace('.', '/'), servletClassName + ".java");
    compileJavaFile(javaFile);

    String classPath = webInf + "/output/compiledJspServlets";

    executeServlet(classPath, miniHttpServletRequest, miniHttpServletResponse, packagePath + "." + servletClassName, param);
  }

  private void executeServlet(String classPath, MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, String servletClassName, Map<String, Object> param) throws ServletException, IOException {
    try {
      File file = new File(classPath);
      URI uri = file.toURI();
      URL url = uri.toURL();

      ClassLoader classLoader = new URLClassLoader(new URL[] { url }, ClassLoader.getSystemClassLoader());

      String actualClassName = IdentifierUtil.makeQualifiedJavaIdentifier(servletClassName);

      Class<?> servletClass = classLoader.loadClass(actualClassName);
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

  private String extractAttributeValue(String directive, String attributeName) {
    int startIndex = directive.indexOf(attributeName + "=\"");
    if (startIndex == -1) {
      return "";
    }

    startIndex += attributeName.length() + 2;
    int endIndex = directive.indexOf("\"", startIndex);
    return directive.substring(startIndex, endIndex);
  }

  private String makeJavaIdentifier(String input) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      boolean valid;
      if (c < 0x80) {
        valid = i == 0 ? Character.isJavaIdentifierStart(c) : Character.isJavaIdentifierPart(c);
      } else {
        valid = false;
      }
      if (valid) {
        sb.append(c);
      } else {
        sb.append('_');
        String hex = Integer.toHexString(c);
        while (hex.length() < 4) {
          hex = '0' + hex;
        }
        sb.append(hex);
      }
    }
    String result = sb.toString();
    if (SourceVersion.isKeyword(result)) {
      result = '_' + result;
    }
    return result;
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
