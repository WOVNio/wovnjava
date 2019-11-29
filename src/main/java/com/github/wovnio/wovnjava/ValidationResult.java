package com.github.wovnio.wovnjava;

class ValidationResult {
    public final boolean success;
    public final String errorMessage;

    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult failure(String errorMessage) {
        return new ValidationResult(false, errorMessage);
    }

    private ValidationResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }
}
