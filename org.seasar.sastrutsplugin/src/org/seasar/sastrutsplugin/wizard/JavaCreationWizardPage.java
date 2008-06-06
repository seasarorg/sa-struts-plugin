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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.seasar.sastrutsplugin.SAStrutsConstants;
import org.seasar.sastrutsplugin.nls.Messages;
import org.seasar.sastrutsplugin.util.IDEUtil;
import org.seasar.sastrutsplugin.util.PreferencesUtil;

public class JavaCreationWizardPage extends WizardNewFileCreationPage {

	private IWorkbench workbench;

	private IFile javaFile;

	private IFile jspFile;

	public JavaCreationWizardPage(IWorkbench workbench,
			IStructuredSelection selection, IFile javaFile, IFile jspFile) {
		super("JavaCreationPage1", selection);
		setTitle(Messages.WIZARD_JAVA_CREATION_PAGE_TITLE);
		this.workbench = workbench;
		this.javaFile = javaFile;
		this.jspFile = jspFile;
	}

	public boolean finish() {
		IFile newFile = createNewFile();
		return newFile != null ? IDEUtil.openEditor(workbench, newFile) != null
				: true;
	}

	protected String getNewFileLabel() {
		return Messages.WIZARD_JAVA_CREATION_PAGE_NEW_FILE_LABEL;
	}

	@Override
	protected InputStream getInitialContents() {
		final String lineDelim = "\n";
		StringBuffer sb = new StringBuffer();
		IProject project = javaFile.getProject();
		String projectPath = project.getFullPath().toOSString();
		String javaPath = javaFile.getFullPath().toOSString();
		String mainJavaPath = PreferencesUtil.getPreferenceStoreOfProject(
				project).getString(SAStrutsConstants.PREF_MAIN_JAVA_PATH);
		int lastIndex = javaPath.lastIndexOf(File.separator);
		String packageName = javaPath.substring(projectPath.length()
				+ mainJavaPath.length() + 1, lastIndex);
		String actionName = javaFile.getName().substring(
				0,
				javaFile.getName().length()
						- SAStrutsConstants.JAVA_SUFFIX.length());

		sb.append("package ").append(packageName.replace(File.separator, "."))
				.append(";").append(lineDelim).append(lineDelim).append(
						"import org.seasar.struts.annotation.Execute;").append(
						lineDelim).append(lineDelim).append("public class ")
				.append(actionName).append(" {").append(lineDelim).append(
						lineDelim).append("\t").append(
						"@Execute(validator = false)").append(lineDelim)
				.append("\t").append("public String index() {").append(
						lineDelim).append("\t").append("\t")
				.append("return \"").append(jspFile.getName()).append("\";")
				.append(lineDelim).append("\t").append("}").append(lineDelim)
				.append("}");

		InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
		return is;
	}
}
