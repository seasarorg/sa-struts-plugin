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

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.seasar.eclipse.common.util.LogUtil;
import org.seasar.eclipse.common.util.WorkbenchUtil;
import org.seasar.sastrutsplugin.Activator;
import org.seasar.sastrutsplugin.SAStrutsConstans;
import org.seasar.sastrutsplugin.nls.Messages;
import org.seasar.sastrutsplugin.util.IDEUtil;
import org.seasar.sastrutsplugin.util.PreferencesUtil;
import org.seasar.sastrutsplugin.util.StringUtil;
import org.seasar.sastrutsplugin.wizard.JavaCreationWizard;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OpenJavaAction implements IWorkbenchWindowActionDelegate,
		IEditorActionDelegate, IObjectActionDelegate {

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
	}

	public void run(IAction action) {
		String actionAttribute = getActionAttribute();
		if (StringUtil.isEmpty(actionAttribute)) {
			return;
		}
		IFile jspFile = ((FileEditorInput) WorkbenchUtil.getActiveEditor()
				.getEditorInput()).getFile();
		IProject project = jspFile.getProject();
		String rootPackageName = getRootPackageName(project);
		if (StringUtil.isEmpty(rootPackageName)) {
			return;
		}
		String javaFileName = getJavaFileName(jspFile, actionAttribute);
		if (StringUtil.isEmpty(javaFileName)) {
			return;
		}
		String mainJavaPath = PreferencesUtil.getPreferenceStoreOfProject(
				project).getString(SAStrutsConstans.PREF_MAIN_JAVA_PATH);
		IFile javaFile = project.getFile(mainJavaPath + File.separator
				+ rootPackageName + File.separator
				+ SAStrutsConstans.LOWER_CASE_ACTION + File.separator
				+ javaFileName);
		if (!javaFile.exists()) {
			if (confirmCreation()) {
				JavaCreationWizard wizard = new JavaCreationWizard();
				wizard.setFileName(javaFileName);
				wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(
						javaFile));
				WizardDialog dialog = new WizardDialog(getShell(), wizard);
				dialog.open();
			}
		} else {
			IDEUtil.openEditor(javaFile);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	private boolean confirmCreation() {
		String title = Messages.JAVA_FILE_OPEN_ACTION_CREATION_CONFIRM_TITLE;
		String msg = Messages.JAVA_FILE_OPEN_ACTION_CREATION_CONFIRM_MESSAGE;
		return MessageDialog.openConfirm(getShell(), title, msg);
	}

	private Shell getShell() {
		return WorkbenchUtil.getWorkbenchWindow().getShell();
	}

	private String getActionAttribute() {
		IEditorPart editor = WorkbenchUtil.getActiveEditor();
		IStructuredModel model = (IStructuredModel) editor
				.getAdapter(IStructuredModel.class);
		IDOMDocument doc = ((IDOMModel) model).getDocument();
		Element element = doc.getDocumentElement();
		NodeList nodeList = element
				.getElementsByTagName(SAStrutsConstans.FORM_TAG);
		String actionAttribute = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String tempActionAttribute = ((Element) node)
					.getAttribute(SAStrutsConstans.LOWER_CASE_ACTION);
			if (!StringUtil.isEmpty(tempActionAttribute)) {
				IDOMNode domNode = (IDOMNode) node;
				int lineNumber = -1;
				try {
					IDocument document = ((ITextEditor) editor)
							.getDocumentProvider().getDocument(
									editor.getEditorInput());
					lineNumber = document.getLineOfOffset(domNode
							.getStartOffset());
				} catch (BadLocationException e) {
					LogUtil.log(Activator.getDefault(), e);
				}
				ITextSelection textSelection = (ITextSelection) ((ITextEditor) editor)
						.getSelectionProvider().getSelection();
				if (lineNumber != -1
						&& lineNumber == textSelection.getStartLine()) {
					actionAttribute = tempActionAttribute;
					break;
				}
			}
		}
		return actionAttribute;
	}

	private String getJavaFileName(IFile jspFile, String actionAttribute) {
		if (actionAttribute.startsWith("/")) {
			if (actionAttribute.lastIndexOf('/') == 0) {
				return StringUtil.capitalize(actionAttribute.substring(1))
						+ SAStrutsConstans.ACTION
						+ SAStrutsConstans.JAVA_SUFFIX;
			} else {
				String subAppName = actionAttribute.substring(1,
						actionAttribute.lastIndexOf('/'));
				return subAppName
						+ StringUtil.capitalize(actionAttribute
								.substring(actionAttribute
										.lastIndexOf('/')))
						+ SAStrutsConstans.ACTION
						+ SAStrutsConstans.JAVA_SUFFIX;
			}
		} else {
			IProject project = jspFile.getProject();
			String jspFilePath = jspFile.getFullPath().toOSString();
			String projectPath = project.getFullPath().toOSString();
			String webRoot = PreferencesUtil.getPreferenceStoreOfProject(
					project).getString(SAStrutsConstans.PREF_WEBCONTENTS_ROOT);
			jspFilePath = jspFilePath.substring(projectPath.length()
					+ webRoot.length() + 1, jspFilePath.length());
			String componentName = jspFilePath.substring(0, jspFilePath
					.lastIndexOf(File.separator));
			return StringUtil.capitalize(componentName)
					+ SAStrutsConstans.ACTION + SAStrutsConstans.JAVA_SUFFIX;
		}
	}

	private String getRootPackageName(IProject project) {
		String conventionDiconPath = PreferencesUtil
				.getPreferenceStoreOfProject(project).getString(
						SAStrutsConstans.PREF_CONVENTION_DICON_PATH);
		File conventionDicon = ((Path) project.getFile(conventionDiconPath)
				.getLocation()).toFile();
		String rootPackageName = null;
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			dbfactory.setNamespaceAware(true);
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			Document doc = builder.parse(conventionDicon);
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath
					.compile(SAStrutsConstans.ROOTPACKAGE_XPATH);
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() == 1) {
				rootPackageName = StringUtil.decodeString(nodes.item(0)
						.getNodeValue());
			}
		} catch (XPathExpressionException e) {
			LogUtil.log(Activator.getDefault(), e);
		} catch (DOMException e) {
			LogUtil.log(Activator.getDefault(), e);
		} catch (ParserConfigurationException e) {
			LogUtil.log(Activator.getDefault(), e);
		} catch (SAXException e) {
			LogUtil.log(Activator.getDefault(), e);
		} catch (IOException e) {
			LogUtil.log(Activator.getDefault(), e);
		}
		return rootPackageName;
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

}