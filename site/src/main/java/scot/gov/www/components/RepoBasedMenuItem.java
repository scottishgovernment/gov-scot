package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.sitemenu.EditableMenuItem;
import org.hippoecm.hst.core.sitemenu.EditableMenuItemImpl;

public class RepoBasedMenuItem extends EditableMenuItemImpl {

    public RepoBasedMenuItem(HippoDocumentBean repoItem, EditableMenuItem parentItem, HstRequest request,
                             HippoBean currentContentBean) {
        super(parentItem);
        this.name = repoItem.getDisplayName();
        this.expanded = true;
        this.properties = repoItem.getProperties();

        this.hstLink = request.getRequestContext().getHstLinkCreator().create(repoItem, request.getRequestContext());

        if (currentContentBean!= null &&  repoItem.isSelf(currentContentBean)) {
            this.selected = true;
            this.getEditableMenu().setSelectedMenuItem(this);
        }

    }

    public RepoBasedMenuItem(HippoFolderBean repoItem, EditableMenuItem parentItem, HstRequest request,
                                  HippoBean currentContentBean) {
        super(parentItem);
        this.name = repoItem.getDisplayName();
        this.depth = parentItem.getDepth() - 1;
        this.repositoryBased = true;
        this.expanded = true;
        this.properties = repoItem.getProperties();

        this.hstLink = request.getRequestContext().getHstLinkCreator().create(repoItem, request.getRequestContext());

        if (currentContentBean!= null &&  repoItem.isSelf(currentContentBean)) {
            this.selected = true;
            this.getEditableMenu().setSelectedMenuItem(this);
        }

        if (this.depth > 0) {

            for (HippoDocumentBean childDocumentItem : repoItem.getDocuments()) {
                if (childDocumentItem.getName().equals("index")) {
                    this.name = childDocumentItem.getDisplayName();
                } else {
                    EditableMenuItem childMenuItem = new RepoBasedMenuItem(childDocumentItem, this, request, currentContentBean);
                    this.addChildMenuItem(childMenuItem);
                }
            }

            for (HippoFolderBean childRepoItem : repoItem.getFolders()) {
                EditableMenuItem childMenuItem = new RepoBasedMenuItem(childRepoItem, this, request,
                        currentContentBean);
                this.addChildMenuItem(childMenuItem);
            }

        }

    }

}
