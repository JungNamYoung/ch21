package com.web.service;

import java.util.List;

import com.web.vo.UserCol;
import com.web.vo.UserVo;

public interface UserService {
	public List<UserVo> selectUserList(UserCol userCol);
	public int changeUser(UserCol userCol);
}
