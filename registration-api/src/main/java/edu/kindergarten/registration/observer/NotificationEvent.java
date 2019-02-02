package edu.kindergarten.registration.observer;

import edu.kindergarten.registration.messaging.MessageService;

public class NotificationEvent {
    private String rec;
    private MessageService messageService;

    public NotificationEvent(String rec, MessageService messageService) {
        this.rec = rec;
        this.messageService = messageService;
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
}
