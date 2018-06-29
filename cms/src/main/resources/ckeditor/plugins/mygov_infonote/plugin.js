CKEDITOR.plugins.add( 'mygov_infonote', {
    init: function( editor ) {
        editor.addContentsCss(this.path + 'infonote.css');
        if (document.getElementById('infoNoteStyles') === null) {
            let link = document.createElement('link');
            link.id = 'infoNoteStyles';
            link.rel = 'stylesheet';
            link.type = 'text/css';
            link.href = this.path + 'infonote.css';
            document.body.appendChild(link);
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

            if (!CKEDITOR.hasAddedInfoNote) {
                let loadedStyleSet = CKEDITOR.stylesSet.loaded;
                for (let key in loadedStyleSet) {
                    if (loadedStyleSet[key] === 1) {
                        loadedStyleSetName = key;
                        break;
                    }
                }

                let stylesSet = CKEDITOR.stylesSet.registered[loadedStyleSetName] || [];
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

                CKEDITOR.hasAddedInfoNote = true;
            }
        }
    }
});
