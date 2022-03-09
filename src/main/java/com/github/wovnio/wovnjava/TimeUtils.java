package com.github.wovnio.wovnjava;

public class TimeUtils {
  public static long roundDownTime(long time, long unitTime) {
    return time - (time % unitTime);
  }
}
