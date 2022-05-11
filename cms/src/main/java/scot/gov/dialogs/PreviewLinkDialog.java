package scot.gov.dialogs;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.plugins.standards.datetime.DateTimePrinter;
import org.hippoecm.frontend.plugins.standards.icon.HippoIcon;
import org.hippoecm.frontend.service.IconSize;
import org.hippoecm.frontend.skin.Icon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.clipboardjs.ClipboardJsBehavior;

import javax.jcr.Node;
import java.text.MessageFormat;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.List;

public class PreviewLinkDialog extends AbstractDialog {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(PreviewLinkDialog.class);
    private static final FormatStyle DATE_STYLE = FormatStyle.LONG;
    private static final CssResourceReference CSS = new CssResourceReference(PreviewLinkDialog.class, "PreviewLinkDialog.css");
    private String title;
    private List<PreviewLink> previewLinkList;

    public PreviewLinkDialog(final List<PreviewLink> previewLinkList) {
        this.title = getString("dialog-title", null, "Preview URLs List");
        this.previewLinkList = previewLinkList;
        add(new PropertyListView<PreviewLink>("listview", previewLinkList) {
            protected void populateItem(ListItem<PreviewLink> item) {
                PreviewLink previewLink = item.getModelObject();
                Label previewURL = new Label("name", previewLink.getUrl());
                item.add(previewURL);

                final String creationDate = printDateProperty(previewLink.getCreationDate());
                final String expirationDate = printDateProperty(previewLink.getExpirationDate());

                Label previewInfo = new Label("preview-info",
                        MessageFormat.format(new StringResourceModel("preview-info", PreviewLinkDialog.this).getString()
                                , previewLink.getUsername()
                                , creationDate
                                , StringUtils.isEmpty(expirationDate) ? "will never expire." : "will expire on the "+expirationDate+"."
                        )
                );
                item.add(previewInfo);


                Label expirationNotification = new Label("expired-notification", "This preview link has expired. Please remove it and generate a new one.");
                expirationNotification.setVisible(!previewLink.isValid());
                item.add(expirationNotification);

                final WebMarkupContainer copyButton = new WebMarkupContainer("copy-button");
                final ClipboardJsBehavior clipboardJsBehavior = new ClipboardJsBehavior();
                copyButton.add(clipboardJsBehavior);
                clipboardJsBehavior.setTarget(previewURL);
                copyButton.add(HippoIcon.fromSprite("copy-icon", Icon.FILES, IconSize.L));
                item.add(copyButton);

                item.add(new AjaxLink<Void>("delete") {

                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        //removing the preview key from the document
                        try {
                            Node nodeToRemove = previewLink.getNode();
                            nodeToRemove.remove();
                            nodeToRemove.getSession().save();
                            previewLinkList.remove(previewLink);
                            target.add(PreviewLinkDialog.this);
                        } catch(Exception exception){
                            LOG.error("Something went wrong while deleting preview with uuid: {}.", previewLink.getPreviewKey(), exception);
                        }
                    }
                }.add(HippoIcon.fromSprite("delete-icon", Icon.TIMES, IconSize.L)));
                item.setOutputMarkupId(true);
            }

        });
        add(new AjaxLink<Void>("remove-all") {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                previewLinkList.forEach(previewLink -> {
                   try {
                       Node nodeToRemove = previewLink.getNode();
                       nodeToRemove.remove();
                       nodeToRemove.getSession().save();
                   } catch(Exception e){
                       LOG.warn("Something went wrong while removing expired links.", e);
                   }
                });
                previewLinkList.clear();
                target.add(PreviewLinkDialog.this);
            }

            @Override
            public boolean isVisible() {
                return !previewLinkList.isEmpty();
            }
        });
        setCancelVisible(false);
    }

    @Override
    public IModel getTitle() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return title;
            }
        };
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS));
    }

    private String printDateProperty(final Calendar date) {
        return DateTimePrinter.of(date).appendDST().print(DATE_STYLE);
    }

    public List<PreviewLink> getPreviewLinkList() {
        return previewLinkList;
    }

    public void setPreviewLinkList(final List<PreviewLink> previewLinkList) {
        this.previewLinkList = previewLinkList;
    }
}