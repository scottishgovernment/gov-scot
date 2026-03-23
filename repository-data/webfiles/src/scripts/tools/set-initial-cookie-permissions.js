import storage from '@scottish-government/design-system/dist/scripts/base/tools/storage/storage';

export default function () {
    const permissionsString = storage.getCookie('cookiePermissions') || '';

    if (!storage.getIsJsonString(permissionsString)) {
        const permissions = {};
        permissions.statistics = false;
        permissions.preferences = true;

        storage.setCookie('necessary',
            'cookiePermissions',
            JSON.stringify(permissions)
        );
    }
};
