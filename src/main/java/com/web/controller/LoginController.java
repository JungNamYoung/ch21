package com.web.controller;

import java.util.logging.Logger;

import haru.annotation.mvc.Controller;
import haru.annotation.mvc.RequestMapping;
import haru.logger.LoggerManager;

@Controller
public class LoginController {

  private final Logger logger = LoggerManager.getLogger(LoginController.class.getSimpleName());

  @RequestMapping("/sayHello.do")
  public String sayHello() {
    return "web/chat";
  }

  @RequestMapping("/my.do")
  public String sayMy() {
    logger.info("sayMy()");
    return "web/chat";
  }

}