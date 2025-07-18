package com.mss301.msblindbox_se183225.externalsecurity;


import lombok.Builder;

@Builder
public record CustomPrincipal(
        String username,
        String role,
        String token
) {
}
