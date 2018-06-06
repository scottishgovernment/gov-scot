package scot.gov.www.components.info;

import org.hippoecm.hst.core.parameters.Parameter;
import org.onehippo.cms7.essentials.components.info.EssentialsListComponentInfo;

public interface FeedbackComponentInfo extends EssentialsListComponentInfo {

    @Parameter(name = "feedbackIsEnabled", defaultValue = "true", required = true)
    Boolean getFeedbackIsEnabled();
}
