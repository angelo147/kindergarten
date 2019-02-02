package edu.kindergarten.registration.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import edu.kindergarten.registration.rest.JwtResponse;
import edu.kindergarten.registration.rest.ResponseCode;
import edu.kindergarten.registration.rest.Role;
import edu.kindergarten.registration.rest.UserInfo;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class JWTUtil {
    public static final String PUBLIC_CALLER = "PUBLIC_CALLER";
    public static final String TRUSTED_CALLER = "TRUSTED_CALLER";
    private static final String BEARER = "Bearer";
    private static final String CLAIM_USERID = "userId";
    private static final String CLAIM_ROLE = "role";

    @Inject
    private TokenCacheUtil cacheUtil;

    public JwtResponse generateJWT(Long userId, List<Role> role, String existingRefreshToken) {
        //refreshTokenExpires.toInstant().atZone(ZoneId.systemDefault()).toEpochSecond()
        Date insert = new Date();
        LocalDateTime localDateTime = insert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.plusMinutes(5);
        Date expires = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        UUID jti = UUID.randomUUID();
        try {
            String token;
            Algorithm algorithm = Algorithm.HMAC256("itsasecret");
            token = JWT.create().withIssuedAt(insert).withNotBefore(insert).withExpiresAt(expires)
                    .withIssuer(TRUSTED_CALLER).withClaim(CLAIM_USERID, userId)
                    .withArrayClaim(CLAIM_ROLE, role.stream().map(Role::toString).collect(Collectors.toList()).toArray(new String[]{}))
                    .withJWTId(jti.toString())
                    .sign(algorithm);
            JwtResponse resp = new JwtResponse(ResponseCode.OK, token, BEARER);
            LocalDateTime thisisforrefresh = localDateTime.plusMinutes(10);
            Date refreshTokenExpires = Date.from(thisisforrefresh.atZone(ZoneId.systemDefault()).toInstant());
            UUID refreshToken = existingRefreshToken == null ? UUID.randomUUID() : UUID.fromString(existingRefreshToken);
            cacheUtil.getRefreshTokens().put(refreshToken, new UserInfo(userId, role, jti));
            resp.setRefreshToken(refreshToken.toString());
            resp.setRefreshTokenExpires(refreshTokenExpires.toInstant().atZone(ZoneId.systemDefault()).toEpochSecond());
            return resp;
        } catch (JWTCreationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public boolean verifyJWT(String auth, Long identifier) {
        DecodedJWT jwt = verifyJWT(auth);
        if (jwt == null)
            return false;
        if (cacheUtil.getInvalidatedJwt().getIfPresent(jwt.getId()) != null)
            return false;
        if (jwt.getIssuer().equalsIgnoreCase(PUBLIC_CALLER)) {
            cacheUtil.getInvalidatedJwt().put(jwt.getId(), jwt.getExpiresAt().getTime());
            return Optional.ofNullable(jwt.getClaim(CLAIM_USERID)).map(Claim::asLong).orElse(0L).equals(identifier);
        } else {
            cacheUtil.getInvalidatedJwt().put(jwt.getId(), jwt.getExpiresAt().getTime());
            return true;
        }
    }

    public DecodedJWT verifyJWT(String auth) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("itsasecret");
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(TRUSTED_CALLER).build();
            DecodedJWT jwt = verifier.verify(auth);
            return jwt;
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    public boolean verifyRefreshToken(String auth, String refreshToken, UserInfo userInfo) {
        DecodedJWT jwt = JWT.decode(auth);
        if (userInfo == null)
            return false;
        long userId = Optional.ofNullable(jwt.getClaim(CLAIM_USERID)).map(Claim::asLong).orElse(0L);
        boolean verified = userInfo.getUserId().equals(userId) && jwt.getId().equals(userInfo.getJwtId().toString());
        if (verified) {
            cacheUtil.getRefreshTokens().invalidate(userId);
        }
        return verified;
    }

    public List<Role> getRoles(String auth) {
        return JWT.decode(auth).getClaim(CLAIM_ROLE).asList(Role.class);
    }
}
