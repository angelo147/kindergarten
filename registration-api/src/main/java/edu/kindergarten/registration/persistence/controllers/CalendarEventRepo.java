package edu.kindergarten.registration.persistence.controllers;

import edu.kindergarten.registration.persistence.model.CalendarEventEntity;

import java.util.List;

public interface CalendarEventRepo {
    List<CalendarEventEntity> findAll();
    List<CalendarEventEntity> findNext();
    public List<CalendarEventEntity> findByUserId(long id);
    void saveEvent(CalendarEventEntity calendarEventEntity);
}
