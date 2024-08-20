package com.example.recipekeeper.scraper;

public class WebsiteNotSupportedException extends Exception {
    public WebsiteNotSupportedException() {
    }

    public WebsiteNotSupportedException(String message) {
        super(message);
    }
}