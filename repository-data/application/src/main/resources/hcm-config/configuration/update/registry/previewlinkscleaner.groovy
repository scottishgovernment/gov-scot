import org.hippoecm.repository.util.JcrUtils
import org.onehippo.repository.update.BaseNodeUpdateVisitor

import javax.jcr.Node
import javax.jcr.Session

class PreviewLinksCleaner extends BaseNodeUpdateVisitor {

    String defaultMode = "all";
    String mode;

    void initialize(Session session) {
        mode = parametersMap.get("mode", defaultMode)

        if (!mode.equals("all") && !mode.equals("expired")) {
            mode = defaultMode
        }

        log.info "PreviewLinksCleaner initialized with parameters: { mode: ${mode} }"

    }

    boolean skipCheckoutNodes() {
        return true;
    }

    boolean doUpdate(Node node) {

        boolean shouldRemove = false
        Calendar expirationCalendar = JcrUtils.getDateProperty(node, "staging:expirationdate", null)

        if("all".equals(mode) || ("expired".equals(mode) && expirationCalendar!=null && expirationCalendar.before(Calendar.getInstance()))){
            shouldRemove = true
        }

        if(shouldRemove) {
            node.remove()
            return true
        } else {
            return false
        }
    }

    boolean undoUpdate(Node node) {
        throw new UnsupportedOperationException('Updater does not implement undoUpdate method')
    }

}