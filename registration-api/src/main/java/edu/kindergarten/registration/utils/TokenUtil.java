package edu.kindergarten.registration.utils;

import edu.kindergarten.registration.persistence.controllers.StatusController;
import edu.kindergarten.registration.persistence.controllers.TokenController;
import edu.kindergarten.registration.persistence.controllers.UserController;
import edu.kindergarten.registration.persistence.model.StatusEntity;
import edu.kindergarten.registration.persistence.model.Token;
import edu.kindergarten.registration.persistence.model.UserEntity;
import edu.kindergarten.registration.rest.JwtResponse;
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
    @Inject
    private UserController userController;
    @Inject
    private StatusController statusController;

    public String generateToken(String email, int userid) {
        UUID id = UUID.randomUUID();
        Token token = controller.findActiveByEmail(email);
        if (token != null) {
            token.setRevokeDate(new Date());
            token.setActive(false);
            //controller.updateToken(token);
        }
        controller.createToken(getFreshToken(email, id.toString(), userid));
        return id.toString();
    }

    private Token getFreshToken(String email, String tokenValue, int userid) {
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
        token.setUserId(userid);
        return token;
    }

    public Response validateToken(String tokenValue) {
        Token token = controller.findActiveByValue(tokenValue);
        if (token != null && token.getActive() && token.getExpireDate().after(new Date())) {
            token.setVerifyDate(new Date());
            token.setActive(false);
            controller.updateToken(token);
            //StatusEntity status = statusController.findAll().stream().filter(att -> "active".equalsIgnoreCase(att.getStatus())).findFirst().orElse(null);
            //UserEntity user = userController.findById(token.getUserId());
            //user.setStatusid(status);
            //userController.createUser(user);
            return new JwtResponse(ResponseCode.OK, token.getUserId());
        } else
            return new Response(ResponseCode.INVTOKEN);
    }
}
