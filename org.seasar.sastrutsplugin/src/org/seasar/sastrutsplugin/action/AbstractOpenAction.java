package org.seasar.sastrutsplugin.action;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.seasar.eclipse.common.util.LogUtil;
import org.seasar.eclipse.common.util.WorkbenchUtil;
import org.seasar.sastrutsplugin.Activator;
import org.seasar.sastrutsplugin.SAStrutsConstants;
import org.seasar.sastrutsplugin.nls.Messages;
import org.seasar.sastrutsplugin.util.PreferencesUtil;
import org.seasar.sastrutsplugin.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class AbstractOpenAction {

	protected void createFolderRecursively(IFolder folder) {
		if (!folder.getParent().exists()) {
			createFolderRecursively((IFolder) folder.getParent());
		}
		try {
			folder.create(false, true, null);
		} catch (CoreException e) {
			LogUtil.log(Activator.getDefault(), e);
		}
	}

	protected String getViewPrefix(File webXmlFile) throws SAXException,
			IOException, ParserConfigurationException {
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		dbfactory.setNamespaceAware(true);
		DocumentBuilder builder = dbfactory.newDocumentBuilder();
		Document doc = builder.parse(webXmlFile);
		Element element = doc.getDocumentElement();
		NodeList contextParamNodeList = element
				.getElementsByTagName(SAStrutsConstants.CONTEXT_PARAM);
		if (contextParamNodeList.getLength() == 1
				&& contextParamNodeList.item(0) instanceof Element) {
			Element contextParamElement = (Element) contextParamNodeList
					.item(0);
			NodeList paramNameNodeList = contextParamElement
					.getElementsByTagName(SAStrutsConstants.PARAM_NAME);
			if (paramNameNodeList.getLength() == 1) {
				if (((Node) paramNameNodeList.item(0)).getTextContent().equals(
						SAStrutsConstants.SASTRUTS_VIEW_PREFIX)) {
					NodeList paramValueNodeList = contextParamElement
							.getElementsByTagName(SAStrutsConstants.PARAM_VALUE);
					if (paramValueNodeList.getLength() == 1) {
						return ((Node) paramValueNodeList.item(0))
								.getTextContent();
					}
				}
			}
		}
		return null;
	}

	protected String getWebRootViewPrefix(IProject project) {
		String webRoot = PreferencesUtil.getPreferenceStoreOfProject(project)
				.getString(SAStrutsConstants.PREF_WEBCONTENTS_ROOT);
		if (webRoot.endsWith(File.separator)) {
			webRoot = webRoot.substring(0, webRoot.length() - 1);
		}
		File webXmlFile = ((Path) project.getFile(
				webRoot + SAStrutsConstants.WEB_INF_WEB_XML).getLocation())
				.toFile();
		if (webXmlFile.exists()) {
			String viewPrefix = null;
			try {
				viewPrefix = getViewPrefix(webXmlFile);
			} catch (ParserConfigurationException e) {
				LogUtil.log(Activator.getDefault(), e);
				showWebXmlAnalyzeErrorDialog(e);
				return null;
			} catch (SAXException e) {
				LogUtil.log(Activator.getDefault(), e);
				showWebXmlAnalyzeErrorDialog(e);
				return null;
			} catch (IOException e) {
				LogUtil.log(Activator.getDefault(), e);
				showWebXmlAnalyzeErrorDialog(e);
				return null;
			}
			if (!StringUtil.isEmpty(viewPrefix)) {
				webRoot += viewPrefix;
			}
			return webRoot;
		} else {
			MessageDialog.openError(getShell(),
					Messages.ERROR_DIALOG_WEB_XML_NOT_FOUND_TITLE,
					Messages.bind(
							Messages.ERROR_DIALOG_WEB_XML_NOT_FOUND_MESSAGE,
							webXmlFile.getAbsolutePath()));
			return null;
		}
	}

	protected Shell getShell() {
		return WorkbenchUtil.getWorkbenchWindow().getShell();
	}

	protected void showWebXmlAnalyzeErrorDialog(Exception e) {
		MessageDialog
				.openError(
						getShell(),
						Messages.ERROR_DIALOG_WEB_XML_ANALYZE_ERROR_TITLE,
						Messages
								.bind(
										Messages.ERROR_DIALOG_WEB_XML_ANALYZE_ERROR_MESSAGE,
										e));
	}

	protected void showConventionDiconAnalyzeErrorDialog(Exception e) {
		MessageDialog
				.openError(
						getShell(),
						Messages.ERROR_DIALOG_CONVENTION_DICON_ANALYZE_ERROR_TITLE,
						Messages
								.bind(
										Messages.ERROR_DIALOG_CONVENTION_DICONANALYZE_ERROR_MESSAGE,
										e));
	}

}
