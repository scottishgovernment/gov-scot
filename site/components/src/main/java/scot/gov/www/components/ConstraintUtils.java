package scot.gov.www.components;

import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstComponentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.DynamicIssue;
import scot.gov.www.beans.Issue;
import scot.gov.www.beans.Topic;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;
import java.util.stream.Collectors;

import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;
import static scot.gov.www.components.FilteredResultsComponent.GOVSCOT_TITLE;

public class ConstraintUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ConstraintUtils.class);

    private ConstraintUtils() {
        // hide public constructor
    }

    public static Constraint topicsConstraint(Collection<String> topics) {
        List<String> topicIds = topicIds(topics);
        if (topicIds.isEmpty()) {
            return null;
        }

        List<Constraint> constraintList = new ArrayList<>();
        for (String topicId : topicIds) {
            constraintList.add(or(constraint("govscot:topics/@hippo:docbase").equalTo(topicId)));
        }

        return ConstraintBuilder.or(constraintList.toArray(new Constraint[constraintList.size()]));
    }

    public static Constraint publicationTypeConstraint(Collection<String> publicationTypes) {

        if (publicationTypes.isEmpty()) {
            return null;
        }

        List<Constraint> constraints = publicationTypes.stream().map(ConstraintUtils::publicationTypeContraint).collect(Collectors.toList());
        return ConstraintBuilder.or(constraints.toArray(new Constraint[constraints.size()]));
    }

    static Constraint publicationTypeContraint(String publicationType) {
        return constraint("govscot:publicationType").equalTo(publicationType);
    }

    private static List<String> topicIds(Collection<String> topics) {
        List<String> topicIds = new ArrayList<>();

        try {
            Session session = RequestContextProvider.get().getSession();
            Node topicsNode = session.getNode("/content/documents/govscot/topics");

            if (topicsNode == null) {
                return Collections.emptyList();
            }
            populateTopics(topicIds, topicsNode, topics);

            return topicIds;
        } catch (RepositoryException e) {
            throw new HstComponentException("Failed to get topics", e);
        }
    }

    private static void populateTopics(List<String> topicIds, Node topicsNode, Collection<String> topics) throws RepositoryException {
        try {
            HstQuery query = HstQueryBuilder.create(topicsNode)
                    .ofTypes(Topic.class, Issue.class, DynamicIssue.class)
                    .orderByAscending(GOVSCOT_TITLE)
                    .build();

            HstQueryResult result = query.execute();
            HippoBeanIterator hippoBeans = result.getHippoBeans();

            while (hippoBeans.hasNext()) {
                Node topicNode = hippoBeans.nextHippoBean().getNode();

                if (isRequired(topicNode, topics)) {
                    topicIds.add(topicNode.getParent().getIdentifier());
                }
            }
        } catch (QueryException e) {
            LOG.error("Failed to get topics", e);
        }
    }


    private static boolean isRequired(Node topicNode, Collection<String> requiredTitles) throws RepositoryException {
        String title = nodeTitle(topicNode);
        return requiredTitles.contains(title);
    }

    private static String nodeTitle(Node node) throws RepositoryException {
        Property titleProperty = node.getProperty(GOVSCOT_TITLE);
        return titleProperty.getString();
    }
}
