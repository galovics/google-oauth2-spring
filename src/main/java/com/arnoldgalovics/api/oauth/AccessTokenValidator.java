package com.arnoldgalovics.api.oauth;

public interface AccessTokenValidator {
    AccessTokenValidationResult validate(String accessToken);
}
