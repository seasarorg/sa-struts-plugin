/*
 * Copyright 2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.seasar.sastrutsplugin.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.seasar.eclipse.common.util.WorkbenchUtil;
import org.seasar.sastrutsplugin.bean.FormInfomation;
import org.seasar.sastrutsplugin.nls.Messages;
import org.seasar.sastrutsplugin.util.IDEUtil;
import org.seasar.sastrutsplugin.util.SAStrutsUtil;
import org.seasar.sastrutsplugin.wizard.JavaCreationWizard;

public class OpenJavaAction implements IWorkbenchWindowActionDelegate,
		IEditorActionDelegate, IObjectActionDelegate {

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
	}

	public void run(IAction action) {
		FormInfomation formInfomation = SAStrutsUtil.getFormInformation();
		IFile jspFile = ((FileEditorInput) WorkbenchUtil.getActiveEditor()
				.getEditorInput()).getFile();
		IFile javaFile = SAStrutsUtil.getJavaFileFromJSP(jspFile,
				formInfomation.actionAttribute);
		if (!javaFile.exists()) {
			if (confirmCreation()) {
				JavaCreationWizard wizard = new JavaCreationWizard();
				wizard.setJavaFile(javaFile);
				wizard.setJspFile(jspFile);
				IResource parentResource = javaFile.getParent();
				if (!parentResource.exists()
						&& parentResource.getType() == IResource.FOLDER) {
					SAStrutsUtil
							.createFolderRecursively((IFolder) parentResource);
				}
				wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(
						javaFile));
				WizardDialog dialog = new WizardDialog(SAStrutsUtil.getShell(),
						wizard);
				dialog.open();
			}
		} else {
			IDEUtil.openEditor(javaFile, formInfomation.nameAttribute);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	private boolean confirmCreation() {
		String title = Messages.JAVA_FILE_OPEN_ACTION_CREATION_CONFIRM_TITLE;
		String msg = Messages.JAVA_FILE_OPEN_ACTION_CREATION_CONFIRM_MESSAGE;
		return MessageDialog.openConfirm(SAStrutsUtil.getShell(), title, msg);
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

}
