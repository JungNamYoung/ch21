package com.web.log;

import java.lang.reflect.Method;

import haru.annotation.aop.After;
import haru.annotation.aop.Around;
import haru.annotation.aop.Before;

//@Aspect
public class TimeCheck {
  @Before("haru.web.controller.MyController.*")
  public void logBefore(Method method, Object target, Object[] args) {
    System.out.println("[time-check] Before | ");
  }

  @After("haru.web.controller.MyController.*")
  public void logAfter(Method method, Object target, Object[] args) {
    System.out.println("[time-check] After | ");
  }

  @Around("haru.web.controller.MyController.*")
  public void logAround(Method method, Object target, Object[] args) {
    System.out.println("[time-check] Around | ");
  }
}