package edu.kindergarten.registration.observer;

import edu.kindergarten.registration.messaging.MessageService;

import java.util.Date;

public class NotificationEvent {
    private String rec;
    private MessageService messageService;
    private Date date;

    public NotificationEvent(String rec, Date date, MessageService messageService) {
        this.rec = rec;
        this.messageService = messageService;
        this.date = date;
    }

    public String getRec() {
        return rec;
    }

    public void setRec(String rec) {
        this.rec = rec;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
