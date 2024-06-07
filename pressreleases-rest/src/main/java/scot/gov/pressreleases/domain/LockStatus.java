package scot.gov.pressreleases.domain;

public class LockStatus {

    String status;

    String lockToken;

    long secondsRemaining;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLockToken() {
        return lockToken;
    }

    public void setLockToken(String lockToken) {
        this.lockToken = lockToken;
    }

    public long getSecondsRemaining() {
        return secondsRemaining;
    }

    public void setSecondsRemaining(long secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
    }

    public static LockStatus status(String status) {

        return status(status, "", -1);
    }

    public static LockStatus status(String status, String lockToken, long secondsRemaining) {
        LockStatus lockStatus = new LockStatus();
        lockStatus.setStatus(status);
        lockStatus.setLockToken(lockToken);
        lockStatus.setSecondsRemaining(secondsRemaining);
        return lockStatus;
    }
}
