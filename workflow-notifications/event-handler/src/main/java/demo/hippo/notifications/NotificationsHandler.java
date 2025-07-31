package demo.hippo.notifications;

import demo.hippo.email.FreemarkerParser;
import demo.hippo.email.MailService;
import demo.hippo.email.TemplateService;
import demo.hippo.notifications.util.HippoUtils;
import demo.hippo.notifications.util.HtmlDiffGenerator;
import demo.hippo.notifications.util.PreviewClient;
import demo.hippo.notifications.util.PreviewClientUtil;
import org.apache.commons.mail.EmailException;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificationsHandler {

    private static final Logger log = LoggerFactory.getLogger(NotificationsHandler.class);

    public boolean sendNotification(final String notificationSenderFullName, final List<EmailAddress> notificationRecipients, Node node, String methodName) {
        final MailService mailService = HippoServiceRegistry.getService(MailService.class);
        final TemplateService templateService = HippoServiceRegistry.getService(TemplateService.class);
        final String textTemplate = templateService.getTemplateByName(methodName + "Text.ftl");
        final String htmlTemplate = templateService.getTemplateByName(methodName + "Html.ftl");
        final Map<String, Object> arguments = new HashMap<>();
        try {
            final String nodeName = node.getName();
            final String documentName = node.hasProperty("hippo:name") ? node.getProperty("hippo:name").getString() : nodeName;

            arguments.put("nodename", nodeName);
            arguments.put("documentname", documentName);
            arguments.put("path", node.getPath());
            arguments.put("cmsRoot", mailService.getCmsRoot());
            arguments.put("user", notificationSenderFullName);
            arguments.put("diff", getDiffs(node, methodName));

            final String emailBodyText = FreemarkerParser.getBodyForTemplate(textTemplate, arguments, false);
            final String emailBodyHtml = FreemarkerParser.getBodyForTemplate(htmlTemplate, arguments, false);

            for (EmailAddress emailAddress : notificationRecipients) {

                simulateSendEmail(emailAddress, emailBodyText);

                try {
                    mailService.sendMail(emailAddress.getEmailAddress(), "no-reply@onehippo.com", "Moderation request for - " + documentName, emailBodyHtml, emailBodyText);
                } catch (EmailException e) {
                    log.error("Error sending email to", e);
                }
            }
            return true;

        } catch (Exception e) {
            log.error("Error sending notifications", e);
            return false;
        }
    }

    public boolean sendPreviewNotification(final String notificationSenderFullName, final List<EmailAddress> notificationRecipients, Node node, String uuid, String methodName) {
        final MailService mailService = HippoServiceRegistry.getService(MailService.class);
        final TemplateService templateService = HippoServiceRegistry.getService(TemplateService.class);
        final String textTemplate = templateService.getTemplateByName(methodName + "Text.ftl");
        final String htmlTemplate = templateService.getTemplateByName(methodName + "Html.ftl");
        final String baseUrl = templateService.getPropertyByName(methodName + "Html.ftl", "preview.link.base");
        final Map<String, Object> arguments = new HashMap<>();
        try {
            final String nodeName = node.getName();
            final String documentName = node.hasProperty("hippo:name") ? node.getProperty("hippo:name").getString() : nodeName;


            arguments.put("nodename", nodeName);
            arguments.put("documentname", documentName);

            final PreviewClient previewClient = PreviewClientUtil.getPreviewClient(baseUrl);
            final String identifier = node.getIdentifier();
            final String previewLink = previewClient.getDocumentUrl(identifier);

            arguments.put("previewLink", previewLink + "?workflowId=" + uuid);
            arguments.put("user", notificationSenderFullName);
            arguments.put("diff", getDiffs(node, methodName));

            final String emailBodyText = FreemarkerParser.getBodyForTemplate(textTemplate, arguments, false);
            final String emailBodyHtml = FreemarkerParser.getBodyForTemplate(htmlTemplate, arguments, false);

            for (EmailAddress emailAddress : notificationRecipients) {

                simulateSendEmail(emailAddress, emailBodyText);

                try {
                    mailService.sendMail(emailAddress.getEmailAddress(), "no-reply@onehippo.com", "Moderation request for - " + documentName, emailBodyHtml, emailBodyText);
                } catch (EmailException e) {
                    log.error("Error sending email to", e);
                }
            }
            return true;

        } catch (Exception e) {
            log.error("Error sending notifications", e);
            return false;
        }
    }

    private String getDiffs(final Node node, String methodName) throws RepositoryException, SAXException, IOException, TransformerConfigurationException {
        Node publishedVariant = HippoUtils.getPublishedVariant(node);
        Node unpublishedVariant = HippoUtils.getUnpublishedVariant(node);

        if (publishedVariant == null) {
            if (NotificationsEventsListener.METHOD_NAME_REQUESTREVIEW.equals(methodName)) {
                return "None, document is new";
            } else if (NotificationsEventsListener.METHOD_NAME_IMPORTTRANSLATION.equals(methodName)) {
                publishedVariant = HippoUtils.getSourceDocumentHandle(node);
            }
        }
        return HtmlDiffGenerator.diff(publishedVariant, unpublishedVariant, Locale.US, "demo:*");
    }

    private void simulateSendEmail(final EmailAddress emailAddress, final String text) {
        System.out.println("\n\n\n******************************************************************");
        System.out.println("Sending the following email to " + emailAddress.getName() + " <" + emailAddress.getEmailAddress() + ">");
        System.out.println(text);
        System.out.println("******************************************************************\n\n");
    }

    public static class EmailAddress {

        private final String name;
        private final String emailAddress;

        public EmailAddress(String name, String emailAddress) {
            this.name = name;
            this.emailAddress = emailAddress;
        }

        public String getName() {
            return name;
        }

        public String getEmailAddress() {
            return emailAddress;
        }
    }


}
