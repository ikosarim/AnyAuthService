package ru.any.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.any.auth.dto.JwtResponseDto;
import ru.any.auth.dto.TokensDto;
import ru.any.auth.service.RefreshTokenService;

@RestController
public class RefreshTokenController implements RefreshTokenApi {

    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public ResponseEntity<JwtResponseDto> refreshAccessToken(TokensDto tokensDto) {
        JwtResponseDto jwtResponseDto = refreshTokenService.refreshAccessToken(tokensDto);
        return ResponseEntity.ok(jwtResponseDto);
    }

    @Override
    public ResponseEntity<JwtResponseDto> refreshRefreshToken(TokensDto tokensDto) {
        JwtResponseDto jwtResponseDto = refreshTokenService.refreshRefreshToken(tokensDto);
        return ResponseEntity.ok(jwtResponseDto);
    }
}
