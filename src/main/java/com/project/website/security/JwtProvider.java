package com.project.website.security;
import static java.util.Date.from;
import java.io.InputStream;
import static io.jsonwebtoken.Jwts.parser;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.project.website.exceptions.RecipeException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.IOException;

@Service
public class JwtProvider {
	private KeyStore keyStore;
	 @Value("${jwt.expiration.time}")
	    private Long jwtExpirationInMillis;


	@PostConstruct
	public void init() {
		try {
			keyStore = KeyStore.getInstance("JKS");
			InputStream resourceAsStream = getClass().getResourceAsStream("/Recipe.jks");
			keyStore.load(resourceAsStream, "secret".toCharArray());
		} catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException
				| java.io.IOException e) {
			throw new RecipeException("Exception occurred while loading keystore");
		}

	}

	public String generateToken(Authentication authentication) {
		User principal = (User) authentication.getPrincipal();
		return Jwts.builder().setSubject(principal.getUsername()).signWith(getPrivateKey()).setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis))).compact();
	}
    public String generateTokenWithUserName(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }
	private PrivateKey getPrivateKey() {
		try {
			return (PrivateKey) keyStore.getKey("Recipe", "secret".toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			throw new RecipeException("Exception occured while retrieving public key from keystore");
		}
	}

	public boolean validateToken(String jwt) {
		parser().setSigningKey(getPublickey()).parseClaimsJws(jwt);
		return true;
	}

	private PublicKey getPublickey() {
		try {
			return keyStore.getCertificate("Recipe").getPublicKey();
		} catch (KeyStoreException e) {
			throw new RecipeException("Exception occured while " + "retrieving public key from keystore");
		}
	}

	public String getUsernameFromJwt(String token) {
		Claims claims = parser().setSigningKey(getPublickey()).parseClaimsJws(token).getBody();

		return claims.getSubject();
	}
	   public Long getJwtExpirationInMillis() {
	        return jwtExpirationInMillis;
	    }
}
