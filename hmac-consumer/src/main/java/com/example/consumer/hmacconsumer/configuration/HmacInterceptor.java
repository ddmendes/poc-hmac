package com.example.consumer.hmacconsumer.configuration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;

import feign.Logger;
import feign.Logger.Level;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.tomcat.util.buf.HexUtils;

@Slf4j
@Configuration
public class HmacInterceptor implements RequestInterceptor {

    private static final String HMAC_TOKEN_TYPE = "HMAC";

    @Value("${credentials.hmac-key}")
    private String hmacKey;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String hmacBaseMessage = new StringBuilder()
            .append(requestTemplate.method())
            .append("\n")
            .append(requestTemplate.headers().get(HttpHeaders.CONTENT_TYPE).stream().findAny().get())
//            .append("\n")
//            .append(requestTemplate.headers().get(HttpHeaders.DATE).stream().findAny().get())
            .toString();

        log.debug("HMAC message: " + hmacBaseMessage);

        String hmacToken;
        SecretKeySpec keySpec = new SecretKeySpec(hmacKey.getBytes(StandardCharsets.UTF_8), "SHA-512");
        try {
            Mac mac = Mac.getInstance(HmacAlgorithms.HMAC_SHA_512.toString());
            mac.init(keySpec);
            hmacToken = HexUtils.toHexString(mac.doFinal(hmacBaseMessage.getBytes(StandardCharsets.UTF_8)));
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            throw new AuthenticationServiceException("Could not calculate HMAC");
        }

        StringBuilder authorization = new StringBuilder();
        authorization.append(HMAC_TOKEN_TYPE).append(" ").append(hmacToken);
        log.debug("HMAC token " + authorization.toString());
        requestTemplate.header(HttpHeaders.AUTHORIZATION, authorization.toString());
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Level.FULL;
    }
}
