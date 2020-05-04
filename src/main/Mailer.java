package main;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * <h1>class Mailer</h1>
 * <p>Actually the <b>G-mailer</b></p>
 */
public class Mailer {
    private String subject, content, senderMail, senderPsswd;
    public static final String DEVELOPERS_MAIL = "utgdashboard@gmail.com";
    public static final String FEEDBACK = "Feedback";
    public static final String DEVELOPERS_REQUEST = "Track";
    public static int REVIEWS_COUNT_TODAY;//it's reset @anotherDay() in Board


    public Mailer(String subject, String content){
        this.subject = subject;
        this.content = content;
        this.senderMail = DEVELOPERS_MAIL;
        this.senderPsswd = "wdrammeh20";
    }

    /**
     * <p>Condition is checked for <b>Reviews / Feedbacks</b> only</p>
     */
    public static boolean canSend(){
        return REVIEWS_COUNT_TODAY <= 3;
    }

    public static void reportQuickFeedbackDenial(){
        App.promptWarning("Feedback Denied","Sorry, you can send no more than 3 reviews per day.\nPlease try again later.");
    }

    public boolean sendAs(String type) {
        final String host = "smtp.gmail.com";

        final Properties properties = System.getProperties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", senderMail);
        properties.put("mail.smtp.password", senderPsswd);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        final Session session = Session.getDefaultInstance(properties);

        try {
            final MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderMail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(DEVELOPERS_MAIL));
            message.setSubject(subject);
            message.setText(content);

            Transport transport = session.getTransport("smtp");
            transport.connect(host, senderMail, senderPsswd);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            if (type.equals(FEEDBACK)) {
                App.promptPlain("Feedback Sent","Message sent successfully. Thank you for the feedback!");
            }
            REVIEWS_COUNT_TODAY++;
            return true;
        } catch (MessagingException mex) {
            if (type.equals(FEEDBACK)) {
                App.signalError("Feedback Unsent","Sorry, an error was encountered while sending the feedback.\n" +
                        "Please try again later.");
            }
            return false;
        }
    }

}
