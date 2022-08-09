package scot.gov.searchjournal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HealthInfo {

    private boolean ok;

    private List<String> errorList = new ArrayList<>();

    private List<String> warningList = new ArrayList<>();

    private long queueSize;

    Map<Long, Integer> attemptBreakdown;

    private long requests;

    private long errors;

    private long failures;

    private String requestRate;

    private String errorRate;

    private String failureRate;

    private String timer;

    private String jobTimer;

    private String journalPosition;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

    public List<String> getWarningList() {
        return warningList;
    }

    public void setWarningList(List<String> warningList) {
        this.warningList = warningList;
    }

    public long getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(long queueSize) {
        this.queueSize = queueSize;
    }

    public Map<Long, Integer> getAttemptBreakdown() {
        return attemptBreakdown;
    }

    public void setAttemptBreakdown(Map<Long, Integer> attemptBreakdown) {
        this.attemptBreakdown = attemptBreakdown;
    }

    public long getRequests() {
        return requests;
    }

    public void setRequests(long requests) {
        this.requests = requests;
    }

    public long getErrors() {
        return errors;
    }

    public void setErrors(long errors) {
        this.errors = errors;
    }

    public long getFailures() {
        return failures;
    }

    public void setFailures(long failures) {
        this.failures = failures;
    }

    public String getRequestRate() {
        return requestRate;
    }

    public void setRequestRate(String requestRate) {
        this.requestRate = requestRate;
    }

    public String getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(String errorRate) {
        this.errorRate = errorRate;
    }

    public String getFailureRate() {
        return failureRate;
    }

    public void setFailureRate(String failureRate) {
        this.failureRate = failureRate;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getJobTimer() {
        return jobTimer;
    }

    public void setJobTimer(String jobTimer) {
        this.jobTimer = jobTimer;
    }

    public String getJournalPosition() {
        return journalPosition;
    }

    public void setJournalPosition(String journalPosition) {
        this.journalPosition = journalPosition;
    }
}
