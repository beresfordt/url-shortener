package com.github.beresfordt.urlshortener.web;

import com.github.beresfordt.urlshortener.utils.UrlHasher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.github.beresfordt.urlshortener.utils.matchers.ResponseEntityMatchers.hasBody;
import static com.github.beresfordt.urlshortener.utils.matchers.ResponseEntityMatchers.hasStatus;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UrlShortenerControllerTest {

    private static final String BASE_URL = "http://urlshortener.com";

    private UrlHasher urlHasher;
    private UrlShortenerController underTest;
    private ValueOperations<String, String> valueOperations;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        urlHasher = mock(UrlHasher.class);

        underTest = new UrlShortenerController(stringRedisTemplate, urlHasher, BASE_URL);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void redirectIfIdIsKnown() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(valueOperations.get("1")).thenReturn("url");

        underTest.redirect("1", response);

        verify(response).sendRedirect("url");
    }

    @Test
    public void returnNotFoundIfIdIsNotKnown() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(valueOperations.get("1")).thenReturn(null);

        underTest.redirect("1", response);

        verify(response).sendError(404);
    }

    @Test
    public void saveUrlReturnsShortenedUrl() {
        final String uriToBeShortened = "http://www.example.com";

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getRequestURI()).thenReturn("/" + uriToBeShortened);
        when(urlHasher.hashUrl(uriToBeShortened)).thenReturn("hash");

        ResponseEntity<String> responseEntity = underTest.save(request);

        assertThat(responseEntity, allOf(hasStatus(200), hasBody(equalTo(BASE_URL + "/hash"))));
        verify(valueOperations).set("hash", uriToBeShortened);
    }

    @Test
    public void saveUrlReturnsShortenedUrlWithQueryParams() {
        final String uriToBeShortened = "http://www.example.com";
        final String queryParamsToBeShortened = "query=String";

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getQueryString()).thenReturn(queryParamsToBeShortened);
        when(request.getRequestURI()).thenReturn("/" + uriToBeShortened);
        when(urlHasher.hashUrl(uriToBeShortened + "?" + queryParamsToBeShortened)).thenReturn("hash");

        ResponseEntity<String> responseEntity = underTest.save(request);

        assertThat(responseEntity, allOf(hasStatus(200), hasBody(equalTo(BASE_URL + "/hash"))));
        verify(valueOperations).set("hash", uriToBeShortened + "?" + queryParamsToBeShortened);
    }

    @Test
    public void returnBadRequestForBadUri() {
        final String uriToBeShortened = "ptth://www.example.com";

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getRequestURI()).thenReturn("/" + uriToBeShortened);
        when(urlHasher.hashUrl(uriToBeShortened)).thenReturn("hash");

        ResponseEntity<String> responseEntity = underTest.save(request);

        assertThat(responseEntity, allOf(hasStatus(400)));
    }
}