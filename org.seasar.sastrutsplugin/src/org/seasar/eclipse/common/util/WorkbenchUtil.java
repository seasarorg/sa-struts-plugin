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
package org.seasar.eclipse.common.util;

import java.net.URL;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.seasar.eclipse.common.CommonPlugin;
import org.seasar.sastrutsplugin.util.StringUtil;

/**
 * 
 * @author taichi
 * 
 */
public class WorkbenchUtil {

	public static Shell getShell() {
		IWorkbenchWindow window = getWorkbenchWindow();
		return window != null ? window.getShell() : new Shell(Display
				.getDefault());
	}

	public static IWorkbenchWindow getWorkbenchWindow() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow result = workbench.getActiveWorkbenchWindow();
		if (result == null && 0 < workbench.getWorkbenchWindowCount()) {
			IWorkbenchWindow[] ws = workbench.getWorkbenchWindows();
			result = ws[0];
		}
		return result;
	}

	public static IEditorPart getActiveEditor() {
		IWorkbenchWindow window = getWorkbenchWindow();
		if (window == null) {
			return null;
		}
		final IWorkbenchPage activePage = window.getActivePage();
		if (activePage == null) {
			return null;
		}
		return activePage.getActiveEditor();
	}

	public static IViewPart findView(String viewId) {
		IViewPart vp = null;
		IWorkbenchWindow window = getWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				vp = page.findView(viewId);
			}
		}
		return vp;
	}

	public static IViewPart showView(String viewId) {
		IViewPart vp = null;
		try {
			IWorkbenchWindow window = getWorkbenchWindow();
			if (window != null) {
				IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					vp = page.showView(viewId);
				}
			}
		} catch (PartInitException e) {
			LogUtil.log(ResourcesPlugin.getPlugin(), e);
		}
		return vp;
	}

	public static void selectAndReveal(IResource newResource) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		BasicNewResourceWizard.selectAndReveal(newResource, workbench
				.getActiveWorkbenchWindow());
	}

	public static void setHelp(Composite composite, String contextId) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, contextId);
	}

	public static void openUrl(String url) {
		try {
			openUrl(new URL(url), true);
		} catch (Exception e) {
			CommonPlugin.log(e);
		}
	}

	public static void openUrl(URL url, boolean maybeInternal) {
		openUrl(url, maybeInternal, "");
	}

	public static void openUrl(URL url, boolean maybeInternal, String browserId) {
		try {
			IWorkbenchBrowserSupport support = PlatformUI.getWorkbench()
					.getBrowserSupport();
			IWebBrowser browser = null;
			if (maybeInternal && support.isInternalWebBrowserAvailable()) {
				int flag = IWorkbenchBrowserSupport.AS_EDITOR
						| IWorkbenchBrowserSupport.LOCATION_BAR
						| IWorkbenchBrowserSupport.NAVIGATION_BAR
						| IWorkbenchBrowserSupport.STATUS
						| IWorkbenchBrowserSupport.PERSISTENT;
				browser = support.createBrowser(flag, StringUtil
						.isEmpty(browserId) ? "" : browserId, null, null);
			} else {
				browser = support.getExternalBrowser();
			}
			if (browser != null) {
				browser.openURL(url);
			}
		} catch (Exception e) {
			CommonPlugin.log(e);
		}
	}
}
