package scot.gov.www.redirects;

import java.util.ArrayList;
import java.util.List;

public class RedirectResult extends Redirect {

    private List<Redirect> children = new ArrayList<>();

    public List<Redirect> getChildren() {
        return children;
    }

    public void setChildren(List<Redirect> children) {
        this.children = children;
    }
}
