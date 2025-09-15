package haru.logger;

import java.io.IOException;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import haru.define.Define;
import haru.kitten.MiniServletContext;

public class LoggerManager {
  private static final String LOG_FORMAT = "[haru][%1$s] %2$tT | %4$s | %3$s";

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

    try {
      FileHandler fileHandler = new FileHandler(MiniServletContext.getWebAppRoot() + Define.WEB_INF + "/logs/application.log", true);
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
    //return String.format(LOG_FORMAT, record.getLevel(), timestamp, record.getLoggerName(), record.getMessage());
    return String.format(LOG_FORMAT, record.getLevel(), timestamp, record.getMessage(), record.getLoggerName());
  }
}
