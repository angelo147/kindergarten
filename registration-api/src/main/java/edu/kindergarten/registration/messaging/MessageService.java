package edu.kindergarten.registration.messaging;

import edu.kindergarten.registration.rest.Response;

public interface MessageService {
	Response sendMessage(String rec, int userid);
	Response sendMessage(String rec);
}
