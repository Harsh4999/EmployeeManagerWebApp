package com.webapp.service;

import static com.webapp.Constant.EmailConstants.CC_EMAIL;
import static com.webapp.Constant.EmailConstants.DEFUALT_PORT;
import static com.webapp.Constant.EmailConstants.EMAIL_SUBJECT;
import static com.webapp.Constant.EmailConstants.FROM_EMAIL;
import static com.webapp.Constant.EmailConstants.GMAIL_SMTP_SERVER;
import static com.webapp.Constant.EmailConstants.PASSWORD;
import static com.webapp.Constant.EmailConstants.SIMPLE_MAIL_TRANSFER_PROTOCOL;
import static com.webapp.Constant.EmailConstants.SMTP_AUTH;
import static com.webapp.Constant.EmailConstants.SMTP_HOST;
import static com.webapp.Constant.EmailConstants.SMTP_PORT;
import static com.webapp.Constant.EmailConstants.SMTP_STARTTLS_ENABLE;
import static com.webapp.Constant.EmailConstants.SMTP_STARTTLS_REQUIRED;
import static com.webapp.Constant.EmailConstants.USERNAME;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.sun.mail.smtp.SMTPTransport;

@Service
public class EmailService {
	
	public void sendNewPasswordEmail(String firstName,String password,String email) throws MessagingException {
		Message message = createEmail(firstName, password, email);
		SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
		smtpTransport.connect(GMAIL_SMTP_SERVER,USERNAME,PASSWORD);
		smtpTransport.sendMessage(message, message.getAllRecipients());
		smtpTransport.close();
	}
	
	private Message createEmail(String firstName,String password,String email) throws AddressException, MessagingException {
		Message message = new MimeMessage(getEmailSession());
		message.setFrom(new InternetAddress(FROM_EMAIL));
		message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(email,false));
		message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC_EMAIL,false));
		message.setSubject(EMAIL_SUBJECT);
		message.setText("Hello "+ firstName + ", \n\n Your new account password is "+password+"\n\n "+"The support team");
		message.setSentDate(new Date());
		message.saveChanges();
		return message;
	}
	
	private Session getEmailSession() {
		Properties properties = System.getProperties();
		properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
		properties.put(SMTP_AUTH, true);
		properties.put(SMTP_PORT, DEFUALT_PORT);
		properties.put(SMTP_STARTTLS_ENABLE, true);
		properties.put(SMTP_STARTTLS_REQUIRED, true);
		return Session.getInstance(properties,null);
	}
}
