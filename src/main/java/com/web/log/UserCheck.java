package com.web.log;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import haru.annotation.aop.After;
import haru.annotation.aop.Around;
import haru.annotation.aop.Aspect;
import haru.annotation.aop.Before;
import haru.logging.LoggerManager;

@Aspect
public class UserCheck {
  private static final Logger logger = LoggerManager.getLogger(UserCheck.class.getSimpleName());

  @Before("com.web.controller.UserController.*")
  public void logBefore(Method method, Object target, Object[] args) {
    logger.info("[aspect] log - Before");
  }

  @After("com.web.controller.UserController.*")
  public void logAfter(Method method, Object target, Object[] args) {
    logger.info("[aspect] log - After");
  }

  @Around("com.web.controller.UserController.*")
  public void logAround(Method method, Object target, Object[] args) {
    logger.info("[aspect] log - Around");
  }
}