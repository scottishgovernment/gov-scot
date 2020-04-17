// using a closure so we don't pollute global scope
const accordionPlugin = function () {

    if (typeof CKEDITOR.config.contentsCss === 'string') {
        CKEDITOR.config.contentsCss = [CKEDITOR.config.contentsCss];
    }
    CKEDITOR.config.contentsCss.push(CKEDITOR.basePath + CKEDITOR.plugins.basePath + 'accordion/accordion.css');


    // (it deserves it)
    CKEDITOR.plugins.ass = CKEDITOR.plugins.add;
    CKEDITOR.plugins.ass('accordion', {
        requires: 'widget',
        icons: 'accordion,accordionitem',
        init: function (editor) {

            editor.on('instanceReady', function (event) {

                const editorContents = document.getElementById(event.editor.id + '_contents');

                editorContents.addEventListener('click', function (innerEvent) {
                    if (innerEvent.target.classList.contains('ds_accordion')) {
                        innerEvent.preventDefault();
                        addAccordionPanel(innerEvent.target, editor);
                    }
                });
            });

            editor.on('afterCommandExec', function (event) {
                if (event.data.name === 'accordion') {
                    const editorElement = document.querySelector(`.${event.editor.id}`);
                    const accordions = [].slice.call(editorElement.querySelectorAll('.ds_accordion'));
                    accordions.forEach(accordion => removeUnwantedAccordionChildren(accordion));

                }
            });

            editor.accordionInsertDefault = false;

            editor.widgets.add('accordion', {
                button: 'Add accordion',
                template: `<div class="ds_accordion" data-module="ds_accordion"></div>`,
                editables: {
                    items: {
                        selector: '.ds_accordion',
                        pathName: 'accordion'
                    }
                },

                requiredContent: 'div(ds_accordion-item)',

                edit: function () {
                    removeUnwantedAccordionChildren(this.element.$);
                    // window.setTimeout(() => addAccordionPanel(this.element.$, editor), 0);
                },

                allowedContent:
                    'div(!ds_accordion)[data-module];div(!ds_accordion-item);input(!ds_accordion-item__control,!hidden)[id,aria-labelledby,type];div(!ds_accordion-item__header);h3(!ds_accordion-item__title)[id];div(!ds_accordion-item__body);div(!ds_accordion-item__indicator);label(!ds_accordion-item__label)[for];span(!hidden)',

                upcast: function( element ) {
                    return element.name == 'div' && element.hasClass( 'ds_accordion' );
                }
            });

            editor.widgets.add('accordionitem', {
                // button: 'Add accordion item',
                template:
                    `<div class="ds_accordion-item">
                    <input type="checkbox" class="ds_accordion-item__control hidden" id="panel-XXXXXXXXX" aria-labelledby="panel-XXXXXXXXX-heading">
                    <div class="ds_accordion-item__header">
                        <h3 id="panel-XXXXXXXXX-heading" class="ds_accordion-item__title">
                            Title
                        </h3>
                        <div class="ds_accordion-item__indicator"></div>
                        <label class="ds_accordion-item__label" for="panel-XXXXXXXXX"><span class="hidden">Show this section</span></label>
                    </div>
                    <div class="ds_accordion-item__body">
                        Content
                    </div>
                </div>`,
                editables: {
                    title: {
                        selector: '.ds_accordion-item__title',
                        pathName: 'accordionTitle',
                        allowedContent: 'strong;em;abbr',
                        disallowedContent: ''
                    },
                    content: {
                        selector: '.ds_accordion-item__body',
                        pathName: 'accordionBody',
                        // allowedContent: 'p;ul;ol;li;strong;em;input;a[*];table;img[*];blockquote'
                    }
                },

                allowedContent:
                    'input;div(!ds_accordion);div(!ds_accordion-item);input(!ds_accordion-item__control)[id,aria-labelledby,type];div(!ds_accordion-item__header);h3(!ds_accordion-item__title)[id];div(!ds_accordion-item__body);span(!ds_accordion-item__indicator);label(!ds_accordion-item__label)[for];span(!hidden)',

                upcast: function (element) {
                    return element.name == 'div' && element.hasClass('ds_accordion-item');
                }
            });
        }
    });
    delete CKEDITOR.plugins.ass;

    function placeCaretAtEnd(el) {
        el.focus();

        if (typeof window.getSelection != "undefined"
            && typeof document.createRange != "undefined") {
            var range = document.createRange();
            range.selectNodeContents(el);
            range.collapse(false);
            var sel = window.getSelection();
            sel.removeAllRanges();
            sel.addRange(range);
        } else if (typeof document.body.createTextRange != "undefined") {
            var textRange = document.body.createTextRange();
            textRange.moveToElementText(el);
            textRange.collapse(false);
            textRange.select();
        }
    }

    function removeUnwantedAccordionChildren(parent) {
        for (let i = 0; i < parent.childNodes.length; i++) {
            const childNode = parent.childNodes[i];

            if (
                childNode.nodeType === Node.TEXT_NODE ||
                (childNode && childNode.classList && !childNode.classList.contains('cke_widget_wrapper'))
            ) {
                childNode.parentNode.removeChild(childNode);
            }
        }
    }

    function addAccordionPanel(accordionElement, editor) {
        placeCaretAtEnd(accordionElement);

        const accordionPanelIdString = parseInt(Math.random() * 1000000, 10);

        editor.commands.accordionitem.exec();

        removeUnwantedAccordionChildren(accordionElement);
        const accordions = [].slice.call(editor.element.$.parentNode.querySelectorAll('.ds_accordion'));
        accordions.forEach(function (accordion) {
            accordion.querySelector('[id="panel-XXXXXXXXX"]').setAttribute('id', `panel-${accordionPanelIdString}`);
            accordion.querySelector('[for="panel-XXXXXXXXX"]').setAttribute('for', `panel-${accordionPanelIdString}`);

            accordion.querySelector('[id="panel-XXXXXXXXX-heading"]').setAttribute('id', `panel-${accordionPanelIdString}-heading`);
            accordion.querySelector('[aria-labelledby="panel-XXXXXXXXX-heading"]').setAttribute('aria-labelledby', `panel-${accordionPanelIdString}-heading`);
        });
    }
}();
