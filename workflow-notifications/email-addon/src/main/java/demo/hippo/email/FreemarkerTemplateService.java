package demo.hippo.email;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class FreemarkerTemplateService implements TemplateService {

    public static final String TEMPLATE_PROPERTY_NAME = "template";
    private String basePath;
    private Session session;

    public FreemarkerTemplateService(final Session session) {
        this.session = session;
    }

    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }

    public String getBasePath() {
        return basePath;
    }

    @Override
    public String getTemplateByName(final String name) {
        try {
            final String templatePath = getBasePath() + "/" + name;
            if (session.nodeExists(templatePath)) {
                final Node node = session.getNode(templatePath);
                if (node.hasProperty(TEMPLATE_PROPERTY_NAME)) {
                    return node.getProperty(TEMPLATE_PROPERTY_NAME).getString();
                }
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getPropertyByName(final String name, final String propertyName) {
        try {
            final String templatePath = getBasePath() + "/" + name;
            if (session.nodeExists(templatePath)) {
                final Node node = session.getNode(templatePath);
                if (node.hasProperty(propertyName)) {
                    return node.getProperty(propertyName).getString();
                }
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return "";
    }

}
