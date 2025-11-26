package com.web.aop;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import haru.annotation.aop.After;
import haru.annotation.aop.Around;
import haru.annotation.aop.Aspect;
import haru.annotation.aop.Before;
import haru.logging.MiniLogger;

@Aspect
public class SessionCheck {
  
  private static final Logger logger = MiniLogger.getLogger(SessionCheck.class.getSimpleName());
  
  @Before("com.web.controller.LoginController.*")
  public void logBefore(Method method, Object target, Object[] args) {
    logger.info("Before");
  }

  @After("com.web.controller.LoginController.*")
  public void logAfter(Method method, Object target, Object[] args) {
    logger.info("After");
  }

  @Around("com.web.controller.LoginController.*")
  public void logAround(Method method, Object target, Object[] args) {
    logger.info("Around");
  }
}