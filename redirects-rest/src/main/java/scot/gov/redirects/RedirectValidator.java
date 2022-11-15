package scot.gov.redirects;

import org.apache.commons.validator.routines.UrlValidator;

import java.util.ArrayList;
import java.util.List;

import static net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.containsNone;
import static net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.isBlank;

public class RedirectValidator extends UrlValidator {

    public RedirectValidator() {
        super(new String [] {"http", "https"});
    }

    public List<String> validateRedirects(List<Redirect> redirects) {
        List<String> violations = new ArrayList<>();
        for (Redirect redirect : redirects) {
            validateRedirect(redirect, violations);
        }
        return violations;
    }

    void validateRedirect(Redirect redirect, List<String> violations) {
        if (!validFrom(redirect)) {
            violations.add("Invalid From url: " + redirect.getFrom());
        }
        if (!validTo(redirect)) {
            violations.add("Invalid To url: " + redirect.getTo());
        }
    }

    boolean validFrom(Redirect redirect) {
        return isValidPath(redirect.getFrom())
                && !isBlank(redirect.getFrom())
                && containsNone(redirect.getFrom(), " %:[]*|\t\n");
    }

    boolean validTo(Redirect redirect) {
        return isValid(redirect.getTo()) || isValidPath(redirect.getTo());
    }

}
