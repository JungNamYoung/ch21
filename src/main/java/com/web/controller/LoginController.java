package com.web.controller;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

import com.web.service.LoginService;
import com.web.vo.UserCol;
import com.web.vo.UserVo;

import haru.annotation.di.Autowired;
import haru.annotation.mvc.Controller;
import haru.annotation.mvc.GetMapping;
import haru.annotation.mvc.PostMapping;
import haru.annotation.mvc.RequestMapping;
import haru.logging.MiniLogger;
import haru.mvc.model.Model;

@Controller
public class LoginController {

  private static final Logger logger = MiniLogger.getLogger(LoginController.class.getSimpleName());

  @Autowired
  LoginService loginService;

  @RequestMapping("/list-login.do")
  public String listLogin(Model model) {

    List<UserVo> list = loginService.selectLogin(null);

    model.addAttribute("results", list);

    return "login/list-login";
  }

  @PostMapping("/change-login.do")
  public String changeLogin(Model model, UserCol userCol) {

    model.addAttribute("message", "사용자 정보가 성공적으로 저장되었습니다.");
    model.addAttribute("userCol", userCol);
    
    return "login/change-login-result";
  }

  @GetMapping("/change-login.do")
  public String changeLogin(Model model) {
    return "login/change-login";
  }

}