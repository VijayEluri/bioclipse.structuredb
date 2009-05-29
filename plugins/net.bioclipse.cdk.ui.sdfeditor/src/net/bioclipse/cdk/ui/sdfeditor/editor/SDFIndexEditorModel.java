/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>.
 *
 * Contributors:
 *     Arvid Berg <goglepox@users.sourceforge.net>
 *
 ******************************************************************************/
package net.bioclipse.cdk.ui.sdfeditor.editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.bioclipse.cdk.domain.CDKMolecule;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.ui.sdfeditor.business.SDFileIndex;
import net.bioclipse.cdk.ui.views.IMoleculesEditorModel;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;


/**
 * @author arvid
 *
 */
public class SDFIndexEditorModel implements IMoleculesEditorModel, Iterable<ICDKMolecule> {

    Logger logger = Logger.getLogger( SDFIndexEditorModel.class );

    SDFileIndex input = SDFileIndex.emptyIndex();

    protected ISimpleChemObjectReader chemReader;
    protected IChemObjectBuilder builder;

    int lastIndex = -1;
    ICDKMolecule lastRead = null;

//    List<Object> visibleProperties= new ArrayList<Object>(10);
    Set<Object> availableProperties= new HashSet<Object>();

    Map<Integer, ICDKMolecule> edited = new HashMap<Integer, ICDKMolecule>();

    private Map<Integer, Map<String,Object>> molProps;

    private Map<String,Class<?>> propertyList;


    public SDFIndexEditorModel() {
        molProps = new HashMap<Integer, Map<String,Object>>();
        propertyList = new HashMap<String, Class<?>>();
        chemReader = new MDLV2000Reader();
        builder = DefaultChemObjectBuilder.getInstance();
    }

    public SDFIndexEditorModel(SDFileIndex input) {
        this();
        this.input = input;
    }

    public IResource getResource() {
        return input.file();
    }
    /* (non-Javadoc)
     * @see net.bioclipse.cdk.ui.views.IMoleculesEditorModel#getMoleculeAt(int)
     */
    public synchronized ICDKMolecule getMoleculeAt( int index ) {

        ICDKMolecule mol = edited.get( index );
        if(mol == null) {
            if(index == lastIndex) {
                mol = lastRead;
            }
            else {
                try {
                    StringReader reader = new StringReader(getRecord( index ));
                    chemReader.setReader( reader );
                    IChemObject chemObj = processContent();
                    mol = new CDKMolecule((IAtomContainer)chemObj);
                    setLastRead( index, mol );
                    readProperties( mol );
                } catch ( CoreException e ) {
                    logger.warn( "Failed to read record "+index);
                    LogUtils.debugTrace( logger, e );
                    setLastRead( index, null );
                    return null;
                } catch ( IOException e ) {
                    logger.warn( "Failed to read record "+index);
                    LogUtils.debugTrace( logger, e );
                    setLastRead( index, null );
                    return null;
                } catch ( CDKException e ) {
                    logger.warn( "Failed to read record "+index);
                    LogUtils.debugTrace( logger, e );
                    setLastRead( index, null );
                    return null;
                }
            }
        }
        return mol;
    }

    /* (non-Javadoc)
     * @see net.bioclipse.cdk.ui.views.IMoleculesEditorModel#getNumberOfMolecules()
     */
    public int getNumberOfMolecules() {

        return input.size();
    }

    /* (non-Javadoc)
     * @see net.bioclipse.cdk.ui.views.IMoleculesEditorModel#save(int, net.bioclipse.cdk.domain.ICDKMolecule)
     */
    public void markDirty( int index, ICDKMolecule moleculeToSave ) {

        edited.put( index, moleculeToSave );

    }

    public void save() {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();

    }

    private String getRecord(int index) throws CoreException, IOException {
        InputStream in = input.file().getContents();
        long start = input.start( index );
        int length = (int) (input.start( index +1 )-start);
        in.skip( start );
        byte[] bytes= new byte[length];
        in.read( bytes , 0  , length );
        in.close();
        String result = new String( bytes );
        int i= -1;
        if((i=result.indexOf( "$$$$" ))!= -1)
            return result.substring( 0,i);
        return result;
    }

    protected IChemObject processContent() throws CDKException {

        IChemObject co = chemReader.read(builder.newChemFile());
        if (co instanceof IChemFile) {
            for(IChemSequence chemSeq:((IChemFile) co).chemSequences()) {
                for(IChemModel chemModel:chemSeq.chemModels()) {
                  for(IAtomContainer ac:chemModel.getMoleculeSet().molecules()) {
                      co = (IMolecule) ac;
                      break;
                    }
                  break;
                }
                break;
            }
        }
        return co;
    }

    private void setLastRead(int index,ICDKMolecule mol) {
        if(mol==null) {
            lastIndex = -1;
        }else {
            lastIndex = index;
        }
        lastRead = mol;
    }

    private void readProperties(ICDKMolecule molecule) {

        availableProperties.addAll(
                        molecule.getAtomContainer()
                        .getProperties().keySet());
    }

    public Collection<Object> getPropertyKeys() {
        availableProperties.addAll( propertyList.keySet() );
        return new HashSet<Object>(availableProperties);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPropertyFor(int moleculeIndex,String property) {
        Map<String,Object> props = molProps.get(moleculeIndex);
        Class<?> c = propertyList.get( property );
        if(props!=null && c != null) {
            Object val = props.get( property );
            if(c.isAssignableFrom( val.getClass() ))
                return (T) val;
        }else {
            ICDKMolecule mol = getMoleculeAt( moleculeIndex );
            Object val = mol.getAtomContainer().getProperty( property );
            return (T) val;
        }
        return null;
    }

    public <T extends Object>void setPropertyFor(int moleculeIndex, String property, T value) {
        Map<String,Object> props = molProps.get( moleculeIndex );
        propertyList.put( property, value.getClass() );
        if(props==null)
            molProps.put( moleculeIndex, props = new HashMap<String, Object>() );
        props.put( property, value );
    }

    public Iterator<ICDKMolecule> iterator() {

        return new Iterator<ICDKMolecule>() {

            int pos = 0;
            public boolean hasNext() {
                return pos < getNumberOfMolecules();
            }

            public ICDKMolecule next() {

                return getMoleculeAt( ++pos );
            }

            public void remove() {
                throw new UnsupportedOperationException(
                                        "remove is not supported for SDFIndex");
            }

        };
    }
}