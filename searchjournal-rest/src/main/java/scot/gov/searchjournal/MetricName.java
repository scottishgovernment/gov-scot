package scot.gov.searchjournal;

public enum MetricName {

    REQUEST_TIMES("request-times"),
    JOB_TIMES("job-times"),
    REQUESTS("requests"),
    ERRORS("errors"),
    FAILURES("failures"),

    // a failure is a journal entry that has errored through all attempts
    REQUEST_RATE("request-rate"),
    ERROR_RATE("error-rate"),
    FAILURE_RATE("failure-rate");

    private final String name;

    MetricName(String metricName) {
        this.name = metricName;
    }

    public String getName() {
        return name;
    }

}
