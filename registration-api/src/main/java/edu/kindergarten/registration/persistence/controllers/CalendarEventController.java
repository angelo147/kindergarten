package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.CalendarEventEntity;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
@Transactional
public class CalendarEventController implements CalendarEventRepo {
    @PersistenceContext(unitName = "KinderGartenDS")
    private EntityManager em;
    private final static Logger log = Logger.getLogger(CalendarEventController.class);

    public List<CalendarEventEntity> findAll() {
        log.infov("finding all CalendarEventEntity instances");
        List<CalendarEventEntity> results = new ArrayList<>();
        try {
            results = em.createNamedQuery("CalendarEventEntity.findAll", CalendarEventEntity.class).getResultList();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
        }
        log.infov("Found {0} CalendarEventEntity relationships!", results.size());
        return results;
    }

    public List<CalendarEventEntity> findNext() {
        log.infov("finding CalendarEventEntity instances");
        List<CalendarEventEntity> results = new ArrayList<>();
        LocalDate dateafter = LocalDate.now().plusDays(1);
        LocalDate datebefore = LocalDate.now().plusDays(2);
        try {
            results = em.createNamedQuery("CalendarEventEntity.findNext", CalendarEventEntity.class)
                    .setParameter("datebefore", Date.from(datebefore.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .setParameter("dateafter", new Date())
                    .getResultList();
        } catch (RuntimeException re) {
            log.error("find by findNext failed", re);
            return null;
        }
        log.infov("Found {0} CalendarEventEntity relationships!", results.size());
        return results;
    }

    public List<CalendarEventEntity> findByUserId(long id) {
        log.infov("finding CalendarEventEntity instances for id {0}", id);
        List<CalendarEventEntity>  events;
        try {
            events = em.createNamedQuery("CalendarEventEntity.findByUserId", CalendarEventEntity.class)
                    .setParameter("id", id)
                    .getResultList();
        } catch (RuntimeException re) {
            log.error("find by Id failed", re);
            return null;
        }
        log.infov("Found {0} CalendarEventEntity relationships!", events.size());
        return events;
    }

    public void saveEvent(CalendarEventEntity calendarEventEntity) {
        if (calendarEventEntity.getId() == 0) {
            em.persist(calendarEventEntity);
        } else {
            em.merge(calendarEventEntity);
        }
    }
}
