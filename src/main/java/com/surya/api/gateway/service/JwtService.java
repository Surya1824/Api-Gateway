package com.surya.api.gateway.service;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    public boolean validateToken(String jwtToken) {
        try {
            Date expDate = extractClaims(jwtToken, Claims::getExpiration); // Validates & parses
            logger.info("Expiration Date: {}", expDate);
            return expDate.after(new Date());
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

	public <T> T extractClaims(String jwtToken, Function<Claims, T> claimsResolver) {
		Claims claims = Jwts.parser()
		    .verifyWith(generateKey())
		    .build()
		    .parseSignedClaims(jwtToken).getPayload();
		logger.info("Claims from Token: {}", claims.toString());
		return claimsResolver.apply(claims);
	}

    private SecretKey generateKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}