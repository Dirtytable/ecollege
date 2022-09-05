package com.example.ecollege.api.security.jwt;

import com.example.ecollege.api.core.model.ERole;
import com.example.ecollege.api.core.model.Role;
import com.example.ecollege.api.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.el.parser.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class JwtUtilsTest {

    String jwtSecret = "sdsadnasjknkjakjbddjasndlsnkndsalkdlas312t38sadsa";
    int jwtExpirationMs = 1000000;
    JwtUtils jwtUtils;

    @BeforeEach
    void setUp(){
        this.jwtUtils = new JwtUtils();
        this.jwtUtils.setJwtSecret(jwtSecret);
        this.jwtUtils.setJwtExpirationMs(jwtExpirationMs);
    }

    @Test
    void generateJwtToken() {
        String username = "username";
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_ADMIN));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new UserDetailsImpl(
                        "id", username,
                        "password", "email@mail.com",
                        "realName", "group", roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                        .collect(Collectors.toList())),
                "password"
        );

        String result = jwtUtils.generateJwtToken(authentication);
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        assertThat(result).isEqualTo(Jwts.builder()
                .setSubject((username))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact());
    }

    @Test
    void getUserNameFromJwtToken() {
        String username = "username";
        String token = Jwts.builder()
                .setSubject((username))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();

        String result = jwtUtils.getUserNameFromJwtToken(token);

        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        assertThat(result).isEqualTo(
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject()
        );
    }

    @Test
    void validateJwtToken() {
        String username = "username";
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        String token = Jwts.builder()
                .setSubject((username))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        boolean result = jwtUtils.validateJwtToken(token);

        assertThat(result).isTrue();
    }

    @Test
    void willThrowMalformedJwtException() {
        String token = "as";

        boolean result = jwtUtils.validateJwtToken(token);

        assertThat(result).isFalse();
    }
    @Test
    void willThrowExpiredJwtException() {
        String username = "username";
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        String token = Jwts.builder()
                .setSubject((username))
                .setIssuedAt(new Date())
                .setExpiration(new Date())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        boolean result = jwtUtils.validateJwtToken(token);

        assertThat(result).isFalse();
    }
    @Test
    void willThrowUnsupportedJwtException() {
        String username = "username";
        String token = Jwts.builder()
                .setSubject((username))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .compact();

        boolean result = jwtUtils.validateJwtToken(token);
        assertThat(result).isFalse();
    }
    @Test
    void willThrowIllegalArgumentException() {
        String token = "";
        boolean result = jwtUtils.validateJwtToken(token);
        assertThat(result).isFalse();
    }

}