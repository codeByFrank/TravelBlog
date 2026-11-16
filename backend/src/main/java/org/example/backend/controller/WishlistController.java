package org.example.backend.controller;

import jakarta.validation.Valid;
import org.example.backend.dto.WishlistEntryRequest;
import org.example.backend.dto.WishlistEntryResponse;
import org.example.backend.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The HTTP endpoints of the wishlist.
 * The controller only translates between HTTP and the service; the logic is in WishlistService.
 * The userId comes from the login token (see JwtFilter), never from the request body.
 */
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public List<WishlistEntryResponse> getAllEntries(@AuthenticationPrincipal String userId) {
        return wishlistService.getAllEntries(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WishlistEntryResponse addEntry(@AuthenticationPrincipal String userId,
                                          @Valid @RequestBody WishlistEntryRequest request) {
        return wishlistService.addEntry(userId, request);
    }

    @PutMapping("/{id}")
    public WishlistEntryResponse updateEntry(@AuthenticationPrincipal String userId,
                                             @PathVariable String id,
                                             @Valid @RequestBody WishlistEntryRequest request) {
        return wishlistService.updateEntry(userId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@AuthenticationPrincipal String userId, @PathVariable String id) {
        wishlistService.deleteEntry(userId, id);
    }

    /**
     * Answers with 404 when an entry does not exist or belongs to another user.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail handleNotFound(NoSuchElementException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * Answers with 400 when the request body breaks a validation rule.
     * The "errors" object tells the frontend which field is wrong: field name → message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationError(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }
}
