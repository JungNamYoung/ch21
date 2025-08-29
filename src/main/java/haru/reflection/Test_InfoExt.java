package haru.reflection;

import java.lang.reflect.Field;

public class Test_InfoExt {
  public static void main(String[] args) {
    try {
      Class<?> infoExClass = Class.forName("haru.reflection.InfoEx");
      Object infoExInstance = infoExClass.getDeclaredConstructor().newInstance();

      Class<?> ageExClass = Class.forName("haru.reflection.AgeEx");
      Object ageExInstance = ageExClass.getDeclaredConstructor().newInstance();

      Field ageField = infoExClass.getDeclaredField("age");
      ageField.setAccessible(true);
      ageField.set(infoExInstance, ageExInstance);

      String result = (String) infoExClass.getDeclaredMethod("ageRange").invoke(infoExInstance);
      System.out.println(result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}