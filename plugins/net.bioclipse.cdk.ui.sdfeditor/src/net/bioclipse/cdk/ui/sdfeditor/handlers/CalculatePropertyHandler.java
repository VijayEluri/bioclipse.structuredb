/*******************************************************************************
 * Copyright (c) 2009  Arvid Berg <goglepox@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.cdk.ui.sdfeditor.handlers;

import java.util.List;

import net.bioclipse.cdk.ui.sdfeditor.Activator;
import net.bioclipse.cdk.ui.sdfeditor.business.IPropertyCalculator;
import net.bioclipse.cdk.ui.sdfeditor.editor.MoleculeTableContentProvider;
import net.bioclipse.cdk.ui.sdfeditor.editor.MoleculesEditor;
import net.bioclipse.cdk.ui.sdfeditor.editor.MultiPageMoleculesEditorPart;
import net.bioclipse.cdk.ui.sdfeditor.editor.SDFIndexEditorModel;
import net.bioclipse.jobs.BioclipseUIJob;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author arvid
 *
 */
public class CalculatePropertyHandler extends AbstractHandler implements IHandler {

    Logger logger = Logger.getLogger( CalculatePropertyHandler.class );

    private static final String PARAMETER_ID = "net.bioclipse.cdk.ui.sdfeditor.calculatorId";

    public Object execute( ExecutionEvent event ) throws ExecutionException {
        IEditorPart editorPart = HandlerUtil.getActiveEditor( event );
        // FIXME there can be other models besides SDFIndexEditorModel
        final MoleculesEditor editor = (MoleculesEditor) ((MultiPageMoleculesEditorPart)
                            editorPart).getAdapter( MoleculesEditor.class );
        if(editor== null) {
            logger.warn( "Could not find a MoleculesEditor" );
            return null;
        }
        SDFIndexEditorModel model = (SDFIndexEditorModel) editor.getModel();
        String calc = event.getParameter( PARAMETER_ID );
        IExtensionRegistry reg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions;
        IExtension ext = reg.getExtension( calc );
        extensions = ext.getConfigurationElements();
        for(IConfigurationElement element:extensions) {
            element.getNamespaceIdentifier();
            element.getName();
            element.getContributor();
            element.getValue();
            try {
                final IPropertyCalculator<?> calculator = (IPropertyCalculator<?>)
                                   element.createExecutableExtension( "class" );
                Activator.getDefault().getMoleculeTableManager()
                    .calculateProperty( model, calculator,
                         new BioclipseUIJob<Void>() {
                        @Override
                        public void runInUI() {

                           String name = calculator.getPropertyName();
                           MoleculeTableContentProvider contentProvider =
                                           editor.getContentProvider();
                           List<Object> props= contentProvider.getProperties();
                           props.add( 0, name );
                           contentProvider.setVisibleProperties( props );
                           contentProvider.updateHeaders();
                        }
                    });
                break;
            } catch ( CoreException e ) {
                logger.debug( "Failed to craete IPropertyCalculator", e );
            }
        }

        return null;
    }
}