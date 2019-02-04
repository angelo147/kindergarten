package edu.kindergarten.registration.messaging;

import edu.kindergarten.registration.persistence.controllers.TemplateController;
import edu.kindergarten.registration.rest.Response;
import edu.kindergarten.registration.rest.ResponseCode;
import edu.kindergarten.registration.utils.TokenUtil;
import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.List;

/**
 * Created by adimo on 31/3/2017.
 */
@Stateless
@Email
public class EmailServiceImpl implements MessageService {
	private final static Logger log = Logger.getLogger(EmailServiceImpl.class);
	@Resource(mappedName = "java:jboss/mail/GOOGLE")
	private Session session;
	@Inject
	private TokenUtil tokenUtil;
	@Inject
	private TemplateController templateController;

	@Override
	public Response sendMessage(String email, int userid) {
		try {
			Document doc = Jsoup.parse(templateController.findByType("REGISTRATION").getHtml());
			String url = "http:localhost:8080?token="+tokenUtil.generateToken(email, userid);
			doc.getElementById("tokenurl").attr("href", url);
			log.info("Invoking SendEmail" + " with email address " + email);
			Message message = new MimeMessage(session);
			//message.setFrom(new InternetAddress("dimoange@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("Test");
			message.setContent(doc.toString(),
					"text/html; charset=utf-8");
			Transport.send(message);
			log.info("Email/SMS has been sent to email address " + email);
		} catch (Exception e) {
			log.error("Could not send email", e);
			return new Response(ResponseCode.ERRSENDMSG);
		}
		return new Response(ResponseCode.OK);
	}

	@Override
	@Asynchronous
	public void sendMessage(List<String> email, String type) {
		try {
			log.info("Invoking SendEmail" + " with email address " + email);
			Message message = new MimeMessage(session);
			for (String t : email) {
				try {
					message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(t));
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
			message.setSubject("Ενημέρωση");
			Multipart multipart = new MimeMultipart();
			MimeBodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setText("Ενημέρωση!");
			MimeBodyPart attachmentBodyPart= new MimeBodyPart();
			DataSource source = new FileDataSource("C:\\"+type+".pdf"); // ex : "C:\\test.pdf"
			attachmentBodyPart.setDataHandler(new DataHandler(source));
			attachmentBodyPart.setFileName(type+".pdf"); // ex : "test.pdf"
			multipart.addBodyPart(textBodyPart);  // add the text part
			multipart.addBodyPart(attachmentBodyPart); // add the attachement part
			message.setContent(multipart);
			Transport.send(message);
			log.info("Email/SMS has been sent to email address " + email);
		} catch (Exception e) {
			log.error("Could not send email", e);
			//return new Response(ResponseCode.ERRSENDMSG);
		}
		//return new Response(ResponseCode.OK);
	}

	@Override
	public Response sendMessage(String email, Date date) {
		try {
			log.info("Invoking SendEmail" + " with email address " + email);
			Message message = new MimeMessage(session);
			//message.setFrom(new InternetAddress("dimoange@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("Υπενθήμιση");
			message.setContent("Έχετε ραντεβού στης " + date,
					"text/html; charset=utf-8");
			Transport.send(message);
			log.info("Email/SMS has been sent to email address " + email);
		} catch (Exception e) {
			log.error("Could not send email", e);
			return new Response(ResponseCode.ERRSENDMSG);
		}
		return new Response(ResponseCode.OK);
	}
}
