package org.seasar.sastrutsplugin.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.seasar.eclipse.common.util.WorkbenchUtil;

public class ViewSAStrutsHelpAction implements IWorkbenchWindowActionDelegate {
	
	public static final String URL = "http://sastruts.seasar.org/index.html";

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		WorkbenchUtil.openUrl(URL);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
