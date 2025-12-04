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
import haru.logging.MiniLogger;

@Service("userService")
//@Repository("userService")
public class UserServiceImpl implements UserService {

  @Autowired
  SqlSession sqlSessionUser;

  private static final Logger logger = MiniLogger.getLogger(UserServiceImpl.class.getSimpleName());
  
  private static String SQL_NAMESPACE = "com.web.service.impl.UserMapper.";
  
  public List<UserVo> selectUserList(UserCol userCol) {

    List<UserVo> list = null;

    try {
      list = sqlSessionUser.selectList(SQL_NAMESPACE + "selectUser", userCol);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return list;
  }
  
  @Transactional(transactionManager = "txUser")
  public int changeUser(UserCol userCol) {
    
    logger.info("changeUser()");
    
    int result = sqlSessionUser.insert(SQL_NAMESPACE + "insertUser", userCol);
    
    result = sqlSessionUser.update(SQL_NAMESPACE + "updateUser", userCol);
    
    return result;
  }

}
