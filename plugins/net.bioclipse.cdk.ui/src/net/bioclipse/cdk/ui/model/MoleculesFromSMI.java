package net.bioclipse.cdk.ui.model;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;

import net.bioclipse.cdk.business.Activator;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.ui.views.IMoleculesEditorModel;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.util.LogUtils;


public class MoleculesFromSMI implements IDeferredWorkbenchAdapter,
                                                        IMoleculesEditorModel {
    Logger logger = Logger.getLogger( MoleculesFromSMI.class );
    IFile file;
    List<ICDKMolecule> molecules;
    
    public MoleculesFromSMI(IFile file) {
       this.file = file;
       molecules = Collections.synchronizedList( new LinkedList<ICDKMolecule>());
    }

    public Object getMoleculeAt( int index ) {
        
        if( molecules.size()> index) {
            return molecules.get( index );
        }
        return null;
    }

    public int getNumberOfMolecules() {

        return molecules.size();
    }

    public void save() {

        throw new UnsupportedOperationException("Can't save SMILES yet.");

    }

    public void fetchDeferredChildren( Object object,
                                       IElementCollector collector,
                                       IProgressMonitor monitor ) {
      
       ICDKManager manager = Activator.getDefault().getCDKManager();
       try {
        List<ICDKMolecule> mols = manager.loadMolecules( file, monitor );
        collector.add( mols.toArray(), monitor );
        molecules.addAll( mols );
    } catch ( IOException e ) {
        // TODO Auto-generated catch block
       LogUtils.debugTrace( logger, e );
    } catch ( BioclipseException e ) {
        // TODO Auto-generated catch block
        LogUtils.debugTrace( logger, e );
    } catch ( CoreException e ) {
        // TODO Auto-generated catch block
        LogUtils.debugTrace( logger, e );
    } finally {
        monitor.done();
    }
        
    }

    public ISchedulingRule getRule( Object object ) {

        // TODO Auto-generated method stub
        return null;
    }

    public boolean isContainer() {
        return true;
    }

    public Object[] getChildren( Object o ) {

        return molecules.toArray();
    }

    public ImageDescriptor getImageDescriptor( Object object ) {

        // TODO Auto-generated method stub
        return null;
    }

    public String getLabel( Object o ) {

        // TODO Auto-generated method stub
        return "SMILES Container";
    }

    public Object getParent( Object o ) {

       return file;
    }

}
