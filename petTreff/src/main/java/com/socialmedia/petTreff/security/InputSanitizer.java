package com.socialmedia.petTreff.security;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import java.net.URI;

public final class InputSanitizer {
    private InputSanitizer() {
    }

    // für einfache Texteingaben
    public static String sanitizePlain(String input) {
        return input == null ? null : Jsoup.clean(input, Safelist.none());
    }

    // für HTML-Inhalte
    public static String sanitizeHtml(String input) {
        return input == null ? null : Jsoup.clean(input, Safelist.basic());
    }

    // Für URLs
    public static String sanitizeUrl(String input) {
        if (input == null || input.isBlank())
            return null;
        try {
            URI uri = new URI(input.trim());
            String scheme = uri.getScheme();
            if (scheme == null ||
                    !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException("Only http/https URLs are allowed");
            }
            return uri.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL: " + input);
        }
    }
}
