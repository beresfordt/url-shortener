package com.github.beresfordt.urlshortener.utils.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.http.ResponseEntity;

public class ResponseEntityMatchers {

    public static Matcher<? super ResponseEntity<String>> hasBody(final Matcher<String> matcher) {
        return new TypeSafeMatcher<ResponseEntity<String>>() {
            @Override
            protected boolean matchesSafely(ResponseEntity<String> actual) {
                return matcher.matches(actual.getBody());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Matches response body");
            }
        };
    }

    public static Matcher<? super ResponseEntity<String>> hasStatus(final Integer statusCode) {
        return new TypeSafeMatcher<ResponseEntity<String>>() {
            @Override
            protected boolean matchesSafely(ResponseEntity<String> actual) {
                return statusCode.equals(actual.getStatusCode().value());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Matches response status code");
            }
        };
    }
}
