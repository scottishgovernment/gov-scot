package scot.gov.www;

import org.hippoecm.repository.ext.DerivedDataFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Value;

import java.util.Map;

public class FoiNumberDerivedData extends DerivedDataFunction {
    private static final Logger LOG = LoggerFactory.getLogger(FoiNumberDerivedData.class);
    static final long serialVersionUID = 1;

    public Map<String,Value[]> compute(Map<String,Value[]> parameters) {
        if (parameters.isEmpty()|| parameters.get("foiNumber").length == 0){
            return parameters;
        }

        Value foiNumber = parameters.get("foiNumber")[0];

        try {
            String foiString = foiNumber.getString();
            String[] parts = foiString.split("/");

            if (parts.length < 3) {
                LOG.warn("Unable to get FOI number");
                return parameters;
            }

            parameters.put("foiSubstring", new Value[] { getValueFactory().createValue(parts[2]) });

        } catch (RepositoryException e) {
            LOG.error("Unexpected exception while trying to set foi tag", e);
        }

        return parameters;
    }
}
