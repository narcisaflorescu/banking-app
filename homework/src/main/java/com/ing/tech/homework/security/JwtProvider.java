package com.ing.tech.homework.security;


import com.ing.tech.homework.service.exceptions.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtProvider {

    private final static String SECRET = "REDFLOWER";
    private static final String AUTHORITIES = "authorities";



    public String generateToken(String username, long ttl, Set<String> authorities) {
        Date expirationDateTime = Date.from(ZonedDateTime.now().plusMinutes(ttl).toInstant());
        return Jwts.builder()
                .setSubject(username)
                .claim(AUTHORITIES, authorities)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .setExpiration(expirationDateTime)
                .compact();
    }

    public boolean validate(String jwt) {
        if (jwt.isBlank()) {
            throw new UnauthorizedException();
        }

        try {
            Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(jwt);

        } catch (JwtException e) {
            throw new UnauthorizedException();
        }
        return true;
    }
    public boolean validateTokenAndUsernameToken(String jwt, String jwtUsername ) {
        if (jwt.isBlank()) {
            throw new UnauthorizedException();
        }
        if (jwtUsername.isBlank()) {
            throw new UnauthorizedException();
        }

        try {
            Claims jwtClaims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(jwt).getBody();
            Claims jwtUsernameClaims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(jwtUsername).getBody();

            String jwtSubject = jwtClaims.getSubject();
            String jwtUsernameSubject = jwtUsernameClaims.getSubject();


            Set<String> jwtAuthorities = ((ArrayList<String>) jwtClaims.get(AUTHORITIES)).stream()
                    .filter(claim -> claim != null && !claim.isBlank())
                    .collect(Collectors.toSet());
            Set<String> jwtUsernameAuthorities = ((ArrayList<String>) jwtUsernameClaims.get(AUTHORITIES)).stream()
                    .filter(claim -> claim != null && !claim.isBlank())
                    .collect(Collectors.toSet());


            if(!(jwtSubject.equals(jwtUsernameSubject) && jwtAuthorities.equals(jwtUsernameAuthorities))){
                throw new UnauthorizedException();
            }
        } catch (JwtException e) {
            throw new UnauthorizedException();
        }

        return true;
    }

    public Authentication authentication(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token).getBody();

            Set<SimpleGrantedAuthority> grantedAuthorities = ((ArrayList<String>) claims.get(AUTHORITIES)).stream()
                    .filter(claim -> claim != null && !claim.isBlank())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());


            User user = new User(claims.getSubject(), "", grantedAuthorities);

            return new UsernamePasswordAuthenticationToken(user, "", grantedAuthorities);
        } catch (JwtException e) {
            log.error(e.getMessage());
            throw  new UnauthorizedException();
        }
    }
}
