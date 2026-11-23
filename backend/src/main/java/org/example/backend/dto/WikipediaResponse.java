package org.example.backend.dto;

/**
 * The beginning of the Wikipedia article about a place.
 *
 * @param title   title of the article, e.g. "Florenz"
 * @param extract the first sentences of the article as plain text
 * @param url     link to the whole article
 */
public record WikipediaResponse(String title, String extract, String url) {
}
