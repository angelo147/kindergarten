package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.KidDocument;
import edu.kindergarten.registration.persistence.model.KidprofileEntity;
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
public class KidController {
    @PersistenceContext(unitName = "KinderGartenDS")
    private EntityManager em;
    private final static Logger log = Logger.getLogger(KidController.class);
    public void createKid(KidprofileEntity kidprofileEntity) {
        em.persist(kidprofileEntity);
    }

    public List<KidprofileEntity> findAll() {
        log.infov("finding all KidprofileEntity instances");
        List<KidprofileEntity> results = new ArrayList<>();
        try {
            results = em.createNamedQuery("KidprofileEntity.findAll", KidprofileEntity.class).getResultList();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
        }
        log.infov("Found {0} KidprofileEntity relationships!", results.size());
        return results;
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
