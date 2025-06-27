package scot.gov.publishing.hippo.sso;

public final class SsoAttributes {

    static final String SAML_ID = SsoAttributes.class.getName() + ".saml.id";

    static final String SAML_GROUPS = SsoAttributes.class.getName() + ".saml.groups";

    private SsoAttributes() {
        // Private constructor
    }
}
