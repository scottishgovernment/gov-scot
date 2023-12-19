(function () {

    const datalayerScriptElement = document.getElementById('gtm-datalayer');

    if (!datalayerScriptElement) {
        return;
    }

    const role = datalayerScriptElement.dataset.role;
    const secondaryRole = datalayerScriptElement.dataset.secondaryrole;
    const directorate = datalayerScriptElement.dataset.directorate;
    const secondaryDirectorate = datalayerScriptElement.dataset.secondarydirectorate;
    const topics = datalayerScriptElement.dataset.topics;
    const publicationDate = datalayerScriptElement.dataset.publicationdate;
    const reportingTags = datalayerScriptElement.dataset.reportingtags;
    const policies = datalayerScriptElement.dataset.policies;
    const collections = datalayerScriptElement.dataset.collections;
    const lastUpdated = datalayerScriptElement.dataset.lastupdated;
    const dateCreated = datalayerScriptElement.dataset.datecreated;
    const uuid = datalayerScriptElement.dataset.uuid;

    window.dataLayer = window.dataLayer || [];

    const obj = {};

    function present(value) {
        return value && !!value.length;
    }

    obj['gtm.whitelist'] = ['google', 'jsm', 'lcl'];

    if (present(role)) {
        obj.responsibleRole = role.split('|').length > 1 ? role.split('|') : role;
    }

    if (present(secondaryRole)) {
        obj.secondaryResponsibleRole = secondaryRole.split('|');
    }

    if (present(directorate)) {
        obj.responsibleDirectorate = directorate.split('|').length > 1 ? directorate.split('|') : directorate;
    }

    if (present(secondaryDirectorate)) {
        obj.secondaryResponsibleDirectorate = secondaryDirectorate.split('|');
    }

    if (present(topics)) {
        obj.topics = topics.split('|');
    }

    if (present(publicationDate)) {
        obj.publicationDate = publicationDate;
    }

    if (present(reportingTags)) {
        obj.reportingTags = reportingTags.split('|');
    }

    if (present(policies)) {
        obj.policies = policies.split('|');
    }

    if (present(collections)) {
        obj.collections = collections.split('|');
    }

    if (present(lastUpdated)) {
        obj.lastUpdated = lastUpdated;
    }

    if (present(dateCreated)) {
        obj.dateCreated = dateCreated;
    }

    if (present(uuid)) {
        obj.uuid = uuid;
    }

    window.dataLayer.push(obj);
})();
