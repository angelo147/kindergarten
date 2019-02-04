package edu.kindergarten.registration.messaging;

import edu.kindergarten.registration.rest.Response;

import java.util.Date;
import java.util.List;

public interface MessageService {
	Response sendMessage(String rec, int userid);
	void sendMessage(List<String> email, String type);
	Response sendMessage(String email, Date date);
}
