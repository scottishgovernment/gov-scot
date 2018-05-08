package scot.gov.www.beans;

import java.util.List;

public class TopicsAndLetter {

    private String letter;

    private List<SimpleContent> topics;

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public List<SimpleContent> getTopics() {
        return topics;
    }

    public void setTopics(List<SimpleContent> topics) {
        this.topics = topics;
    }
}


