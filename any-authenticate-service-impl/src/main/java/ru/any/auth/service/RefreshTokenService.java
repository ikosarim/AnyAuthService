package ru.any.auth.service;

import ru.any.auth.dto.JwtResponseDto;
import ru.any.auth.dto.TokensDto;

public interface RefreshTokenService {
    JwtResponseDto refreshAccessToken(TokensDto tokensDto);

    JwtResponseDto refreshRefreshToken(TokensDto tokensDto);
}
