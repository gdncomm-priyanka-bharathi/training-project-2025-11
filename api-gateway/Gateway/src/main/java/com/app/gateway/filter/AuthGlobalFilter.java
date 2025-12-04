package com.app.gateway.filter;

import com.app.gateway.security.JwtUtils;
import com.app.gateway.security.TokenStore;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private final JwtUtils jwtUtils;
    private final TokenStore tokenStore;
    private final String jwtHeader;
    private final String jwtPrefix;

    public AuthGlobalFilter(JwtUtils jwtUtils,
                            TokenStore tokenStore,
                            @Value("${jwt.header}") String jwtHeader,
                            @Value("${jwt.prefix}") String jwtPrefix) {
        this.jwtUtils = jwtUtils;
        this.tokenStore = tokenStore;
        this.jwtHeader = jwtHeader;
        this.jwtPrefix = jwtPrefix;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Public paths
        if (path.startsWith("/members/register") || path.startsWith("/members/login")) {
            return chain.filter(exchange);
        }

        // Logout path – blacklist token and forward
        if (path.startsWith("/members/logout")) {
            String token = resolveToken(exchange);
            if (token != null) {
                Jws<Claims> jws = jwtUtils.parseToken(token);
                String jti = (String) jws.getBody().get("jti");
                long ttlSeconds = (jws.getBody().getExpiration().getTime() - System.currentTimeMillis()) / 1000;
                tokenStore.blacklistToken(jti, ttlSeconds);
            }
            return chain.filter(exchange);
        }

        // For protected routes – validate token
        String token = resolveToken(exchange);
        if (token == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            Jws<Claims> jws = jwtUtils.parseToken(token);
            String jti = (String) jws.getBody().get("jti");
            if (tokenStore.isBlacklisted(jti) || jwtUtils.isExpired(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String userId = jws.getBody().getSubject();

            // Propagate userId
            ServerWebExchange mutated = exchange.mutate()
                    .request(r -> r.headers(h -> h.add("X-User-Id", userId)))
                    .build();

            return chain.filter(mutated);

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private String resolveToken(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst(jwtHeader);
        if (header != null && header.startsWith(jwtPrefix)) {
            return header.substring(jwtPrefix.length()).trim();
        }
        return null;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
