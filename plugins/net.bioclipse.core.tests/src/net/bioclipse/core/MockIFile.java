/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * Mocking of IFile for testing purposes
 * 
 * @author jonalv
 *
 */
public class MockIFile implements IFile {

    private InputStream inStream;
    private boolean exists=false;

    public MockIFile(InputStream inStream) {
        this.inStream = inStream;
    }
    
    public MockIFile(String path) throws FileNotFoundException {
        this.inStream = new FileInputStream( new File(path) );
        exists=true;
    }
    
    public MockIFile() throws FileNotFoundException {
        exists=false;
    }

    public void appendContents( InputStream source, int updateFlags,
                                IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void appendContents( InputStream source, boolean force,
                                boolean keepHistory, IProgressMonitor monitor )
                                                                               throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void create( InputStream source, boolean force,
                        IProgressMonitor monitor ) throws CoreException {

        inStream=source;
        exists=true;
        
    }

    public void create( InputStream source, int updateFlags,
                        IProgressMonitor monitor ) throws CoreException {

        inStream=source;
        exists=true;
        
    }

    public void createLink( IPath localLocation, int updateFlags,
                            IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void createLink( URI location, int updateFlags,
                            IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void delete( boolean force, boolean keepHistory,
                        IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public String getCharset() throws CoreException {

        // TODO Auto-generated method stub
        return null;
    }

    public String getCharset( boolean checkImplicit ) throws CoreException {

        // TODO Auto-generated method stub
        return null;
    }

    public String getCharsetFor( Reader reader ) throws CoreException {

        // TODO Auto-generated method stub
        return null;
    }

    public IContentDescription getContentDescription() throws CoreException {

        // TODO Auto-generated method stub
        return null;
    }

    public InputStream getContents() throws CoreException {

    	if(!exists)
    		throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 1, "File does not (yet) exist", null));
        return inStream;
    }

    public InputStream getContents( boolean force ) throws CoreException {

        // TODO Auto-generated method stub
        return null;
    }

    public int getEncoding() throws CoreException {

        // TODO Auto-generated method stub
        return 0;
    }

    public IPath getFullPath() {

        // TODO Auto-generated method stub
        return null;
    }

    public IFileState[] getHistory( IProgressMonitor monitor )
                                                              throws CoreException {

        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {

        // TODO Auto-generated method stub
        return null;
    }

    public boolean isReadOnly() {

        // TODO Auto-generated method stub
        return false;
    }

    public void move( IPath destination, boolean force, boolean keepHistory,
                      IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void setCharset( String newCharset ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void setCharset( String newCharset, IProgressMonitor monitor )
                                                                         throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void setContents( InputStream source, int updateFlags,
                             IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void setContents( IFileState source, int updateFlags,
                             IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void setContents( InputStream source, boolean force,
                             boolean keepHistory, IProgressMonitor monitor )
                                                                            throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void setContents( IFileState source, boolean force,
                             boolean keepHistory, IProgressMonitor monitor )
                                                                            throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void accept( IResourceVisitor visitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void accept( IResourceProxyVisitor visitor, int memberFlags )
                                                                        throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void accept( IResourceVisitor visitor, int depth,
                        boolean includePhantoms ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void accept( IResourceVisitor visitor, int depth, int memberFlags )
                                                                              throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void clearHistory( IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void copy( IPath destination, boolean force, IProgressMonitor monitor )
                                                                                  throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void copy( IPath destination, int updateFlags,
                      IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void copy( IProjectDescription description, boolean force,
                      IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void copy( IProjectDescription description, int updateFlags,
                      IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public IMarker createMarker( String type ) throws CoreException {

        // TODO Auto-generated method stub
        return null;
    }

    public IResourceProxy createProxy() {

        // TODO Auto-generated method stub
        return null;
    }

    public void delete( boolean force, IProgressMonitor monitor )
                                                                 throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void delete( int updateFlags, IProgressMonitor monitor )
                                                                   throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void deleteMarkers( String type, boolean includeSubtypes, int depth )
                                                                                throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public boolean exists() {

        return exists;
    }

    public IMarker findMarker( long id ) throws CoreException {

        // TODO Auto-generated method stub
        return null;
    }

    public IMarker[] findMarkers( String type, boolean includeSubtypes,
                                  int depth ) throws CoreException {

        // TODO Auto-generated method stub
        return null;
    }

    public int findMaxProblemSeverity( String type, boolean includeSubtypes,
                                       int depth ) throws CoreException {

        // TODO Auto-generated method stub
        return 0;
    }

    public String getFileExtension() {

        // TODO Auto-generated method stub
        return null;
    }

    public long getLocalTimeStamp() {

        // TODO Auto-generated method stub
        return 0;
    }

    public IPath getLocation() {

        // TODO Auto-generated method stub
        return null;
    }

    public URI getLocationURI() {

        // TODO Auto-generated method stub
        return null;
    }

    public IMarker getMarker( long id ) {

        // TODO Auto-generated method stub
        return null;
    }

    public long getModificationStamp() {

        // TODO Auto-generated method stub
        return 0;
    }

    public IContainer getParent() {

        // TODO Auto-generated method stub
        return null;
    }

    public Map getPersistentProperties() throws CoreException {

        // TODO Auto-generated method stub
        return null;
    }

    public String getPersistentProperty( QualifiedName key )
                                                            throws CoreException {

        // TODO Auto-generated method stub
        return null;
    }

    public IProject getProject() {

        // TODO Auto-generated method stub
        return null;
    }

    public IPath getProjectRelativePath() {

        // TODO Auto-generated method stub
        return null;
    }

    public IPath getRawLocation() {

        // TODO Auto-generated method stub
        return null;
    }

    public URI getRawLocationURI() {

        // TODO Auto-generated method stub
        return null;
    }

    public ResourceAttributes getResourceAttributes() {

        // TODO Auto-generated method stub
        return null;
    }

    public Map getSessionProperties() throws CoreException {

        // TODO Auto-generated method stub
        return null;
    }

    public Object getSessionProperty( QualifiedName key ) throws CoreException {

        // TODO Auto-generated method stub
        return null;
    }

    public int getType() {

        // TODO Auto-generated method stub
        return 0;
    }

    public IWorkspace getWorkspace() {

        // TODO Auto-generated method stub
        return null;
    }

    public boolean isAccessible() {

        // TODO Auto-generated method stub
        return false;
    }

    public boolean isDerived() {

        // TODO Auto-generated method stub
        return false;
    }

    public boolean isDerived( int options ) {

        // TODO Auto-generated method stub
        return false;
    }

    public boolean isHidden() {

        // TODO Auto-generated method stub
        return false;
    }

    public boolean isLinked() {

        // TODO Auto-generated method stub
        return false;
    }

    public boolean isLinked( int options ) {

        // TODO Auto-generated method stub
        return false;
    }

    public boolean isLocal( int depth ) {

        // TODO Auto-generated method stub
        return false;
    }

    public boolean isPhantom() {

        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSynchronized( int depth ) {

        // TODO Auto-generated method stub
        return false;
    }

    public boolean isTeamPrivateMember() {

        // TODO Auto-generated method stub
        return false;
    }

    public void move( IPath destination, boolean force, IProgressMonitor monitor )
                                                                                  throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void move( IPath destination, int updateFlags,
                      IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void move( IProjectDescription description, int updateFlags,
                      IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void move( IProjectDescription description, boolean force,
                      boolean keepHistory, IProgressMonitor monitor )
                                                                     throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void refreshLocal( int depth, IProgressMonitor monitor )
                                                                   throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void revertModificationStamp( long value ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void setDerived( boolean isDerived ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void setHidden( boolean isHidden ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void setLocal( boolean flag, int depth, IProgressMonitor monitor )
                                                                             throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public long setLocalTimeStamp( long value ) throws CoreException {

        // TODO Auto-generated method stub
        return 0;
    }

    public void setPersistentProperty( QualifiedName key, String value )
                                                                        throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void setReadOnly( boolean readOnly ) {

        // TODO Auto-generated method stub
        
    }

    public void setResourceAttributes( ResourceAttributes attributes )
                                                                      throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void setSessionProperty( QualifiedName key, Object value )
                                                                     throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void setTeamPrivateMember( boolean isTeamPrivate )
                                                             throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public void touch( IProgressMonitor monitor ) throws CoreException {

        // TODO Auto-generated method stub
        
    }

    public Object getAdapter( Class adapter ) {

        // TODO Auto-generated method stub
        return null;
    }

    public boolean contains( ISchedulingRule rule ) {

        // TODO Auto-generated method stub
        return false;
    }

    public boolean isConflicting( ISchedulingRule rule ) {

        // TODO Auto-generated method stub
        return false;
    }

}
