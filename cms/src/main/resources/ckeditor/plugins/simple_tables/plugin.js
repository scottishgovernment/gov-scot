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
        });
    }
});