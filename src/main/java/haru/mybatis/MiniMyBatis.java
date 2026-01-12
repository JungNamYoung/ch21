package haru.mybatis;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import haru.constants.Define;
import haru.constants.Haru;
import haru.mybatis.config.MyBatisSessionConfig;
import haru.support.PropertyConfig;
import haru.support.UtilExt;
import haru.transaction.SqlSessionTxHandler;
import haru.transaction.TxHandler;

public class MiniMyBatis {

  private final Map<String, TransactionalSqlSession> sessionsById = new HashMap<>();

  private final Map<String, TxHandler> txHandlersByName = new HashMap<>();

  /**
   * (선택) type 기반 조회를 빠르게 하려면 캐시를 둘 수 있습니다. 타입 조회가 자주 호출되지 않는다면 굳이 유지하지 않아도 됩니다.
   */
  private final Map<Class<?>, SqlSession> sessionByTypeCache = new HashMap<>();
  
  public void initSessionFactory() {

    // txt 로드 (haru.txt 읽는 방식과 동일)
    String text = UtilExt.loadTextSmart(Haru.MYBATIS_TXT);

    // TokenEx로 파싱
    PropertyConfig property = new PropertyConfig(text);

    // configs[x] 인덱스 수집
    List<Integer> indexes = property.findIndexedConfigs("haru.mybatis.configs");

    // 인덱스별로 MyBatisSessionConfig 생성
    List<MyBatisSessionConfig> configs = new ArrayList<>();

    for (int idx : indexes) {
      String base = "haru.mybatis.configs[" + idx + "].";

      String sqlSession = property.get(base + "sqlSession").toString();
      String tx = property.get(base + "tx").toString();
      String configPath = property.get(base + "config").toString();

      // configPath는 "haru/dbconfig/..." 형태이므로,
      // 기존 코드와 동일하게 Haru.ROOT_PACKAGE를 붙여 MyBatis가 읽을 수 있게 맞춥니다.
      configs.add(MyBatisSessionConfig.of(sqlSession, tx, Haru.ROOT_PACKAGE + "/" + stripLeadingSlash(configPath)));
    }

    // 실제 초기화 호출
    initSessionFactory(configs);
  }

  /**
   * "haru/dbconfig/..." 앞에 "/"가 붙는 실수를 방지하기 위한 보호 코드입니다.
   */
  private String stripLeadingSlash(String path) {
    if (path == null)
      return "";
    if (path.startsWith("/"))
      return path.substring(1);
    return path;
  }

  /**
   * 설정 기반 초기화(확장) - 여러 DB/세션이 필요할 때 configs에 추가만 하면 됩니다.
   */
  private void initSessionFactory(List<MyBatisSessionConfig> configs) {
    try {
      if (configs == null || configs.isEmpty()) {
        throw new IllegalArgumentException("MyBatis 세션 설정(configs)이 비어 있습니다.");
      }

      // 초기화 재호출 시 안전하게 비웁니다(원치 않으면 제거해도 됩니다).
      sessionsById.clear();
      txHandlersByName.clear();
      sessionByTypeCache.clear();

      for (MyBatisSessionConfig cfg : configs) {
        registerOne(cfg);
      }

    } catch (Exception e) {
      throw new RuntimeException("MyBatis 설정 초기화 실패", e);
    }
  }

  /**
   * 중복 검증 + try-with-resources + Map 등록을 한 번에 처리합니다.
   */
  private void registerOne(MyBatisSessionConfig cfg) throws Exception {

    // 중복 검증: sessionId
    if (sessionsById.containsKey(cfg.sessionId())) {
      throw new IllegalStateException("중복 sessionId 감지: " + cfg.sessionId());
    }

    // 중복 검증: txName
    if (txHandlersByName.containsKey(cfg.txName())) {
      throw new IllegalStateException("중복 txName 감지: " + cfg.txName());
    }

    SqlSessionFactory factory;
    // try-with-resources
    try (InputStream inputStream = Resources.getResourceAsStream(cfg.dbConfigPath())) {
      factory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    SqlSession sqlSessionProxy = SqlSessionProxyFactory.create(factory);

    TransactionalSqlSession transactionalSqlSession = new TransactionalSqlSession(cfg.sessionId(), cfg.txName(), factory, sqlSessionProxy);

    // Map 기반 등록
    sessionsById.put(cfg.sessionId(), transactionalSqlSession);
    txHandlersByName.put(cfg.txName(), new SqlSessionTxHandler(factory));
  }

  /**
   * txName으로 트랜잭션 핸들러를 조회합니다.
   */
  public TxHandler findMiniTxHandler(String txName) {
    return txHandlersByName.get(txName);
  }

  /**
   * Map 기반 조회: sessionId -> SqlSession
   */
  public SqlSession getSqlSessionBySessionId(String sessionId) {
    TransactionalSqlSession tss = sessionsById.get(sessionId);
    if (tss == null) {
      throw new RuntimeException("등록되지 않은 sessionId입니다: " + sessionId);
    }
    return tss.getSqlSession();
  }

  /**
   * 타입 기반 조회(기존 기능 유지) - "List 순회"는 제거했지만, Map에 들어있는 값들을 순회하는 것은 본질적으로 필요합니다. -
   * 대신 캐시를 두어 동일 타입 조회를 반복할 때 비용을 줄입니다.
   */
  public SqlSession getSqlSessionByType(Class<?> type) {
    SqlSession cached = sessionByTypeCache.get(type);
    if (cached != null)
      return cached;

    for (TransactionalSqlSession tss : sessionsById.values()) {
      SqlSession s = tss.getSqlSession();
      if (type.isAssignableFrom(s.getClass())) {
        sessionByTypeCache.put(type, s);
        return s;
      }
    }

    throw new RuntimeException("해당 타입에 매칭되는 SqlSession이 없습니다:" + type.getName());
  }
}