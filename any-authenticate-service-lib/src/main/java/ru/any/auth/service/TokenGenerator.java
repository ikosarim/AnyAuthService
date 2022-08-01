package ru.any.auth.service;

import io.jsonwebtoken.Claims;
import lombok.NonNull;

public interface TokenGenerator {

    String generateAccessToken(String phone);

    String generateRefreshToken(@NonNull String phone);

    Boolean validateAccessToken(@NonNull String token);

    Boolean validateRefreshToken(@NonNull String token);

    Claims getAccessClaims(@NonNull String token);

    Claims getRefreshClaims(@NonNull String token);
}
