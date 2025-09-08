package haru.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import haru.define.Define;

public class TokenEx {

  Map<String, String> map = new HashMap<>();

  public TokenEx(String path, String src) {

    ArrayList<String> list = null;
    
    if(path.length() > 0 && src.length() == 0) {
    FileEx.exist(path);
    	list =	FileEx.readEx(path, false);
    }
    else if(path.length() == 0 && src.length() > 0) {
    	list = LineEx.toEffectiveLines(src);
    }
    else {
    	throw new RuntimeException(Define.NOT_APPLICABLE);
    }

    for (int index = 0; index < list.size(); ++index) {

      String str = list.get(index);

      String[] values = str.split(Define.EQUAL);

      if(values.length == 2) {
      map.put(values[Define.INDEX_0], values[Define.INDEX_1]);
    }
      else if(values.length == 1) {
    	  map.put(values[Define.INDEX_0], Define.STR_BLANK);
      }
      else {
    	  throw new RuntimeException(Define.NOT_APPLICABLE);
      }
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
