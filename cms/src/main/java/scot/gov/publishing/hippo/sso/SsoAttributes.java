package scot.gov.publishing.hippo.sso;

public final class SsoAttributes {

    static final String SSO_ID = SsoAttributes.class.getName() + ".sso.id";

    static final String SSO_GROUPS = SsoAttributes.class.getName() + ".sso.groups";

    static final String SSO_NAME = SsoAttributes.class.getName() + ".sso.name";

    static final String SSO_GIVEN_NAME = SsoAttributes.class.getName() + ".sso.givenName";

    static final String SSO_FAMILY_NAME = SsoAttributes.class.getName() + ".sso.familyName";

    private SsoAttributes() {
        // Private constructor
    }
}
