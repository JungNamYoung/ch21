package haru.web.vo;

public class UserCol {
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
