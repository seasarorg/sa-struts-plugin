package org.seasar.sastrutsplugin.action;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.seasar.eclipse.common.util.LogUtil;
import org.seasar.sastrutsplugin.Activator;
import org.seasar.sastrutsplugin.SAStrutsConstants;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class AbstractOpenAction {

	protected String getViewPrefix(File webXmlFile) {
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
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
					if (((Node) paramNameNodeList.item(0)).getTextContent()
							.equals(SAStrutsConstants.SASTRUTS_VIEW_PREFIX)) {
						NodeList paramValueNodeList = contextParamElement
								.getElementsByTagName(SAStrutsConstants.PARAM_VALUE);
						if (paramValueNodeList.getLength() == 1) {
							return ((Node) paramValueNodeList.item(0))
									.getTextContent();
						}
					}
				}
			}
		} catch (DOMException e) {
			LogUtil.log(Activator.getDefault(), e);
		} catch (ParserConfigurationException e) {
			LogUtil.log(Activator.getDefault(), e);
		} catch (SAXException e) {
			LogUtil.log(Activator.getDefault(), e);
		} catch (IOException e) {
			LogUtil.log(Activator.getDefault(), e);
		}
		return "/";
	}

}
