package com.mss301.msaccount_se183225.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(
            @RequestBody AuthDTO.LoginRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authenticationService.login(request));
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<?> register(
            @RequestBody AuthDTO.RegisterRequest request
    ) {
        authenticationService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/api/auth/validate")
    public ResponseEntity<?> validate(
            @RequestBody AuthDTO.ValidationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.validate(request));
    }
}
