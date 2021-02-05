package scot.gov.www.beans;

import org.hippoecm.hst.content.beans.standard.HippoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by z418868 on 12/04/2018.
 */
public class LettersAndBeans {

    private SortedSet<String> letters = new TreeSet<>();
    private List<HippoBean> beans = new ArrayList<>();
    private String label;

    public SortedSet<String> getLetters() {
        return letters;
    }

    public List<HippoBean> getBeans() {
        return beans;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
