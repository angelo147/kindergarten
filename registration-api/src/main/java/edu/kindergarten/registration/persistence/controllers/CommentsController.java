package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.ActivityComment;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Stateless
@Transactional
public class CommentsController {
    @PersistenceContext(unitName = "KinderGartenDS")
    private EntityManager em;
    private final static Logger log = Logger.getLogger(CommentsController.class);

    public ActivityComment createComment(ActivityComment activityComment) {
        return em.merge(activityComment);
    }
    public ActivityComment findByportofolioandactivity(int portofolioid, int activityid) {
        log.infov("finding ActivityComment instances ActiveByUsername for username {0}", portofolioid);
        ActivityComment user;
        try {
            user = em.createNamedQuery("ActivityComment.findByportofolioandactivity", ActivityComment.class)
                    .setParameter("portofolioid", portofolioid)
                    .setParameter("activityid", activityid)
                    .getResultList().stream().findFirst().orElse(null);
        } catch (RuntimeException re) {
            log.error("find by ActivityComment failed", re);
            return null;
        }
        log.infov("Found {0} ActivityComment relationships!", user);
        return user;
    }

    public ActivityComment findById(int id) {
        log.infov("finding ActivityComment instances for id {0}", id);
        ActivityComment activity;
        try {
            activity = em.find(ActivityComment.class, id);
        } catch (RuntimeException re) {
            log.error("find by Id failed", re);
            return null;
        }
        log.infov("Found {0} ActivityComment relationships!", activity);
        return activity;
    }
}
