package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import scot.gov.www.beans.*;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;


public class TopicsComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        HippoBean base = request.getRequestContext().getSiteContentBaseBean();
        HippoBean topicsBean = base.getBean("topics/");
        List<SimpleContent> topics = topicsBean.getChildBeans(SimpleContent.class);
        Map<String, List<SimpleContent>> topicsByFirstLetter  = topics
                .stream()
                .filter(this::include)
                .collect(groupingBy(topic -> String.format("%s", topic.getTitle().toUpperCase().charAt(0))));
        List<TopicsAndLetter> topicsAndLetters = new ArrayList<>();
        for (Map.Entry<String, List<SimpleContent>> entry : topicsByFirstLetter.entrySet()) {
            TopicsAndLetter topicAndLetter = new TopicsAndLetter();
            topicAndLetter.setLetter(entry.getKey());
            topicAndLetter.setTopics(entry.getValue());
            topicsAndLetters.add(topicAndLetter);
        }
        Collections.sort(topicsAndLetters, comparing(TopicsAndLetter::getLetter));
        request.setAttribute("topicsByLetter", topicsAndLetters);
    }

    private boolean include(SimpleContent item) {
        if (item instanceof Topic) {
            return true;
        }

        if (item instanceof DynamicIssue) {
            DynamicIssue issue = (DynamicIssue) item;
            return issue.getShowOnTopicsLandingPage();
        }
        if (item instanceof Issue) {
            Issue issue = (Issue) item;
            return issue.getShowOnTopicsLandingPage();
        }

        return false;
    }
}