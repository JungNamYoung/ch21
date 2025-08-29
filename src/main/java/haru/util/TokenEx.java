package haru.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import haru.define.Define;

public class TokenEx {

  Map<String, String> map = new HashMap<>();

  public TokenEx(String path) {

    FileEx.exist(path);

    ArrayList<String> list = FileEx.readEx(path, false);

    for (int index = 0; index < list.size(); ++index) {

      String str = list.get(index);

      String[] values = str.split(Define.EQUAL);

      map.put(values[Define.INDEX_0], values[Define.INDEX_1]);
    }
  }

  public String get(String key) {
    if (map.containsKey(key) == false) {
      throw new RuntimeException(Define.NOT_APPLICABLE);
//      return Define.STR_BLANK;
    }
    return map.get(key);
  }
}
