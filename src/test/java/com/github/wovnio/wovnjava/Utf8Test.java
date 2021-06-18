package com.github.wovnio.wovnjava;

import static org.junit.Assert.assertNotEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Ignore;

import junit.framework.TestCase;

public class Utf8Test extends TestCase {
    public void testDefaultEncodingUTF8() {
        Utf8 unicodeConverter = new Utf8("");
        String data = new String("Unicøde Sentence　東京 الخط العربي في يونيكود");
        byte[] rawData = null;
        try {
            rawData = data.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            System.exit(1);
        } 
        assertEquals(data, unicodeConverter.toStringUtf8(rawData));
    }

    public void testDefaultEncodingShiftJIS() {
        Utf8 unicodeConverter = new Utf8("");
        String data = new String("新字体 関	鉄 気 １２３");
        byte[] rawData = null;
        try {
            rawData = data.getBytes("Shift_JIS");
        } catch (UnsupportedEncodingException e) {
            System.exit(1);
        } 
        assertEquals(data, unicodeConverter.toStringUtf8(rawData));
    }


    public void testDefaultEncodingEUCJP__expected_to_fail() {
        Utf8 unicodeConverter = new Utf8("");
        String data = new String("新字体 関	鉄 気１２３");
        byte[] rawData = null;
        try {
            rawData = data.getBytes("EUC-JP");
        } catch (UnsupportedEncodingException e) {
            System.exit(1);
        } 
        assertNotEquals(data, unicodeConverter.toStringUtf8(rawData));
    }

    public void testWrongEncodingProvidedShouldNotDecode() {
        Utf8 unicodeConverter = new Utf8("Shift_JIS");
        String data = new String("Unicøde Sentence　東京");
        byte[] rawData = null;
        try {
            rawData = data.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            System.exit(1);
        } 
        assertNotEquals(data, unicodeConverter.toStringUtf8(rawData));
    }

    public void testSpecifyEncoding() {
        Utf8 unicodeConverter = new Utf8("EUC-JP");
        String data = new String("Unicøde Sentence　東京");
        byte[] rawData = null;
        try {
            rawData = data.getBytes("EUC-JP");
        } catch (UnsupportedEncodingException e) {
            System.exit(1);
        } 
        assertEquals(data, unicodeConverter.toStringUtf8(rawData));
    }
}
