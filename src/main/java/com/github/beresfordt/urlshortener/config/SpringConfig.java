package com.github.beresfordt.urlshortener.config;

import com.github.beresfordt.urlshortener.utils.MurMur3_32;
import com.github.beresfordt.urlshortener.utils.UrlHasher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisShardInfo;

@Configuration
public class SpringConfig {

    @Bean
    public UrlHasher urlHasher() {
        return new MurMur3_32();
    }

    @Bean
    public String applicationBaseUrl() {
        return System.getProperty("base.url");
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate(jedisConnFactory());
    }

    private JedisConnectionFactory jedisConnFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setUsePool(true);
        jedisConnectionFactory.setHostName("127.0.0.1");
        jedisConnectionFactory.setShardInfo(new JedisShardInfo("127.0.0.1"));
        return jedisConnectionFactory;
    }
}
