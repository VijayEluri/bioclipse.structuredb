/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.cdk.ui.editors.sdf;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class StructureTablePage extends FormPage {

	private Table table;
	private TableViewer viewer;
	
	public StructureTablePage(FormEditor editor) {
		super(editor, "bc.structuretable", "Structure table");
	}

	/**
	 * Add content to form
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {

		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Structure table");
//		form.setBackgroundImage(FormArticlePlugin.getDefault().getImage(FormArticlePlugin.IMG_FORM_BG));
		final Composite body = form.getBody();
		FillLayout layout=new FillLayout();
		body.setLayout(layout);
		
		viewer = new TableViewer(body, SWT.NONE);
		table = viewer.getTable();
		toolkit.adapt(table, true, true);

		//Add columns
		TableViewerColumn col=new TableViewerColumn(viewer,SWT.NONE);
		col.getColumn().setWidth(300);
		col.getColumn().setText("Structure");
		TableViewerColumn col2=new TableViewerColumn(viewer,SWT.NONE);
		col2.getColumn().setWidth(200);
		col2.getColumn().setText("Name");
		TableViewerColumn col3=new TableViewerColumn(viewer,SWT.NONE);
		col3.getColumn().setWidth(200);
		col3.getColumn().setText("Property X");

		viewer.setContentProvider(new MoleculeListContentProvider());
		viewer.setLabelProvider(new MoleculeListLabelProvider());
		viewer.setInput(((SDFEditor)getEditor()).getMolList());
		
	}
}
