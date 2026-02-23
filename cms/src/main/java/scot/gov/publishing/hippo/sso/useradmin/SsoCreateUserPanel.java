package scot.gov.publishing.hippo.sso.useradmin;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.DefaultMarkupCacheKeyProvider;
import org.apache.wicket.markup.DefaultMarkupResourceStreamProvider;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.resource.IResourceStream;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.cms.admin.users.CreateUserPanel;
import org.hippoecm.frontend.plugins.cms.admin.users.User;
import org.hippoecm.frontend.util.EventBusUtils;
import org.onehippo.cms7.event.HippoEventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;

/**
 * Customised panel for creating new user accounts.
 * This panel removes the password fields and validation from the new user form.
 * The use of password reset or single sign-on obviate the need for setting a
 * password when the user is created.
 */
public class SsoCreateUserPanel extends CreateUserPanel
        implements IMarkupResourceStreamProvider, IMarkupCacheKeyProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SsoCreateUserPanel.class);

    public SsoCreateUserPanel(final String id, final IBreadCrumbModel breadCrumbModel,
                              final IPluginContext context, final IPluginConfig config) {
        super(id, breadCrumbModel, context, config);

        Form<?> form = (Form<?>) get("form");

        // Remove only the EqualPasswordInputValidator
        form.getFormValidators().stream()
                .filter(v -> v instanceof EqualPasswordInputValidator)
                .findFirst()
                .ifPresent(form::remove);

        // Hide password fields
        form.get("password").setVisible(false);
        form.get("password-check").setVisible(false);

        // Replace the create button with one that doesn't save the password
        AjaxButton createButton = new CreateButton(form);

        form.replace(createButton);
        form.setDefaultButton(createButton);
    }

    @Override
    public void setPassword(String password) {
        // No-op - ignore password setting
    }

    @Override
    public void setPasswordCheck(String passwordCheck) {
        // No-op - ignore password check setting
    }

    private class CreateButton extends AjaxButton {

        public CreateButton(Form<?> form) {
            super("create-button", form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target) {
            // Get the user model from the form
            User user = (User) getForm().getModelObject();
            final String username = user.getUsername();

            try {
                final String userSecurityProviderName = getSelectedProvider();
                final String defaultProvider = "internal";

                final String providerToUse = StringUtils.isNotBlank(userSecurityProviderName)
                        ? userSecurityProviderName
                        : defaultProvider;

                user.create(providerToUse);
                // Saving the password would go here...

                EventBusUtils.post("create-user", HippoEventConstants.CATEGORY_USER_MANAGEMENT,
                        "created user " + username);

                String infoMsg = getString("user-created", new Model<>(user));
                activateParentAndDisplayInfo(infoMsg);
            } catch (RepositoryException e) {
                target.add(SsoCreateUserPanel.this);
                error(getString("user-create-failed", new Model<>(user)));
                LOG.error("Unable to create user '{}' : ", username, e);
            }
        }

        @Override
        protected void onError(AjaxRequestTarget target) {
            target.add(SsoCreateUserPanel.this);
        }
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        Class<?> containerType;
        if (containerClass == CreateUserPanel.class) {
            // Ignore CreateUserPanel markup - markup for this class replaces it.
            // That is, markup for this class extends that of the grandparent class.
            containerType = containerClass.getSuperclass();
        } else {
            containerType = containerClass;
        }
        return new DefaultMarkupResourceStreamProvider()
                .getMarkupResourceStream(container, containerType);
    }

    @Override
    public String getCacheKey(MarkupContainer container, Class<?> containerClass) {
        IMarkupCacheKeyProvider cacheKeyProvider = new DefaultMarkupCacheKeyProvider();
        String defaultCacheKey = cacheKeyProvider.getCacheKey(container, containerClass);
        if (containerClass == CreateUserPanel.class) {
            // Unique cache key so we don't collide with Parent's normal cached markup
            return getClass().getName() + "::" + defaultCacheKey;
        }
        return defaultCacheKey;
    }

}
