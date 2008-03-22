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

package org.seasar.sastrutsplugin.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.seasar.sastrutsplugin.nls.Messages;
import org.seasar.sastrutsplugin.util.IDEUtil;

public class JavaCreationWizardPage extends WizardNewFileCreationPage {

	private IWorkbench workbench;

	public JavaCreationWizardPage(IWorkbench workbench,
			IStructuredSelection selection) {
		super("JavaCreationPage1", selection);
		setTitle(Messages.WIZARD_JAVA_CREATION_PAGE_TITLE);
		this.workbench = workbench;
	}

	public boolean finish() {
		IFile newFile = createNewFile();
		return newFile != null ? IDEUtil.openEditor(workbench, newFile) != null
				: true;
	}

	protected String getNewFileLabel() {
		return Messages.WIZARD_JAVA_CREATION_PAGE_NEW_FILE_LABEL;
	}
}