package scot.gov.www.components.payment;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.resourcebundle.ResourceBundleUtils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by z441571 on 04/12/2019.
 */
public class PaymentComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {

        HstRequestContext context = request.getRequestContext();
        String path = context.getBaseURL().getRequestPath();
        String[] parts = path.split("/");
        String result = parts[parts.length-1];

        ResourceBundle bundle = ResourceBundleUtils.getBundle(
                ResourceBundleMessage.valueOf(result.toUpperCase()).getParamName(), Locale.getDefault());

        PaymentResult paymentResult = new PaymentResult(bundle.getString("title"), bundle.getString("content"));

        request.setAttribute("paymentResult", paymentResult);

    }
}
