package com.example.timeShare.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final ServerSecurityContextRepository securityContextRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, ServerSecurityContextRepository securityContextRepository) {
        this.jwtUtil = jwtUtil;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    String role = jwtUtil.getRoleFromToken(token);
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                    System.out.println("Tworzona autoryzacja dla użytkownika: " + username + " z rolą: " + role);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(username, null, List.of(authority));
                    SecurityContext context = new SecurityContextImpl(auth);

                    System.out.println("SecurityContext Authorities: " + context.getAuthentication().getAuthorities());

                    return this.securityContextRepository.save(exchange, context)
                            .then(chain.filter(exchange));
                } else {
                    System.err.println("Token nie jest prawidłowy");
                    return Mono.error(new RuntimeException("Token nie jest prawidłowy."));
                }
            } catch (Exception e) {
                System.err.println("Błąd podczas walidacji tokena: " + e.getMessage());
                return Mono.error(new RuntimeException("Błąd podczas walidacji tokena: " + e.getMessage()));
            }
        }

        return chain.filter(exchange);
    }
}
