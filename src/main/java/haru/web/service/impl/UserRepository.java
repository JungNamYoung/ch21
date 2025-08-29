package haru.web.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import haru.web.vo.UserVo;

public class UserRepository {

  Connection connection = null;

  public UserRepository(Connection conn) {
    this.connection = conn;
  }

  public List<UserVo> selectUser() {
    List<UserVo> results = new ArrayList<>();
    try {
      PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM user");

      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        String str1 = rs.getString("user_sn");
        String str2 = rs.getString("user_id");
        String str3 = rs.getString("user_name");
        String str4 = rs.getString("description");
        String str5 = rs.getString("use_yn");
        String str6 = rs.getString("reg_user");

        System.out.println(String.format("-> %s, %s, %s, %s, %s, %s", str1, str2, str3, str4, str5, str6));

        UserVo vo = new UserVo();
        vo.setUserSn(str3);
        vo.setUserName(str1);
        vo.setUserId(str2);

        results.add(vo);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return results;
  }

  public void updateUser() {

    String sql = "UPDATE user SET description=? WHERE user_id = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, "사용자-홍길동");
      pstmt.setString(2, "hong123");

      int affectedRows = pstmt.executeUpdate();

      System.out.println("업데이트된 행의 수 : " + affectedRows);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    System.out.println("updateUser 수행함");
  }

  public void insertUser() {
    String sql = "INSERT INTO user(id, NAME, description) VALUE('nam', 'daniel', 'high school')";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      int affectedRows = pstmt.executeUpdate();
    } catch (Exception ex) {
      throw new RuntimeException("#1: 실패: ", ex);
    }

    System.out.println("insertUser 수행함");
  }

}
