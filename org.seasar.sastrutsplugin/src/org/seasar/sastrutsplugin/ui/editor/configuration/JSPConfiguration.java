package org.seasar.sastrutsplugin.ui.editor.configuration;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jst.jsp.core.text.IJSPPartitions;
import org.eclipse.jst.jsp.ui.StructuredTextViewerConfigurationJSP;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.seasar.sastrutsplugin.ui.editor.contentassist.JSPAssistProcessor;

public class JSPConfiguration extends StructuredTextViewerConfigurationJSP {

	protected IContentAssistProcessor[] getContentAssistProcessors(
			ISourceViewer sourceViewer, String partitionType) {
		if ((partitionType == IXMLPartitions.XML_DEFAULT)
				|| (partitionType == IHTMLPartitions.HTML_DEFAULT)
				|| (partitionType == IHTMLPartitions.HTML_COMMENT)
				|| (partitionType == IJSPPartitions.JSP_DEFAULT)
				|| (partitionType == IJSPPartitions.JSP_DIRECTIVE)
				|| (partitionType == IJSPPartitions.JSP_CONTENT_DELIMITER)
				|| (partitionType == IJSPPartitions.JSP_CONTENT_JAVASCRIPT)
				|| (partitionType == IJSPPartitions.JSP_COMMENT)) {
			return new IContentAssistProcessor[] { new JSPAssistProcessor() };
		}
		return super.getContentAssistProcessors(sourceViewer, partitionType);
	}

}
