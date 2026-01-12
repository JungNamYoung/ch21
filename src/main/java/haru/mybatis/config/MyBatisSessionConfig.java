package haru.mybatis.config;

import java.util.Objects;

/**
 * 설정 객체: sessionId/txName/configPath를 한 덩어리로 묶어 "설정 기반 초기화"를 가능하게 합니다.
 */
public final class MyBatisSessionConfig {
  private final String sessionId;
  private final String txName;
  private final String dbConfigPath;

  private MyBatisSessionConfig(String sessionId, String txName, String dbConfigPath) {
    this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
    this.txName = Objects.requireNonNull(txName, "txName");
    this.dbConfigPath = Objects.requireNonNull(dbConfigPath, "dbConfigPath");
    if (sessionId.isBlank())
      throw new IllegalArgumentException("sessionId가 비어 있습니다.");
    if (txName.isBlank())
      throw new IllegalArgumentException("txName이 비어 있습니다.");
    if (dbConfigPath.isBlank())
      throw new IllegalArgumentException("dbConfigPath가 비어 있습니다.");
  }

  public static MyBatisSessionConfig of(String sessionId, String txName, String dbConfigPath) {
    return new MyBatisSessionConfig(sessionId, txName, dbConfigPath);
  }

  public String sessionId() {
    return sessionId;
  }

  public String txName() {
    return txName;
  }

  public String dbConfigPath() {
    return dbConfigPath;
  }
}