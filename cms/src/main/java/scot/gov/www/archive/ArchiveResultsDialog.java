package scot.gov.www.archive;

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
import scot.gov.publishing.hippo.redirects.Redirect;

import java.io.Serializable;
import java.util.List;

public class ArchiveResultsDialog extends AbstractDialog<Void> {

    @FunctionalInterface
    interface OnCloseCallback extends Runnable, Serializable {}

    private final String title;
    private final OnCloseCallback onClose;

    public ArchiveResultsDialog(List<Redirect> redirects, String title, OnCloseCallback onClose) {
        this.title = title;
        this.onClose = onClose;

        add(new ListView<Redirect>("redirects", redirects) {
            @Override
            protected void populateItem(ListItem<Redirect> item) {
                Redirect redirect = item.getModelObject();
                item.add(new Label("from", redirect.getFrom()));
                item.add(new Label("to", redirect.getTo()));
            }
        });

        setOkVisible(false);
        setCancelLabel("Close");
    }

    @Override
    protected void onCancel() {
        onClose.run();
    }

    @Override
    public IModel<String> getTitle() {
        return Model.of(title);
    }

    @Override
    public IValueMap getProperties() {
        return DialogConstants.LARGE_AUTO;
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(
                new CssResourceReference(ArchiveResultsDialog.class, "ArchiveDialog.css")));
    }
}
