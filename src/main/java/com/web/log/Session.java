package com.web.log;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import haru.annotation.aop.After;
import haru.annotation.aop.Around;
import haru.annotation.aop.Aspect;
import haru.annotation.aop.Before;
import haru.logging.MiniLogger;

@Aspect
public class Session {
  
  private static final Logger logger = MiniLogger.getLogger(Session.class.getSimpleName());
  
  @Before("com.web.controller.LoginController.*")
  public void logBefore(Method method, Object target, Object[] args) {
    logger.info("[session] Before");
  }

  @After("com.web.controller.LoginController.*")
  public void logAfter(Method method, Object target, Object[] args) {
    logger.info("[session] After");
  }

  @Around("com.web.controller.LoginController.*")
  public void logAround(Method method, Object target, Object[] args) {
    logger.info("[session] Around");
  }
}