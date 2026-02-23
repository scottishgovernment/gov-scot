package scot.gov.publishing.hippo.sso;

public final class SsoAttributes {

    static final String SSO_ID = SsoAttributes.class.getName() + ".id";

    static final String SSO_EMAIL = SsoAttributes.class.getName() + ".email";

    static final String SSO_NAME = SsoAttributes.class.getName() + ".name";

    static final String SSO_GIVEN_NAME = SsoAttributes.class.getName() + ".givenName";

    static final String SSO_FAMILY_NAME = SsoAttributes.class.getName() + ".familyName";

    private SsoAttributes() {
        // Private constructor
    }
}
