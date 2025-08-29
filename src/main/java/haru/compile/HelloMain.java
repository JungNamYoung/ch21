package haru.compile;

public class HelloMain {

  public static void main(String[] args) {
    
    HelloWorld helloWorld = new HelloWorld();
    helloWorld.greet("Daniel");
    
    DynamicCompiler dynamicCompiler = new DynamicCompiler();
    dynamicCompiler.make("Samuel");
    
  }
}
