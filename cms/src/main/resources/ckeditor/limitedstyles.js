(function() {
    "use strict";

    var ELEMENTS = ['p','h2','h3','h4','address'],
        TRANSLATIONS = {
            de: {
                address: 'Adresse',
                h2: 'Überschrift 2',
                h3: 'Überschrift 3',
                h4: 'Überschrift 4',
                p: 'Normal'
            },
            en: {
                address: 'Address',
                h2: 'Heading 2',
                h3: 'Heading 3',
                h4: 'Heading 4',
                p: 'Normal'
            },
            fr: {
                address: 'Adresse',
                h2: 'En-tête 2',
                h3: 'En-tête 3',
                h4: 'En-tête 4',
                p: 'Normal'
            },
            nl: {
                address: 'Adres',
                h2: 'Kop 2',
                h3: 'Kop 3',
                h4: 'Kop 4',
                p: 'Normaal'
            },
            es: {
                address: 'Dirección',
                h2: 'Encabezado 2',
                h3: 'Encabezado 3',
                h4: 'Encabezado 4',
                p: 'Normal'
            },
            zh: {
                address: '地址',
                h2: '标题二',
                h3: '标题三',
                h4: '标题四',
                p: '平常'
            }
        },
        stylesSet, language, element, i, len;

    // add a separate styles resource per language
    for (language in TRANSLATIONS) {
        stylesSet = [];
        for (i = 0, len = ELEMENTS.length; i < len; i++) {
            element = ELEMENTS[i];
            stylesSet.push({
                element: element,
                name: TRANSLATIONS[language][element] || element
            });
        }
        CKEDITOR.stylesSet.add('limitedstyles_' + language, stylesSet);
    }

}());
