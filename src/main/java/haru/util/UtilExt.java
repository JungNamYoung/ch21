package haru.util;

import java.net.URL;

import haru.define.Define;

public class UtilExt {
//  static public JSONObject postex(Map<String, Object> param) {
//
//    JSONObject tParam = new JSONObject(param);
//
//    String tUrl = (String) param.get(Define.URL);
//
//    return post(tParam.toString(), tUrl);
//
//  }

//  static public JSONObject post(String strJson, String tUrl) {
//    try {
//      URL url = new URL(tUrl);
//
//      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
  ////			con.setConnectTimeout(500);
////			con.setReadTimeout(1000);
//      connection.setRequestMethod(Define.POST);
//
//      connection.setRequestProperty("Accept", Define.APP_JSON);
//      connection.setRequestProperty("Content-type", Define.APP_JSON);
//
//      connection.setDoInput(true);
//      connection.setDoOutput(true);
//      // con.setUseCaches(false);
//      // con.setDefaultUseCaches(false);
//
//      OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), Define.UTF8);
//      writer.write(strJson);
//      writer.flush();
//      writer.close();
//
//      System.out.println(url + ", " + strJson);
//
//      StringBuilder sb = new StringBuilder();
//
//      if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), Define.UTF8));
//
//        String line;
//
//        while ((line = br.readLine()) != null) {
//          sb.append(line).append(Define.ENTER_EX);
//        }
//        br.close();
//
//        JSONParser parser = new JSONParser();
//
//        JSONObject responseData = (JSONObject) parser.parse(sb.toString());
//
//        System.out.println("response = " + sb.toString());
//        return responseData;
//      } else {
//        System.out.println(connection.getResponseMessage());
//      }
//
//      connection.disconnect();
//
//    } catch (Exception ex) {
//      System.out.println(ex.toString());
//    }
//
//    return null;
//  }

//  public static void error(String... msgs) {
//
//    String msg = Define.STR_BLANK;
//
////		if (msgs.length > Define.COUNT_0) {
////			msg = Define.ERR_MSG + msgs[Define.INDEX_0];
////		}
//
//    msg = Define.ERR_MSG;
//
//    for (String str : msgs) {
//      msg += (str + " | ");
//    }
//
//    try {
//      throw new Exception(msg);
//    } catch (Exception ex) {
//      ex.printStackTrace();
//    }
//  }

  // getClassPath("config/info.txt")
  public static String getClassPath(String path) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    URL resource = classLoader.getResource(path);

    if (resource != null) {
      try {
        String path1 = resource.getPath();
        return path1;
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    } else {
      throw new RuntimeException("에러");
    }

    return null;
  }

  public static int safeInt(String str, int defaultValue) {
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }
}
