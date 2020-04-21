package com.github.wovnio.wovnjava;

import junit.framework.TestCase;

public class FileExtensionMatcherTest extends TestCase {
    private FileExtensionMatcher sut;

    protected void setUp() {
        this.sut = new FileExtensionMatcher();
    }

    public void testIsFile() {
        assertIsFile(false, "");
        assertIsFile(false, "/");
        assertIsFile(false, "html");
        assertIsFile(false, "jp");
        assertIsFile(true, "png");
        assertIsFile(true, "jpg");
        assertIsFile(true, "gif");
        assertIsFile(true, "mp3");
        assertIsFile(true, "mp4");
        assertIsFile(true, "zip");
        assertIsFile(true, "7zip");
        assertIsFile(true, "7z");
        assertIsFile(true, "gzip");
        assertIsFile(true, "tar");
        assertIsFile(true, "gz");
        assertIsFile(true, "rar");
        assertIsFile(true, "pdf");
        assertIsFile(true, "js");
        assertIsFile(true, "css");
        assertIsFile(true, "doc");
        assertIsFile(true, "docx");
        assertIsFile(true, "xls");
        assertIsFile(true, "xlsx");
        assertIsFile(true, "xlsm");
        assertIsFile(false, "unknown");
    }

    private void assertIsFile(boolean expect, String ext) {
        assertEquals(expect, this.sut.isFile("foo." + ext));
        assertEquals(expect, this.sut.isFile("/foo." + ext));
        assertEquals(expect, this.sut.isFile("/dir/foo." + ext));
        assertEquals(expect, this.sut.isFile("/dir/foo." + ext + "?query=1"));
        assertEquals(expect, this.sut.isFile("/dir/foo." + ext + "?query=file.html"));
        assertEquals(expect, this.sut.isFile("/dir/foo." + ext + "?query=file.png"));
        assertEquals(expect, this.sut.isFile("/dir/foo." + ext + "#hash"));
        assertEquals(expect, this.sut.isFile("/dir/foo." + ext + "#hash.html"));
        assertEquals(expect, this.sut.isFile("/dir/foo." + ext + "#hash.png"));
        assertEquals(expect, this.sut.isFile("/dir/foo." + ext + "#hash.png?query=file.png&upload=file.html"));
        assertEquals(false, this.sut.isFile("foo" + ext));
        assertEquals(false, this.sut.isFile("/foo." + ext + "unknown"));
        assertEquals(false, this.sut.isFile("/foo." + ext + "/"));
        assertEquals(expect, this.sut.isFile("/foo.html/bar." + ext));
        assertEquals(expect, this.sut.isFile("/foo.png/bar." + ext));
    }
}
