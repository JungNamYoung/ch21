package com.web.service;

import java.util.List;

import com.web.vo.UserCol;
import com.web.vo.UserVo;

public interface LoginService {
  
  List<UserVo> selectLogin(UserCol userCol);
  
  int changeLogin(UserCol userCol);

}
