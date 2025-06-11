package com.cts.onlineexamportall.service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtilService{
	
	private static final Logger logger = LogManager.getLogger(JwtUtilService.class);
	protected String secret_key = "";
	
	public JwtUtilService() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
			SecretKey sk = keyGen.generateKey();
			secret_key = Base64.getEncoder().encodeToString(sk.getEncoded());
			logger.info("Secret key generated successfully");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Error generating secret key: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	public String generateToken(UUID userId, String username) {
		logger.info("Generating token for userId: {} and username: {}", userId, username);
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", userId.toString());
		
		String token = Jwts.builder()
				.claims()
				.add(claims)
				.subject(username)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 60 * 60 * 6000))
				.and()
				.signWith(getKey())
				.compact();
		logger.info("Token generated successfully for username: {}", username);
		return token;
	}

	SecretKey getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secret_key);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	public String extractUserName(String token) {
		logger.info("Extracting username from token");
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUserId(String token) {
		logger.info("Extracting userId from token");
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
		logger.info("Extracting all claims from token");
        return Jwts.parser()
        		.verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
		logger.info("Validating token for username: {}", userDetails.getUsername());
        final String userName = extractUserName(token);
        boolean isValid = (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
		logger.info("Token validation result: {}", isValid);
        return isValid;
    }

    private boolean isTokenExpired(String token) {
		logger.info("Checking if token is expired");
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
		logger.info("Extracting expiration date from token");
        return extractClaim(token, Claims::getExpiration);
    }	
	
}