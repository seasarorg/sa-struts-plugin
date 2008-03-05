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

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.seasar.eclipse.common.util.LogUtil;
import org.seasar.eclipse.common.util.WorkbenchUtil;
import org.seasar.sastrutsplugin.Activator;

public class IDEUtil {

	public static IEditorPart openEditor(IFile file) {
		return openEditor(PlatformUI.getWorkbench(), file);
	}

	public static IEditorPart openEditor(IWorkbench workbench, IFile file) {
		if (workbench == null || file == null || !file.exists()) {
			return null;
		}
		IWorkbenchWindow window = WorkbenchUtil.getWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		if (page != null) {
			try {
				return IDE.openEditor(page, file);
			} catch (PartInitException e) {
				LogUtil.log(Activator.getDefault(), e);
			}
		}
		return null;
	}
}
