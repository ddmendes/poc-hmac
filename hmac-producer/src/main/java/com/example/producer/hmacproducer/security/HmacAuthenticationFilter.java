package com.example.producer.hmacproducer.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;

@Slf4j
public class HmacAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String HMAC_TOKEN_TYPE = "HMAC";

    @Value("${credentials.hmac-key}")
    private String hmacKey;

    public HmacAuthenticationFilter(String bindUrl) {
        super(bindUrl, new HmacAuthenticationManager());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException, IOException, ServletException {
        String requestHmac = getHmacDigest(request);
        log.debug("Request HMAC: " + requestHmac);
        String data = getHmacBaseMessage(request);
        log.debug("HMAC message: " + data);
        String expectedHmac = calculateHmac(data);
        log.debug("Expected HMAC: " + expectedHmac);
        Authentication token = new PreAuthenticatedAuthenticationToken(requestHmac,
            new Principal(requestHmac, expectedHmac),
            Collections.singleton(new SimpleGrantedAuthority("Jedi")));
        token = getAuthenticationManager().authenticate(token);
        return token;
    }

    private String getHmacDigest(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (ObjectUtils.isEmpty(authorization) || !StringUtils.startsWithIgnoreCase(authorization, HMAC_TOKEN_TYPE)) {
            throw new BadCredentialsException("Missing valid authorization");
        }
        return authorization.split(HMAC_TOKEN_TYPE)[1].strip();
    }

    private String getHmacBaseMessage(HttpServletRequest request) {
        String verb = request.getMethod();
        String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        String timestamp = request.getHeader(HttpHeaders.DATE);
        return new StringBuilder()
            .append(verb).append("\n")
            .append(contentType)
//            .append("\n")
//            .append(timestamp)
            .toString();
    }

    private String calculateHmac(String data) {
        SecretKeySpec keySpec = new SecretKeySpec(hmacKey.getBytes(StandardCharsets.UTF_8), "SHA-512");
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(keySpec);
            return HexUtils.toHexString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            throw new AuthenticationServiceException("Could not calculate HMAC");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    @Getter
    @AllArgsConstructor
    private static class Principal {
        private String requestHmac;
        private String expectedHmac;
    }

    private static class HmacAuthenticationManager implements AuthenticationManager {

        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            Principal principal = (Principal) authentication.getPrincipal();
            if (!principal.getExpectedHmac().equals(principal.getRequestHmac())) {
                throw new BadCredentialsException("Invalid token");
            }
            authentication.setAuthenticated(true);
            return authentication;
        }
    }
}
