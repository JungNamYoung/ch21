package com.web.controller;

import java.util.logging.Logger;

import haru.annotation.mvc.Controller;
import haru.annotation.mvc.RequestMapping;
import haru.logging.MiniLogger;

@Controller
public class LoginController {

  private static final Logger logger = MiniLogger.getLogger(LoginController.class.getSimpleName());

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