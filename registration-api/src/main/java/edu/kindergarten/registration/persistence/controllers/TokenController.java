package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.Token;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
@Transactional
public class TokenController {
    @PersistenceContext(unitName = "KinderGartenDS")
    private EntityManager em;
    private final static Logger log = Logger.getLogger(TokenController.class);

    public List<Token> findAll() {
        log.infov("finding all token instances");
        List<Token> results = new ArrayList<>();
        try {
            results = em.createNamedQuery("Token.findAll", Token.class).getResultList();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
        }
        log.infov("Found {0} Token relationships!", results.size());
        return results;
    }

    public Token findActiveByEmail(String email) {
        log.infov("finding token instances ActiveByEmail for email {0}", email);
        Token token;
        try {
            token = em.createNamedQuery("Token.findActiveByEmail", Token.class)
                    .setParameter("date", new Date())
                    .setParameter("email", email)
                    .getResultList().stream().findFirst().orElse(null);
        } catch (RuntimeException re) {
            log.error("find by ActiveByEmail failed", re);
            return null;
        }
        log.infov("Found {0} token relationships!", token);
        return token;
    }

    public Token findActiveByValue(String tokenValue) {
        log.infov("finding token instances ActiveByValue for email {0}", tokenValue);
        Token token;
        try {
            token = em.createNamedQuery("Token.findActiveByValue", Token.class)
                    .setParameter("date", new Date())
                    .setParameter("token", tokenValue)
                    .getResultList().stream().findFirst().orElse(null);
        } catch (RuntimeException re) {
            log.error("find by ActiveByValue failed", re);
            return null;
        }
        log.infov("Found {0} token relationships!", token);
        return token;
    }

    public void updateToken(Token token) {
        em.merge(token);
    }

    public void createToken(Token token) {
        em.persist(token);
    }
}
