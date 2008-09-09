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
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
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
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.seasar.eclipse.common.util.LogUtil;
import org.seasar.eclipse.common.util.WorkbenchUtil;
import org.seasar.sastrutsplugin.Activator;
import org.seasar.sastrutsplugin.SAStrutsConstants;
import org.seasar.sastrutsplugin.bean.FormInfomation;
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
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class OpenJavaAction extends AbstractOpenAction implements
		IWorkbenchWindowActionDelegate, IEditorActionDelegate,
		IObjectActionDelegate {

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
	}

	public void run(IAction action) {
		FormInfomation formInfomation = getFormInfomation();
		if (formInfomation == null) {
			String href = getHref();
			if (StringUtil.isEmpty(href)) {
				formInfomation = new FormInfomation(null,
						SAStrutsConstants.INDEX);
			} else {
				formInfomation = new FormInfomation(null, href);
			}
		}
		IFile jspFile = ((FileEditorInput) WorkbenchUtil.getActiveEditor()
				.getEditorInput()).getFile();
		IProject project = jspFile.getProject();
		String rootPackageName = getRootPackageName(project);
		if (StringUtil.isEmpty(rootPackageName)) {
			return;
		}
		String webRootViewPrefix = getWebRootViewPrefix(project);
		if (StringUtil.isEmpty(webRootViewPrefix)) {
			return;
		}
		String[] splitSubApplications = getSplitSubApplications(jspFile,
				formInfomation.actionAttribute, webRootViewPrefix);
		IFile javaFile = getJavaFile(rootPackageName, jspFile,
				splitSubApplications);
		if (!javaFile.exists()) {
			if (confirmCreation()) {
				JavaCreationWizard wizard = new JavaCreationWizard();
				wizard.setJavaFile(javaFile);
				wizard.setJspFile(jspFile);
				IResource parentResource = javaFile.getParent();
				if (!parentResource.exists()
						&& parentResource.getType() == IResource.FOLDER) {
					createFolderRecursively((IFolder) parentResource);
				}
				wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(
						javaFile));
				WizardDialog dialog = new WizardDialog(getShell(), wizard);
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
		return MessageDialog.openConfirm(getShell(), title, msg);
	}

	private String getHref() {
		IEditorPart editor = WorkbenchUtil.getActiveEditor();
		IStructuredModel model = (IStructuredModel) editor
				.getAdapter(IStructuredModel.class);
		IDOMDocument doc = ((IDOMModel) model).getDocument();
		Element element = doc.getDocumentElement();
		NodeList nodeList = null;
		String hrefAttribute = null;
		nodeList = element.getElementsByTagName(SAStrutsConstants.A_TAG);
		hrefAttribute = getHrefAttribute(editor, nodeList);
		if (StringUtil.isEmpty(hrefAttribute)) {
			nodeList = element.getElementsByTagName(SAStrutsConstants.LINK_TAG);
			return getHrefAttribute(editor, nodeList);
		} else {
			return hrefAttribute;
		}
	}

	private String getHrefAttribute(IEditorPart editor, NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node aNode = nodeList.item(i);
			String hrefAttribute = ((Element) aNode)
					.getAttribute(SAStrutsConstants.HREF_ATTRIBUTE);
			if (!StringUtil.isEmpty(hrefAttribute)) {
				IDOMNode aDomNode = (IDOMNode) aNode;
				if (isMatchLineNumber(editor, aDomNode)) {
					if (hrefAttribute.indexOf("/") != -1) {
						return hrefAttribute.substring(0, hrefAttribute
								.indexOf("/"));
					}
				}
			}
		}
		return null;
	}

	private FormInfomation getFormInfomation() {
		IEditorPart editor = WorkbenchUtil.getActiveEditor();
		IStructuredModel model = (IStructuredModel) editor
				.getAdapter(IStructuredModel.class);
		IDOMDocument doc = ((IDOMModel) model).getDocument();
		Element element = doc.getDocumentElement();
		NodeList formNodeList = element
				.getElementsByTagName(SAStrutsConstants.FORM_TAG);
		for (int i = 0; i < formNodeList.getLength(); i++) {
			Node formNode = formNodeList.item(i);
			String actionAttribute = ((Element) formNode)
					.getAttribute(SAStrutsConstants.LOWER_CASE_ACTION);
			IDOMNode formDomNode = (IDOMNode) formNode;
			if (isMatchLineNumber(editor, formDomNode)) {
				if (StringUtil.isEmpty(actionAttribute)
						|| actionAttribute.startsWith("/")) {
					return new FormInfomation(actionAttribute,
							SAStrutsConstants.INDEX);
				} else {
					return new FormInfomation(actionAttribute, actionAttribute
							.substring(0, actionAttribute.indexOf("/")));
				}
			}
			NodeList inputNodeList = ((Element) formNode)
					.getElementsByTagName(SAStrutsConstants.INPUT);
			for (int j = 0; j < inputNodeList.getLength(); j++) {
				Node inputNode = inputNodeList.item(j);
				String typeAttribute = ((Element) inputNode)
						.getAttribute(SAStrutsConstants.TYPE);
				if (!StringUtil.isEmpty(typeAttribute)
						&& typeAttribute.equals(SAStrutsConstants.SUBMIT)) {
					String nameAttribute = ((Element) inputNode)
							.getAttribute(SAStrutsConstants.NAME);
					IDOMNode inputDomNode = (IDOMNode) inputNode;
					if (isMatchLineNumber(editor, inputDomNode)) {
						if (StringUtil.isEmpty(nameAttribute)) {
							if (StringUtil.isEmpty(actionAttribute)
									|| actionAttribute.startsWith("/")) {
								nameAttribute = SAStrutsConstants.SUBMIT;
							} else {
								nameAttribute = actionAttribute.substring(0,
										actionAttribute.indexOf("/"));
							}
						}
						return new FormInfomation(actionAttribute,
								nameAttribute);
					}
				}
			}
		}
		return null;
	}

	private boolean isMatchLineNumber(IEditorPart editor, IDOMNode domNode) {
		int lineNumber = -1;
		try {
			IDocument document = ((ITextEditor) editor).getDocumentProvider()
					.getDocument(editor.getEditorInput());
			lineNumber = document.getLineOfOffset(domNode.getStartOffset());
		} catch (BadLocationException e) {
			LogUtil.log(Activator.getDefault(), e);
		}
		ITextSelection textSelection = (ITextSelection) ((ITextEditor) editor)
				.getSelectionProvider().getSelection();
		if (lineNumber != -1 && lineNumber == textSelection.getStartLine()) {
			return true;
		} else {
			return false;
		}
	}

	private String[] getSplitSubApplications(IFile jspFile,
			String actionAttribute, String webRootViewPrefix) {
		String[] names = null;
		if (!StringUtil.isEmpty(actionAttribute)
				&& actionAttribute.startsWith("/")) {
			names = StringUtil.split(actionAttribute, "/");
		} else {
			IProject project = jspFile.getProject();
			String jspFilePath = jspFile.getFullPath().toOSString();
			String projectPath = project.getFullPath().toOSString();
			jspFilePath = jspFilePath.substring(projectPath.length()
					+ webRootViewPrefix.length() + 1, jspFilePath.length());
			int lastIndexOf = jspFilePath.lastIndexOf(File.separator);
			if (lastIndexOf == -1) {
				return null;
			} else {
				String componentName = jspFilePath.substring(0, lastIndexOf);
				names = StringUtil.split(componentName, File.separator);
			}
		}
		return names;
	}

	private IFile getJavaFile(String rootPackageName, IFile jspFile,
			String[] splitSubApplications) {
		String mainJavaPath = PreferencesUtil.getPreferenceStoreOfProject(
				jspFile.getProject()).getString(
				SAStrutsConstants.PREF_MAIN_JAVA_PATH);
		if (splitSubApplications == null) {
			IFile indexActionJavaFile = jspFile.getProject().getFile(
					mainJavaPath + File.separator
							+ rootPackageName.replace('.', '/')
							+ File.separator
							+ SAStrutsConstants.LOWER_CASE_ACTION
							+ File.separator
							+ SAStrutsConstants.CAPITALIZE_INDEX_ACTION
							+ SAStrutsConstants.JAVA_SUFFIX);
			return indexActionJavaFile;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < splitSubApplications.length - 1; i++) {
			sb.append(splitSubApplications[i]).append(File.separator);
		}
		String firstCandidateAction = sb.toString()
				+ StringUtil
						.capitalize(splitSubApplications[splitSubApplications.length - 1])
				+ SAStrutsConstants.ACTION + SAStrutsConstants.JAVA_SUFFIX;
		String secondCandidateAction = sb.toString()
				+ splitSubApplications[splitSubApplications.length - 1]
				+ File.separator + SAStrutsConstants.CAPITALIZE_INDEX_ACTION
				+ SAStrutsConstants.JAVA_SUFFIX;
		IFile firstCandidateJavaFile = jspFile.getProject().getFile(
				mainJavaPath + File.separator
						+ rootPackageName.replace('.', '/') + File.separator
						+ SAStrutsConstants.LOWER_CASE_ACTION + File.separator
						+ firstCandidateAction);
		IFile secondCandidateJavaFile = jspFile.getProject().getFile(
				mainJavaPath + File.separator
						+ rootPackageName.replace('.', '/') + File.separator
						+ SAStrutsConstants.LOWER_CASE_ACTION + File.separator
						+ secondCandidateAction);
		if (firstCandidateJavaFile.exists()) {
			return firstCandidateJavaFile;
		} else if (secondCandidateJavaFile.exists()) {
			return secondCandidateJavaFile;
		} else {
			return firstCandidateJavaFile;
		}
	}

	private String getRootPackageName(IProject project) {
		String conventionDiconPath = PreferencesUtil
				.getPreferenceStoreOfProject(project).getString(
						SAStrutsConstants.PREF_CONVENTION_DICON_PATH);
		File conventionDicon = ((Path) project.getFile(conventionDiconPath)
				.getLocation()).toFile();
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			dbfactory.setNamespaceAware(true);
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			builder.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {
					if (publicId.equals(SAStrutsConstants.PUBLIC_ID_DICON_24)
							&& systemId
									.equals(SAStrutsConstants.SYSTEM_ID_DICON_24)) {
						try {
							InputSource source = new InputSource(Activator
									.getDefault().getBundle().getEntry(
											SAStrutsConstants.DTD_DICON_24)
									.openStream());
							return source;
						} catch (IOException e) {
							LogUtil.log(Activator.getDefault(), e);
						}
					}
					return null;
				}
			});
			Document doc = builder.parse(conventionDicon);
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath
					.compile(SAStrutsConstants.ROOTPACKAGE_XPATH);
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes.getLength() == 0) {
				String title = Messages.ERROR_DIALOG_ROOT_PACKAGE_NAME_TITLE;
				String msg = Messages.ERROR_DIALOG_ROOT_PACKAGE_NAME_NOT_FOUND_MESSAGE;
				MessageDialog.openError(getShell(), title, msg);
			} else if (nodes.getLength() == 1) {
				return StringUtil.decodeString(nodes.item(0).getNodeValue());
			} else {
				for (int i = 0; i < nodes.getLength(); i++) {
					String rootPackageName = StringUtil.decodeString(nodes
							.item(i).getNodeValue());
					IJavaProject javaProject = JavaCore.create(project);
					IPackageFragmentRoot[] roots = javaProject
							.getPackageFragmentRoots();
					for (int j = 0; j < roots.length; j++) {
						if (roots[j].getKind() == IPackageFragmentRoot.K_SOURCE) {
							IPackageFragment packageFragment = roots[j]
									.getPackageFragment(rootPackageName
											+ "."
											+ SAStrutsConstants.LOWER_CASE_ACTION);
							if (packageFragment.exists()) {
								return rootPackageName;
							}
						}
					}
				}
			}
		} catch (XPathExpressionException e) {
			LogUtil.log(Activator.getDefault(), e);
			showConventionDiconAnalyzeErrorDialog(e);
		} catch (DOMException e) {
			LogUtil.log(Activator.getDefault(), e);
			showConventionDiconAnalyzeErrorDialog(e);
		} catch (ParserConfigurationException e) {
			LogUtil.log(Activator.getDefault(), e);
			showConventionDiconAnalyzeErrorDialog(e);
		} catch (SAXException e) {
			LogUtil.log(Activator.getDefault(), e);
			showConventionDiconAnalyzeErrorDialog(e);
		} catch (IOException e) {
			LogUtil.log(Activator.getDefault(), e);
			showConventionDiconAnalyzeErrorDialog(e);
		} catch (JavaModelException e) {
			LogUtil.log(Activator.getDefault(), e);
			showConventionDiconAnalyzeErrorDialog(e);
		}
		return null;
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

}
