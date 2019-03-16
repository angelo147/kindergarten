package edu.kindergarten.registration.rest;

import edu.kindergarten.registration.persistence.model.CalendarEventEntity;
import edu.kindergarten.registration.persistence.model.Category;
import edu.kindergarten.registration.persistence.model.KidprofileEntity;
import edu.kindergarten.registration.persistence.model.UserEntity;

import java.util.List;

public class ResponseWrapper extends Response {
    private List<UserEntity> users;
    private List<CalendarEventEntity> events;
    private List<Category> categories;
    private List<KidprofileEntity> kids;

    public ResponseWrapper(ResponseCode ec, List<UserEntity> users, List<CalendarEventEntity> events, List<Category> categories) {
        super(ec);
        this.users = users;
        this.events = events;
        this.categories = categories;
    }
    public ResponseWrapper() {
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public List<CalendarEventEntity> getEvents() {
        return events;
    }

    public void setEvents(List<CalendarEventEntity> events) {
        this.events = events;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<KidprofileEntity> getKids() {
        return kids;
    }

    public void setKids(List<KidprofileEntity> kids) {
        this.kids = kids;
    }
}
