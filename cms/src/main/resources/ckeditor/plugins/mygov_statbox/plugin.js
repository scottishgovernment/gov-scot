CKEDITOR.plugins.add( 'mygov_statbox', {
    init: function( editor ) {
        editor.addCommand( 'insertStatbox', new CKEDITOR.dialogCommand( 'statboxDialog' ) );
        editor.ui.addButton( 'Stat Box', {
            label: 'Insert Stat Box',
            command: 'insertStatbox',
            toolbar: 'insert',
            icon: this.path + 'icons/mygov_statbox.png'
        });

        if ( editor.contextMenu ) {
            editor.addMenuGroup('statboxGroup');
            editor.addMenuItem('statboxItem', {
                label: 'Edit Stat Box',
                icon: this.path + 'icons/mygov_statbox.png',
                command: 'insertStatbox',
                group: 'statboxGroup'
            });

            editor.contextMenu.addListener(function (element) {
                if (element.getParent().getParent().hasClass('stat-boxes') || element.getParent().getParent().getParent().hasClass('stat-boxes')) {
                    return {statboxItem: CKEDITOR.TRISTATE_OFF};
                }
            });
        }

        CKEDITOR.dialog.add( 'statboxDialog', this.path + 'dialogs/statbox.js' );
    }
});
