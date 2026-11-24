package org.example.backend.exception;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

// Parameters are non-null unless marked @Nullable, like in Spring's ResponseEntityExceptionHandler.
// The texts come from messages.properties (German) or messages_en.properties (English), depending on the
// Accept-Language header of the request; without the header they are German (spring.web.locale).
@NullMarked
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Errors of the services, e.g. "Reise nicht gefunden." with status 404
    @ExceptionHandler(LocalizedException.class)
    public ProblemDetail handleLocalized(LocalizedException ex) {
        return ProblemDetail.forStatusAndDetail(ex.getStatusCode(), text(ex.getMessageKey()));
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            // Bean Validation already looked up the text in the user's language;
            // the fallback is only there because the API allows null
            String message = Objects.requireNonNullElse(fieldError.getDefaultMessage(), text("error.invalidValue"));
            errors.putIfAbsent(fieldError.getField(), message);
        }

        ProblemDetail problem = ex.getBody();
        problem.setDetail(text("error.invalidInput"));
        problem.setProperty("errors", errors);
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    // JSON that cannot be read, e.g. an unknown tag or a wrong date format
    @Override
    protected @Nullable ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, text("error.unreadableRequest"));
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    // The text of a key in the language of the current request
    private String text(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
