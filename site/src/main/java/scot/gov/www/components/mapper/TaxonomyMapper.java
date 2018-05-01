package scot.gov.www.components.mapper;

import org.hippoecm.hst.site.HstServices;
import org.onehippo.taxonomy.api.Taxonomy;
import org.onehippo.taxonomy.api.TaxonomyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.*;

/**
 * Created by z441571 on 24/04/2018.
 */
@Singleton
public class TaxonomyMapper {

    private static final Logger LOG = LoggerFactory.getLogger(TaxonomyMapper.class);
    private static final String PUBLICATION_TYPES = "publication-types";

    private static TaxonomyMapper instance;
    private TaxonomyManager taxonomyManager = HstServices.getComponentManager().getComponent(TaxonomyManager.class.getName());

    /**
     * private constructor to enforce the use of getInstance() method to create singleton
     */
    private TaxonomyMapper(){}

    public static synchronized TaxonomyMapper getInstance(){
        if(instance == null){
            synchronized (TaxonomyMapper.class) {
                if(instance == null){
                    instance = new TaxonomyMapper();
                }
            }
        }
        return instance;
    }

    /**
     *  Map of Groupings and categories for selection in the front end, which contains a list of
     *  valuelist IDs to look up the correct documents from the repository
     */
    public List<String> getPublicationTypes(Set<String> taxonomyIds, Locale locale) {

        LOG.info("Into TaxonomyMapper.getPublicationTypes(List<String> taxonomyIds)");
        List<String> derivedList = new ArrayList<>();

        Taxonomy taxonomy = taxonomyManager.getTaxonomies().getTaxonomy(PUBLICATION_TYPES);

        taxonomy.getCategories().forEach(category ->
                category.getChildren()
                        .stream()
                        .filter(c -> taxonomyIds.contains(c.getKey()))
                        .forEach(child -> {
                            derivedList.add(child.getKey());
                            Arrays.stream(child.getInfo(locale).getSynonyms())
                                    .forEach(synonym -> derivedList.add(synonym));
                        }));

        return derivedList;
    }

    public Taxonomy getPublicationTypesTaxonomy() {
        return taxonomyManager.getTaxonomies().getTaxonomy(PUBLICATION_TYPES);
    }
}
