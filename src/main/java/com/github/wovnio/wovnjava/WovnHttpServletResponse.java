package com.github.wovnio.wovnjava;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

class WovnHttpServletResponse extends HttpServletResponseWrapper {
    int status;

    private ByteArrayOutputStream buff;
    private PrintWriter writer;
    private ServletOutputStream output;
    private Headers headers;

    WovnHttpServletResponse(HttpServletResponse response, Headers headers) {
        super(response);
        this.buff = new ByteArrayOutputStream();
        this.headers = headers;
    }

    byte[] getData() {
        if (this.writer != null) {
            this.writer.close();
        }
        if (this.output != null) {
            try {
                this.output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] ret = buff.toByteArray();

        this.buff = new ByteArrayOutputStream();
        this.output = null;
        this.writer = null;

        return ret;
    }

    @Override
    public String toString() {
        return Utf8.toStringUtf8(this.getData());
    }

    @Override
    public void setStatus(int sc) {
        status = sc;
        super.setStatus(sc);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.output == null)
            this.output = new FilterServletOutputStream(this.buff);

        return this.output;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (this.writer == null) {
            this.writer = new PrintWriter(
                    new OutputStreamWriter(this.getOutputStream(), this.getCharacterEncoding()),
                    true
            );
        }
        return this.writer;
    }

    @Override
    public void sendRedirect(String location) throws java.io.IOException {
        super.sendRedirect(headers.convertToAbsoluteUrlForCurrentLanguage(location));
    }

    @Override
    public void setHeader(String name, String value) {
        if (name.toLowerCase() == "location") {
            value = headers.convertToAbsoluteUrlForCurrentLanguage(value);
        }
        super.setHeader(name, value);
    }

    @Override
    public void flushBuffer() throws IOException {
        flush();

        // Calling `super.flushBuffer` may lead to response already being committed.
        // This prevents us from, among other things, changing the HTTP content-length.
        // flushBuffer for that purpose is disabled by default
        // See: https://jira.terracotta.org/jira/si/jira.issueviews:issue-html/EHC-447/EHC-447.html
        if (this.headers.settings.enableFlushBuffer) {
          super.flushBuffer();
        }
    }

    public void flush() throws IOException {
        if (writer != null) {
            writer.flush();
        }
        if (output != null) {
            output.flush();
        }
    }
}
