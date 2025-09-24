package haru.support;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import haru.constants.Define;

public class FileEx {

  String filePath;

  public FileEx(String str) {
    filePath = str;
  }

  public static void writeFile(String fileName, List<String> list) {

    FileWriter fw = null;
    try {
      fw = new FileWriter(fileName, false);
      BufferedWriter bw = new BufferedWriter(fw);

      for (String str : list) {
        bw.write(str + Define.CR);
      }
      bw.close();
      fw.close();

    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public ArrayList<String> read(boolean notTrim) {

    return readEx(filePath, notTrim);
  }

  static public ArrayList<String> readEx(String tFileName, boolean notTrim) {

    exist(tFileName);

//		LOGGER.info("FileEx :: read , path : " + tFileName);

    ArrayList<String> list = new ArrayList<>();

    FileReader reader = null;
    BufferedReader buffer = null;

    try {
      reader = new FileReader(tFileName);
      buffer = new BufferedReader(reader);

      String str;

      while ((str = buffer.readLine()) != null) {

        if (notTrim == false) {

          str = str.trim();

          if (str.length() > Define.COUNT_0)
            list.add(str);
        } else {
          list.add(str);
        }

      }

    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      try {
        buffer.close();
        reader.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    return list;
  }

  public void write(String str) {
    try {

//			Name name = new Name(filePath);

//			LOGGER.info("write , path : " + filePath);

//			BufferedWriter bw = new BufferedWriter(new FileWriter(name.dir + name.fileName + ".out" + name.ext, false));

//			bw.write(str);
//			bw.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static Boolean exists(String dir) {

    Path path = Paths.get(dir);

    if (Files.exists(path)) {
      return true;
    }

//		if (Files.isDirectory(path)) {
//			return true;
//		}

    return false;
  }

  public static void mkDir(String dir) {

//		String path = "D:\\Eclipse\\Java\\새폴더"; // 폴더 경로
    File Folder = new File(dir);

    // 해당 디렉토리가 없을경우 디렉토리를 생성합니다.
    if (!Folder.exists()) {
      try {
        Folder.mkdirs(); // 폴더 생성합니다.
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  static public boolean exist(String filePath) {

    if (filePath.trim().length() == Define.COUNT_0) {
      throw new RuntimeException("문자열이 공백 입니다");
    }

    File file = new File(filePath);

    if (file.isFile() == false) {
      throw new RuntimeException(Define.NOT_FOUND_FILE + Define.SPACE + Define.COMMA + filePath);
    }

    if (file.exists() == false) {
      throw new RuntimeException(Define.NOT_FOUND_FILE + Define.SPACE + Define.COMMA + filePath);
    }

    return true;
  }
}
