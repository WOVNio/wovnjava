package com.github.wovnio.wovnjava;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.ServletOutputStream;

class FilterServletOutputStream extends ServletOutputStream {
    private OutputStream stream;

    FilterServletOutputStream(OutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void write(int b) throws IOException  {
        stream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException  {
        stream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException  {
        stream.write(b,off,len);
    }

    @Override
    public void close() throws java.io.IOException {
        stream.close();
    }

    @Override
    public void flush() throws IOException {
        stream.flush();
    }
}
