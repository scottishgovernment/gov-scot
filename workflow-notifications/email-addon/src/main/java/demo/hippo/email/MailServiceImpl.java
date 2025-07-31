package demo.hippo.email;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import javax.mail.Session;
import java.util.Properties;

public class MailServiceImpl implements MailService {

    private String cmsRoot;

    @Override
    public void sendMail(final String to, final String from, final String subject, final String html, final String text) throws EmailException {
        sendMail(new String[]{to}, from, subject, html, text);
    }

    @Override
    public void sendMail(final String[] to, final String from, final String subject, final String html, final String text) throws EmailException {
        final HtmlEmail email = new HtmlEmail();

        final Session session = getSession();
        if (session == null) {
            throw new EmailException("Unable to send mail; no mail session available");
        }

        email.setMailSession(session);
        email.addTo(to);
        email.setFrom(from);
        email.setSubject(subject);
        email.setHtmlMsg(html);
        email.setTextMsg(text);
        email.send();
    }

    protected Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.host", "127.0.0.1");
        props.put("mail.smtp.port", "2525");
        return Session.getInstance(props);
    }

    public String getCmsRoot() {
        return cmsRoot;
    }

    @Override
    public void setCmsRoot(final String cmsRoot) {
        this.cmsRoot = cmsRoot;
    }
}
