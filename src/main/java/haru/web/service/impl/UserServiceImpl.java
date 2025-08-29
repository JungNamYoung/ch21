package haru.web.service.impl;

import java.util.List;
import java.util.logging.Logger;

import org.apache.ibatis.session.SqlSession;
//import org.mybatis.haru.SqlSessionTemplate;

import haru.define.Haru;
import haru.logger.LoggerManager;
import haru.annotation.aop.Transactional;
import haru.annotation.di.Autowired;
import haru.annotation.di.Service;
import haru.web.service.UserService;
import haru.web.vo.UserCol;
import haru.web.vo.UserVo;

@Service("userService")
public class UserServiceImpl implements UserService {

  @Autowired(name = "sqlSessionUser")
  SqlSession sqlSessionUser;

  private final Logger logger = LoggerManager.getLogger(this.getClass().getSimpleName());
  
  public List<UserVo> selectUserList(UserCol userCol) {

    List<UserVo> list = null;

    try {
      list = sqlSessionUser.selectList(Haru.SQL_NAME_SPACE + "selectUser", userCol);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return list;
  }
  
  @Transactional(transactionManager = "txUser")
  public int changeUser(UserCol userCol) {
    logger.info("changeUser()");
    int result = sqlSessionUser.insert(Haru.SQL_NAME_SPACE + "insertUser", userCol);
    result = sqlSessionUser.update(Haru.SQL_NAME_SPACE + "updateUser", userCol);
    return result;
  }

}
