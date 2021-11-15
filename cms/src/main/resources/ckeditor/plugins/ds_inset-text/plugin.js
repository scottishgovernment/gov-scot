CKEDITOR.plugins.add('ds_inset-text', {
    requires: 'widget',
    icons: 'ds_inset-text',
    init: function (editor) {
        editor.widgets.add('ds_inset-text', {
            button: 'Inset text',
            pathName: 'inset',

            template: '<div class="ds_inset-text">' +
                '<div class="ds_inset-text__text">' +
                    '' +
                '</div>' +
            '</div>',

            editables: {
                content: {
                    selector: '.ds_inset-text__text',
                    pathName: 'inner',
                    allowedContent: 'p br ul ol li strong em; a[href]; a[data-uuid]'
                },
            },

            parts: {
                content: 'div.ds_inset-text__text'
            },

            allowedContent: 'div(!ds_inset-text__text)',

            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'ds_inset-text' );
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
