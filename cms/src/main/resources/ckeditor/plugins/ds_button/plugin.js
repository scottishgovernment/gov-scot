CKEDITOR.plugins.add( 'ds_button', {
    init: function (editor) {
        editor.addCommand( 'ds_button', new CKEDITOR.dialogCommand( 'dsButtonDialog' ) );
        editor.ui.addButton( 'ds_button', {
            label: 'Button',
            command: 'ds_button',
            toolbar: 'insert, 1',
            icon: this.path + 'images/ds_button.png'
        });
        editor.on('doubleclick', function (evt) {
            var element = evt.data.element;
            if ( element.hasClass('ds_button') ) {
                evt.data.dialog = 'dsButtonDialog';

                window.buttonClickElement = element;
                window.setTimeout(
                    () => delete window.buttonClickElement,
                    200
                );
            }
        });

        CKEDITOR.dialog.add( 'dsButtonDialog', this.path + 'dialogs/ds_button.js' );
    }
});
