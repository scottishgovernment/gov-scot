package scot.gov.www.rest.splitpostcodes;

import java.util.ArrayList;
import java.util.List;

public class SplitPostcodes {

    List<SplitPostcode> postcodes = new ArrayList<>();

    public List<SplitPostcode> getPostcodes() {
        return postcodes;
    }

    public void setPostcodes(List<SplitPostcode> postcodes) {
        this.postcodes = postcodes;
    }
}
