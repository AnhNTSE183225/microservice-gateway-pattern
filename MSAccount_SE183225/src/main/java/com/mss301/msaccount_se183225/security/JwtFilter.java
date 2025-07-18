package com.mss301.msaccount_se183225.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mss301.msaccount_se183225.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AntPathMatcher antPathMatcher;
    private final ObjectMapper objectMapper;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JwtFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            AntPathMatcher antPathMatcher,
            ObjectMapper objectMapper,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.antPathMatcher = antPathMatcher;
        this.objectMapper = objectMapper;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    private final List<String> PUBLIC_PATHS = List.of(
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api/auth/**"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // SWAGGER redirection
            if (request.getServletPath().equals("/")) {
                response.sendRedirect(request.getContextPath() + "/swagger-ui/index.html");
                return;
            }
            // PUBLIC path checking
            final String authorizationHeader = request.getHeader(AUTHORIZATION);
            final String jwt;
            final String username;
            if (isPublicPath(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Missing required token");
            }
            jwt = authorizationHeader.substring(7);
            username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new UnauthorizedException("Invalid token");
                }
            } else {
                throw new UnauthorizedException("Invalid token");
            }
        } catch (Exception e) {
            // buildResponse(response, "Unauthorized: " + e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    @SuppressWarnings("unused")
    private void buildResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> apiResponse = Map.of(
                "response", HttpStatus.UNAUTHORIZED.value(),
                "error", message
        );

        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    private boolean isPublicPath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();

        String pathToMatch;
        if (contextPath != null && !contextPath.isEmpty() && requestURI.startsWith(contextPath)) {
            pathToMatch = requestURI.substring(contextPath.length());
        } else {
            pathToMatch = requestURI;
        }

        return PUBLIC_PATHS.stream().anyMatch(pattern -> antPathMatcher.match(pattern, pathToMatch));
    }
}
