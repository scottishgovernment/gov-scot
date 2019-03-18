package scot.gov.www.validation;

import org.apache.wicket.model.IModel;
import org.hippoecm.frontend.editor.validator.plugins.AbstractCmsValidator;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.frontend.validation.IFieldValidator;
import org.hippoecm.frontend.validation.ValidationException;
import org.hippoecm.frontend.validation.Violation;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.HashSet;
import java.util.Set;

/**
 * Validate that the govscot:slug is unique.
 *
 * https://www.onehippo.org/library/concepts/plugins/create-a-custom-field-validator.html
 *
 */
public class PublicationSlugValidator extends AbstractCmsValidator {

    public PublicationSlugValidator(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    /**
     * Used to check the field type; e.g. if the field is a String type field.
     */
    @Override
    public void preValidation(IFieldValidator type) throws ValidationException {
        if (!"String".equals(type.getFieldType().getType())) {
            throw new ValidationException("Invalid validation exception; cannot validate non-string as a slug");
        }
    }

    /**
     * The method where you perform the check and return the violations.
     */
    @Override
    public Set<Violation> validate(
            IFieldValidator fieldValidator,
            JcrNodeModel model,
            IModel childModel)
                throws ValidationException {

        Node node = model.getNode();

        Set<Violation> violations = new HashSet<>();
        String candidateSlug = (String) childModel.getObject();
        try {
            if (!isValid(candidateSlug, node)) {
                violations.add(fieldValidator.newValueViolation(childModel, getTranslation()));
            }
            return violations;
        } catch (RepositoryException e) {
            throw new ValidationException(e);
        }
    }

    /**
     * The slug is valid if no other nodes share this slug
     * (with the exception of ones that share the same handle).
     */
    private boolean isValid(String candidateSlug, Node node) throws RepositoryException {
        Session session = UserSession.get().getJcrSession();
        String sql = String.format("SELECT * FROM govscot:SimpleContent WHERE govscot:slug = '%s'", candidateSlug);
        Query query = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
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
