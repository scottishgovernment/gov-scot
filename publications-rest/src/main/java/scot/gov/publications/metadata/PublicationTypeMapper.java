package scot.gov.publications.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * The publication types were rationalised as part of MGS-4940.
 *
 * Until the registration form is updated we still need to be able to undestand the old delted types.  This class maps
 * the old types on to the new ones.
 */
public class PublicationTypeMapper {

    static Map<String, String> typeMap = new HashMap<>();

    static final String CORRESPONDENCE = "Correspondence";

    static final String STATISTICS = "Statistics";

    static final String SPEECH_STATEMENT = "Speech/statement";

    static final String RESEARCH_AND_ANALYSIS = "Research and analysis";

    static final String PUBLICATION = "Publication";

    static {
        // how to map types that are moving...
        typeMap.put("consultation", "Consultation paper");
        typeMap.put("consultation responses", "Consultation analysis");
        typeMap.put("forms", "Form");
        typeMap.put("guidance", "Advice and guidance");
        typeMap.put("letter/circular", CORRESPONDENCE);
        typeMap.put("newsletter", CORRESPONDENCE);
        typeMap.put("research findings", RESEARCH_AND_ANALYSIS);
        typeMap.put("research finding", RESEARCH_AND_ANALYSIS);
        typeMap.put("research publications", RESEARCH_AND_ANALYSIS);
        typeMap.put("research publication", RESEARCH_AND_ANALYSIS);
        typeMap.put("speech", SPEECH_STATEMENT);
        typeMap.put("statistics dataset", STATISTICS);
        typeMap.put("statistics publication", STATISTICS);
        typeMap.put("info page", PUBLICATION);
        typeMap.put("legislation", PUBLICATION);
        typeMap.put("report", PUBLICATION);
    }

    public String map(String publicationType) {
        String lowerCaseType = publicationType.toLowerCase();
        return  typeMap.getOrDefault(lowerCaseType, publicationType);
    }

    public boolean isEmbargoType(String type) {
        return STATISTICS.equals(map(type));
    }
}
