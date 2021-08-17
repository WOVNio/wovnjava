package com.github.wovnio.wovnjava;

public class ApiNoPageDataException extends Exception {
    private String details;

    ApiNoPageDataException(String details) {
        super(details);
        this.details = details;
    }

    public String getDetails() {
        return this.details;
    }
}
