package net.twisterrob.blt.gapp;

import java.io.IOException;
import java.util.Properties;

//import javax.mail.*;
//import javax.mail.internet.*;
import jakarta.servlet.http.*;

import org.slf4j.*;

import net.twisterrob.java.io.IOTools;

@SuppressWarnings("serial")
public class InternalFeedbackServlet extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(InternalFeedbackServlet.class);

	@Override public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Properties props = new Properties();
		//STOPSHIP use GitHub API to raise an issue
		//Session session = Session.getDefaultInstance(props, null);
		//Message msg = new MimeMessage(session);
		//try {
		//	msg.setFrom(new InternetAddress("feedback@twisterrob-london.appspotmail.com", "BLT Internal Feedback"));
		//	msg.addRecipient(Message.RecipientType.TO, new InternetAddress("papp.robert.s@gmail.com", "Me"));
		//	msg.setSubject("Better London Travel automated internal feedback");
		//	msg.setText(IOTools.readAll(req.getInputStream(), IOTools.ENCODING));
		//	Transport.send(msg);
		//} catch (MessagingException e) {
		//	throw new IOException("Cannot send mail", e);
		//}
	}
}
