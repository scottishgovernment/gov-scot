package scot.gov.www.components.payment;

/**
 * Created by z441571 on 06/12/2019.
 */
public class PaymentResult {

    String title;

    String content;

    public PaymentResult(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
