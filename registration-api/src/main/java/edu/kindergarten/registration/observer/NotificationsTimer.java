package edu.kindergarten.registration.observer;

import edu.kindergarten.registration.messaging.Email;
import edu.kindergarten.registration.messaging.MessageService;
import edu.kindergarten.registration.persistence.controllers.CalendarEventRepo;
import edu.kindergarten.registration.persistence.model.CalendarEventEntity;
import org.jboss.logging.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

@Startup
@Singleton
public class NotificationsTimer {
    private final static Logger log = Logger.getLogger(NotificationsTimer.class);
    @Resource
    TimerService timerService;
    @Inject
    Event<NotificationEvent> event;
    @Inject
    @Email
    private MessageService email;
    @Inject
    private CalendarEventRepo eventRepo;
    @Inject
    @ConfigurationValue("notification.hours")
    private String hours;
    @Inject
    @ConfigurationValue("notification.minutes")
    private String minutes;

    @PostConstruct
    public void initialize() {
        ScheduleExpression schedule = new ScheduleExpression();
        schedule.hour(hours);
        if(minutes!=null)
            schedule.minute(minutes);
        Timer timer = timerService.createCalendarTimer(schedule);
    }

    @Timeout
    public void programmaticTimout(Timer timer) {
        List<CalendarEventEntity> results = eventRepo.findNext();
        results.parallelStream().forEach(clevent -> {
            log.info(clevent.getId() + clevent.getContact());
            event.fire(new NotificationEvent(clevent.getContact(), clevent.getEventDate(), email));
            clevent.setNotified(true);
            eventRepo.saveEvent(clevent);
        });
    }

    /*@Schedule(hour = "16", minute = "38", info = "Every day timer")
    public void automaticallyScheduled(Timer timer) {
        List<CalendarEventEntity> results = eventRepo.findNext();
        results.parallelStream().forEach(clevent -> {
            log.info(clevent.getId() + clevent.getContact());
            event.fire(new NotificationEvent(clevent.getContact(), email));
            clevent.setNotified(true);
            eventRepo.saveEvent(clevent);
        });
    }*/
}
