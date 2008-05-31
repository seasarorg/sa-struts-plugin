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

package org.seasar.sastrutsplugin;

public final class SAStrutsConstants {

	public static final String PREF_WEBCONTENTS_ROOT = "WebContentsRoot";

	public static final String PREF_DEFAULT_WEBCONTENTS_ROOT = "/webapp";

	public static final String PREF_MAIN_JAVA_PATH = "MainJavaPath";

	public static final String PREF_DEFAULT_MAIN_JAVA_PATH = "/src/main/java";

	public static final String PREF_CONVENTION_DICON_PATH = "ConventionDiconPath";

	public static final String PREF_DEFAULT_CONVENTION_DICON_PATH = "/src/main/resources/convention.dicon";

	public static final String ACTION = "Action";

	public static final String LOWER_CASE_ACTION = "action";

	public static final String INDEX_ACTION = "indexAction";
	
	public static final String CAPITALIZE_INDEX_ACTION = "IndexAction";

	public static final String JSP_SUFFIX = ".jsp";

	public static final String JAVA_SUFFIX = ".java";

	public static final String FORM_TAG = "s:form";

	public static final String ROOTPACKAGE_XPATH = "/components/component[@class='org.seasar.framework.convention.impl.NamingConventionImpl']/initMethod[@name='addRootPackageName']/arg/text()";

	public static final String INDEX = "index";

	public static final String INPUT = "input";

	public static final String SUBMIT = "submit";

	public static final String NAME = "name";

	public static final String TYPE = "type";

	public static final String SYSTEM_ID_DICON_24 = "http://www.seasar.org/dtd/components24.dtd";

	public static final String PUBLIC_ID_DICON_24 = "-//SEASAR//DTD S2Container 2.4//EN";

	public static final String DTD_DICON_24 = "/components24.dtd";

	public static final String WEB_INF_WEB_XML = "/WEB-INF/web.xml";

	public static final String CONTEXT_PARAM = "context-param";

	public static final String PARAM_NAME = "param-name";

	public static final String SASTRUTS_VIEW_PREFIX = "sastruts.VIEW_PREFIX";

	public static final String PARAM_VALUE = "param-value";

	public static final String A_TAG = "a";

	public static final String HREF_ATTRIBUTE = "href";

}
