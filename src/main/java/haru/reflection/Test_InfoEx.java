package haru.reflection;

import java.lang.reflect.Field;

public class Test_InfoEx {
  public static void main(String[] args) {
    try {
      Class<?> infoExClass = Class.forName("haru.reflection.InfoEx");
      Object infoExInstance = infoExClass.getDeclaredConstructor().newInstance();

      Class<?> ageClass = Class.forName("haru.reflection.Age");
      Object ageInstance = ageClass.getDeclaredConstructor().newInstance();

      Field ageField = infoExClass.getDeclaredField("age");
      ageField.setAccessible(true);
      ageField.set(infoExInstance, ageInstance);

      String result = (String) infoExClass.getDeclaredMethod("ageRange").invoke(infoExInstance);
      System.out.println(result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}