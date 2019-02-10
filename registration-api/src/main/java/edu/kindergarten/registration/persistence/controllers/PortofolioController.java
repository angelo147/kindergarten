package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.Portofolio;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Stateless
@Transactional
public class PortofolioController {
    @PersistenceContext(unitName = "KinderGartenDS")
    private EntityManager em;
    private final static Logger log = Logger.getLogger(PortofolioController.class);
    public Portofolio createPortofolio(Portofolio portofolio) {
        return em.merge(portofolio);
    }

    public Portofolio findById(int id) {
        log.infov("finding Portofolio instances for id {0}", id);
        Portofolio portofolio;
        try {
            portofolio = em.find(Portofolio.class, id);
        } catch (RuntimeException re) {
            log.error("find by Id failed", re);
            return null;
        }
        log.infov("Found {0} Portofolio relationships!", portofolio);
        return portofolio;
    }
}
