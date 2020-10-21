package scot.gov.www.validation;

import java.util.Optional;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.hippoecm.frontend.session.UserSession;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.ValidationContextException;
import org.onehippo.cms.services.validation.api.Validator;
import org.onehippo.cms.services.validation.api.Violation;
import org.slf4j.LoggerFactory;

/**
 * Validate that the govscot:slug is unique.
 *
 * https://documentation.bloomreach.com/library/concepts/plugins/create-a-custom-field-validator.html
 *
 */
public class SlugValidator implements Validator<String> {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SlugValidator.class);

    public SlugValidator(final Node config) {

    }

    @Override
    public Optional<Violation> validate(final ValidationContext context, final String value) {
        try {
            if (isValid(context.getDocumentNode().getPrimaryNodeType().getName(), value, context.getDocumentNode())) {
                return Optional.empty();
            }
        } catch (RepositoryException e) {
            throw new ValidationContextException("", e);
        }
        return Optional.of(context.createViolation());
    }

    /**
     * The slug is valid if no other nodes share this slug
     * (with the exception of ones that share the same handle).
     */
    public boolean isValid(String documentType, String candidateSlug, Node node) throws RepositoryException {
        Session session = UserSession.get().getJcrSession();
        String sql = String.format("SELECT * FROM %s WHERE govscot:slug = '%s'", documentType, candidateSlug);
        Query query = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
        LOG.info("Query being run: {}", sql);
        QueryResult result = query.execute();

        // if any of the results do not belong to the same handle then this slug is already used elsewhere
        NodeIterator it = result.getNodes();
        while (it.hasNext()) {
            Node next = it.nextNode();
            if (!next.getParent().getIdentifier().equals(node.getParent().getIdentifier())) {
                return false;
            }
        }
        return true;
    }
}
