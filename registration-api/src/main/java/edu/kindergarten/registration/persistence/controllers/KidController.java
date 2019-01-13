package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.KidDocument;
import edu.kindergarten.registration.persistence.model.KidprofileEntity;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Stateless
@Transactional
public class KidController {
    @PersistenceContext(unitName = "KinderGartenDS")
    private EntityManager em;
    private final static Logger log = Logger.getLogger(KidController.class);
    public void createKid(KidprofileEntity kidprofileEntity) {
        em.persist(kidprofileEntity);
    }

    public KidprofileEntity findById(int id) {
        log.infov("finding KidprofileEntity instances ActiveByUsername for id {0}", id);
        KidprofileEntity kidprofileEntity;
        try {
            kidprofileEntity = em.find(KidprofileEntity.class, id);
        } catch (RuntimeException re) {
            log.error("find by Id failed", re);
            return null;
        }
        log.infov("Found {0} KidprofileEntity relationships!", kidprofileEntity);
        return kidprofileEntity;
    }

    public KidDocument findDocById(int id) {
        log.infov("finding KidprofileEntity instances ActiveByUsername for id {0}", id);
        KidDocument kidDocument;
        try {
            kidDocument = em.find(KidDocument.class, id);
        } catch (RuntimeException re) {
            log.error("find by Id failed", re);
            return null;
        }
        log.infov("Found {0} KidprofileEntity relationships!", kidDocument);
        return kidDocument;
    }
}
