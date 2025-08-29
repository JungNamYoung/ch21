package haru.reflection;

import java.lang.reflect.Method;

public class Test_Info {
  public static void main(String[] args) {
    try {
      Class<?> infoClass = Class.forName("haru.reflection.Info");

      Object infoInstance = infoClass.getDeclaredConstructor().newInstance();

      Method setNameMethod = infoClass.getMethod("setName", String.class);
      setNameMethod.invoke(infoInstance, "jung");

      Method setAgeMethod = infoClass.getMethod("setAge", Integer.class);
      setAgeMethod.invoke(infoInstance, 51);

      Method getNameMethod = infoClass.getMethod("getName");
      Method getAgeMethod = infoClass.getMethod("getAge");

      String name = (String) getNameMethod.invoke(infoInstance);
      Integer age = (Integer) getAgeMethod.invoke(infoInstance);

      System.out.println("Name : " + name + ", Age: " + age);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
