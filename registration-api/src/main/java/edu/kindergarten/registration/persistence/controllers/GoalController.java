package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.Goal;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Stateless
@Transactional
public class GoalController {
    @PersistenceContext(unitName = "KinderGartenDS")
    private EntityManager em;
    private final static Logger log = Logger.getLogger(GoalController.class);
    public Goal findById(int id) {
        log.infov("finding Goal instances for id {0}", id);
        Goal goal;
        try {
            goal = em.find(Goal.class, id);
        } catch (RuntimeException re) {
            log.error("find by Id failed", re);
            return null;
        }
        log.infov("Found {0} Goal relationships!", goal.getGoalid());
        return goal;
    }
}
