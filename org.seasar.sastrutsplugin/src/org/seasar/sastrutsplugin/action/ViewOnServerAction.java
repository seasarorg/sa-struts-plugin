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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.seasar.eclipse.common.util.ResouceUtil;
import org.seasar.eclipse.common.util.WorkbenchUtil;
import org.seasar.sastrutsplugin.SAStrutsConstants;
import org.seasar.sastrutsplugin.util.PreferencesUtil;
import org.seasar.sastrutsplugin.util.StringUtil;

public class ViewOnServerAction implements IWorkbenchWindowActionDelegate {

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		IResource resource = ResouceUtil.getCurrentSelectedResouce();
		if (resource instanceof IFile) {
			IFile actionFile = (IFile) resource;
			IProject project = actionFile.getProject();
			String actionFilePath = actionFile.getFullPath().toOSString();
			IPreferenceStore store = PreferencesUtil
					.getPreferenceStoreOfProject(project);
			int lastIndexOfLowerCaseAction = actionFilePath
					.lastIndexOf(SAStrutsConstants.LOWER_CASE_ACTION + File.separator);
			int lastIndexOfAction = actionFilePath
					.lastIndexOf(SAStrutsConstants.ACTION);
			if (lastIndexOfLowerCaseAction != -1 && lastIndexOfAction != -1) {
				String path = actionFilePath.substring(
						lastIndexOfLowerCaseAction
								+ SAStrutsConstants.LOWER_CASE_ACTION.length()
								+ 1, lastIndexOfAction);
				String[] splitSubApplications = StringUtil.split(path,
						File.separator);
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < splitSubApplications.length - 1; i++) {
					sb.append(splitSubApplications[i]).append("/");
				}
				String context = store.getString(SAStrutsConstants.PREF_CONTEXT);
				if(StringUtil.isEmpty(context)){
					context = resource.getProject().getName();
				}
				
				path = sb.toString()
						+ StringUtil
								.decapitalize(splitSubApplications[splitSubApplications.length - 1]);
				IPath p = new Path(store
						.getString(SAStrutsConstants.PREF_WEBSERVER)).append(
						"/").append(context)
						.append("/");
				if (!path.equals(SAStrutsConstants.INDEX)) {
					p = p.append(path);
				}
				WorkbenchUtil.openUrl(p.toString());
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
