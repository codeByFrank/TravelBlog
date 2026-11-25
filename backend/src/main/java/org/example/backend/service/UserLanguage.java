package org.example.backend.service;

import java.util.Locale;

/**
 * The languages of the user interface: German (the default) and English.
 * The frontend sends the chosen language in the Accept-Language header; controllers get it as a Locale
 * and pass the language code to services that call external services in that language (Wikipedia, Nominatim).
 */
public final class UserLanguage {

    public static final String GERMAN = "de";
    public static final String ENGLISH = "en";

    private UserLanguage() {
    }

    // "en" for English, "de" for German and for every language the app does not have
    public static String of(Locale locale) {
        return ENGLISH.equals(locale.getLanguage()) ? ENGLISH : GERMAN;
    }
}
