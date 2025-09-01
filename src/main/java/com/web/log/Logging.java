package com.web.log;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import haru.annotation.aop.After;
import haru.annotation.aop.Around;
import haru.annotation.aop.Aspect;
import haru.annotation.aop.Before;
import haru.logger.LoggerManager;

@Aspect
public class Logging {
  Logger logger = LoggerManager.getLogger(this.getClass().getSimpleName());

//  @Before("execution( * haru.web.controller.UserController.*(..)")
  @Before("haru.web.controller.UserController.*")
  public void logBefore(Method method, Object target, Object[] args) {
    logger.info("[aspect] log - Before");
  }

  @After("haru.web.controller.UserController.*")
  public void logAfter(Method method, Object target, Object[] args) {
    logger.info("[aspect] log - After");
  }

  @Around("haru.web.controller.UserController.*")
  public void logAround(Method method, Object target, Object[] args) {
    logger.info("[aspect] log - Around");
  }
}