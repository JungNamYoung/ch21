package haru.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import haru.constants.Define;

public class PropertyConfig {

  Map<String, String> map = new HashMap<>();

//public PropertyConfig(String path, String src)
  public PropertyConfig(String src) {
    ArrayList<String> list = null;

//    if(path.length() > 0 && src.length() == 0) {
//    FileEx.exist(path);
//    	list =	FileEx.readEx(path, false);
//    }
//    else if(path.length() == 0 && src.length() > 0) {
//    	list = LineEx.toEffectiveLines(src);
//    }
//    else {
//    	throw new RuntimeException(Define.NOT_APPLICABLE);
//    }

    if (src.length() > 0) {
      list = LineEx.toEffectiveLines(src);
    }

    for (int index = 0; index < list.size(); ++index) {

      String str = list.get(index);

      String[] values = str.split(Define.EQUAL);

      if (values.length == 2) {
        map.put(values[Define.INDEX_0], values[Define.INDEX_1]);
      } else if (values.length == 1) {
        map.put(values[Define.INDEX_0], Define.STR_BLANK);
      } else {
        throw new RuntimeException(Define.NOT_APPLICABLE);
      }
    }
  }

  public String get(String key) {
    if (map.containsKey(key) == false) {
      throw new RuntimeException(Define.NOT_APPLICABLE);
    }
    return map.get(key);
  }

  public List<Integer> findIndexedConfigs(String prefix) {

    Pattern p = Pattern.compile(Pattern.quote(prefix) + "\\[(\\d+)\\]\\..+");

    Set<Integer> set = new HashSet<>();

    for (String key : map.keySet()) {
      Matcher m = p.matcher(key);
      if (m.matches()) {
        set.add(Integer.parseInt(m.group(1)));
      }
    }

    List<Integer> list = new ArrayList<>(set);
    list.sort(Integer::compareTo);
    return list;
  }
}
