package com.web.controller;

import java.util.List;
import java.util.logging.Logger;

import com.web.service.UserService;
import com.web.vo.UserVo;

import haru.annotation.di.Autowired;
import haru.annotation.di.Resource;
import haru.annotation.mvc.Controller;
import haru.annotation.mvc.RequestMapping;
import haru.annotation.mvc.RequestParam;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpSession;
import haru.logging.MiniLogger;
import haru.mvc.model.Model;
import haru.mvc.model.ModelMap;

@Controller
public class UserController {

  private static final Logger logger = MiniLogger.getLogger(UserController.class.getSimpleName());

  @Autowired
  public UserService userService;
  
  @Resource(name="userService")
  public UserService userServiceExt;

  @RequestMapping({"/selectUser.do", "/selectUserExt.do"})
  public String selectUser(Model model, MiniHttpSession session, MiniHttpServletRequest request) {

    logger.info("selectUser()");
    
    logger.info("id : #1 : " + session.getId());
    
    List<UserVo> result = userServiceExt.selectUserList(null);

    model.addAttribute("author", "david");
    model.addAttribute("results", result);
    
    return "web/user";
  }

  @RequestMapping("/changeUser.do")
  public String changeUser(ModelMap modelMap) {
    
    userServiceExt.changeUser(null);
    modelMap.addAttribute("url", "www.naver.com");
    
    return "web/chat";
  }

  @RequestMapping("/chat.do")
  public String chat(@RequestParam("url") String siteUrl) {

    logger.info("실행함 : chat.do : " + siteUrl);

    return "web/chat";
  }
}
