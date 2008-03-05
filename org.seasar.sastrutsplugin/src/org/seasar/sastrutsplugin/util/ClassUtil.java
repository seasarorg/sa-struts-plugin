/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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

/**
 * {@link Class}用のユーティリティクラスです。
 * 
 */
public final class ClassUtil {

	/**
	 * クラス名の要素を結合します。
	 * 
	 * @param s1
	 * @param s2
	 * @return 結合された名前
	 */
	public static String concatName(String s1, String s2) {
		if (StringUtil.isEmpty(s1) && StringUtil.isEmpty(s2)) {
			return null;
		}
		if (!StringUtil.isEmpty(s1) && StringUtil.isEmpty(s2)) {
			return s1;
		}
		if (StringUtil.isEmpty(s1) && !StringUtil.isEmpty(s2)) {
			return s2;
		}
		return s1 + '.' + s2;
	}
}
