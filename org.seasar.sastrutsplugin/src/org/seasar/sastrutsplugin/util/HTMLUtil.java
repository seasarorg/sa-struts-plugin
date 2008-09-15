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

package org.seasar.sastrutsplugin.util;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavadocContentAccess;
import org.seasar.eclipse.common.util.LogUtil;
import org.seasar.sastrutsplugin.Activator;

public class HTMLUtil {

	public static String extractJavadoc(IMember member) {
		if (member != null) {
			Reader reader = null;
			try {
				reader = JavadocContentAccess.getContentReader(member, true);
			} catch (JavaModelException e) {
				LogUtil.log(Activator.getDefault(), e);
				return null;
			}
			if (reader != null) {
				String info = getStringFromReader(reader);
				return info;
			}
		}
		return null;
	}

	private static String getStringFromReader(Reader reader) {
		StringBuffer buf = new StringBuffer();
		char[] buffer = new char[1024];
		int count;
		try {
			while ((count = reader.read(buffer)) != -1)
				buf.append(buffer, 0, count);
		} catch (IOException e) {
			return null;
		}
		return buf.toString();
	}

}
