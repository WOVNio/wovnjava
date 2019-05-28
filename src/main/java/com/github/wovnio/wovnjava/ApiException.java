package com.github.wovnio.wovnjava;

class ApiException extends Exception {
    private String type;
    private String details;

    ApiException(String type, String details) {
        super(type + " : " + details);
        this.type = type;
        this.details = details;
    }

    String getType() {
        return this.type;
    }

    String getDetails() {
        return this.details;
    }
}
