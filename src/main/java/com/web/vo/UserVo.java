package com.web.vo;

public class UserVo {
  private String userSn;
  private String userId;
  private String userName;
  private String description;
  private String useYn;
  private String regUser;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userid) {
    this.userId = userid;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String name) {
    this.userName = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUseYn() {
    return useYn;
  }

  public void setUseYn(String useYn) {
    this.useYn = useYn;
  }

  public String getRegUser() {
    return regUser;
  }

  public void setRegUser(String regUser) {
    this.regUser = regUser;
  }

  public String getUserSn() {
    return userSn;
  }

  public void setUserSn(String userSn) {
    this.userSn = userSn;
  }
}
