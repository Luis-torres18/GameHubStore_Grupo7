package com.GameHubStore.auth_service.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET = "GameHubStore2024SecretKeyForJWTSigning123456";
    private static final long EXPIRATION_MS = 86_400_000L; // 24h

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION_MS);
    }

    @Test
    @DisplayName("generateToken debe crear un token valido que contenga el email y el rol")
    void generateToken_deberiaCrearTokenValido() {
        String token = jwtUtil.generateToken("ana@mail.com", "ADMINISTRADOR");

        assertThat(token).isNotBlank();
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("ana@mail.com");

        Claims claims = jwtUtil.extractClaims(token);
        assertThat(claims.get("rol")).isEqualTo("ADMINISTRADOR");
    }

    @Test
    @DisplayName("isTokenValid debe retornar false para un token expirado")
    void isTokenValid_deberiaRetornarFalseSiExpiro() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L); // ya expirado

        String token = jwtUtil.generateToken("ana@mail.com", "CLIENTE");

        assertThat(jwtUtil.isTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid debe retornar false para un token malformado")
    void isTokenValid_deberiaRetornarFalseSiEsInvalido() {
        assertThat(jwtUtil.isTokenValid("token-invalido-123")).isFalse();
    }

    @Test
    @DisplayName("extractEmail debe lanzar excepcion si el token es invalido")
    void extractEmail_deberiaLanzarExcepcionSiTokenInvalido() {
        org.junit.jupiter.api.Assertions.assertThrows(
                Exception.class,
                () -> jwtUtil.extractEmail("token-invalido-123"));
    }
}
