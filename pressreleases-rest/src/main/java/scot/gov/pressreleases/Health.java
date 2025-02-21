package scot.gov.pressreleases;

public class Health {

    String status;

    String message;

    String performanceData;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPerformanceData() {
        return performanceData;
    }

    public void setPerformanceData(String performanceData) {
        this.performanceData = performanceData;
    }
}
