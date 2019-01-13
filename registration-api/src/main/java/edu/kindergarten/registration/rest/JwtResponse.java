package edu.kindergarten.registration.rest;

import edu.kindergarten.registration.persistence.model.UserEntity;

public class JwtResponse extends Response {
    private String accessToken;
    private String accessTokenType;
    private String refreshToken;
    private Long refreshTokenExpires;
    private UserEntity user;

    public JwtResponse(ResponseCode ec, String accessToken, String accessTokenType) {
        super(ec);
        this.accessToken = accessToken;
        this.accessTokenType = accessTokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenType() {
        return accessTokenType;
    }

    public void setAccessTokenType(String accessTokenType) {
        this.accessTokenType = accessTokenType;
    }

    public Long getRefreshTokenExpires() {
        return refreshTokenExpires;
    }

    public void setRefreshTokenExpires(Long refreshTokenExpires) {
        this.refreshTokenExpires = refreshTokenExpires;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
