package scot.gov.publishing.hippo.sso;

import org.hippoecm.hst.core.container.ComponentManager;
import org.hippoecm.hst.core.container.ContainerConfiguration;
import org.hippoecm.hst.site.HstServices;

public record SsoConfig(
        Mode mode,
        Default enabled
) {

    static SsoConfig get() {
        ComponentManager componentManager = HstServices.getComponentManager();
        ContainerConfiguration config = componentManager.getContainerConfiguration();

        String modeString = config.getString("sso.mode", Mode.OFF.name());
        Mode mode = Mode.valueOf(modeString.toUpperCase());

        Default enabled = switch (mode) {
            case REQUIRED -> Default.ON;
            case OFF -> Default.OFF;
            case OPTIONAL -> {
                String string = config.getString("sso.default", Default.ON.name());
                yield Default.valueOf(string.toUpperCase());
            }
        };

        return new SsoConfig(mode, enabled);
    }

    enum Mode {
        REQUIRED,
        OPTIONAL,
        OFF,
    }

    enum Default {
        ON,
        OFF,
    }

}
