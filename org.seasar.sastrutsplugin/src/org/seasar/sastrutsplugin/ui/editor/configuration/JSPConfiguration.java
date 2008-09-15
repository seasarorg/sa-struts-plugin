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
