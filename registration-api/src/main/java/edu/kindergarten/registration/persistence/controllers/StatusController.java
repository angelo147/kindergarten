package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.StatusEntity;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Transactional
public class StatusController {
    @PersistenceContext(unitName = "KinderGartenDS")
    private EntityManager em;
    private final static Logger log = Logger.getLogger(StatusController.class);

    public List<StatusEntity> findAll() {
        log.infov("finding all Status instances");
        List<StatusEntity> results = new ArrayList<>();
        try {
            results = em.createNamedQuery("StatusEntity.findAll", StatusEntity.class).getResultList();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
        }
        log.infov("Found {0} Status relationships!", results.size());
        return results;
    }

}
