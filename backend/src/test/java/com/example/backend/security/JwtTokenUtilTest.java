package com.example.backend.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.ExpiredJwtException;

class JwtTokenUtilTest {

    private static final String SECRET = "unit-test-secret-unit-test-secret-unit-test-secret";

    private JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", 60_000L);
    }

    @Test
    void generateToken_roundTripsUsername() {
        String token = jwtTokenUtil.generateToken("alice");

        assertThat(jwtTokenUtil.getUsernameFromToken(token)).isEqualTo("alice");
    }

    @Test
    void validateToken_withMatchingUsername_returnsTrue() {
        String token = jwtTokenUtil.generateToken("alice");

        assertThat(jwtTokenUtil.validateToken(token, "alice")).isTrue();
    }

    @Test
    void validateToken_withMismatchedUsername_returnsFalse() {
        String token = jwtTokenUtil.generateToken("alice");

        assertThat(jwtTokenUtil.validateToken(token, "bob")).isFalse();
    }

    @Test
    void expiredToken_throwsOnParse() {
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", -1_000L);
        String expiredToken = jwtTokenUtil.generateToken("alice");

        assertThatThrownBy(() -> jwtTokenUtil.getUsernameFromToken(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
