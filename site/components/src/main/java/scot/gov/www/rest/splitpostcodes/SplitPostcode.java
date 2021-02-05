package scot.gov.www.rest.splitpostcodes;

import java.util.ArrayList;
import java.util.List;

public class SplitPostcode {

    String postcode;

    List<Geography> splits = new ArrayList<>();

    public SplitPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public List<Geography> getSplits() {
        return splits;
    }

    public void setSplits(List<Geography> splits) {
        this.splits = splits;
    }
}
