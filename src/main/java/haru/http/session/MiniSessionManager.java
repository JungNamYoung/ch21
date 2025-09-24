package haru.http.session;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import haru.constants.Define;
import haru.http.MiniHttpSession;
//import haru.config.MiniHttpSession;
import haru.logging.LoggerManager;
import jakarta.servlet.ServletContext;

public class MiniSessionManager {
  private static final Map<String, MiniHttpSession> sessions = new ConcurrentHashMap<>();
  private static final Logger logger = LoggerManager.getLogger(MiniSessionManager.class.getSimpleName());

  public static MiniHttpSession createSession(ServletContext context) {
    MiniHttpSession miniHttpSession = new MiniHttpSession(context);

    sessions.put(miniHttpSession.getId(), miniHttpSession);

    logger.info("session - create - " + miniHttpSession.getId());

    return miniHttpSession;
  }

  public static MiniHttpSession getSession(String sessionId) {

    if (sessionId == null)
      return null;

    MiniHttpSession miniHttpSession = sessions.get(sessionId);

    if (miniHttpSession != null && miniHttpSession.isValid()) {
      miniHttpSession.updateLastAccessedTime();
      return miniHttpSession;
    }

    return null;
  }

  public static void invalidateSession(String sessionId) {
    MiniHttpSession session = sessions.remove(sessionId);

    if (session != null) {
      session.invalidate();
    }
  }

  public static void checkCleanUpSessions() {

//		logger.info("period - checkCleanUpSessions()");

    Iterator<Entry<String, MiniHttpSession>> iterator = sessions.entrySet().iterator();
    while (iterator.hasNext()) {
      Entry<String, MiniHttpSession> entry = iterator.next();
      if (!entry.getValue().isValid()) {

        logger.info("session - remove - " + entry.getValue().getId());
        iterator.remove();
      }
    }
  }

  static {
    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(MiniSessionManager::checkCleanUpSessions, Define.SECOND_5, Define.SECOND_5, TimeUnit.SECONDS);
  }
}