package scot.gov.www.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;

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

    @Override
    public void doBeforeRender(final HstRequest request,
                               final HstResponse response) {
        HstRequestContext context = request.getRequestContext();
        OrgRoles document = context.getContentBean(OrgRoles.class);
        request.setAttribute("document", document);
        request.setAttribute("primaryPeople", peopleWithRoles(document.getOrgRole()));
        request.setAttribute("secondaryPeople", peopleWithRoles(document.getSecondaryOrgRole()));
    }

    private List<Person> peopleWithRoles(List<HippoBean> roles) {

        // group the roles by incumbent
        Map<String, List<HippoBean>> orgRoleByIncumbent =
                roles.stream().collect(groupingBy(role -> incumbentTitle(role)));
        // now list the incumbet and list their roles, avoiding duplicates
        Set<String> seen = new HashSet<>();
        List<Person> peopleWithRoles = new ArrayList<>();
        for (HippoBean bean : roles) {
            Person incumbent = incumbent(bean);
            if (!seen.contains(incumbent.getTitle()))  {
                incumbent.setRoles(orgRoleByIncumbent.get(incumbent.getTitle()));
                peopleWithRoles.add(incumbent);
                seen.add(incumbent.getTitle());
            }
        }
        return peopleWithRoles;
    }

    private Person incumbent(HippoBean bean) {
        if (bean instanceof Person) {
            return (Person) bean;
        }
        Role role = (Role) bean;
        return (Person) role.getIncumbent();
    }

    private String incumbentTitle(HippoBean bean) {
        HippoBean incumbent = incumbent(bean);
        return ((SimpleContent) incumbent).getTitle();
    }

}