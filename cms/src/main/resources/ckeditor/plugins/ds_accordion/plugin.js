// using a closure so we don't pollute global scope
const accordionPlugin = function () {
    // (it deserves it)
    CKEDITOR.plugins.ass = CKEDITOR.plugins.add;
    CKEDITOR.plugins.ass('ds_accordion', {
        requires: 'widget',
        icons: 'ds_accordion',

        init: function (editor) {
            editor.on('beforeCommandExec', function () {
                editor.accordionPanelIdString = parseInt(Math.random() * 1000000, 10);
            });

            editor.widgets.add('ds_accordion', {
                button: 'Accordion item',
                template: () =>
                    `<div class="ds_accordion-item">
                    <input type="checkbox" class="ds_accordion-item__control visually-hidden" id="panel-${editor.accordionPanelIdString}" aria-labelledby="panel-${editor.accordionPanelIdString}-heading">
                    <div class="ds_accordion-item__header">
                        <h3 id="panel-${editor.accordionPanelIdString}-heading" class="ds_accordion-item__title">
                            Title
                        </h3>
                        <div class="ds_accordion-item__indicator"></div>
                        <label class="ds_accordion-item__label" for="panel-${editor.accordionPanelIdString}"><span class="visually-hidden">Show this section</span></label>
                    </div>
                    <div class="ds_accordion-item__body">
                        Content
                    </div>
                </div>`,
                editables: {
                    title: {
                        selector: '.ds_accordion-item__title',
                        pathName: 'accordionTitle',
                        allowedContent: 'strong;em;abbr;span[lang,dir]',
                        disallowedContent: ''
                    },
                    content: {
                        selector: '.ds_accordion-item__body',
                        pathName: 'accordionBody'
                    }
                },
                parts: {
                    title: '.ds_accordion-item__title'
                },

                init: function () {
                    this.editables.title.$.addEventListener('focus', () => {
                        window.setTimeout(() => {
                            Object.values(editor.commands).forEach(command => {
                                if (command.name.substring(0, 3) === 'ds_') {
                                    command.disable();
                                }
                            });
                        }, 120);
                    });

                    this.editables.content.$.addEventListener('focus', () => {
                        window.setTimeout(() => {
                            Object.values(editor.commands).forEach(command => {
                                if (command.name === 'ds_accordion') {
                                    command.disable();
                                }
                            });
                        }, 120);
                    });

                    this.editables.content.$.addEventListener('blur', () => {
                        window.setTimeout(() => {
                            Object.values(editor.commands).forEach(command => {
                                if (command.name === 'ds_accordion') {
                                    command.enable();
                                }
                            });
                        }, 100);
                    });

                    this.editables.title.$.addEventListener('blur', () => {
                        window.setTimeout(() => {
                            Object.values(editor.commands).forEach(command => {
                                if (command.name.substring(0, 3) === 'ds_') {
                                    command.enable();
                                }
                            });
                        }, 100);
                    });
                },

                allowedContent:
                    [
                        // native element support
                        'h2 h3 h4 p ul ol li strong em input blockquote table button',

                        // generic DS support
                        'span(!visually-hidden)',
                        'strong(!visually-hidden)',

                        // accordion support
                        'input(!ds_accordion-item__control,!visually-hidden)[id,aria-labelledby,type]',
                        'div(!ds_accordion-item)',
                        'div(!ds_accordion-item__header)',
                        'h3(!ds_accordion-item__title)[id]',
                        'div(!ds_accordion-item__body)',
                        'div(!ds_accordion-item__indicator)',
                        'label(!ds_accordion-item__label)[for]',
                        'button(!ds_accordion__open-all)',

                        // warning text support
                        'div(!ds_warning-text);strong(!ds_warning-text__icon);div(!ds_warning-text__text)',

                        // inset text support
                        'div(!ds_inset-text);div(!ds_inset-text__text)'
                    ].join(';'),

                upcast: function (element) {
                    if (element.attributes.class === 'ds_accordion__open-all') {
                        element.remove();
                    }
                    return element.name == 'div' && element.hasClass('ds_accordion-item');
                }
            });
        }
    });
    delete CKEDITOR.plugins.ass;
}();
