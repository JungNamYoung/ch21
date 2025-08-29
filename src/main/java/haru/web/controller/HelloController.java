package haru.web.controller;

import java.util.logging.Logger;

import haru.annotation.mvc.Controller;
import haru.annotation.mvc.RequestMapping;
import haru.logger.LoggerManager;

@Controller
public class HelloController {

  private final Logger logger = LoggerManager.getLogger(HelloController.class.getSimpleName());

  @RequestMapping("/sayHello.do")
  public String sayHello() {
    return "haru/say_hello";
  }

  @RequestMapping("/my.do")
  public String sayMy() {
    logger.info("sayMy()");
    return "haru/my";
  }

}
