package com.mss301.msaccount_se183225.authentication;

public interface AuthenticationService {
    AuthDTO.LoginResponse login(AuthDTO.LoginRequest request);

    void register(AuthDTO.RegisterRequest request);

    AuthDTO.ValidationResponse validate(AuthDTO.ValidationRequest request);
}
