CKEDITOR.dialog.add('dsButtonDialog', function (editor) {
    return {
        title: 'Button',
        minWidth: 400,
        minHeight: 200,
        contents: [
            {
                id: 'tab-basic',
                elements: [
                    {
                        type: 'text',
                        id: 'button-text',
                        label: 'Button text',

                        default: 'Button',
                        validate: CKEDITOR.dialog.validate.notEmpty("Button text field cannot be empty."),
                        setup: function (element) {
                            if (!!element.getText()) {
                                this.setValue(element.getText());
                            } else {
                                let selectedText = editor.getSelection().getSelectedText();
                                if (selectedText) {
                                    this.setValue(selectedText);
                                }
                            }
                        },
                        commit: function (element) {
                            element.setText(this.getValue());
                        }
                    },
                    {
                        type: 'text',
                        id: 'button-url',
                        label: 'URL',
                        setup: function (element) {
                            this.setValue(element.getAttribute("href"));
                        },
                        commit: function (element) {
                            element.setAttribute("href", this.getValue());
                            element.removeAttribute('data-cke-saved-href');
                        }
                    },
                    {
                        type: 'select',
                        id: 'button-target',
                        label: 'Target',
                        items: [ [ '_self' ], [ '_blank' ] ],
                        'default': '_self',
                        setup: function (element) {
                            this.setValue(element.getAttribute("target"));
                        },
                        commit: function(element) {
                            element.setAttribute("target", this.getValue());
                        }
                    },
                    {
                        type: 'select',
                        id: 'button-type',
                        label: 'Type',
                        items: [ [ 'Primary' ], [ 'Secondary' ] ],
                        'default': 'Primary',
                        setup: function (element) {
                            if (element.hasClass('ds_button--secondary')) {
                                this.setValue('Secondary');
                            } else {
                                this.setValue('Primary');
                            }
                        },
                        commit: function (element) {
                            element.$.classList.remove('ds_button--secondary');
                            if (this.getValue() === 'Secondary') {
                                element.$.classList.add('ds_button--secondary');
                            }
                        }
                    },
                    {
                        type: 'select',
                        id: 'button-width',
                        label: 'Width',
                        items: [ [ 'Fixed' ], [ 'Flexible' ], [ 'Max' ] ],
                        'default': 'Fixed',
                        setup: function (element) {
                            if (element.hasClass('ds_button--fixed')) {
                                this.setValue('Fixed');
                            } else if (element.hasClass('ds_button--max')) {
                                this.setValue('Max');
                            } else {
                                this.setValue('Flexible');
                            }
                        },
                        commit: function (element) {
                            element.$.classList.remove('ds_button--fixed', 'ds_button--max');
                            if (this.getValue() === 'Fixed') {
                                element.$.classList.add('ds_button--fixed');
                            }
                            if (this.getValue() === 'Max') {
                                element.$.classList.add('ds_button--max');
                            }
                        }
                    }
                ]
            }
        ],

        onShow: function () {
            var element;
            var selection = editor.getSelection();

            if (window.buttonClickElement) {
                element = window.buttonClickElement;
            } else {
                element = selection.getStartElement();
            }

            if (!element || !element.hasClass('ds_button')) {
                element = editor.document.createElement('a');
                element.setAttribute('class', 'ds_button');
                this.insertMode = true;
            } else
                this.insertMode = false;

            this.element = element;
            this.setupContent(this.element);
        },

        onOk: function () {
            var ds_btn = this.element;
            this.commitContent(ds_btn);

            if (this.insertMode)
                editor.insertElement(ds_btn);
        }
    };
});
