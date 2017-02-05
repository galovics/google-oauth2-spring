package com.arnoldgalovics.api.oauth;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.Map;

import static java.util.Collections.singleton;

public class GoogleTokenServices implements ResourceServerTokenServices, InitializingBean {
    private String userInfoUrl;

    private RestTemplate restTemplate = new RestTemplate();
    private AccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
    private AccessTokenValidator tokenValidator;

    public GoogleTokenServices(AccessTokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(userInfoUrl, "userInfoUrl must not be blank");
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        AccessTokenValidationResult validationResult = tokenValidator.validate(accessToken);
        if (!validationResult.isValid()) {
            throw new UnapprovedClientAuthenticationException("The token is not intended to be used for this application.");
        }
        Map<String, ?> tokenInfo = validationResult.getTokenInfo();
        OAuth2Authentication authentication = getAuthentication(tokenInfo, accessToken);
        return authentication;
    }

    private OAuth2Authentication getAuthentication(Map<String, ?> tokenInfo, String accessToken) {
        OAuth2Request request = tokenConverter.extractAuthentication(tokenInfo).getOAuth2Request();
        Authentication authentication = getAuthenticationToken(accessToken);
        return new OAuth2Authentication(request, authentication);
    }

    private Authentication getAuthenticationToken(String accessToken) {
        Map<String, ?> userInfo = getUserInfo(accessToken);
        String idStr = (String) userInfo.get("id");
        if (idStr == null) {
            throw new InternalAuthenticationServiceException("Cannot get id from user info");
        }
        return new UsernamePasswordAuthenticationToken(new GooglePrincipal(new BigInteger(idStr)), null, singleton(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private Map<String, ?> getUserInfo(String accessToken) {
        HttpHeaders headers = getHttpHeaders(accessToken);
        Map map = restTemplate.exchange(userInfoUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class).getBody();
        return (Map<String, Object>) map;
    }

    private HttpHeaders getHttpHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }

    public void setUserInfoUrl(String userInfoUrl) {
        this.userInfoUrl = userInfoUrl;
    }
}
