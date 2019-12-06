package scot.gov.www.components.payment;

/**
 * Created by z441571 on 04/12/2019.
 */
public enum ResourceBundleMessage {

    AUTHORISED("payment.authorised"),
    REDIRECTED("payment.redirected"),
    REFUSED("payment.refused"),
    CANCELLED("payment.cancelled"),
    ERROR("payment.error");

    private final String paramName;

    ResourceBundleMessage(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() { return paramName; }

}

