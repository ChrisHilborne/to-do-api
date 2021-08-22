package com.chilborne.todoapi;

import java.time.LocalDateTime;

public class TestVariables {

  public static String USERNAME = "this is a username";
  public static String PASSWORD = "this is a password";
  public static String EMAIL = "this.is@an.email";

  public static long LIST_ID = 100L;
  public static String LIST_NAME = "this is a to-do list";
  public static String LIST_DESC = "this describes a to-do list";
  public static boolean LIST_ACTIVE = true;
  public static LocalDateTime LIST_DATE_MADE = 
    LocalDateTime.of(1999, 12, 31, 23, 59).withNano(0);

  public static long TASK_ID = 50L;
  public static String TASK_NAME = "this is a task";
  public static String TASK_DESC = "this describes a task";
  public static boolean TASK_ACTIVE = true;
  public static LocalDateTime TASK_DATE_MADE = 
    LocalDateTime.of(2000, 1, 1, 0, 0).withNano(0);
  public static LocalDateTime TASK_DATE_FINISHED = 
    LocalDateTime.of(2000, 1, 1, 0, 1).withNano(0);
}
