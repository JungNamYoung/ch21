package com.web.test;

import java.util.logging.*;

class LoggerManager {
  public static Logger getLogger(String name) {
    Logger logger = Logger.getLogger(name);
    logger.setUseParentHandlers(false);

    ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setFormatter(new SimpleFormatter());
    logger.addHandler(consoleHandler);

    return logger;
  }
}

class IdParameterFilter {
  private final Logger logger = LoggerManager.getLogger("IdParameterFilter");

  public void doFilter(String id) {
    logger.info("필터 실행 - id=" + id);
  }
}

class StaticFilter {
  
  private static final Logger logger = LoggerManager.getLogger("StaticFilter");

  public void doFilter(String id) {
    try {
      logger.info("필터 실행 - id=" + id);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

public class LoggerDuplicateDemo {
  public static void main(String[] args) {
    
    System.out.println("===요청을 3번 보냄===");
    for (int i = 1; i <= 3; i++) {
      IdParameterFilter filter = new IdParameterFilter();
      filter.doFilter("request" + i);
    }

    System.out.println("\n===static logger 사용===");
    
    for (int i = 1; i <= 3; i++) {
      StaticFilter staticFilter = new StaticFilter();
      staticFilter.doFilter("request" + i);
    }
  }
}

