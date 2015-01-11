package com.github.beresfordt.urlshortener.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class MurMur3_32 implements UrlHasher {
    @Override
    public String hashUrl(String url) {
        return Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
    }
}
