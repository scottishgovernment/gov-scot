package scot.gov.www.components.info;

import org.hippoecm.hst.core.parameters.Parameter;

import java.util.Date;

/**
 * Created by z441571 on 04/05/2018.
 */
public interface FilteredResultsSideComponentInfo {

    @Parameter(name = "searchType", required = true)
    String getSearchType();

    @Parameter(name = "localeRequired", defaultValue = "false", required = true)
    Boolean getLocaleRequired();

    @Parameter(name = "includeDateFilter", defaultValue = "true", required = true)
    Boolean getIncludeDateFilter();

    @Parameter(name = "fromDate")
    Date getFromDate();

    @Parameter(name = "includePublicationTypesFilter", defaultValue = "false", required = true)
    Boolean getIncludePublicationTypesFilter();

    @Parameter(name = "publicationTypes")
    String getPublicationTypes();

}
