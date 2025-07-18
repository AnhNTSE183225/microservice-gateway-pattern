package com.mss301.msaccount_se183225.authentication;

import com.mss301.msaccount_se183225.exception.BadRequestException;
import com.mss301.msaccount_se183225.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthDTO.LoginResponse login(AuthDTO.LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        HashMap<String, Object> claims = new HashMap<>();
        User user;
        Object principal = authentication.getPrincipal();
        user = (User) principal;
        claims.put("username", user.getUsername());
        String jwtToken = jwtService.generateToken(user, claims);
        return AuthDTO.LoginResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    @Override
    @Transactional
    public void register(AuthDTO.RegisterRequest request) {
        if (userRepository.findAuthentication(request.email()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }
        int nextInt = ((int) userRepository.count()) + 1;
        User user = User.builder()
                .id(nextInt)
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(4)
                .build();
        userRepository.save(user);
    }

    @Override
    public AuthDTO.ValidationResponse validate(AuthDTO.ValidationRequest request) {
        try {
            String username = jwtService.extractUsername(request.token());
            User user = userRepository.findAuthentication(username).orElse(null);

            if (user != null && jwtService.isTokenValid(request.token(), user)) {
                return AuthDTO.ValidationResponse.builder()
                        .valid(true)
                        .username(username)
                        .role(user.getRoleName())
                        .build();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return AuthDTO.ValidationResponse.builder()
                .valid(false)
                .build();
    }

}
