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
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.corext.refactoring.nls.NLSElement;
import org.eclipse.jdt.internal.corext.refactoring.nls.NLSLine;
import org.eclipse.jdt.internal.corext.refactoring.nls.NLSScanner;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.seasar.eclipse.common.util.WorkbenchUtil;
import org.seasar.sastrutsplugin.SAStrutsConstans;
import org.seasar.sastrutsplugin.naming.AutoNaming;
import org.seasar.sastrutsplugin.naming.DefaultAutoNaming;
import org.seasar.sastrutsplugin.nls.Messages;
import org.seasar.sastrutsplugin.util.IDEUtil;
import org.seasar.sastrutsplugin.util.PreferencesUtil;
import org.seasar.sastrutsplugin.util.StringUtil;
import org.seasar.sastrutsplugin.wizard.JspCreationWizard;

public class OpenJspAction implements IEditorActionDelegate,
		IObjectActionDelegate {

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		IEditorPart editor = WorkbenchUtil.getActiveEditor();
		ITextSelection textSelection = (ITextSelection) ((ITextEditor) editor)
				.getSelectionProvider().getSelection();
		IJavaElement javaElement = JavaUI.getEditorInputJavaElement(editor
				.getEditorInput());
		if (javaElement instanceof ICompilationUnit) {
			ICompilationUnit cunit = (ICompilationUnit) javaElement;
			NLSLine[] lines = createRawLines(cunit);
			String selectedElementText = null;
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].getLineNumber() == textSelection.getStartLine()) {
					selectedElementText = StringUtil
							.decodeString(((NLSElement) lines[i].getElements()[0])
									.getValue());
					break;
				}
			}
			String className = cunit.findPrimaryType().getFullyQualifiedName();
			if (className.endsWith(SAStrutsConstans.ACTION)
					&& !StringUtil.isEmpty(selectedElementText)
					&& selectedElementText
							.endsWith(SAStrutsConstans.JSP_SUFFIX)) {
				String componentName = getComponentName(className);
				String jspPath = getActionPath(componentName)
						+ selectedElementText;
				IFile file = ((FileEditorInput) editor.getEditorInput())
						.getFile();
				IProject project = file.getProject();
				String webRoot = PreferencesUtil.getPreferenceStoreOfProject(
						project).getString(
						SAStrutsConstans.PREF_WEBCONTENTS_ROOT);
				IFile jspFile = project.getFile(webRoot + jspPath);
				if (!jspFile.exists()) {
					if (confirmCreation()) {
						JspCreationWizard wizard = new JspCreationWizard();
						wizard.setFileName(selectedElementText);
						wizard.init(PlatformUI.getWorkbench(),
								new StructuredSelection(jspFile));
						WizardDialog dialog = new WizardDialog(getShell(),
								wizard);
						dialog.open();
					}
				} else {
					IDEUtil.openEditor(jspFile);
				}
			}
		}
	}

	private String getComponentName(String className) {
		AutoNaming naming = new DefaultAutoNaming();
		int index = className.lastIndexOf(".");
		String packageName = className.substring(0, index);
		String shortClassName = className.substring(index + 1);
		return naming.defineName(packageName, shortClassName);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
	}

	/**
	 * Viewのディレクトリを返します。
	 * 
	 * @param componentName
	 *            アクションのコンポーネント名
	 * @return Viewのディレクトリ
	 */
	private String getActionPath(String componentName) {
		if (componentName.equals(SAStrutsConstans.INDEX_ACTION)) {
			return "/";
		}
		if (componentName.endsWith(SAStrutsConstans.ACTION)) {
			return "/"
					+ componentName.substring(0, componentName.length() - 6)
							.replace('_', '/') + "/";
		}
		throw new IllegalArgumentException(componentName);
	}

	private boolean confirmCreation() {
		String title = Messages.JSP_FILE_OPEN_ACTION_CREATION_CONFIRM_TITLE;
		String msg = Messages.JSP_FILE_OPEN_ACTION_CREATION_CONFIRM_MESSAGE;
		return MessageDialog.openConfirm(getShell(), title, msg);
	}

	private Shell getShell() {
		return WorkbenchUtil.getWorkbenchWindow().getShell();
	}

	private static NLSLine[] createRawLines(ICompilationUnit cu) {
		try {
			return NLSScanner.scan(cu);
		} catch (JavaModelException x) {
			return new NLSLine[0];
		} catch (InvalidInputException x) {
			return new NLSLine[0];
		}
	}
}
