/*
 *  $RCSfile: ModelPropsAction.java,v $
 *  $Author: egonw $
 *  $Date: 2005/10/22 16:25:38 $
 *  $Revision: 1.5 $
 *
 *  Copyright (C) 1997-2005  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package net.bioclipse.cdk10.jchempaint.action;

import java.awt.event.ActionEvent;

import net.bioclipse.cdk10.jchempaint.ui.editor.MDLMolfileEditor;
import net.bioclipse.cdk10.jchempaint.ui.editor.action.JCPAction;

import org.eclipse.swt.widgets.Display;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;


/**
 * Shows an editor for editing the model properties
 *
 * @cdk.module jchempaint
 *@author     steinbeck
 */
public class ModelPropsAction extends JCPAction
{

	public void run() {
		run(null);
	}
	
	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of the Parameter
	 */
	public void run(ActionEvent e)
	{
		final JChemPaintModel jcpModel = ((MDLMolfileEditor)this.getContributor().getActiveEditorPart()).getJcpModel();
		Display display = this.getContributor().getPage().getActivePart().getSite().getShell().getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
//				JChemPaintModelPropsDialog dialog = new JChemPaintModelPropsDialog(ModelPropsAction.this.getContributor().getActiveEditorPart().getEditorSite().getShell(), 0, jcpModel);
//				dialog.open();
			}			
		});
	}
}

