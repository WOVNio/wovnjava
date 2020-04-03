package com.github.wovnio.wovnjava;

class ApiException extends Exception {
    private String type;
    private String details;
    private String errorBody;

    ApiException(String type, String details) {
        super(type + " : " + details);
        this.type = type;
        this.details = details;
    }

    ApiException(String type, String details, String errorBody) {
        super(type + " : " + details);
        this.type = type;
        this.details = details;
        this.errorBody = errorBody;
    }

    String getType() {
        return this.type;
    }

    String getDetails() {
        return this.details;
    }

    String getErrorBody() {
        return this.errorBody;
    }
}
