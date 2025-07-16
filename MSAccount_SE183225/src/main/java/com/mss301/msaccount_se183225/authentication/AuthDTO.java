package com.mss301.msaccount_se183225.authentication;

import lombok.Builder;

@Builder
public record AuthDTO() {

    @Builder
    public record LoginRequest(
            String username,
            String password
    ) {
    }

    @Builder
    public record LoginResponse(
            String accessToken
    ) {
    }

    @Builder
    public record RegisterRequest(
            String username,
            String email,
            String password
    ) {
    }

    @Builder
    public record ValidationRequest(
            String token
    ) {
    }

    @Builder
    public record ValidationResponse(
            boolean valid,
            String username,
            String role
    ) {
    }
}
