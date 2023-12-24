package net.twisterrob.blt.gapp.controller;

import java.io.IOException;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

import org.slf4j.*;

@Controller
public class InternalFeedbackController {
	private static final Logger LOG = LoggerFactory.getLogger(InternalFeedbackController.class);

	@Post("/InternalFeedback")
	@Consumes(MediaType.ALL)
	public void doPost(@Body String body) throws IOException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		Message msg = new MimeMessage(session);
		try {
			msg.setFrom(new InternetAddress("feedback@twisterrob-london.appspotmail.com", "BLT Internal Feedback"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress("papp.robert.s@gmail.com", "Me"));
			msg.setSubject("Better London Travel automated internal feedback");
			msg.setText(body);
			Transport.send(msg);
		} catch (MessagingException e) {
			throw new IOException("Cannot send mail", e);
		}
	}
}
