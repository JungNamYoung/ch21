package haru.web.service;

import java.util.List;

import haru.web.vo.UserCol;
import haru.web.vo.UserVo;

public interface UserService {
	public List<UserVo> selectUserList(UserCol userCol);
	public int changeUser(UserCol userCol);
}
