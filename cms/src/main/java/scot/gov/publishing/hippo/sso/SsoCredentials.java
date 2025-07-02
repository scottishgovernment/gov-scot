package scot.gov.publishing.hippo.sso;

import javax.jcr.SimpleCredentials;
import java.util.ArrayList;
import java.util.List;

public class SsoCredentials {

    private final SimpleCredentials credentials;

    public SsoCredentials(SimpleCredentials creds) {
        this.credentials = creds;
    }

    public String getUserId() {
        return credentials.getUserID();
    }

    public List<String> getGroups() {
        return getAttribute(new ArrayList<String>() {}.getClass(), "");
//        return (List<String>) credentials.getAttribute(SsoAttributes.SAML_GROUPS);
    }

    private <T> T getAttribute(Class<T> t, String name) {
        return t.cast(credentials.getAttribute(name));
    }

}
