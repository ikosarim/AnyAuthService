package ru.any.auth.service;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import ru.any.auth.dto.JwtResponseDto;
import ru.any.auth.dto.TokensDto;

import java.util.function.Function;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final TokenGenerator tokenGenerator;

    public RefreshTokenServiceImpl(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public JwtResponseDto refreshAccessToken(TokensDto tokensDto) {
        return refreshToken(
                tokensDto,
                tokenGenerator::generateAccessToken,
                (phone) -> tokensDto.getRefreshToken()
        );
    }

    @Override
    public JwtResponseDto refreshRefreshToken(TokensDto tokensDto) {
        return refreshToken(
                tokensDto,
                (phone) -> tokensDto.getAccessToken(),
                tokenGenerator::generateRefreshToken
        );
    }

    private JwtResponseDto refreshToken(
            TokensDto tokensDto,
            Function<String, String> newAccessToken,
            Function<String, String> newRefreshToken
    ) {
        final String refreshToken = tokensDto.getRefreshToken();
        final String accessToken = tokensDto.getAccessToken();
        if (!tokenGenerator.validateRefreshToken(refreshToken)) {
            return new JwtResponseDto().isSuccessful(false).errorMessage("Invalid tokens");
        }
        final Claims refreshClaims = tokenGenerator.getRefreshClaims(refreshToken);
        if (tokensAreInvalid(accessToken, refreshClaims)) {
            return new JwtResponseDto().isSuccessful(false).errorMessage("Invalid tokens");
        }
        final String phone = refreshClaims.getSubject();
        return new JwtResponseDto().isSuccessful(true)
                .accessToken(newAccessToken.apply(phone))
                .refreshToken(newRefreshToken.apply(phone));
    }

    private boolean tokensAreInvalid(String oldAccessToken, Claims refreshClaims) {
        final Claims accessClaims = tokenGenerator.getAccessClaims(oldAccessToken);
        if (!"refresh_token".equals(refreshClaims.get("token_type"))) {
            return true;
        }
        if (!"access_token".equals(accessClaims.get("token_type"))) {
            return true;
        }
        if (!"any-authenticate-service".equals(refreshClaims.get("issuer"))) {
            return true;
        }
        if (!"any-authenticate-service".equals(accessClaims.get("issuer"))) {
            return true;
        }
        return !accessClaims.getSubject().equals(refreshClaims.getSubject());
    }
}
