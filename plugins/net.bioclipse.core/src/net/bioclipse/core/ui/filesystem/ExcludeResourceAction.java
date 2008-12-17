/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.core.ui.filesystem;
import java.net.URI;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.*;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
public class ExcludeResourceAction implements IObjectActionDelegate {
        private ISelection selection;
        private IWorkbenchPart targetPart;
        /**
         * Constructor for Action1.
         */
        public ExcludeResourceAction() {
                super();
        }
        private void exclude(IResource resource) {
                try {
                        URI nullURI = new URI(EFS.SCHEME_NULL, null, "/", null, null);
                        if (resource.getType() == IResource.FILE) {
                                IFile link = (IFile) resource;
                                link.createLink(nullURI, IResource.REPLACE | IResource.ALLOW_MISSING_LOCAL, null);
                        } else if (resource.getType() == IResource.FOLDER){
                                IFolder link = (IFolder) resource;
                                link.createLink(nullURI, IResource.REPLACE | IResource.ALLOW_MISSING_LOCAL, null);
                        } else {
                            // not a folder or file, should it still be excluded?
                        }
                } catch (Exception e) {
                        MessageDialog.openError(getShell(), "Error", "Error excluding resource");
                        e.printStackTrace();
                }
        }
        private Shell getShell() {
                return targetPart.getSite().getShell();
        }
        /**
         * @see IActionDelegate#run(IAction)
         */
        public void run(IAction action) {
                if (!(selection instanceof IStructuredSelection))
                        return;
                Object element = ((IStructuredSelection) selection).getFirstElement();
                if (!(element instanceof IResource))
                        return;
                IResource resource = (IResource) element;
                if (resource.isLinked())
                        return;
                exclude(resource);
        }
        /**
         * @see IActionDelegate#selectionChanged(IAction, ISelection)
         */
        public void selectionChanged(IAction action, ISelection selection) {
                this.selection = selection;
        }
        /**
         * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
         */
        public void setActivePart(IAction action, IWorkbenchPart targetPart) {
                this.targetPart = targetPart;
        }
}
