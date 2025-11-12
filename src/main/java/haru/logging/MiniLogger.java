package haru.logging;

import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class MiniLogger {
  private static final String LOG_FORMAT = "[haru][%1$s] %2$tT | %4$s | %5$s | %3$s";

  public static Logger getLogger(String name) {
    Logger logger = Logger.getLogger(name);
    logger.setUseParentHandlers(false);

    ConsoleHandler consoleHandler = new ConsoleHandler() {
      @Override
      public void publish(LogRecord record) {
        if (isLoggable(record)) {
          System.out.println(formatLog(record));
        }
      }
    };

    consoleHandler.setLevel(Level.ALL);
    logger.addHandler(consoleHandler);
    logger.setLevel(Level.ALL);
    
    return logger;
  }

  private static String formatLog(LogRecord record) {
    
    Date timestamp = new Date(record.getMillis());

    String methodName = "unknown";
    for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
      if (ste.getClassName().equals(record.getSourceClassName())) {
        methodName = ste.getMethodName();
        break;
      }
    }
    return String.format(LOG_FORMAT, record.getLevel(), timestamp, record.getMessage(), record.getLoggerName(), methodName);
  }
}
