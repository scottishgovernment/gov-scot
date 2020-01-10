/*
 *  Copyright 2008-2015 Hippo B.V. (http://www.onehippo.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.hippoecm.frontend.plugins.reviewedactions;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.*;
import org.apache.wicket.util.io.IClusterable;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.dialog.Dialog;
import org.hippoecm.frontend.dialog.DialogConstants;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.icon.HippoIcon;
import org.hippoecm.frontend.service.EditorException;
import org.hippoecm.frontend.service.IEditor;
import org.hippoecm.frontend.service.IEditorManager;
import org.hippoecm.frontend.service.IconSize;
import org.hippoecm.frontend.skin.CmsIcon;
import org.hippoecm.frontend.skin.Icon;
import org.hippoecm.repository.api.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;

public class EditingWorkflowPlugin extends AbstractDocumentWorkflowPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(EditingWorkflowPlugin.class);
    private static final String SERVICE_NAME = "service.edit";
    private static final String UNABLE_TO_SAVE_MESSAGE = "Unable to save the document in the editor";
    private static final String UNABLE_TO_DISCARD_MESSAGE = "Unable to discard the document in the editor";

    public EditingWorkflowPlugin(final IPluginContext context, final IPluginConfig config) {
        super(context, config);

        StdWorkflow saveWorkflow = new StdWorkflow("save", new StringResourceModel("save", this).setModel(null)
                .setDefaultValue("Save")
                .setParameters(), context, getModel()) {

            @Override
            public String getSubMenu() {
                return "top";
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.FLOPPY);
            }

            @Override
            public boolean isFormSubmitted() {
                return true;
            }

            @Override
            protected String execute(Workflow wf) throws Exception {
                final IEditorManager editorMgr = context.getService(SERVICE_NAME, IEditorManager.class);
                IEditor<Node> editor = editorMgr.getEditor(new JcrNodeModel(getModel().getNode()));
                editor.save();

                return null;
            }
        };

        StdWorkflow doneWorkflow = new StdWorkflow("done", new StringResourceModel("done", this).setModel(null)
                .setDefaultValue("Done")
                .setParameters(), context, getModel()) {

            @Override
            public String getSubMenu() {
                return "top";
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.inline(id, CmsIcon.FLOPPY_TIMES_CIRCLE);
            }

            @Override
            public boolean isFormSubmitted() {
                return true;
            }

            @Override
            public String execute(Workflow wf) throws Exception {
                final IEditorManager editorMgr = context.getService(SERVICE_NAME, IEditorManager.class);
                IEditor<Node> editor = editorMgr.getEditor(new JcrNodeModel(getModel().getNode()));
                editor.done();
                return null;
            }
        };

        StdWorkflow closeWorkflow = new StdWorkflow("close", new StringResourceModel("close", this).setModel(null)
                .setDefaultValue("Close")
                .setParameters(), context, getModel()) {
            @Override
            public String getSubMenu() {
                return "top";
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.TIMES);
            }

            @Override
            public boolean isFormSubmitted() {
                return false;
            }

            @Override
            protected String execute(Workflow wf) throws Exception {
                final IEditorManager editorMgr = context.getService(SERVICE_NAME, IEditorManager.class);
                IEditor<Node> editor = editorMgr.getEditor(new JcrNodeModel(getModel().getNode()));

                OnCloseDialog.Actions actions = new OnCloseDialog.Actions() {
                    public void revert() {
                        try {
                            editor.discard();
                        } catch(EditorException e) {
                            LOG.warn(UNABLE_TO_DISCARD_MESSAGE, e);
                            throw new RuntimeException(UNABLE_TO_DISCARD_MESSAGE, e);
                        }
                    }

                    public void save() {
                        try {
                            editor.done();
                        } catch (EditorException e) {
                            LOG.warn(UNABLE_TO_SAVE_MESSAGE, e);
                            throw new RuntimeException(UNABLE_TO_SAVE_MESSAGE, e);
                        }
                    }

                    public void close() {
                        try {
                            editor.close();
                        } catch (EditorException e) {
                            LOG.error(e.getMessage(), e);
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    }
                };

                try {
                    if (editor.isModified() || !editor.isValid()) {
                        OnCloseDialog onCloseDialog = new OnCloseDialog(actions, editor.isValid(), (JcrNodeModel) editor.getModel(), editor.getMode());

                        IDialogService dialogService = getPluginContext().getService(IDialogService.class.getName(),
                                IDialogService.class);
                        dialogService.show(onCloseDialog);
                    } else {
                        if (editor.getMode() == IEditor.Mode.EDIT) {
                            editor.discard();
                        }
                        editor.close();
                    }
                } catch (EditorException e) {
                    LOG.warn(UNABLE_TO_SAVE_MESSAGE, e);
                    throw new RuntimeException(UNABLE_TO_SAVE_MESSAGE, e);
                }

                return null;
            }
        };

        add(saveWorkflow);
        add(doneWorkflow);
        add(closeWorkflow);
    }

    private static class OnCloseDialog extends Dialog<Node> {
        public interface Actions extends IClusterable {

            void save();

            void revert();

            void close();
        }

        public OnCloseDialog(final Actions actions, final boolean isValid, JcrNodeModel model, final IEditor.Mode mode) {
            super(model);

            setOkVisible(false);
            setSize(DialogConstants.SMALL_AUTO);

            add(new Label("label", new ResourceModel(isValid ? "message" : "invalid")));
            add(HippoIcon.fromSprite("icon", Icon.EXCLAMATION_TRIANGLE, IconSize.L));

            final Label exceptionLabel = new Label("exception", "");
            exceptionLabel.setOutputMarkupId(true);
            add(exceptionLabel);

            ResourceModel discardButtonLabel = isValid ? new ResourceModel("discard", "Discard") : new ResourceModel("discard-invalid", "Discard");
            addButton(new AjaxButton(DialogConstants.BUTTON, discardButtonLabel) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form) {
                    try {
                        actions.revert();
                        actions.close();
                        closeDialog();
                    } catch (Exception ex) {
                        exceptionLabel.setDefaultModel(Model.of(ex.getMessage()));
                        target.add(exceptionLabel);
                        LOG.warn("Unable to close the Close button's dialog", ex);
                    }
                }
            });


            final AjaxButton saveButton = new AjaxButton(DialogConstants.BUTTON, new ResourceModel("save", "Save")) {
                @Override
                public boolean isVisible() {
                    return isValid;
                }

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form) {
                    try {
                        actions.save();
                        actions.close();
                        closeDialog();
                    } catch (Exception ex) {
                        exceptionLabel.setDefaultModel(Model.of(ex.getMessage()));
                        target.add(exceptionLabel);
                        LOG.warn("Unable to close the Save button's dialog", ex);
                    }
                }
            };
            saveButton.setEnabled(mode == IEditor.Mode.EDIT);
            addButton(saveButton);
        }

        @Override
        public IModel<String> getTitle() {
            return new StringResourceModel("close-document", this).setModel(null)
                    .setDefaultValue("Close {0}")
                    .setParameters(new PropertyModel(getModel(), "displayName"));
        }
    }

    public WorkflowDescriptorModel getModel() {
        return (WorkflowDescriptorModel) getDefaultModel();
    }
}
