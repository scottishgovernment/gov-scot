package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;

import org.hippoecm.hst.util.ContentBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.www.beans.Directorate;
import scot.gov.www.beans.OrgRoles;
import scot.gov.www.beans.Person;

import scot.gov.www.beans.Role;
import scot.gov.www.beans.SimpleContent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import static java.util.stream.Collectors.groupingBy;

public class OrgRolesComponent  extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(OrgRolesComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        HstRequestContext context = request.getRequestContext();
        HippoBean baseBean = context.getSiteContentBaseBean();
        OrgRoles document = context.getContentBean(OrgRoles.class);
        request.setAttribute("document", document);
        request.setAttribute("primaryPeople", peopleWithRoles(baseBean, document.getOrgRole()));
        request.setAttribute("secondaryPeople", peopleWithRoles(baseBean, document.getSecondaryOrgRole()));
    }

    private List<Person> peopleWithRoles(HippoBean baseBean, List<HippoBean> roles) {

        // group the roles by incumbent and enrich with directorates.
        Map<String, List<HippoBean>> orgRoleByIncumbent =
                roles.stream()
                        .map(role -> enrichRoleWithDirectorates(baseBean, role))
                        .collect(groupingBy(role -> incumbentTitle(role)));

        // now list the incumbents and list their roles, avoiding duplicates
        Set<String> seen = new HashSet<>();
        List<Person> peopleWithRoles = new ArrayList<>();
        for (HippoBean bean : roles) {
            Person incumbent = incumbent(bean);
            if (!seen.contains(incumbent.getTitle())) {
                incumbent.setRoles(orgRoleByIncumbent.get(incumbent.getTitle()));
                peopleWithRoles.add(incumbent);
                seen.add(incumbent.getTitle());
            }
        }
        return peopleWithRoles;

    }

    private HippoBean enrichRoleWithDirectorates(HippoBean baseBean, HippoBean role) {

        if (!(role instanceof Role)) {
            return role;
        }

        try {
            Role castRole = (Role) role;
            HstQuery query = ContentBeanUtils.createIncomingBeansQuery(
                    castRole, baseBean, "*/@hippo:docbase", Directorate.class, false);
            query.addOrderByAscending("govscot:title");
            HstQueryResult res = query.execute();
            List<HippoBean> directorates = new ArrayList<>();
            res.getHippoBeans().forEachRemaining(directorates::add);
            castRole.setDirectorates(directorates);
        } catch (QueryException e) {
            LOG.warn("Unable to get Directorates for role {}", role.getPath(), e);
        }
        return role;
    }

    private Person incumbent(HippoBean bean) {
        if (bean instanceof Person) {
            return (Person) bean;
        }
        Role role = (Role) bean;
        return role.getIncumbent();

    }

    private String incumbentTitle(HippoBean bean) {
        if (bean instanceof Person) {
            return ((Person) bean).getRoleTitle();
        }

        HippoBean incumbent = incumbent(bean);
        return ((SimpleContent) incumbent).getTitle();
    }

}
