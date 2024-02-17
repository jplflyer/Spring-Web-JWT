package org.showpage.springwebjwt.config;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetals) {
        long now = System.currentTimeMillis();

        JwtBuilder builder = Jwts.builder();
        if (extraClaims != null) {
            builder.setClaims(extraClaims);
        }
        return builder
                .setSubject(userDetals.getUsername())
                .setIssuedAt( new Date(now) )
                .setExpiration( new Date(now + 60000 * 60) ) // 1 hour
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * This extracts a single claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> funct) {
        final Claims claims = extractAllClaims(token);
        return funct.apply(claims);
    }

    /**
     * Retrieve all claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey( getSigningKey() )
                .build()
                .parseClaimsJws(token)
                .getBody()
                ;
    }

    private Key getSigningKey() {
        String privateKey = "This is any random string at least 256 bits long. 256 / 8 = 42, "
                + "so a string that is at least 42 characters long would suffice. "
                + "In production, you would want to store this key in some sort of secure fashion."
                ;

        return Keys.hmacShaKeyFor(privateKey.getBytes(StandardCharsets.UTF_8));
    }
}
