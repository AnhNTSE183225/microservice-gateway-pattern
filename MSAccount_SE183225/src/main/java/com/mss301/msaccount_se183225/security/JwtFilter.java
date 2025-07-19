package com.mss301.msaccount_se183225.security;

import com.mss301.msaccount_se183225.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
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

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AntPathMatcher antPathMatcher;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JwtFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            AntPathMatcher antPathMatcher,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.antPathMatcher = antPathMatcher;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    private final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/**"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
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
