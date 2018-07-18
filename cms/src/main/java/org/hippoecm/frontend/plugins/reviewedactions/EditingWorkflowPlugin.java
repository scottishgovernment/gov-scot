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

import java.text.DateFormat;
import java.util.Date;

import javax.jcr.Node;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.icon.HippoIcon;
import org.hippoecm.frontend.service.IEditor;
import org.hippoecm.frontend.service.IEditorManager;
import org.hippoecm.frontend.skin.CmsIcon;
import org.hippoecm.frontend.skin.Icon;
import org.hippoecm.repository.api.Workflow;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.IClusterable;
import org.hippoecm.frontend.dialog.Dialog;
import org.hippoecm.frontend.dialog.DialogConstants;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.service.IconSize;
import org.hippoecm.frontend.service.EditorException;

public class EditingWorkflowPlugin extends AbstractDocumentWorkflowPlugin {

    public EditingWorkflowPlugin(final IPluginContext context, final IPluginConfig config) {
        super(context, config);

        add(new StdWorkflow("save", new StringResourceModel("save", this, null, "Save"), context, getModel()) {

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
                final IEditorManager editorMgr = context.getService("service.edit", IEditorManager.class);
                IEditor<Node> editor = editorMgr.getEditor(new JcrNodeModel(getModel().getNode()));
                editor.save();

                return null;
            }
        });

        add(new StdWorkflow("done", new StringResourceModel("done", this, null, "Done"), context, getModel()) {

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
                final IEditorManager editorMgr = context.getService("service.edit", IEditorManager.class);
                IEditor<Node> editor = editorMgr.getEditor(new JcrNodeModel(getModel().getNode()));
                editor.done();
                return null;
            }
        });

        add(new StdWorkflow("close", new StringResourceModel("close", this, null, "Close"), context, getModel()) {
            @Override
            public String getSubMenu() {
                return "top";
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.TIMES_CIRCLE);
            }

            @Override
            public boolean isFormSubmitted() {
                return true;
            }
            
            @Override
            protected String execute(Workflow wf) throws Exception {
                final IEditorManager editorMgr = context.getService("service.edit", IEditorManager.class);
                IEditor<Node> editor = editorMgr.getEditor(new JcrNodeModel(getModel().getNode()));
                editor.close();

                return null;


                try {
                    if (editor.isModified() || !editor.isValid()) {
                        OnCloseDialog onCloseDialog = new OnCloseDialog(new OnCloseDialog.Actions() {
                            public void revert() {
                                try {
                                    editor.discard();
                                } catch(EditorException e) {
                                    log.warn("Unable to discard the document in the editor", e);
                                    throw new RuntimeException("Unable to discard the document in the editor", e);
                                }
                            }

                            public void save() {
                                try {
                                    editor.done();
                                } catch (EditorException e) {
                                    log.warn("Unable to save the document in the editor", e);
                                    throw new RuntimeException("Unable to save the document in the editor", e);
                                }
                            }

                            public void close() {
                                try {
                                    editor.close();
                                } catch (EditorException ex) {
                                    log.error(ex.getMessage());
                                }
                            }
                        });

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
                    log.warn("Unable to save the document in the editor", e);
                    throw new RuntimeException("Unable to save the document in the editor", e);
                }
            }
        });
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

            ResourceModel discardButtonLabel = isValid ? new ResourceModel("discard", "Discard") : new ResourceModel("discard-invalid");
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
                    }
                }
            };
            saveButton.setEnabled(mode == IEditor.Mode.EDIT);
            addButton(saveButton);
        }

        @Override
        public IModel<String> getTitle() {
            return new StringResourceModel("close-document", this, null, "Close {0}",
                    new PropertyModel(getModel(), "displayName"));
        }
    }

    public WorkflowDescriptorModel getModel() {
        return (WorkflowDescriptorModel) getDefaultModel();
    }
}
