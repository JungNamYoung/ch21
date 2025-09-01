package com.web.log;

import java.lang.reflect.Method;

import haru.annotation.aop.After;
import haru.annotation.aop.Around;
import haru.annotation.aop.Before;

//@Aspect
public class Session {
  @Before("haru.web.controller.MyController.*")
  public void logBefore(Method method, Object target, Object[] args) {
    System.out.println("[session] Before | ");
  }

  @After("haru.web.controller.MyController.*")
  public void logAfter(Method method, Object target, Object[] args) {
    System.out.println("[session] After | ");
  }

  @Around("haru.web.controller.MyController.*")
  public void logAround(Method method, Object target, Object[] args) {
    System.out.println("[session] Around | ");
  }
}