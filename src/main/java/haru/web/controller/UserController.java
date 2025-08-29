package haru.web.controller;

import java.util.List;

import haru.annotation.di.Autowired;
import haru.annotation.mvc.Controller;
import haru.annotation.mvc.RequestMapping;
import haru.logger.LoggerManager;
import haru.model.MiniModel;
//import haru.model.ModelAndView;
import haru.web.service.UserService;
import haru.web.vo.UserVo;

import java.util.logging.Logger;

@Controller
public class UserController {

  private final Logger logger = LoggerManager.getLogger(this.getClass().getSimpleName());

  @Autowired(name = "userService")
  public UserService userService;

  @RequestMapping("/selectUser.do")
  public String selectUser(MiniModel model) {

    logger.info("selectUser()");

    List<UserVo> result = userService.selectUserList(null);

    model.addAttribute("author", "david");
    model.addAttribute("results", result);
    
    return "haru/user";
  }

  @RequestMapping("/changeUser.do")
  public String changeUser(MiniModel model) {
    userService.changeUser(null);
    return null;
  }

  @RequestMapping("/chat.do")
  public String chat() {

    logger.info("실행함 : chat.do");

    return "haru/chat";
  }
}
