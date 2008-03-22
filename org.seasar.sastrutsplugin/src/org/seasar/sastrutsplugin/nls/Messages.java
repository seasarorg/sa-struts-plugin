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

package org.seasar.sastrutsplugin.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	static {
		Class clazz = Messages.class;
		NLS.initializeMessages(clazz.getName(), clazz);
	}

	public static String JSP_FILE_OPEN_ACTION_CREATION_CONFIRM_TITLE;

	public static String JSP_FILE_OPEN_ACTION_CREATION_CONFIRM_MESSAGE;

	public static String JAVA_FILE_OPEN_ACTION_CREATION_CONFIRM_TITLE;

	public static String JAVA_FILE_OPEN_ACTION_CREATION_CONFIRM_MESSAGE;

	public static String WIZARD_JSP_CREATION_TITLE;

	public static String WIZARD_JSP_CREATION_PAGE_TITLE;

	public static String WIZARD_JSP_CREATION_PAGE_NEW_FILE_LABEL;

	public static String WIZARD_JAVA_CREATION_TITLE;

	public static String WIZARD_JAVA_CREATION_PAGE_TITLE;

	public static String WIZARD_JAVA_CREATION_PAGE_NEW_FILE_LABEL;

	public static String PROPERTY_PAGE_WEBAPP_ROOT;

	public static String PROPERTY_PAGE_BROWSE;

	public static String PROPERTY_PAGE_MAIN_JAVA_PATH;

	public static String PROPERTY_PAGE_CONVENTION_DICON_PATH;

}
