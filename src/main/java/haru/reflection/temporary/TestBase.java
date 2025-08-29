package haru.reflection.temporary;

import java.lang.reflect.Method;

public class TestBase {

  public TestBase(String str) {

    System.out.println("Test : " + str);
  }

  public static void main(String[] args) {
    System.out.println("main()");
    TestBase.test_02();
  }

  static void test_02() {

    try {
      Class<?> clazz = String.class;

      Method method = clazz.getMethod("toUpperCase");
      String result = (String) method.invoke("hello");

      result = "";

    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  static void test_01() {
    Class<?> clazz = String.class;

    System.out.println("#1: " + clazz.getName());

    try {
      Object obj = clazz.getDeclaredConstructor().newInstance();

      System.out.println("#2: " + obj.getClass().getName());

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    String str = "hello";
    String str1 = str.toUpperCase();
    str1 = "";
  }
}
