package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.Template;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Stateless
@Transactional
public class TemplateController {
    @PersistenceContext(unitName = "KinderGartenDS")
    private EntityManager em;
    private final static Logger log = Logger.getLogger(TemplateController.class);

    public Template findByType(String type) {
        log.infov("finding Template instances for type {0}", type);
        Template  template;
        try {
            template = em.createNamedQuery("Template.findByType", Template.class)
                    .setParameter("templatetype", type)
                    .getResultList().stream().findFirst().orElse(null);
        } catch (RuntimeException re) {
            log.error("find by Id failed", re);
            return null;
        }
        log.infov("Found {0} CalendarEventEntity relationships!", template);
        return template;
    }

}
