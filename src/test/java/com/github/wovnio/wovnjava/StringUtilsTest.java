package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {
  public void testEncodeHexString() {
    byte[] data = new byte[] { (byte) 0xe0, 0x4f, (byte) 0xd0, 0x20, };
    assertEquals("e04fd020", StringUtils.encodeHexString(data));

    data = new byte[] { (byte) 0x00, 0x00, (byte) 0x00, 0x00, };
    assertEquals("00000000", StringUtils.encodeHexString(data));
  }
}
