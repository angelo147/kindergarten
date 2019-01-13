package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.RoleEntity;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Transactional
public class RoleController {
    @PersistenceContext(unitName = "KinderGartenDS")
    private EntityManager em;
    private final static Logger log = Logger.getLogger(RoleController.class);

    public List<RoleEntity> findAll() {
        log.infov("finding all Role instances");
        List<RoleEntity> results = new ArrayList<>();
        try {
            results = em.createNamedQuery("RoleEntity.findAll", RoleEntity.class).getResultList();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
        }
        log.infov("Found {0} Role relationships!", results.size());
        return results;
    }
}
