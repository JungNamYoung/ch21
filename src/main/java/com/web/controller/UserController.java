package com.web.controller;

import java.util.List;
import java.util.logging.Logger;

import com.web.service.UserService;
import com.web.vo.UserVo;

import haru.annotation.di.Autowired;
import haru.annotation.di.Resource;
import haru.annotation.mvc.Controller;
import haru.annotation.mvc.RequestMapping;
import haru.kitten.MiniHttpServletRequest;
import haru.kitten.MiniHttpSession;
import haru.logger.LoggerManager;
import haru.model.Model;

@Controller
public class UserController {

  private final Logger logger = LoggerManager.getLogger(this.getClass().getSimpleName());

  @Autowired
  public UserService userService;
  
  @Resource(name="userService")
  public UserService userServiceExt;

  @RequestMapping({"/selectUser.do", "/selectUserExt.do"})
  public String selectUser(Model model, MiniHttpSession session, MiniHttpServletRequest request) {

    logger.info("selectUser()");
    
    logger.info("id : #1 : " + session.getId());

    MiniHttpSession sess = (MiniHttpSession)request.getAttribute("miniHttpSession");
    
    logger.info("id : #2 : " + sess.getId());
    
    List<UserVo> result = userServiceExt.selectUserList(null);

    model.addAttribute("author", "david");
    model.addAttribute("results", result);
    
    return "web/user";
  }

  @RequestMapping("/changeUser.do")
  public String changeUser(Model model) {
    userServiceExt.changeUser(null);
    return null;
  }

  @RequestMapping("/chat.do")
  public String chat() {

    logger.info("실행함 : chat.do");

    return "web/chat";
  }
}
