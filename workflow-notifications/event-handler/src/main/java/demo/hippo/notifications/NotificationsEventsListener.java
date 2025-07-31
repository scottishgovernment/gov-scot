package demo.hippo.notifications;

import demo.hippo.notifications.util.HippoUtils;
import org.apache.commons.lang.StringUtils;
import org.onehippo.cms7.event.HippoEventConstants;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.eventbus.HippoEventListenerRegistry;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;
import org.onehippo.repository.security.Group;
import org.onehippo.repository.security.SecurityService;
import org.onehippo.repository.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NotificationsEventsListener extends AbstractReconfigurableDaemonModule {

    private static final Logger log = LoggerFactory.getLogger(NotificationsEventsListener.class);
    public static final String METHOD_NAME_REQUESTPUBLICATION = "requestPublication";
    public static final String METHOD_NAME_REQUESTREVIEW = "requestReview";
    public static final String METHOD_NAME_REQUESTREVIEW_ONLINE = "requestReviewOnline";
    public static final String METHOD_NAME_IMPORTTRANSLATION = "importTranslation";

    private static final String IMPORTTRANSLATION_INTERACTION = "translations-workflow:translations-workflow:importTranslation";


    private static final List<String> ENABLED_METHOD_NAMES =
            Arrays.asList(METHOD_NAME_REQUESTPUBLICATION, METHOD_NAME_REQUESTREVIEW, METHOD_NAME_REQUESTREVIEW_ONLINE);

    @Override
    protected void doConfigure(final Node moduleConfig) {
    }

    @Override
    protected void doInitialize(final Session jcrSession) {
        HippoEventListenerRegistry.get().register(this);
    }

    @Subscribe
    public void handleEvent(final HippoWorkflowEvent event) {
        if (HippoEventConstants.CATEGORY_WORKFLOW.equals(event.category())) {

            if (ENABLED_METHOD_NAMES.contains(event.action())) {
                try {
                    final String handleUuid = event.subjectId();
                    final Node documentHandleNode = session.getNodeByIdentifier(handleUuid);

                    if (METHOD_NAME_REQUESTREVIEW.equals(event.action())) {
                        final String recipientGroupName = String.valueOf(((List<String>) event.getValues().get("arguments")).get(0));
                        final List<NotificationsHandler.EmailAddress> notificationRecipients = getEmailsOfGroupMembers(recipientGroupName);
                        final String notificationSenderFullName = NotificationsEventsListener.this.getFullName(event.user());
                        new NotificationsHandler().sendNotification(notificationSenderFullName, notificationRecipients, documentHandleNode, METHOD_NAME_REQUESTREVIEW);
                    }

                    if (METHOD_NAME_REQUESTREVIEW_ONLINE.equals(event.action())) {
                        final List<NotificationsHandler.EmailAddress> notificationRecipients = new ArrayList<>();
                        final String recipientEmail = String.valueOf(((List<String>) event.getValues().get("arguments")).get(0));
                        final String uuid = String.valueOf(((List<String>) event.getValues().get("arguments")).get(1));
                        notificationRecipients.add(new NotificationsHandler.EmailAddress(recipientEmail, recipientEmail));
                        final String notificationSenderFullName = NotificationsEventsListener.this.getFullName(event.user());

                        new NotificationsHandler().sendPreviewNotification(notificationSenderFullName, notificationRecipients, documentHandleNode, uuid, METHOD_NAME_REQUESTREVIEW_ONLINE);
                    }

                    if (METHOD_NAME_REQUESTPUBLICATION.equals(event.action())) {
                        if (IMPORTTRANSLATION_INTERACTION.equals(event.interaction())) {
                            Node sourceDocumentHandleNode = HippoUtils.getSourceDocumentHandle(documentHandleNode);
                            if (sourceDocumentHandleNode != null) { //otherwise we can't find the user to send the notification to
                                final List<NotificationsHandler.EmailAddress> notificationRecipients = new ArrayList<>();
                                final String recipientUsername = HippoUtils.getUnpublishedVariant(sourceDocumentHandleNode).getProperty("hippostdpubwf:lastModifiedBy").getString();
                                final String recipientFullName = NotificationsEventsListener.this.getFullName(recipientUsername);
                                final String recipientEmail = NotificationsEventsListener.this.getUserEmail(recipientUsername);
                                notificationRecipients.add(new NotificationsHandler.EmailAddress(recipientFullName, recipientEmail));
                                final String notificationSenderFullName = "Hippo System user";

                                new NotificationsHandler().sendNotification(notificationSenderFullName, notificationRecipients, documentHandleNode, METHOD_NAME_IMPORTTRANSLATION);
                            } else {
                                log.warn("Can't retrieve source document from variant {}", documentHandleNode.getPath());
                            }
                        }
                    }
                } catch (RepositoryException e) {
                    log.error("Error resolving url ", e);
                }
            }
        }
    }

    private List<NotificationsHandler.EmailAddress> getEmailsOfGroupMembers(final String groupName) {
        try {
            final Group group = getSecurityService().getGroup(groupName);
            final ArrayList<NotificationsHandler.EmailAddress> emailAddresses = new ArrayList<>();
            for (String member : group.getMembers()) {
                final User user = getSecurityService().getUser(member);
                final String email = user.getEmail();
                if (StringUtils.isNotBlank(email)) {
                    emailAddresses.add(new NotificationsHandler.EmailAddress(constructFullName(user.getFirstName(), user.getLastName(), user.getId()), email));
                }
            }
            return emailAddresses;
        } catch (RepositoryException ex) {
            log.error(ex.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    protected void doShutdown() {
        HippoEventListenerRegistry.get().unregister(this);
    }

    private String getFullName(final String username) throws RepositoryException {
        final User user = getSecurityService().getUser(username);
        return constructFullName(user.getFirstName(), user.getLastName(), username);
    }

    private String constructFullName(final String firstName, final String lastName, final String defaultName) {
        final StringBuilder fullName = new StringBuilder();
        if (StringUtils.isNotBlank(firstName)) {
            fullName.append(firstName);
        }
        if (StringUtils.isNotBlank(lastName)) {
            fullName.append(" ").append(lastName);
        }
        if (fullName.length() == 0) {
            fullName.append(defaultName);
        }
        return fullName.toString();
    }

    private String getUserEmail(final String username) throws RepositoryException {
        return getSecurityService().getUser(username).getEmail();
    }

    private SecurityService getSecurityService() {
        return HippoServiceRegistry.getService(SecurityService.class);
    }
}
