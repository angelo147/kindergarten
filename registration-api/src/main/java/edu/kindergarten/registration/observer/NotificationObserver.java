package edu.kindergarten.registration.observer;

import javax.enterprise.event.Observes;

public class NotificationObserver {
    public void notify(@Observes NotificationEvent notificationEvent) {
        notificationEvent.getMessageService().sendMessage(notificationEvent.getRec());
    }
}
