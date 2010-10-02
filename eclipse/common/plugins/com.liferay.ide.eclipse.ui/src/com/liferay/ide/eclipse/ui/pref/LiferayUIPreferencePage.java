/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.ui.pref;

import com.liferay.ide.eclipse.ui.LiferayUIPlugin;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.server.ui.ServerUIUtil;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class LiferayUIPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public LiferayUIPreferencePage() {
		setImageDescriptor(LiferayUIPlugin.getDefault().getImageDescriptor(LiferayUIPlugin.IMG_LIFERAY_ICON_SMALL));
	}

	public LiferayUIPreferencePage(String title) {
		super(title);
	}

	public LiferayUIPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
	}

	@Override
	protected Control createContents(final Composite parent) {
		Composite pageParent = new Composite(parent, SWT.NONE);
		pageParent.setLayout(GridLayoutFactory.swtDefaults().create());

		Group group = new Group(pageParent, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		group.setText("Liferay shortcuts");
		group.setLayout(new GridLayout(1, false));

		Hyperlink link = new Hyperlink(group, SWT.NULL);
		link.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		link.setUnderlined(true);
		link.setText("Configure installed Liferay SDKs.");
		link.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {
				final IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();

				parent.getDisplay().asyncExec(new Runnable() {

					public void run() {
						container.openPage("com.liferay.ide.eclipse.sdk.preferences.installedSDKs", null);
					}

				});
			}

		});

		Hyperlink link2 = new Hyperlink(group, SWT.NULL);
		link2.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		link2.setUnderlined(true);
		link2.setText("Create a new Liferay runtime environment.");
		link2.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {
				ServerUIUtil.showNewRuntimeWizard(
					LiferayUIPreferencePage.this.getShell(), IModuleConstants.JST_WEB_MODULE, "2.5", "com.liferay.");
			}

		});

		Hyperlink link3 = new Hyperlink(group, SWT.NULL);
		link3.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		link3.setUnderlined(true);
		link3.setText("Create a new Liferay server.");
		link3.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {
				ServerUIUtil.showNewServerWizard(
					LiferayUIPreferencePage.this.getShell(), IModuleConstants.JST_WEB_MODULE, "2.5", "com.liferay.");
			}

		});

		return pageParent;
	}

}
