package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import scot.gov.www.beans.Topic;

/**
 * the stats about page is very similar to a Topic page.  However, the publications should be as they are on the homepage
 * stats panel.
 */
public class StatsAboutComponent extends TopicComponent {

    @Override
    void populatePublications(HippoBean base, Topic topic, HstRequest request) {
        HomeComponent.populatePublications(base, request);
    }
}
