package scot.gov.www.components.mapper;

import org.hippoecm.hst.site.HstServices;
import org.onehippo.taxonomy.api.Category;
import org.onehippo.taxonomy.api.Taxonomy;
import org.onehippo.taxonomy.api.TaxonomyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Arrays.asList;

public class TaxonomyMapper {

    private static final Logger LOG = LoggerFactory.getLogger(TaxonomyMapper.class);

    private static final String PUBLICATION_TYPES = "publication-types";

    private static TaxonomyMapper instance;

    private final TaxonomyManager taxonomyManager;

    /**
     * private constructor to enforce the use of getInstance() method to create singleton
     */
    private TaxonomyMapper() {
        taxonomyManager = HstServices.getComponentManager().getComponent(TaxonomyManager.class);
    }

    public static synchronized TaxonomyMapper getInstance() {
        if (instance == null) {
            instance = new TaxonomyMapper();
        }
        return instance;
    }

    /**
     * Returns the categories and synonyms that match the given taxonomy ids.
     */
    public List<String> getPublicationTypes(Set<String> taxonomyIds, Locale locale) {

        List<String> derivedList = new ArrayList<>();
        Taxonomy taxonomy = taxonomyManager.getTaxonomies().getTaxonomy(PUBLICATION_TYPES);

        for (Category category : taxonomy.getCategories()) {
            for (Category child : category.getChildren()) {
                if (taxonomyIds.contains(child.getKey())) {
                    derivedList.add(child.getKey());
                    derivedList.addAll(asList(child.getInfo(locale).getSynonyms()));
                }
            }
        }

        return derivedList;
    }

    public Taxonomy getPublicationTypesTaxonomy() {
        return taxonomyManager.getTaxonomies().getTaxonomy(PUBLICATION_TYPES);
    }

}
