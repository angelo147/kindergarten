package edu.kindergarten.registration.messaging;

import edu.kindergarten.registration.persistence.controllers.TemplateController;
import edu.kindergarten.registration.rest.Response;
import edu.kindergarten.registration.rest.ResponseCode;
import edu.kindergarten.registration.utils.TokenUtil;
import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
		/*Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		final String username = "dimoange@gmail.com";
		final String password = "k@ss@r0cksnfuck!";
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});*/

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
	public Response sendMessage(String email) {
		try {
			log.info("Invoking SendEmail" + " with email address " + email);
			Message message = new MimeMessage(session);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("Test");
			message.setContent("",
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
