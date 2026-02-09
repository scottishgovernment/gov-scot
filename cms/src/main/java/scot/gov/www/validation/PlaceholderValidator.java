package scot.gov.www.validation;

import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Validator;
import org.onehippo.cms.services.validation.api.Violation;

import java.util.Optional;

public class PlaceholderValidator implements Validator<String> {

    @Override
    public Optional<Violation> validate(ValidationContext context, String value) {
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }

        if (value.matches("(?s).*\\[.*\\].*")) {
            return Optional.of(context.createViolation());
        }

        return Optional.empty();
    }
}