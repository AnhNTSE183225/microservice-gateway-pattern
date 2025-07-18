package com.mss301.msbrand_se183225.externalsecurity;


import lombok.Builder;

@Builder
public record CustomPrincipal(
        String username,
        String role,
        String token
) {
}
