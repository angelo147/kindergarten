package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.Address;
import edu.kindergarten.registration.persistence.model.UserEntity;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Transactional
public class UserController {
    @PersistenceContext(unitName = "KinderGartenDS")
    private EntityManager em;
    private final static Logger log = Logger.getLogger(UserController.class);
    public UserEntity createUser(UserEntity user) {
        return em.merge(user);
    }
    public void createAddr(Address address) {
        em.persist(address);
    }
    public List<UserEntity> findAll() {
        log.infov("finding all UserEntity instances");
        List<UserEntity> results = new ArrayList<>();
        try {
            results = em.createNamedQuery("UserEntity.findAll", UserEntity.class).getResultList();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
        }
        log.infov("Found {0} UserEntity relationships!", results.size());
        return results;
    }

    public UserEntity findActiveByUsername(String username) {
        log.infov("finding UserEntity instances ActiveByUsername for username {0}", username);
        UserEntity user;
        try {
            user = em.createNamedQuery("UserEntity.findActiveByUsername", UserEntity.class)
                    .setParameter("username", username)
                    .getResultList().stream().findFirst().orElse(null);
        } catch (RuntimeException re) {
            log.error("find by ActiveByUsername failed", re);
            return null;
        }
        log.infov("Found {0} UserEntity relationships!", user);
        return user;
    }

    public UserEntity findById(int id) {
        log.infov("finding UserEntity instances for id {0}", id);
        UserEntity user;
        try {
            user = em.find(UserEntity.class, id);
            user.getUserkids();
        } catch (RuntimeException re) {
            log.error("find by Id failed", re);
            return null;
        }
        log.infov("Found {0} UserEntity relationships!", user);
        return user;
    }
}
