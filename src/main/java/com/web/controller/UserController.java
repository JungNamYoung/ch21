package com.web.controller;

import java.util.List;
import java.util.logging.Logger;

import com.web.service.UserService;
import com.web.vo.UserVo;

import haru.annotation.di.Autowired;
import haru.annotation.mvc.Controller;
import haru.annotation.mvc.RequestMapping;
import haru.logger.LoggerManager;
import haru.model.Model;

@Controller
public class UserController {

  private final Logger logger = LoggerManager.getLogger(this.getClass().getSimpleName());

  @Autowired
  public UserService userService;

  @RequestMapping("/selectUser.do")
  public String selectUser(Model model) {

    logger.info("selectUser()");

    List<UserVo> result = userService.selectUserList(null);

    model.addAttribute("author", "david");
    model.addAttribute("results", result);
    
    return "web/user";
  }

  @RequestMapping("/changeUser.do")
  public String changeUser(Model model) {
    userService.changeUser(null);
    return null;
  }

  @RequestMapping("/chat.do")
  public String chat() {

    logger.info("실행함 : chat.do");

    return "web/chat";
  }
}
