CKEDITOR.plugins.add( 'mygov_infonote', {
    init: function( editor ) {
        editor.addContentsCss(this.path + 'infonote.css');
        if ($('#infoNoteStyles').length === 0) {
            $('<link id="infoNoteStyles" rel="stylesheet" type="text/css" href="' + this.path + 'infonote.css"/>').appendTo('body');
        }

        if (editor.config.addButtons) {
            editor.addCommand( 'information', new CKEDITOR.dialogCommand( 'informationDialog' ) );
            editor.ui.addButton( 'InfoNote', {
                label: 'Insert information note',
                command: 'information',
                toolbar: 'mygov',
                icon: this.path + 'icons/note.png'
            });
            
            editor.addCommand( 'caution', new CKEDITOR.dialogCommand( 'cautionDialog' ) );
            editor.ui.addButton( 'Caution', {
                label: 'Insert caution note',
                command: 'caution',
                toolbar: 'mygov',
                icon: this.path + 'icons/caution.png'
            });        
        
            CKEDITOR.dialog.add( 'informationDialog', this.path + 'dialogs/information.js' );
            CKEDITOR.dialog.add( 'cautionDialog', this.path + 'dialogs/caution.js' );
        } else {
            let stylesSet = CKEDITOR.stylesSet.registered.hippo_en || [];
            stylesSet.push({
                element: 'div',
                name: 'Information',
                attributes: {
                    class: "info-note note"
                }
            });
            stylesSet.push({
                element: 'div',
                name: 'Caution',
                attributes: {
                    class: "info-note caution"
                }
            });
        }
    }
});
