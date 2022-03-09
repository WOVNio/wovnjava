package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

public class TimeUtilsTest extends TestCase {
  public void testRoundDownTime() {
    assertEquals(0, TimeUtils.roundDownTime(0, 10));
    assertEquals(10, TimeUtils.roundDownTime(16, 10));
    assertEquals(10, TimeUtils.roundDownTime(10, 10));
    assertEquals(30, TimeUtils.roundDownTime(30, 15));
  }
}
