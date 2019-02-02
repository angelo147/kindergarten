package edu.kindergarten.registration.persistence.model;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name="CALENDAREVENT")
@NamedQueries({
        @NamedQuery(name="CalendarEventEntity.findAll", query="SELECT ce FROM CalendarEventEntity ce"),
        @NamedQuery(name="CalendarEventEntity.findNext", query="SELECT ce FROM CalendarEventEntity ce where ce.active = true and ce.notified = false and ce.eventDate >= :dateafter and ce.eventDate < :datebefore"),
        @NamedQuery(name="CalendarEventEntity.findByUserId", query="SELECT ce FROM CalendarEventEntity ce where ce.active = true and ce.userid = :id")
})
public class CalendarEventEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;
    private String type;
    private Boolean active;
    private String contact;
    private Boolean notified;
    private long userid;

    @PrePersist
    private void prep() {
        active = true;
        notified = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }
}
