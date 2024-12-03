package scot.gov.pressreleases;

import java.util.ArrayList;
import java.util.List;

public class Health {

    boolean ok = true;

    List<String> performanceData = new ArrayList<>();

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public List<String> getPerformanceData() {
        return performanceData;
    }

    public void setPerformanceData(List<String> performanceData) {
        this.performanceData = performanceData;
    }
}
