package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.Activity;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Stateless
@Transactional
public class ActivityController {
    @PersistenceContext(unitName = "KinderGartenDS")
    private EntityManager em;
    private final static Logger log = Logger.getLogger(ActivityController.class);
    public Activity createActivity(Activity activity) {
        return em.merge(activity);
    }
    public Activity findById(int id) {
        log.infov("finding Activity instances for id {0}", id);
        Activity activity;
        try {
            activity = em.find(Activity.class, id);
        } catch (RuntimeException re) {
            log.error("find by Id failed", re);
            return null;
        }
        log.infov("Found {0} Activity relationships!", activity);
        return activity;
    }

}
