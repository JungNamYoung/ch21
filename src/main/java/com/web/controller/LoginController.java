package com.web.controller;

import java.util.List;
import java.util.logging.Logger;

import com.web.service.LoginService;
import com.web.vo.UserCol;
import com.web.vo.UserVo;

import haru.annotation.di.Autowired;
import haru.annotation.mvc.Controller;
import haru.annotation.mvc.RequestMapping;
import haru.logging.MiniLogger;
import haru.mvc.model.Model;

@Controller
public class LoginController {

  private static final Logger logger = MiniLogger.getLogger(LoginController.class.getSimpleName());
  
  @Autowired
  LoginService loginService;

  @RequestMapping("/say.do")
  public String say() {
    return "login/say";
  }

  @RequestMapping("/selectLogin.do")
  public String selectLogin(Model model) {
    
    List<UserVo> list = loginService.selectLogin(null);
    
    model.addAttribute("results", list);
    
    return "login/chat";
  }


  @RequestMapping("/view-login.do")
  public String viewLogin(Model model) {
    
    List<UserVo> list = loginService.selectLogin(null);
    
    model.addAttribute("results", list);
    
    return "login/view-login";
  }
  
  @RequestMapping("/login/change-login.do")
  public String changeLogin(Model model, UserCol userCol) {
            
    return "login/change-login";
  }
  
}