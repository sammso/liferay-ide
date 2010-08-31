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

package com.liferay.ide.eclipse.ui.snippets.wizard;

import com.liferay.ide.eclipse.ui.util.SWTUtil;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.SuperInterfaceSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.jsf.common.util.JDTBeanIntrospector;
import org.eclipse.jst.jsf.common.util.JDTBeanProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class LiferayUISearchContainerWizardPage extends NewTypeWizardPage {

	protected class TypeFieldAdapter implements IStringButtonAdapter, IDialogFieldListener, IListAdapter {

		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			typeChangeControlPressed(field);
		}

		public void customButtonPressed(ListDialogField field, int index) {
			// doButtonPressed(index);
		}

		public void dialogFieldChanged(DialogField field) {

		}

		public void doubleClicked(ListDialogField field) {

		}

		public void selectionChanged(ListDialogField field) {

		}

	}

	protected static final int IDX_DESELECT = 1;

	protected static final int IDX_SELECT = 0;

	protected IEditorPart editorPart;

	protected String lastVarName = "";

	protected StringButtonDialogField modelClassDialogField;

	protected CheckedListDialogField propertyListField;

	protected Text varNameText;

	public LiferayUISearchContainerWizardPage(String pageName, IEditorPart editor) {
		super(true, pageName);
		setTitle("Liferay UI Search Container");
		setDescription("Insert a Liferay UI Search Container JSP tag.");

		editorPart = editor;

		TypeFieldAdapter adapter = new TypeFieldAdapter();

		modelClassDialogField = new StringButtonDialogField(adapter);
		modelClassDialogField.setLabelText("Model class:");
		modelClassDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_superclass_button);

		String[] buttonLabels = new String[] {
			"Select All", "Deselect All"
		};

		propertyListField = new CheckedListDialogField(adapter, buttonLabels, new LabelProvider());
		propertyListField.setDialogFieldListener(adapter);
		propertyListField.setLabelText("Property columns:");
		propertyListField.setCheckAllButtonIndex(IDX_SELECT);
		propertyListField.setUncheckAllButtonIndex(IDX_DESELECT);
	}

	public void createControl(Composite parent) {
		Composite topComposite = SWTUtil.createTopComposite(parent, 3);

		modelClassDialogField.doFillIntoGrid(topComposite, 3);
		// Text modelClassText = modelClassDialogField.getTextControl(null);
		//
		// JavaTypeCompletionProcessor classCompletionProcessor = new JavaTypeCompletionProcessor(false, false, true);
		// classCompletionProcessor.setCompletionContextRequestor(new CompletionContextRequestor() {
		//
		// @Override
		// public StubTypeContext getStubTypeContext() {
		// return getClassStubTypeContext();
		// }
		// });
		//
		// ControlContentAssistHelper.createTextContentAssistant(modelClassText, classCompletionProcessor);
		// TextFieldNavigationHandler.install(modelClassText);

		propertyListField.doFillIntoGrid(topComposite, 3);
		LayoutUtil.setHorizontalSpan(propertyListField.getLabelControl(null), 1);
		LayoutUtil.setWidthHint(propertyListField.getLabelControl(null), convertWidthInCharsToPixels(40));
		LayoutUtil.setHorizontalGrabbing(propertyListField.getListControl(null));

		propertyListField.getTableViewer().setComparator(new ViewerComparator());

		Label varNameLabel = new Label(topComposite, SWT.LEFT);
		varNameLabel.setText("Variable name:");
		varNameLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		varNameText = new Text(topComposite, SWT.SINGLE | SWT.BORDER);
		varNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		varNameText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				lastVarName = varNameText.getText();
			}
		});

		setControl(topComposite);
	}

	public IJavaProject getJavaProject() {
		IJavaProject javaProject = null;

		if (editorPart != null) {
			IEditorInput editorInput = editorPart.getEditorInput();
			if (editorInput instanceof IFileEditorInput) {
				IProject project = ((IFileEditorInput) editorInput).getFile().getProject();
				return JavaCore.create(project);
			}
		}

		return javaProject;
	}

	public String getModel() {
		try {
			IType type = getJavaProject().findType(getModelClass());
			return type.getElementName();
		}
		catch (Exception e) {

		}
		return "";
	}

	public String getModelClass() {
		return modelClassDialogField.getText();
	}

	public String[] getPropertyColumns() {
		return (String[]) propertyListField.getCheckedElements().toArray(new String[0]);
	}

	// protected StubTypeContext getClassStubTypeContext() {
	// if (fClassStubTypeContext == null) {
	// fClassStubTypeContext = TypeContextChecker.createSuperClassStubTypeContext(getTypeName(), null, null);
	// }
	// return fClassStubTypeContext;
	// }

	public String getTypeName() {
		return modelClassDialogField.getText();
	}

	public String getVarName() {
		return lastVarName;
	}

	protected IType chooseClass() {
		IJavaProject project = getJavaProject();
		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] {
			project
		};
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog =
			new FilteredTypesSelectionDialog(
				getShell(), false, getWizard().getContainer(), scope, IJavaSearchConstants.CLASS_AND_INTERFACE);
		dialog.setTitle("Model class selection");
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_SuperClassDialog_message);
		dialog.setInitialPattern(getSuperClass());

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	protected void handleBrowseButtonPressed() {

	}

	protected void typeChangeControlPressed(DialogField field) {
		IType type = chooseClass();
		if (type != null) {
			modelClassDialogField.setText(SuperInterfaceSelectionDialog.getNameWithTypeParameters(type));

			updatePropertyList(type);
		}
	}

	protected void updatePropertyList(IType type) {
		JDTBeanIntrospector beanIntrospector = new JDTBeanIntrospector(type);
		Map<String, JDTBeanProperty> properties = beanIntrospector.getProperties();

		Object[] props = properties.keySet().toArray();
		propertyListField.setElements(Arrays.asList(props));

		varNameText.setText("a" + getModel());
	}

}