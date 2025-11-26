package com.web.aop;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import haru.annotation.aop.After;
import haru.annotation.aop.Around;
import haru.annotation.aop.Aspect;
import haru.annotation.aop.Before;
import haru.logging.MiniLogger;

@Aspect
public class UserCheck {
  private static final Logger logger = MiniLogger.getLogger(UserCheck.class.getSimpleName());

  @Before("com.web.controller.UserController.*")
  public void logBefore(Method method, Object target, Object[] args) {
    logger.info("Before");
  }

  @After("com.web.controller.UserController.*")
  public void logAfter(Method method, Object target, Object[] args) {
    logger.info("After");
  }

  @Around("com.web.controller.UserController.*")
  public void logAround(Method method, Object target, Object[] args) {
    logger.info("Around");
  }
}