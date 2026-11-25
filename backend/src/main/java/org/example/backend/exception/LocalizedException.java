package org.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * An error the frontend shows to the user, e.g. "Reise nicht gefunden." / "Trip not found.".
 * It carries the key of the text in messages.properties instead of the text itself;
 * GlobalExceptionHandler looks up the text in the user's language (Accept-Language header).
 * It is a ResponseStatusException, so the status is set like before, e.g. 404.
 */
public class LocalizedException extends ResponseStatusException {

    private final String messageKey;

    public LocalizedException(HttpStatus status, String messageKey) {
        super(status, messageKey);
        this.messageKey = messageKey;
    }

    // For errors caused by another exception, e.g. Wikipedia could not be reached
    public LocalizedException(HttpStatus status, String messageKey, Throwable cause) {
        super(status, messageKey, cause);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
