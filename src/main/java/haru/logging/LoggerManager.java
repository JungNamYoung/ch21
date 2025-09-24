package haru.logging;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import haru.constants.Define;
import haru.core.bootstrap.MiniServletContainer;

public class LoggerManager {
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

    String logDirPath = MiniServletContainer.getRealPath(Define.STR_BLANK) + Define.WEB_INF + "/logs";

    File logDir = new File(logDirPath);
    if (!logDir.exists()) {
      if (logDir.mkdirs()) {
        System.out.println("Log directory created : " + logDirPath);
      } else {
        System.err.println("Failed to create log directory : " + logDirPath);
      }
    }

    try {
      FileHandler fileHandler = new FileHandler(logDirPath + "/application.log", true);
      fileHandler.setFormatter(new SimpleFormatter() {
        @Override
        public String format(LogRecord record) {
          return formatLog(record);
        }
      });
      fileHandler.setLevel(Level.ALL);
      logger.addHandler(fileHandler);
    } catch (IOException e) {
      System.err.println("Failed to initialize log file: " + e.getMessage());
    }
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
