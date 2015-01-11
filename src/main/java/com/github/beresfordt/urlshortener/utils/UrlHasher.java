package com.github.beresfordt.urlshortener.utils;

@FunctionalInterface
public interface UrlHasher {
    String hashUrl(String url);
}
