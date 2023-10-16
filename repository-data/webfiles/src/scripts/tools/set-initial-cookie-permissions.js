import storage from '../../../node_modules/@scottish-government/design-system/src/base/tools/storage/storage';

export default function () {
    const permissionsString = storage.getCookie('cookiePermissions') || '';

    if (!storage.isJsonString(permissionsString)) {
        const permissions = {};
        permissions.statistics = true;
        permissions.preferences = true;

        storage.setCookie(storage.categories.necessary,
            'cookiePermissions',
            JSON.stringify(permissions)
        );
    }
};
