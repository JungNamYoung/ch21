package haru.support;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JasperGeneratedNameResolver {
  /**
   * 특정 JSP(단 1개)에 대응되는 Jasper 생성 서블릿 .java 파일을 찾아 "클래스명(확장자 제외)"을 반환합니다.
   *
   * @param jspFilePath 예: "C:/.../webapp/3_user.jsp"
   * @param outputDir   JspC setOutputDir()로 준 디렉터리
   * @param packagePath JspC setPackage()로 준 패키지 (예: "haru.jsp")
   */
  public static String resolveServletClassName(String jspFilePath, String outputDir, String packagePath) {
    Path jspPath = Paths.get(jspFilePath).toAbsolutePath().normalize();
    String jspFileName = jspPath.getFileName().toString();

    Path pkgDir = Paths.get(outputDir).toAbsolutePath().normalize().resolve(packagePath.replace('.', File.separatorChar));

    if (!Files.isDirectory(pkgDir)) {
      throw new IllegalStateException("JSP 서블릿 출력 패키지 디렉터리가 존재하지 않습니다: " + pkgDir);
    }
    
//    File outputDirectory = new File(pkgDir.toString());
//    
//    if(outputDirectory.exists() == false) {
//      outputDirectory.mkdirs();
//    }

    List<Path> candidates = listCandidates(pkgDir);

    // 1순위: _jspx_jspFile = "/3_user.jsp" 또는 "3_user.jsp"
    List<Path> p1 = matchByJspxJspFile(candidates, jspFileName);

    if (p1.size() == 1) {
      return stripJavaExt(p1.get(0).getFileName().toString());
    }
    if (p1.size() > 1) {
      throw new IllegalStateException("동일 JSP에 대해 _jspx_jspFile매칭 후보가 2개 이상입니다:" + p1.stream().map(p -> p.getFileName().toString()).collect(Collectors.joining(",")));
    }

    // 2순위: dependants 등에서 파일명 문자열 포함으로 좁히기
    List<Path> p2 = matchByAnyStringHit(candidates, jspFileName);

    if (p2.size() == 1) {
      return stripJavaExt(p2.get(0).getFileName().toString());
    }
    if (p2.size() > 1) {
      throw new IllegalStateException("JSP 파일명 문자열 포함 후보가 2개 이상입니다(매칭이 모호합니다):" + p2.stream().map(p -> p.getFileName().toString()).collect(Collectors.joining(",")));
    }

    // 3) 마지막 안전장치: 후보가 딱 1개면 그걸 사용
    if (candidates.size() == 1) {
      return stripJavaExt(candidates.get(0).getFileName().toString());
    }

    throw new IllegalStateException("생성된 JSP 서블릿 .java파일을 특정하지 못했습니다. 후보: " + candidates.stream().map(p -> p.getFileName().toString()).collect(Collectors.joining(",")));
  }

  private static List<Path> listCandidates(Path pkgDir) {
    try (Stream<Path> s = Files.list(pkgDir)) {
      return s.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith("_jsp.java")).collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("JSP 서블릿 후보 파일 탐색 실패: " + pkgDir, e);
    }
  }

  private static List<Path> matchByJspxJspFile(List<Path> candidates, String jspFileName) {
    // Jasper 생성 코드에 흔히 등장하는 형태:
    // private static final String _jspx_jspFile = "/3_user.jsp";
    String q1 = "_jspx_jspFile";
    String v1 = "\"/" + jspFileName + "\"";
    String v2 = "\"" + jspFileName + "\"";

    List<Path> matched = new ArrayList<>();
    for (Path p : candidates) {
      String text = readSmallText(p);
      if (text.contains(q1) && (text.contains(v1) || text.contains(v2))) {
        matched.add(p);
      }
    }
    return matched;
  }

  private static List<Path> matchByAnyStringHit(List<Path> candidates, String jspFileName) {
    // Jasper 버전/옵션에 따라 1순위가 없을 수도 있어, 최소한 파일명 문자열이 들어있는지로 좁힙니다.
    String needle1 = "\"" + jspFileName + "\"";
    String needle2 = "/" + jspFileName; // 따옴표 없이 포함될 수도 있어 보조로 체크

    List<Path> matched = new ArrayList<>();
    for (Path p : candidates) {
      String text = readSmallText(p);
      if (text.contains(needle1) || text.contains(needle2)) {
        matched.add(p);
      }
    }
    return matched;
  }

  private static String readSmallText(Path p) {
    try {
      // Jasper 생성 서블릿 .java는 보통 크지 않아서 통째로 읽어도 부담이 적습니다.
      // (필요하면 Files.newBufferedReader로 라인 단위 스캔으로 바꿀 수 있습니다.)
      return Files.readString(p, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("파일 읽기 실패: " + p, e);
    }
  }

  private static String stripJavaExt(String fileName) {
    if (fileName.endsWith(".java")) {
      return fileName.substring(0, fileName.length() - ".java".length());
    }
    return fileName;
  }

  private JasperGeneratedNameResolver() {
  }
}