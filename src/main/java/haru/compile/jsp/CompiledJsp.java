package haru.compile.jsp;

public record CompiledJsp(String outputDir, // compiledJspServlets 절대경로
    String fullyQualifiedClass, // org.apache.jsp.xxx.My_jsp
    long compiledAtMillis// 로그/캐시/디버깅용
) {
}