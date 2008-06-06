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
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.seasar.sastrutsplugin.nls.Messages;

public class JavaCreationWizard extends Wizard implements INewWizard {

	private IWorkbench workbench;
	private IStructuredSelection selection;
	private JavaCreationWizardPage mainPage;
	private IFile javaFile;
	private IFile jspFile;

	@Override
	public boolean performFinish() {
		return mainPage.finish();
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(Messages.WIZARD_JAVA_CREATION_TITLE);
		this.workbench = workbench;
		this.selection = selection;
	}

	/**
	 * @param javaFile
	 *            the javaFile to set
	 */
	public void setJavaFile(IFile javaFile) {
		this.javaFile = javaFile;
	}

	/**
	 * @param jspFile
	 *            the jspFile to set
	 */
	public void setJspFile(IFile jspFile) {
		this.jspFile = jspFile;
	}

	public void addPages() {
		mainPage = new JavaCreationWizardPage(workbench, selection, javaFile,
				jspFile);
		mainPage.setFileName(javaFile.getName());
		addPage(mainPage);
	}

}
