package scot.gov.www.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.sitemenu.EditableMenuItem;
import org.hippoecm.hst.core.sitemenu.EditableMenuItemImpl;


public class RepoBasedMenuItem extends EditableMenuItemImpl {

    public RepoBasedMenuItem(
            HippoDocumentBean repoItem,
            EditableMenuItem parentItem,
            HstRequest request,
            HippoBean currentContentBean) {
        super(parentItem);
        this.name = repoItem.getDisplayName();
        this.properties = repoItem.getProperties();

        this.hstLink = request.getRequestContext().getHstLinkCreator().create(repoItem, request.getRequestContext());

        if (currentContentBean!= null && repoItem.isSelf(currentContentBean)) {
            this.selected = true;
            this.getEditableMenu().setSelectedMenuItem(this);
        }

    }

    public RepoBasedMenuItem(
            HippoFolderBean repoItem,
            EditableMenuItem parentItem,
            HstRequest request,
            HippoBean currentContentBean) {
        super(parentItem);
        this.name = repoItem.getDisplayName();
        this.depth = parentItem.getDepth() - 1;
        this.repositoryBased = true;
        this.properties = repoItem.getProperties();

        this.hstLink = request.getRequestContext().getHstLinkCreator().create(repoItem, request.getRequestContext());

        if (currentContentBean!= null && repoItem.isSelf(currentContentBean)) {
            this.selected = true;
            this.getEditableMenu().setSelectedMenuItem(this);
        }

        if (this.depth == 0) {
            return;
        }

        for (HippoDocumentBean childDocumentItem : repoItem.getDocuments()) {
            EditableMenuItem childMenuItem = new RepoBasedMenuItem(childDocumentItem, this, request, currentContentBean);

            if ("index".equals(childDocumentItem.getName())) {
                // don't add the item as a child if it is the 'index' item for that folder
                this.name = childDocumentItem.getDisplayName();
                this.selected = childMenuItem.isSelected();
            } else {
                this.name = this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
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
