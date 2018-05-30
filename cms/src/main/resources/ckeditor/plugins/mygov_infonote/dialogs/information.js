CKEDITOR.dialog.add( 'informationDialog', function( editor ) {
    return {
        title: '"Information" note properties',
        minWidth: 400,
        minHeight: 200,
        contents: [
            {
                id: 'tab-basic',
                label: 'Settings',
                elements: [
                    {
                        type: 'textarea',
                        id: 'noteText',
                        label: 'Text',
                        validate: CKEDITOR.dialog.validate.notEmpty( "Note text field cannot be empty." )
                    }
                ]
            }
        ],
        onOk: function() {
            var dialog = this;

            let html = `
                <div class="info-note note">
                    ${dialog.getValueOf('tab-basic', 'noteText')}
                </div>
            `;
            editor.insertHtml( html );
        },
        onShow: function () {
            let selectedHtml = this._.editor.getSelectedHtml();
            if (selectedHtml) {
                // set dialog text to selection.getSelectedText();
                let textField = this.getContentElement('tab-basic', 'noteText');
                textField.setValue(selectedHtml.getHtml());
            }
        }
    };
});
