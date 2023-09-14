package com.convio.client.utils;

import java.util.Date;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.convio.client.dto.TokenResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	// Configuration for cloud
	
	public static final String jwtSecret = System.getenv("JWT_SECRET_KEY");
	public static final String jwtRefreshTokenSecret = System.getenv("JWT_REFRESH_SECRET_KEY");
	public static final int jwtExpiration = System.getenv("JWT_TOKEN_EXPIRATION_IN_SECONDS") != null ? Integer.parseInt(System.getenv("JWT_TOKEN_EXPIRATION_IN_SECONDS")): 0;
	public static final int jwtRefreshTokenExpiration = System.getenv("JWT_REFRESH_TOKEN_EXPIRATION_IN_SECONDS") != null ? Integer.parseInt(System.getenv("JWT_REFRESH_TOKEN_EXPIRATION_IN_SECONDS")):0;
	
	
	// Configuration for local
	
//	public static final String jwtSecret = "mySecret";
//	public static final String jwtRefreshTokenSecret = "myRefreshSecret";
//	public static final int jwtExpiration = 30000;
//	public static final int jwtRefreshTokenExpiration = 300000;
	
	
	public TokenResponse generateJwtToken(Authentication authentication) {
		TokenResponse tokenResponse = new TokenResponse();
		String userPrincipal = authentication.getPrincipal().toString();
		
		Date expirationDate = new Date((new Date()).getTime() + jwtExpiration * 1000);
		Date refreshTokenExpirationDate = new Date((new Date()).getTime() + jwtRefreshTokenExpiration * 1000);
		
		String token=getAccessToken(userPrincipal, expirationDate);
		String refreshToken = getRefreshToken(userPrincipal, refreshTokenExpirationDate);
		tokenResponse.setAccessToken(token);
		tokenResponse.setExpiresIn(expirationDate.getTime());
		tokenResponse.setRefreshToken(refreshToken);
		tokenResponse.setRefreshTokenExpiresIn(refreshTokenExpirationDate.getTime());
		
		return tokenResponse;
	}

	public String getAccessToken(String userPrincipal,Date expirationDate) {
		return Jwts.builder()
				.setSubject((userPrincipal))
				.setIssuedAt(new Date())
				.setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
		
	}
	
	public String getRefreshToken(String userPrincipal,Date refreshTokenExpirationDate) {
	 return Jwts.builder()
		.setSubject((userPrincipal))
		.setIssuedAt(new Date())
		.setExpiration(refreshTokenExpirationDate)
		.signWith(SignatureAlgorithm.HS512, jwtRefreshTokenSecret)
		.compact();
	}
	
	public TokenResponse refreshToken(String token) {
		TokenResponse tokenResponse = new TokenResponse();
		Date createdAt = new Date();
		Date expirationDate = new Date((createdAt).getTime() + jwtExpiration * 1000);
		Date refreshTokenExpirationDate = new Date((createdAt).getTime() + jwtRefreshTokenExpiration * 1000);
		
		Claims claims = getAllClaimsFromToken(token,"refresh");
		claims.setIssuedAt(createdAt);
		claims.setExpiration(expirationDate);
		
		token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
		
		String refreshToken = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, jwtRefreshTokenSecret).compact();

		tokenResponse.setAccessToken(token);
		tokenResponse.setExpiresIn(expirationDate.getTime());
		tokenResponse.setRefreshToken(refreshToken);
		tokenResponse.setRefreshTokenExpiresIn(refreshTokenExpirationDate.getTime());
		
		return tokenResponse;
	}
	
	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}
	
	public Boolean canTokenBeRefreshed(String token,String keyType) {
		return (!isTokenExpired(token,keyType) || ignoreTokenExpiration(token));
	}

	private Boolean isTokenExpired(String token,String keyType) {
		final Date expiration = getExpirationDateFromToken(token,keyType);
		return expiration.before(new Date());
	}

	public Date getExpirationDateFromToken(String token, String keyType) {
		return getClaimFromToken(token, Claims::getExpiration,keyType);
	}
	
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver, String keyType) {
	    final Claims claims = getAllClaimsFromToken(token,keyType);
	    return claimsResolver.apply(claims);
	  }

	private Claims getAllClaimsFromToken(String token,String keyType) {
		if("refresh".equals(keyType)) {
			return Jwts.parser().setSigningKey(jwtRefreshTokenSecret).parseClaimsJws(token).getBody();
		}else {
			return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();	
		}
		
	}
	
	private Boolean ignoreTokenExpiration(String token) {
		// here you specify tokens, for that the expiration is ignored
		return false;
	}
	
	public String getUsernameFromToken(String token,String keyType) {
		return getClaimFromToken(token, Claims::getSubject,keyType);
	}

	public boolean validateJwtToken(String authToken) throws Exception {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
			throw new SignatureException("Invalid JWT signature");
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
			throw new MalformedJwtException("Invalid JWT token");
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
			throw new ExpiredJwtException(null,null,"JWT token is expired");
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
			throw new UnsupportedJwtException("JWT token is unsupported");
		} catch (Exception e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}
	
	public boolean validateJwtRefreshToken(String refreshToken) throws Exception {
		try {
			Jwts.parser().setSigningKey(jwtRefreshTokenSecret).parseClaimsJws(refreshToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
			throw new SignatureException("Invalid JWT signature in refresh token");
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
			throw new MalformedJwtException("Invalid JWT refresh token");
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
			throw new ExpiredJwtException(null,null,"JWT refresh token is expired");
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
			throw new UnsupportedJwtException("JWT refresh token is unsupported");
		} catch (Exception e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}
}
