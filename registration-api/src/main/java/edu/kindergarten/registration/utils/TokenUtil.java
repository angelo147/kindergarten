package edu.kindergarten.registration.utils;

import edu.kindergarten.registration.persistence.controllers.TokenController;
import edu.kindergarten.registration.persistence.model.Token;
import edu.kindergarten.registration.rest.Response;
import edu.kindergarten.registration.rest.ResponseCode;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@RequestScoped
public class TokenUtil {
    @Inject
    private TokenController controller;

    public String generateToken(String email) {
        UUID id = UUID.randomUUID();
        Token token = controller.findActiveByEmail(email);
        if (token != null) {
            token.setRevokeDate(new Date());
            token.setActive(false);
            controller.updateToken(token);
        }
        controller.createToken(getFreshToken(email, id.toString()));
        return id.toString();
    }

    private Token getFreshToken(String email, String tokenValue) {
        Date insert = new Date();
        LocalDateTime localDateTime = insert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.plusDays(2);
        Date currentDatePlusTwoDays = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Token token = new Token();
        token.setToken(tokenValue);
        token.setEmail(email);
        token.setActive(true);
        token.setCreateDate(insert);
        token.setExpireDate(currentDatePlusTwoDays);
        return token;
    }

    public Response validateToken(String tokenValue) {
        Token token = controller.findActiveByValue(tokenValue);
        if (token != null) {
            token.setVerifyDate(new Date());
            token.setActive(false);
            controller.updateToken(token);
            return new Response(ResponseCode.OK);
        } else
            return new Response(ResponseCode.INVTOKEN);
    }
}
