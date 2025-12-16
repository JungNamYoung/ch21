package haru.support;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.lang.model.SourceVersion;

public class IdentifierUtil {
	private IdentifierUtil() {
	}

	public static String makeJavaIdentifier(String input) {
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

	public static String makeQualifiedJavaIdentifier(String input) {
		return Arrays.stream(input.split("\\.")).map(IdentifierUtil::makeJavaIdentifier)
				.collect(Collectors.joining("."));
	}
}
