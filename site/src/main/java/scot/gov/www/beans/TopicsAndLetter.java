package scot.gov.www.beans;

import java.util.List;

public class TopicsAndLetter {

    private String letter;

    private List<Topic> topics;

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}


