package scot.gov.www.components.info;

import org.hippoecm.hst.core.parameters.Parameter;
import org.onehippo.cms7.essentials.components.info.EssentialsListComponentInfo;

/**
 * Created by z441571 on 04/05/2018.
 */
public interface FilteredResultsComponentInfo extends EssentialsListComponentInfo {

    @Parameter(name = "searchTermPlural", required = true)
    String getSearchTermPlural();

    @Parameter(name = "searchTermSingular", required = true)
    String getSearchTermSingular();

    @Parameter(name = "publicationTypes")
    String getPublicationTypes();

}
