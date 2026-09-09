package scot.gov.www;

import org.hippoecm.repository.ext.DerivedDataFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Value;

import java.util.Map;

public class PublicationTypeDerivedData extends DerivedDataFunction {
    static final long serialVersionUID = 1;
    private static final Logger LOG = LoggerFactory.getLogger(PublicationTypeDerivedData.class);

    static final String PUBLICATION_TYPE = "publicationType";

    static final String PATH = "path";

    static final String PUBLICATIONS_PATH_PREFIX = "/content/documents/govscot/publications/";

    public Map<String,Value[]> compute(Map<String,Value[]> parameters) {

        if (parameters.isEmpty() || parameters.get(PATH).length == 0){
            return parameters;
        }

        try {
            Value pathValue = parameters.get(PATH)[0];
            String path = pathValue.getString();
            if (!path.startsWith(PUBLICATIONS_PATH_PREFIX)) {
                // not a real publication document (e.g. a template-query prototype
                // or document-type prototype) - leave publicationType untouched
                return parameters;
            }
            String publicationType = path.split("/")[5];
            parameters.put(PUBLICATION_TYPE, new Value[] {getValueFactory().createValue(publicationType)});
        } catch (RepositoryException e) {
            LOG.error("Couldn't set publication type via derived data, {}", e);
        }

        return parameters;
    }

}
