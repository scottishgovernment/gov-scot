package scot.gov.www.redirects;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.value.IValueMap;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.dialog.DialogConstants;

import java.util.List;

public class RedirectsErrorDialog extends AbstractDialog<Void> {

    private final int errorCount;

    public RedirectsErrorDialog(List<String> errors) {
        this.errorCount = errors.size();

        add(new ListView<String>("errors", errors) {
            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(new Label("error", item.getModelObject()));
            }
        });

        setOkVisible(false);
        setCancelLabel("Close");
    }

    @Override
    public IModel<String> getTitle() {
        String noun = errorCount == 1 ? "error" : "errors";
        return Model.of(errorCount + " validation " + noun);
    }

    @Override
    public IValueMap getProperties() {
        return DialogConstants.MEDIUM_AUTO;
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(
                new CssResourceReference(RedirectsErrorDialog.class, "RedirectsErrorDialog.css")));
    }
}
