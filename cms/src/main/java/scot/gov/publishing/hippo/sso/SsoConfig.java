package scot.gov.publishing.hippo.sso;

import org.hippoecm.hst.core.container.ComponentManager;
import org.hippoecm.hst.core.container.ContainerConfiguration;
import org.hippoecm.hst.site.HstServices;

public record SsoConfig(
        Mode mode,
        Redirect redirect,
        Form form
) {

    static SsoConfig get() {
        ComponentManager componentManager = HstServices.getComponentManager();
        ContainerConfiguration config = componentManager.getContainerConfiguration();

        Mode mode = enumValue(config, "sso.mode", Mode.OFF);

        Redirect redirect = switch (mode) {
            case OFF -> Redirect.MANUAL;
            case REQUIRED, OPTIONAL -> enumValue(config, "sso.redirect", Redirect.AUTO);
        };

        Form form = switch (mode) {
            case REQUIRED -> Form.SSO;
            case OFF      -> Form.NATIVE;
            case OPTIONAL -> enumValue(config, "sso.form", Form.REVEAL);
        };

        return new SsoConfig(mode, redirect, form);
    }

    static <T extends Enum<T>> T enumValue(ContainerConfiguration config, String name, T defaultValue) {
        String value = config.getString(name, defaultValue.name()).toUpperCase();
        return Enum.valueOf(defaultValue.getDeclaringClass(), value);
    }

    enum Mode     {
        REQUIRED,
        OPTIONAL,
        OFF
    }

    enum Redirect {
        AUTO,
        MANUAL
    }

    enum Form {
        NATIVE,
        REVEAL,
        EXPANDED,
        // SSO is internal-only for REQUIRED
        SSO
    }

}
