package com.example.recipekeeper.scraper;

public class FailedToConnectException extends Exception {
    public FailedToConnectException() {
    }

    public FailedToConnectException(String message) {
        super(message);
    }
}
