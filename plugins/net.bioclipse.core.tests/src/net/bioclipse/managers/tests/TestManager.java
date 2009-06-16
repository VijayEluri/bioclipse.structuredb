package net.bioclipse.managers.tests;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.IReturner;
import net.bioclipse.managers.business.IBioclipseManager;

import static org.junit.Assert.*;

/**
 * @author jonalv
 */
public class TestManager implements IBioclipseManager {

    public static volatile Boolean methodRun = false;
    public static Object lock = new Object(); 
    
    public String getManagerName() {
        return "test";
    }

    public String getGreeting(String name) {
        done();
        return "OH HAI " + name;
    }
    
    public void dontRunAsJob(IFile file) {
        done();
    }
    
    public void runAsJob(IFile file, IProgressMonitor monitor) {
        monitor.beginTask( "bla", 2 );
        monitor.worked( 1 );
        monitor.worked( 1 );
        monitor.done();
        done();
    }

    public IFile returnsAFile(IFile file) {
        done();
        return file;
    }
    
    public String getPath(IFile file) {
        done();
        return file.getFullPath().toPortableString();
    }
    
    public void getBioObjects( IFile file, 
                               IReturner returner, 
                               IProgressMonitor monitor ) {

        assertNotNull( file );
        assertNotNull( monitor );
        returner.partialReturn( new BioObject(){} );
        returner.partialReturn( new BioObject(){} );
        done();
    }
    
    public void getBioObject( IFile file, 
                              IReturner returner, 
                              IProgressMonitor monitor ) {

        assertNotNull( file );
        assertNotNull( monitor );
        returner.completeReturn( new BioObject(){} );
        done();
    }
    
    public void guiAction() {
        assertNotNull( Display.getCurrent() );
        done();
    }

    public void done() {
        methodRun = true;
        synchronized ( lock ) {
            lock.notifyAll();
        }
    }

    public IBioObject createBioObject(IFile file, IProgressMonitor monitor) {
        assertNotNull( file );
        assertNotNull( monitor );
        IBioObject o =  new BioObject() {};
        o.setResource( file );
        done();
        return o;
    }
    
    public void voidJobMethod(IFile file, IProgressMonitor monitor) {
        assertNotNull( file );
        assertNotNull( monitor );
        done();
    }
}
