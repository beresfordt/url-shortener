package com.github.beresfordt.urlshortener.web;

import com.github.beresfordt.urlshortener.utils.UrlHasher;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class UrlShortenerController {

    private final StringRedisTemplate stringRedisTemplate;
    private final UrlHasher urlHasher;
    private final String applicationBaseUrl;

    @Autowired
    public UrlShortenerController(StringRedisTemplate stringRedisTemplate, UrlHasher urlHasher, String applicationBaseUrl) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.urlHasher = urlHasher;
        this.applicationBaseUrl = applicationBaseUrl;
    }

    @RequestMapping(value = "/{id}", method = GET)
    public void redirect(@PathVariable String id, HttpServletResponse response) throws IOException {
        final String url = stringRedisTemplate.opsForValue().get(id);

        if (url != null) {
            response.sendRedirect(url);
        }
        else {
            response.sendError(SC_NOT_FOUND);
        }
    }

    @RequestMapping(method = POST)
    public ResponseEntity<String> save(HttpServletRequest request) {
        final String queryParameters = (request.getQueryString() != null) ? "?" + request.getQueryString() : "";
        final String url = (request.getRequestURI() + queryParameters).substring(1);

        final UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});

        if (urlValidator.isValid(url)) {
            final String id = urlHasher.hashUrl(url);
            stringRedisTemplate.opsForValue().set(id, url);
            return new ResponseEntity<String>(applicationBaseUrl + "/" + id, OK);
        }
        else {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
}
