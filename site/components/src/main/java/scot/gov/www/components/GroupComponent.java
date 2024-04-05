package scot.gov.www.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.ContentBeanUtils;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.*;

import java.util.*;

public class GroupComponent extends EssentialsContentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(GroupComponent.class);

    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);


        HstRequestContext context = request.getRequestContext();
        Group document = context.getContentBean(Group.class);
        if (document == null) {
            return;
        }

        try {
            addPublications(document, request);
        } catch (QueryException e) {
            LOG.warn("Unable to get publications for group {}", document.getPath(), e);
        }
    }

    void addPublications(Group group, HstRequest request) throws QueryException {
        HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                group, request.getRequestContext().getSiteContentBaseBean(), "*/@hippo:docbase", Publication.class, true);
        query.addOrderByDescending("govscot:publicationDate");
        HstQueryResult groupsResult = query.execute();

        SortedMap<String, PublicationsGroup> groups = new TreeMap<>(Collections.reverseOrder());
        HippoBeanIterator it = groupsResult.getHippoBeans();
        while (it.hasNext()) {
            Publication next = (Publication) it.next();
            String yearSting =  Integer.toString(date(next).get(Calendar.YEAR));
            PublicationsGroup publicationsGroup = groups.getOrDefault(yearSting, new PublicationsGroup(yearSting));
            publicationsGroup.getPublications().add(next);
            groups.put(yearSting, publicationsGroup);
        }
        groupYearsByMonth(groups);
        request.setAttribute("groupedPublications", groups.values());
    }

    Calendar date(Publication publication) {
        if (publication.getOfficialdate() != null) {
            LOG.info("using official date for {}", publication.getTitle());
            return publication.getOfficialdate();
        }
        return publication.getDisplayDate();
    }

    void groupYearsByMonth(SortedMap<String, PublicationsGroup> groups) {
        for (PublicationsGroup yearGroup : groups.values()) {
            if (yearGroup.getPublications().size()  > 12) {
                groupByMonth(yearGroup);
            }
        }
    }

    void groupByMonth(PublicationsGroup yearGroup) {
        SortedMap<Integer, PublicationsGroup> monthGroups = new TreeMap<>(Collections.reverseOrder());
        for (HippoBean minute : yearGroup.getPublications()) {
            Publication publication = (Publication) minute;
            String monthString = date(publication).getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, Locale.getDefault());
            Integer monthKey = date(publication).get(Calendar.MONTH);
            PublicationsGroup minutesGroup = monthGroups.getOrDefault(monthKey, new PublicationsGroup(monthString));
            minutesGroup.getPublications().add(minute);
            monthGroups.put(monthKey, minutesGroup);
        }
        yearGroup.getPublications().clear();
        yearGroup.getSubgroups().addAll(monthGroups.values());
    }

    public static class PublicationsGroup {
        List<HippoBean> publications = new ArrayList<>();

        List<PublicationsGroup> subgroups = new ArrayList<>();

        String label;

        public PublicationsGroup(String label) {
            this.label = label;
        }

        public List<HippoBean> getPublications() {
            return publications;
        }

        public void setPublications(List<HippoBean> publications) {
            this.publications = publications;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<PublicationsGroup> getSubgroups() {
            return subgroups;
        }

        public void setSubgroups(List<PublicationsGroup> subgroups) {
            this.subgroups = subgroups;
        }
    }

}
