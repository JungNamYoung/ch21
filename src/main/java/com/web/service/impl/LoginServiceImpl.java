package com.web.service.impl;

import java.util.List;
import java.util.logging.Logger;

import org.apache.ibatis.session.SqlSession;

import com.web.service.LoginService;
import com.web.vo.UserCol;
import com.web.vo.UserVo;

import haru.annotation.aop.Transactional;
import haru.annotation.di.Autowired;
import haru.annotation.di.Repository;
import haru.logging.MiniLogger;

@Repository("loginService")
public class LoginServiceImpl implements LoginService {

  Logger logger = MiniLogger.getLogger(LoginServiceImpl.class.getSimpleName());

  @Autowired
  SqlSession sqlSessionLogin;

  private static String SQL_NAMESPACE = "com.web.service.impl.UserMapper.";

  @Override
  public List<UserVo> selectLogin(UserCol userCol) {

    List<UserVo> list = sqlSessionLogin.selectList(SQL_NAMESPACE + "selectUser", userCol);

    return list;
  }

  @Transactional(transactionManager = "txUser")
  public int changeLogin(UserCol userCol) {
    logger.info("changeLogin()");

    int result = sqlSessionLogin.insert(SQL_NAMESPACE + "insertUser", userCol);

    result = sqlSessionLogin.update(SQL_NAMESPACE + "updateUser", userCol);

    return result;
  }
}
