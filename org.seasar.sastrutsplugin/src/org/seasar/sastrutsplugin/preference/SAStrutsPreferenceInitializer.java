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

package org.seasar.sastrutsplugin.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.seasar.sastrutsplugin.SAStrutsConstants;
import org.seasar.sastrutsplugin.util.PreferencesUtil;

public class SAStrutsPreferenceInitializer extends
		AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = PreferencesUtil
				.getPreferenceStoreOfWorkspace();
		store.setDefault(SAStrutsConstants.PREF_WEBCONTENTS_ROOT,
				SAStrutsConstants.PREF_DEFAULT_WEBCONTENTS_ROOT);
		store.setDefault(SAStrutsConstants.PREF_MAIN_JAVA_PATH,
				SAStrutsConstants.PREF_DEFAULT_MAIN_JAVA_PATH);
		store.setDefault(SAStrutsConstants.PREF_CONVENTION_DICON_PATH,
				SAStrutsConstants.PREF_DEFAULT_CONVENTION_DICON_PATH);
	}

}
