package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;

import scot.gov.www.beans.Topic;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * the stats about page is very similar to a Topic page.  However, the publications should be as they are on the homepage
 * stats panel.
 */
public class StatsAboutComponent extends TopicComponent {

    
    
    @Override

    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

    HstRequestContext context = request.getRequestContext();
    HippoBean scope = context.getSiteContentBaseBean();
    TopicsList(scope.getBean("topics/"), request);
    
    }

        void populatePublications(HippoBean base, Topic topic, HstRequest request) {
        HomeComponent.populateStatsAndReasearch(base, request);
  
    }
        void TopicsList(HippoBean scope, HstRequest request) {
        List<Topic> topics = scope.getChildBeans(Topic.class);
        Comparator<Topic> titleComparator = Comparator.comparing(Topic::getTitle);
                Collections.sort(topics, titleComparator);
        request.setAttribute("topics", topics);
    }

}