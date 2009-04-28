/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>.
 *
 * Contributors:
 *      Arvid Berg
 *
 *
 ******************************************************************************/
package net.bioclipse.cdk.ui.sdfeditor.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.cdk.business.Activator;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.business.SDFileIndex;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.domain.MoleculesIndexEditorInput;
import net.bioclipse.cdk.domain.SDFElement;
import net.bioclipse.cdk.ui.views.IMoleculesEditorModel;
import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.ui.jobs.BioclipseUIJob;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.nebula.widgets.compositetable.CompositeTable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorInputTransfer;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.EditorInputTransfer.EditorInputData;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;

public class MoleculesEditor extends EditorPart implements
        //ISelectionProvider,
        ISelectionListener {

    public final static int STRUCTURE_COLUMN_WIDTH = 200;

    Logger logger = Logger.getLogger( MoleculesEditor.class );

    public List<String> propertyHeaders = new ArrayList<String>();

    private MoleculeTableViewer molTableViewer;

    public MoleculesEditor() {
    }

    public MoleculeViewerContentProvider getContentProvider() {
        IContentProvider provider = molTableViewer.getContentProvider();
        if(provider instanceof MoleculeViewerContentProvider)
            return (MoleculeViewerContentProvider) provider;
        return null;
    }

    public MoleculeTableViewer getMolTableViewer() {
        return molTableViewer;
    }

    @Override
    public void doSave( IProgressMonitor monitor ) {
        // TODO Use a SDF-iterator and a appending writer to create a SDfile from the file and index.
    }

    @Override
    public void doSaveAs() {
        // TODO see doSave(...)
    }

    @Override
    public void init( IEditorSite site, IEditorInput input )
                                                      throws PartInitException {

        super.setSite( site );
        super.setInput( input );
        setPartName(input.getName() );
        // TODO listen to selections check and focus on selected element from
        // common navigator, load it and get columns
    }

    @Override
    public boolean isDirty() {
        // TODO Check the index
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void createPartControl( Composite parent ) {

        molTableViewer = new MoleculeTableViewer(parent,SWT.NONE);
//        molTableViewer.setContentProvider( contentProvider =
//                                        new MoleculeViewerContentProvider() );

        //molTableViewer.setInput( getEditorInput() );
        getIndexFromInput( getEditorInput() );


        MenuManager menuMgr = new MenuManager("Molecuels table","net.bioclipse.cdk.ui.sdfeditor.menu");
        menuMgr.add( new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        getSite().registerContextMenu( "net.bioclipse.cdk.ui.sdfeditor.menu",menuMgr, molTableViewer);
        Menu menu = menuMgr.createContextMenu(molTableViewer.getControl());
        molTableViewer.getControl().setMenu(menu);
        logger.debug( "Menu id for SDFEditor " +menuMgr.getId());

        getSite().setSelectionProvider( molTableViewer );
    }

    private void getIndexFromInput(IEditorInput editorInput) {
        ICDKManager cdkManager = Activator.getDefault().getCDKManager();
        SDFileIndex input = null;
        input = (SDFileIndex) editorInput.getAdapter( SDFileIndex.class );
        if(input==null) {
            IFile file = (IFile) editorInput.getAdapter( IFile.class );
            if(file!=null) {
                IContentType fContentType= Platform.getContentTypeManager()
                   .getContentType("net.bioclipse.contenttypes.smi");
                IContentDescription contentDescr;
                try {
                    contentDescr = file.getContentDescription();
                }catch ( CoreException e) {
                    contentDescr = null;
                }
                if(contentDescr !=null && fContentType !=null &&
                     contentDescr.getContentType().isKindOf( fContentType )) {
                    try {
                      cdkManager.loadSMILESFile( file,
                       new BioclipseUIJob<List<ICDKMolecule>>() {

                          @Override
                          public void runInUI() {
                              final List<ICDKMolecule> list = getReturnValue();

                              // FIXME there should be a IMoleculesEditorModel content provider
                              Object input = new IAdaptable() {

                                @SuppressWarnings("unchecked")
                                public Object getAdapter( Class adapter ) {

                                    if(adapter.isAssignableFrom( IMoleculesEditorModel.class ))
                                        return new IMoleculesEditorModel() {
                                        List<ICDKMolecule> molecules;
                                        {
                                            molecules = list;
                                        }
                                        public ICDKMolecule getMoleculeAt( int index ) {

                                            return molecules.get( index );
                                        }

                                        public int getNumberOfMolecules() {

                                            return molecules.size();
                                        }

                                        public void save() {
                                            throw new UnsupportedOperationException();
                                        }
                                    };
                                    return null;
                                }
                              };


                              molTableViewer.setContentProvider(
                                           new MoleculeTableContentProvider() );
                              molTableViewer.setInput( input );
                              molTableViewer.refresh();
                          }

                      } );
                    }catch(IOException e) {
                        LogUtils.debugTrace( logger, e );
                    } catch ( CoreException e ) {
                        LogUtils.debugTrace( logger, e );
                    }

                }else {

                    cdkManager.createSDFIndex( file,
                    new BioclipseUIJob<SDFileIndex>() {

                        @Override
                        public void runInUI() {
                            molTableViewer.setContentProvider(
                                new MoleculeViewerContentProvider() );
                            molTableViewer.setInput( getReturnValue() );
                            molTableViewer.refresh();
                        }

                    });
                }
            }else {
            final BioList<ICDKMolecule> list = (BioList<ICDKMolecule>) editorInput.getAdapter( BioList.class );
            if(list!=null) {

                Object inp = new IAdaptable() {

                    @SuppressWarnings("unchecked")
                    public Object getAdapter( Class adapter ) {

                        if(adapter.isAssignableFrom( IMoleculesEditorModel.class ))
                            return new IMoleculesEditorModel() {
                            List<ICDKMolecule> molecules;
                            {
                                molecules = list;
                            }
                            public ICDKMolecule getMoleculeAt( int index ) {

                                return molecules.get( index );
                            }

                            public int getNumberOfMolecules() {

                                return molecules.size();
                            }

                            public void save() {
                                throw new UnsupportedOperationException();
                            }
                        };
                        return null;
                    }
                  };


                  molTableViewer.setContentProvider(
                               new MoleculeTableContentProvider() );
                  molTableViewer.setInput( inp );
                  molTableViewer.refresh();
            }
        }
        }
//        molTableViewer.setInput( input );
    }

    void reactOnSelection( ISelection selection ) {

        //if ( element instanceof ICDKMolecule )
//            if (((IStructuredSelection)viewer.getSelection()).toList()
//                                            .containsAll( selection.toList() ))
//                return;
//            else
//        if(viewer != null)
//                setSelectedRows(selection);
    }

    @Override
    public void setFocus() {

     molTableViewer.getControl().setFocus();

    }

    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
        logger.debug( "Selection has chaged" + this.getClass().getName() );
        logger.debug( part.toString() + this.getSite().getPart().toString());
        if(part != null && part.equals( this )) return;
            setSelectedRows( selection );
//        if( part != null && part.equals( this )) return;
//        if( selection == null || selection.isEmpty() ) {
//            if(!viewer.getSelection().isEmpty())
//                viewer.setSelection( selection );
//            return;
//        }
//        if(selection instanceof IStructuredSelection)
//            reactOnSelection( (IStructuredSelection) selection );
        //viewer.setSelection( selection );
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter( Class adapter ) {

//        if(IContentOutlinePage.class.equals( adapter )) {
//            if(outlinePage == null) {
//                outlinePage = new MoleculesOutlinePage();
//                outlinePage.setInput(getEditorInput());
//            }
//            return outlinePage;
//        }
        return super.getAdapter( adapter );
    }

    public ISelection getSelection() {
            return molTableViewer.getSelection();
    }

    private ISelection getSelectedRows() {
//        viewer.getSelection();
//        viewer.getTopRow();

        return StructuredSelection.EMPTY;

    }
    private void setSelectedRows(ISelection selection) {
        // mapping between selections and index
        //viewer.setSelection(  );
    }

    public IRenderer2DConfigurator getRenderer2DConfigurator() {
        return molTableViewer.getRenderer2DConfigurator();
    }

    public void setRenderer2DConfigurator(
                             IRenderer2DConfigurator renderer2DConfigurator ) {
        molTableViewer.setRenderer2DConfigurator( renderer2DConfigurator );
    }


    protected void setupDragSource() {
        int operations = DND.DROP_COPY | DND.DROP_MOVE;
        CompositeTable viewer=null;
        DragSource dragSource = new DragSource(viewer,operations);
        Transfer[] transferTypes = new Transfer[]
                                        {
                                          LocalSelectionTransfer.getTransfer()};
        dragSource.setTransfer( transferTypes );

        dragSource.addDragListener(  new DragSourceListener() {


            public void dragStart( DragSourceEvent event ) {
               if(!getSelectedRows().isEmpty()) {
                   LocalSelectionTransfer.getTransfer()
                           .setSelection( getSelectedRows() );
                   event.doit = true;
               } else
                   event.doit = false;
            }
            public void dragSetData( DragSourceEvent event ) {
                ISelection selection = LocalSelectionTransfer
                                            .getTransfer()
                                            .getSelection();

                if ( LocalSelectionTransfer
                                        .getTransfer()
                                        .isSupportedType( event.dataType )) {

                    event.data = selection;


                } else {
                IStructuredSelection selection1 =
                                  (IStructuredSelection) getSelectedRows();
                List<EditorInputData> data = new ArrayList<EditorInputData>();
                for(Object o : selection1.toList()) {
                    MoleculesIndexEditorInput input =
                                  new MoleculesIndexEditorInput((SDFElement)o);
                    data.add( EditorInputTransfer
                              .createEditorInputData(
                                      "net.bioclipse.cdk.ui.editors.jchempaint",
                                      input ));
                }
                event.data = data.toArray( new EditorInputData[0] );
                }

            }

            public void dragFinished( DragSourceEvent event ) {
            }

        });
    }

    public void setUseExtensionGenerators( boolean useGenerators ) {

        molTableViewer.cellPainter.setUseExtensionGenerators( useGenerators );
    }
}
