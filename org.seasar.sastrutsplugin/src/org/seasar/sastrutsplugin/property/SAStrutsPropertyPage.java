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

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.seasar.eclipse.common.wiget.ResourceTreeSelectionDialog;
import org.seasar.sastrutsplugin.SAStrutsConstants;
import org.seasar.sastrutsplugin.nls.Messages;
import org.seasar.sastrutsplugin.util.PreferencesUtil;

public class SAStrutsPropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {

	private Pattern httpUrl = Pattern
			.compile("https?://[-_.!~*'()a-zA-Z0-9;/?:\\@&=+\\$,%#]+");

	private Text webRoot;

	private Text mainJavaPath;

	private Text conventionDiconPath;

	private Text webServer;

	private Text context;

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

		Label webRootLabel = new Label(composite, SWT.NULL);
		webRootLabel.setText(Messages.PROPERTY_PAGE_WEBAPP_ROOT);
		webRoot = new Text(composite, SWT.BORDER);
		webRoot.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		webRoot.setText(store
				.getString(SAStrutsConstants.PREF_WEBCONTENTS_ROOT));

		Button webRootBotton = new Button(composite, SWT.BUTTON1);
		webRootBotton.setText(Messages.PROPERTY_PAGE_BROWSE);
		webRootBotton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooseFolder(webRoot);
			}
		});

		Label mainJavaPathLabel = new Label(composite, SWT.NULL);
		mainJavaPathLabel.setText(Messages.PROPERTY_PAGE_MAIN_JAVA_PATH);
		mainJavaPath = new Text(composite, SWT.BORDER);
		mainJavaPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mainJavaPath.setText(store
				.getString(SAStrutsConstants.PREF_MAIN_JAVA_PATH));

		Button mainJavaPathbutton = new Button(composite, SWT.BUTTON1);
		mainJavaPathbutton.setText(Messages.PROPERTY_PAGE_BROWSE);
		mainJavaPathbutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooseFolder(mainJavaPath);
			}
		});

		Label conventionDiconPathLabel = new Label(composite, SWT.NULL);
		conventionDiconPathLabel
				.setText(Messages.PROPERTY_PAGE_CONVENTION_DICON_PATH);
		conventionDiconPath = new Text(composite, SWT.BORDER);
		conventionDiconPath
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		conventionDiconPath.setText(store
				.getString(SAStrutsConstants.PREF_CONVENTION_DICON_PATH));

		Button conventionDiconPathButton = new Button(composite, SWT.BUTTON1);
		conventionDiconPathButton.setText(Messages.PROPERTY_PAGE_BROWSE);
		conventionDiconPathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooseFile(conventionDiconPath);
			}
		});

		Label webServerLabel = new Label(composite, SWT.NONE);
		webServerLabel.setText(Messages.PROPERTY_PAGE_WEB_SERVER);
		webServer = new Text(composite, SWT.SINGLE | SWT.BORDER);
		webServer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		webServer.setText(store.getString(SAStrutsConstants.PREF_WEBSERVER));
		this.webServer.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String port = webServer.getText();
				boolean is = httpUrl.matcher(port).matches();
				if (is) {
					setErrorMessage(null);
				} else {
					setErrorMessage(NLS.bind(
							Messages.PROPERTY_PAGE_ERROR_MESSAGE_WEB_SERVER,
							"WebServer"));
				}
				setValid(is);
			}
		});

		new Label(composite, SWT.NULL);
		Label contextLabel = new Label(composite, SWT.NONE);
		contextLabel.setText(Messages.PROPERTY_PAGE_CONTEXT);
		context = new Text(composite, SWT.SINGLE | SWT.BORDER);
		context.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		context.setText(store.getString(SAStrutsConstants.PREF_CONTEXT));

		return composite;
	}

	private void chooseFile(Text txt) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setInput(getProject());
		dialog.setAllowMultiple(false);
		if (dialog.open() == Dialog.OK) {
			Object[] results = dialog.getResult();
			if (results != null && results.length == 1) {
				IResource r = (IResource) results[0];
				txt.setText(getFileName(r));
			}
		}
	}

	private String getFileName(Object result) {
		if (result instanceof IFile) {
			IFile file = (IFile) result;
			String fileName = file.getLocation().toString();
			String projectPath = getProject().getLocation().toString();
			if (fileName.length() <= projectPath.length()) {
				return fileName;
			} else {
				return fileName.substring(projectPath.length());
			}
		}
		return "/";
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
		getPreferenceStore().setValue(SAStrutsConstants.PREF_WEBCONTENTS_ROOT,
				webRoot.getText());
		getPreferenceStore().setValue(SAStrutsConstants.PREF_MAIN_JAVA_PATH,
				mainJavaPath.getText());
		getPreferenceStore().setValue(
				SAStrutsConstants.PREF_CONVENTION_DICON_PATH,
				conventionDiconPath.getText());
		getPreferenceStore().setValue(SAStrutsConstants.PREF_WEBSERVER,
				webServer.getText());
		getPreferenceStore().setValue(SAStrutsConstants.PREF_CONTEXT,
				context.getText());
		return true;
	}

	public void performDefaults() {
		webRoot.setText(SAStrutsConstants.PREF_DEFAULT_WEBCONTENTS_ROOT);
		mainJavaPath.setText(SAStrutsConstants.PREF_DEFAULT_MAIN_JAVA_PATH);
		conventionDiconPath
				.setText(SAStrutsConstants.PREF_DEFAULT_CONVENTION_DICON_PATH);
		webServer.setText(SAStrutsConstants.PREF_DEFAULT_WEBSERVER);
		context.setText("");
	}

}
