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

    public Map<String,Value[]> compute(Map<String,Value[]> parameters) {
        if (parameters.isEmpty()|| parameters.get(PUBLICATION_TYPE).length == 0){
            return parameters;
        }

        try {
            Value publicationTypeValue = parameters.get(PUBLICATION_TYPE)[0];
            String publicationType = publicationTypeValue.getString();
            parameters.put(PUBLICATION_TYPE, new Value[] {getValueFactory().createValue(publicationType)});
        } catch (RepositoryException e) {
            LOG.error("Couldn't set publication type via derived data, {}", e);
        }

        return parameters;
    }

}
