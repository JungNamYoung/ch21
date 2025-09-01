package com.web.service.impl;

import java.util.List;
import java.util.logging.Logger;

import org.apache.ibatis.session.SqlSession;
//import org.mybatis.haru.SqlSessionTemplate;

import com.web.service.UserService;
import com.web.vo.UserCol;
import com.web.vo.UserVo;

import haru.annotation.aop.Transactional;
import haru.annotation.di.Autowired;
import haru.annotation.di.Service;
import haru.define.Haru;
import haru.logger.LoggerManager;

@Service("userService")
public class UserServiceImpl implements UserService {

  @Autowired
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
