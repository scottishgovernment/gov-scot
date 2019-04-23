var statBoxDialogContents = [
    {
        id: 'tab-1',
        label: 'Box 1',
        elements: [
            {
                type: 'text',
                id: 'number1',
                label: 'Box 1 Number',
                validate: CKEDITOR.dialog.validate.regex( /^.{1,4}$/, 'Number field must be 1-4 characters long' ),
                setup: function( element ) {
                    this.setValue( element.$.querySelectorAll('.stat-box__value')[0].innerText );
                }
            },
            {
                type: 'text',
                id: 'description1',
                label: 'Box 1 Description Text',
                validate: CKEDITOR.dialog.validate.notEmpty( 'Description field cannot be empty.' ),
                setup: function( element ) {
                    this.setValue( element.$.querySelectorAll('.stat-box__label-text')[0].innerText );
                }
            },
            {
                type: 'text',
                id: 'url1',
                label: 'Box 1 URL',
                setup: function( element ) {
                    this.setValue( element.$.querySelectorAll('.stat-box')[0].getAttribute('href') );
                }
            }
        ]
    },
    {
        id: 'tab-2',
        label: 'Box 2',
        elements: [
            {
                type: 'text',
                id: 'number2',
                label: 'Box 2 Number',
                validate: CKEDITOR.dialog.validate.regex( /^.{1,4}$/, 'Number field must be 1-4 characters long' ),
                setup: function( element ) {
                    this.setValue( element.$.querySelectorAll('.stat-box__value')[1].innerText );
                }
            },
            {
                type: 'text',
                id: 'description2',
                label: 'Box 2 Description Text',
                validate: CKEDITOR.dialog.validate.notEmpty( 'Description field cannot be empty.' ),
                setup: function( element ) {
                    this.setValue( element.$.querySelectorAll('.stat-box__label-text')[1].innerText );
                }
            },
            {
                type: 'text',
                id: 'url2',
                label: 'Box 2 URL',
                setup: function( element ) {
                    this.setValue( element.$.querySelectorAll('.stat-box')[1].getAttribute('href') );
                }
            }
        ]
    },
    {
        id: 'tab-3',
        label: 'Box 3',
        elements: [
            {
                type: 'text',
                id: 'number3',
                label: 'Box 3 Number',
                validate: CKEDITOR.dialog.validate.regex( /^.{1,4}$/, 'Number field must be 1-4 characters long' ),
                setup: function( element ) {
                    this.setValue( element.$.querySelectorAll('.stat-box__value')[2].innerText );
                }
            },
            {
                type: 'text',
                id: 'description3',
                label: 'Box 3 Description Text',
                validate: CKEDITOR.dialog.validate.notEmpty( 'Description field cannot be empty.' ),
                setup: function( element ) {
                    this.setValue( element.$.querySelectorAll('.stat-box__label-text')[2].innerText );
                }
            },
            {
                type: 'text',
                id: 'url3',
                label: 'Box 3 URL',
                setup: function( element ) {
                    this.setValue( element.$.querySelectorAll('.stat-box')[2].getAttribute('href') );
                }
            }
        ]
    }
];

CKEDITOR.dialog.add( 'statboxDialog', function( editor ) {
    return {
        title: 'Add a Stat Box block',
        minWidth: 400,
        minHeight: 200,
        contents: statBoxDialogContents,
        onShow: function () {
            var selection = editor.getSelection();
            var element = selection.getStartElement();

            if (element && element.hasClass('stat-box__value')) {
                element = element.getParent().getParent();
            } else if (element && element.hasClass('stat-box__label-text')){
                element = element.getParent().getParent().getParent();
            }

            this.insertMode = (!element || !element.hasClass('stat-boxes'));

            if (!this.insertMode) {
                this.element = element;
                this.setupContent(element);
            }
        },
        onOk: function() {
            var dialog = this;

            let number1 = dialog.getValueOf('tab-1', 'number1');
            let description1 = dialog.getValueOf('tab-1', 'description1');
            let url1 = dialog.getValueOf('tab-1', 'url1');
            let number2 = dialog.getValueOf('tab-2', 'number2');
            let description2 = dialog.getValueOf('tab-2', 'description2');
            let url2 = dialog.getValueOf('tab-2', 'url2');
            let number3 = dialog.getValueOf('tab-3', 'number3');
            let description3 = dialog.getValueOf('tab-3', 'description3');
            let url3 = dialog.getValueOf('tab-3', 'url3');

            var html = (url1 ? '<a href="' + url1 + '" class="stat-box">' : '<div class="stat-box">')
                + '<span class="stat-box__value">' + number1 + '</span>'
                + '<span class="stat-box__label">'
                +   '<span class="stat-box__label-text">' + description1 + '</span>'
                +   '</span>'
                + (url1 ? '</a>' : '</div>')
                + (url2 ? '<a href="' + url2 + '" class="stat-box">' : '<div class="stat-box">')
                + '<span class="stat-box__value">' + number2 + '</span>'
                +   '<span class="stat-box__label">'
                +   '<span class="stat-box__label-text">' + description2 + '</span>'
                +   '</span>'
                + (url2 ? '</a>' : '</div>')
                + (url3 ? '<a href="' + url3 + '" class="stat-box">' : '<div class="stat-box">')
                + '<span class="stat-box__value">' + number3 + '</span>'
                +    '<span class="stat-box__label">'
                +    '<span class="stat-box__label-text">' + description3 + '</span>'
                +    '</span>'
                + (url3 ? '</a>' : '</div>');

            if (!this.insertMode){
                this.element.$.innerHTML = html;
            } else {
                var statBox = editor.document.createElement('div');
                statBox.setAttribute('class', 'stat-boxes');
                statBox.setHtml(html);

                editor.insertElement(statBox);
            }
        }
    };
});
