CKEDITOR.plugins.add('ds_warning-text', {
    requires: 'widget',
    icons: 'ds_warning-text',

    init: function (editor) {
        CKEDITOR.dtd.$removeEmpty.strong = false;
        editor.widgets.add('ds_warning-text', {
            button: 'Warning text',
            pathName: 'warning',

            template: `<div class="ds_warning-text">
                <div class="ds_warning-text__icon" aria-hidden="true">
                    <svg class="ds_icon  ds_icon--fill" aria-hidden="true" role="img">
                        <use href="/webfiles/latest/assets/images/icons/icons.stack.svg#warning"></use>
                    </svg>
                </div>
                <strong class="visually-hidden">Warning</strong>
                <div class="ds_warning-text__text">

                </div>
            </div>`,

            editables: {
                content: {
                    selector: '.ds_warning-text__text',
                    pathName: 'inner',
                    allowedContent: 'p br ul ol li strong em; span[lang,dir]; a[!href]; a[!data-uuid]'
                },
            },

            parts: {
                content: 'div.ds_warning-text__text'
            },

            allowedContent: 'div strong(!ds_warning-text__icon)[!aria-hidden]; svg(!ds_icon, !ds_icon--fill)[!aria-hidden, !role]; use[!href]; strong(!visually-hidden); div(!ds_warning-text__text)',


            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'ds_warning-text' );
            },

            init: function () {
                const selection = editor.getSelection();
                if (selection) {
                    const range = selection.getRanges()[0];

                    if (!range.collapsed) {
                        this.parts.content.setHtml(range.cloneContents().getHtml());
                    } else if (range.startContainer.type === CKEDITOR.NODE_TEXT) {
                        // expand to nearest block-level element
                        var iterator = range.createIterator();
                        var block;

                        while ((block = iterator.getNextParagraph())) {
                            this.parts.content.setHtml(block.getHtml());
                            block.remove();
                        }
                    }
                }
            }
        });
    }
});
