package com.web.vo;

public class UserCol {

  private int page = 1; // 현재 페이지
  private int size = 10; // 페이지당 보여줄 개수

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getOffset() {
    return (page - 1) * size;
  }

  public UserCol() {
  }

  public UserCol(UserCmd userCmd) {
  }

  private String colSn;
  private String colId;
  private String colName;
  private String colDescription;
  private String colUseYn;
  private String colRegUser;

  public String getColId() {
    return colId;
  }

  public void setColId(String colId) {
    this.colId = colId;
  }

  public String getColName() {
    return colName;
  }

  public void setColName(String colName) {
    this.colName = colName;
  }

  public String getColDescription() {
    return colDescription;
  }

  public void setColDescription(String colDescription) {
    this.colDescription = colDescription;
  }

  public String getColUseYn() {
    return colUseYn;
  }

  public void setColUseYn(String colUseYn) {
    this.colUseYn = colUseYn;
  }

  public String getColRegUser() {
    return colRegUser;
  }

  public void setColRegUser(String colRegUser) {
    this.colRegUser = colRegUser;
  }

  public String getColSn() {
    return colSn;
  }

  public void setColSn(String colSn) {
    this.colSn = colSn;
  }
}
