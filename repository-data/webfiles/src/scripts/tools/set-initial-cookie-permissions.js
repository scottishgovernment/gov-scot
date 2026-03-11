import storage from '@scottish-government/design-system/dist/scripts/base/tools/storage/storage';

export default function () {
    const permissionsString = storage.getCookie('cookiePermissions') || '';

    if (!storage.isJsonString(permissionsString)) {
        const permissions = {};
        permissions.statistics = false;
        permissions.preferences = true;

        storage.setCookie(storage.categories.necessary,
            'cookiePermissions',
            JSON.stringify(permissions)
        );
    }
};
