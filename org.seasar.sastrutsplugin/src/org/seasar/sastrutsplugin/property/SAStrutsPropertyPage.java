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

package org.seasar.sastrutsplugin.property;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.seasar.eclipse.common.wiget.ResourceTreeSelectionDialog;
import org.seasar.sastrutsplugin.SAStrutsConstans;
import org.seasar.sastrutsplugin.nls.Messages;
import org.seasar.sastrutsplugin.util.PreferencesUtil;

public class SAStrutsPropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {

	private Text webRoot;

	public SAStrutsPropertyPage() {
		super();
	}

	@Override
	protected Control createContents(Composite parent) {
		IPreferenceStore store = PreferencesUtil
				.getPreferenceStoreOfProject(getProject());
		setPreferenceStore(store);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		Label label = new Label(composite, SWT.NULL);
		label.setText(Messages.PROPERTY_PAGE_WEBAPP_ROOT);
		webRoot = new Text(composite, SWT.BORDER);
		webRoot.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		webRoot
				.setText(store
						.getString(SAStrutsConstans.PREF_WEBCONTENTS_ROOT));

		Button button = new Button(composite, SWT.BUTTON1);
		button.setText(Messages.PROPERTY_PAGE_BROWSE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooseFolder(webRoot);
			}
		});

		return composite;
	}

	private void chooseFolder(Text txt) {
		ResourceTreeSelectionDialog dialog = new ResourceTreeSelectionDialog(
				getShell(), getProject(), IResource.FOLDER);
		dialog.setInitialSelection(getProject());
		dialog.setAllowMultiple(false);
		if (dialog.open() == Dialog.OK) {
			Object[] results = dialog.getResult();
			if (results != null && 0 < results.length) {
				IResource r = (IResource) results[0];
				txt.setText(getFolderName(r));
			}
		}
	}

	private String getFolderName(Object result) {
		if (result instanceof IFolder) {
			IFolder folder = (IFolder) result;
			String folderName = folder.getLocation().toString();
			String projectPath = getProject().getLocation().toString();
			if (folderName.length() <= projectPath.length()) {
				return folderName;
			} else {
				return folderName.substring(projectPath.length());
			}
		}
		return "/";
	}

	private IProject getProject() {
		return (IProject) getElement();
	}

	public boolean performOk() {
		getPreferenceStore().setValue(SAStrutsConstans.PREF_WEBCONTENTS_ROOT,
				webRoot.getText());
		return true;
	}

	public void performDefaults() {
		webRoot.setText(SAStrutsConstans.PREF_DEFAULT_WEBCONTENTS_ROOT);
	}

}
