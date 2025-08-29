package haru.reflection.delete;

//interface Hello{
//	void say();
//}
//
//class HelloEx implements Hello{
//    public void say() {    	
//    	System.out.println("1.say");
//    }
//}
//
//public class TestEx4 {
//
//	public static void main(String[] args) {
//
//		Hello hello = new HelloEx();
//		hello.say();
//
//		Method method = null;
//		Object result = null;
//
//		try {
//			method = Hello.class.getMethod("say");
//		} catch (NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			result = method.invoke(hello);
//		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//			e.printStackTrace();
//		}
//
//		result = 3;
//
//	}
//}
//
