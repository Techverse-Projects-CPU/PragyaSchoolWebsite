 

package com.techverse.Service;

import java.io.File;
import java.net.URL;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.annotation.PostConstruct;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService1 {
	  private static final String DIGITS = "0123456789";
	    private static final SecureRandom random = new SecureRandom();
	    private Session session;

	 
	    @Value("${mail.smtp.host}")
	    private String host;

	    @Value("${mail.smtp.port}")
	    private String port;

	    @Value("${mail.smtp.user}")
	    private String senderEmail;

	    @Value("${mail.smtp.password}")
	    private String senderPassword;

     
     @Autowired
     private JavaMailSender emailSender;

     @Autowired
     private SpringTemplateEngine templateEngine;

     public String generateEmailContent(String templateName, Map<String, Object> variables) {
         Context context = new Context();
         context.setVariables(variables);
         return templateEngine.process(templateName, context);
     }
     
     
    
     @PostConstruct
     private void initializeSession() {
         Properties props = new Properties();
         props.put("mail.smtp.host", host);
         props.put("mail.smtp.port", port);
         props.put("mail.smtp.auth", "true");
         props.put("mail.smtp.starttls.enable", "true");
         props.put("mail.smtp.starttls.required", "true");
         props.put("mail.smtp.ssl.enable", "true");
         props.put("mail.smtp.ssl.trust", host);

         this.session = Session.getInstance(props, new Authenticator() {
             @Override
             protected PasswordAuthentication getPasswordAuthentication() {
                 return new PasswordAuthentication(senderEmail, senderPassword);
             }
         });
     }
     //using java Mail sender 
     public boolean sendEmail2(String recipientEmail, String emailSubject, String emailBody) {
         try {
             MimeMessage mimeMessage = emailSender.createMimeMessage();
             MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
             
             helper.setFrom(senderEmail); // You can use the same as senderEmail if required
             helper.setTo(recipientEmail);
             helper.setSubject(emailSubject);
             helper.setText(emailBody, true); // true indicates HTML content

             emailSender.send(mimeMessage);
             return true;
         } catch (MessagingException e) {
             System.err.println("Error sending email: " + e.getMessage());
             return false;
         }
     }
     
     public boolean sendEmail(String recipientEmail, String emailSubject, String emailBody) {
         try {
             // Create a message
             Message message = new MimeMessage(session);
             message.setFrom(new InternetAddress(senderEmail)); // Correctly setting the sender email
             message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
             message.setSubject(emailSubject);
             message.setContent(emailBody, "text/html");


             // Use Transport object for better control
             Transport transport = session.getTransport("smtp");
             transport.connect();
             transport.sendMessage(message, message.getAllRecipients());
             transport.close();

             
             return true;
         } catch (MessagingException e) {
             System.err.println("Error sending email: " + e.getMessage());
             return false;
         }
     }
     
     
     
    
	/*
	public boolean sendEmail(String recipientEmail,String emailSubject, String emailBody)
	{
		Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", "mail.pragyagirlsschool.com");
        //props.put("mail.debug", "true");
        // Create a mail session with the specified properties
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create a message
        	
        	
        	
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(emailSubject);
            message.setContent(emailBody, "text/html");
            // message.setContent();

            // Send the message
            Transport.send(message);

            System.out.println("Email sent successfully.");
            return true;

        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
            return false;
        }
    }
	 
	*/
	
	public boolean sendEmail1(String recipientEmail, String emailSubject, String emailBody, String imagePath) {
	    Properties props = new Properties();
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.port", port);
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");

	    // Create a mail session with the specified properties
	    Session session = Session.getInstance(props, new Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(senderEmail, senderPassword);
	        }
	    });

	    try {
	        // Create a MimeMessage
	        Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(senderEmail));
	        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
	        message.setSubject(emailSubject);

	        // Create a multipart message for email content (text + image)
	        Multipart multipart = new MimeMultipart();

	        // Part one is the HTML text
	        BodyPart messageBodyPart = new MimeBodyPart();
	        String htmlText = emailBody + "<br><img src='cid:image'>";
	        messageBodyPart.setContent(htmlText, "text/html");
	        multipart.addBodyPart(messageBodyPart);

	        // Part two is the image
	        messageBodyPart = new MimeBodyPart();
	        DataSource fds = new FileDataSource(imagePath);
	        messageBodyPart.setDataHandler(new DataHandler(fds));
	        messageBodyPart.setHeader("Content-ID", "<image>");
	        multipart.addBodyPart(messageBodyPart);

	        // Set the multipart message to the email message
	        message.setContent(multipart);

	        // Send the message
	        Transport.send(message);

	        System.out.println("Email sent successfully with embedded image.");
	        return true;

	    } catch (MessagingException e) {
	        System.out.println("Error sending email: " + e.getMessage());
	        return false;
	    }
	}

	public boolean sendEmailWithAttachment(String recipientEmail, String emailSubject, String emailBody, MultipartFile birthCertificate ,
           MultipartFile lastResult,
             MultipartFile parentAadhar,
             MultipartFile studentAadhar,
            MultipartFile bankDoc,
             MultipartFile cast,
             MultipartFile transferCertificate,
              MultipartFile profile,
            MultipartFile sssmid) {
	    Properties props = new Properties();
	    props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", "mail.pragyagirlsschool.com");
     //   props.put("mail.debug", "true");

	    // Create a mail session with the specified properties
	    Session session = Session.getInstance(props, new Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(senderEmail, senderPassword);
	        }
	    });

	    try {
	        // Create a message
	        MimeMessage message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(senderEmail));
	        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
	        message.setSubject(emailSubject);

	        // Create the message body part
	        MimeBodyPart messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setContent(emailBody, "text/html");

	        
	     // Create the multipart message
	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(messageBodyPart);
	        
	       
	        
	        
	         multipart.addBodyPart(createAttachment(birthCertificate));
	         multipart.addBodyPart(createAttachment( lastResult));
	         multipart.addBodyPart(createAttachment(parentAadhar));
	         multipart.addBodyPart(createAttachment(studentAadhar));
	         multipart.addBodyPart(createAttachment(bankDoc));
	         multipart.addBodyPart(createAttachment( cast));
	         multipart.addBodyPart(createAttachment(transferCertificate));
	         multipart.addBodyPart(createAttachment(profile));
	         multipart.addBodyPart(createAttachment(sssmid));
	         
	        // Set the multipart message to the email message
	        message.setContent(multipart);

	        // Send the message
	        Transport.send(message);

	        System.out.println("Email with attachment sent successfully.");
	        return true;

	    } catch (Exception e) {
	        System.out.println("Error sending email with attachment: " + e.getMessage());
	        return false;
	    }
	}


	 public MimeBodyPart createAttachment(MultipartFile file) throws Exception {
	        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
	        DataSource source = new ByteArrayDataSource(file.getBytes(), file.getContentType());
	        attachmentBodyPart.setDataHandler(new DataHandler(source));
	        attachmentBodyPart.setFileName(file.getOriginalFilename());
	        return attachmentBodyPart;
	    }
	 
	 
	 public  String generateOTP(int length) {
	        StringBuilder otp = new StringBuilder(length);
	        for (int i = 0; i < length; i++) {
	            int index = random.nextInt(DIGITS.length());
	            otp.append(DIGITS.charAt(index));
	        }
	        return otp.toString();
	    }
}
