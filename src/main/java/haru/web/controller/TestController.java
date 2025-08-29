package haru.web.controller;

import haru.annotation.mvc.Controller;
import haru.annotation.mvc.RequestMapping;

@Controller
public class TestController {

  @RequestMapping("/test")
  public String test() {
    System.out.println("핸들러 실행됨");
    return "haru/testView";
  }

}
