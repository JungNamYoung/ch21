package com.web.log;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import haru.annotation.aop.After;
import haru.annotation.aop.Around;
import haru.annotation.aop.Aspect;
import haru.annotation.aop.Before;
import haru.logging.LoggerManager;

@Aspect
public class TimeCheck {
  
  Logger logger = LoggerManager.getLogger(this.getClass().getSimpleName());
  
  @Before("com.web.controller.JobController.*")
  public void logBefore(Method method, Object target, Object[] args) {
    logger.info("[time-check] Before");
  }

  @After("com.web.controller.JobController.*")
  public void logAfter(Method method, Object target, Object[] args) {
    logger.info("[time-check] After");
  }

  @Around("com.web.controller.JobController.*")
  public void logAround(Method method, Object target, Object[] args) {
    logger.info("[time-check] Around");
  }
}