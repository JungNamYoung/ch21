package haru.util;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class LineEx {

	public static ArrayList<String> toLines(String text) {
		return text.lines().collect(Collectors.toCollection(ArrayList::new));
	}

	public static ArrayList<String> toEffectiveLines(String text) {
		return text.lines().map(String::trim).filter(s -> !s.isEmpty() && !s.startsWith("#") && !s.startsWith("//"))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static void main(String[] args) {
		String src = "ROOT_PATH=src/main/webapp\nSCAN_PACKAGE=com.national";
		ArrayList<String> list = toLines(src);
		System.out.println(list);
	}
}