// This plugin removes unneeded fields and the Advanced tab from the Tables dialog
CKEDITOR.plugins.add( 'simple_tables', {
    init: function( editor ) {
        CKEDITOR.on( 'dialogDefinition', function( ev )
        {
            var dialogName = ev.data.name;
            var dialogDefinition = ev.data.definition;

            if (dialogName == 'table') {

                dialogDefinition.removeContents('advanced');

                var infoTab = dialogDefinition.getContents('info');

                infoTab.remove('txtBorder');
                infoTab.remove('cmbAlign');
                infoTab.remove('txtWidth');
                infoTab.remove('txtHeight');
                infoTab.remove('txtCellSpace');
                infoTab.remove('txtCellPad');
                infoTab.remove('txtCaption');
                infoTab.remove('txtSummary');
            }

            // Replace the Cell Properties dialog's "info" tab with just a horizontal
            // alignment field. The stock hAlign/vAlign/wordWrap/width/height/bgColor/
            // borderColor fields all apply inline styles, so CKEditor's own content
            // filter (disallowedContent: *[style]{*}) strips them out of the dialog
            // before this listener even runs - they're simply not there to trim.
            // Our replacement field applies alignment as a class instead, so it
            // survives that filter.
            if (dialogName == 'cellProperties') {

                var ALIGN_CLASSES = ['lAlign', 'cAlign', 'rAlign'];
                var CLASS_TO_VALUE = { lAlign: 'left', cAlign: 'center', rAlign: 'right' };
                var VALUE_TO_CLASS = { left: 'lAlign', center: 'cAlign', right: 'rAlign' };

                dialogDefinition.removeContents('info');
                dialogDefinition.addContents({
                    id: 'info',
                    label: editor.lang.table.cell.title,
                    elements: [{
                        type: 'select',
                        id: 'hAlign',
                        requiredContent: 'td(rAlign)',
                        label: editor.lang.table.cell.hAlign,
                        'default': '',
                        items: [
                            [editor.lang.common.notSet, ''],
                            [editor.lang.common.left, 'left'],
                            [editor.lang.common.center, 'center'],
                            [editor.lang.common.right, 'right']
                        ],
                        setup: function( cells ) {
                            var value = '';
                            for (var i = 0; i < ALIGN_CLASSES.length; i++) {
                                if (cells[0].hasClass(ALIGN_CLASSES[i])) {
                                    value = CLASS_TO_VALUE[ALIGN_CLASSES[i]];
                                    break;
                                }
                            }
                            this.setValue(value);
                        },
                        commit: function( cell ) {
                            var value = this.getValue();

                            for (var i = 0; i < ALIGN_CLASSES.length; i++) {
                                cell.removeClass(ALIGN_CLASSES[i]);
                            }

                            if (value && VALUE_TO_CLASS[value]) {
                                cell.addClass(VALUE_TO_CLASS[value]);
                            }
                        }
                    }]
                });
            }
        });
    }
});
