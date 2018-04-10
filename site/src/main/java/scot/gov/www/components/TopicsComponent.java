package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import scot.gov.www.beans.Topic;
import scot.gov.www.beans.TopicsAndLetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;


public class TopicsComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        HippoBean base = request.getRequestContext().getSiteContentBaseBean();
        HippoBean topicsBean = base.getBean("topics/");
        List<Topic> topics = topicsBean.getChildBeans(Topic.class);
        Map<String, List<Topic>> topicsByFirstLetter  = topics
                .stream()
                .collect(groupingBy(topic -> String.format("%s", topic.getName().toUpperCase().charAt(0))));
        List<TopicsAndLetter> topicsAndLetters = new ArrayList<>();
        for (Map.Entry<String, List<Topic>> entry : topicsByFirstLetter.entrySet()) {
            TopicsAndLetter topicAndLetter = new TopicsAndLetter();
            topicAndLetter.setLetter(entry.getKey());
            topicAndLetter.setTopics(entry.getValue());
            topicsAndLetters.add(topicAndLetter);
        }
        request.setAttribute("topicsByLetter", topicsAndLetters);
    }

}