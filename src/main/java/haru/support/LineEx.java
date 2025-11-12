package haru.support;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class LineEx {

  public static ArrayList<String> toLines(String text) {
    return text.lines().collect(Collectors.toCollection(ArrayList::new));
  }

  public static ArrayList<String> toEffectiveLines(String text) {
		ArrayList<String> effectiveLines = new ArrayList<>();

		String[] lines = text.split("\\R");

		for (String line : lines) {
			String trimmedLine = line.trim();

			if (trimmedLine.isEmpty() || trimmedLine.startsWith("#") || trimmedLine.startsWith("//")) {
				continue;
			}

			String processedLine = trimmedLine.replaceFirst("\\s*=\\s*", "=");

			effectiveLines.add(processedLine);
		}

		return effectiveLines;
  }

  public static void main(String[] args) {
    String src = "servlet.doc.base=src/main/webapp\nSCAN_PACKAGE=com.national";
    ArrayList<String> list = toLines(src);
    System.out.println(list);
  }
}