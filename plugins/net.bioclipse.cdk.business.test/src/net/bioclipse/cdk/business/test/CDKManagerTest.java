/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth
 *     Jonathan Alvarsson
 *
 ******************************************************************************/

package net.bioclipse.cdk.business.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.business.CDKManagerHelper;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.domain.ICDKReaction;
import net.bioclipse.cdkdebug.business.ICDKDebugManager;
import net.bioclipse.core.MockIFile;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.business.IMoleculeManager;
import net.bioclipse.core.business.MoleculeManager;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.core.tests.AbstractManagerTest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.MDLRXNFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.formats.Mol2Format;
import org.openscience.cdk.io.formats.SDFFormat;
import org.openscience.cdk.io.formats.SMILESFormat;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.templates.MoleculeFactory;

public class CDKManagerTest extends AbstractManagerTest {

    //Needed to run these tests on some systems. If it breaks them on 
    //other systems we need to do some sort of checking before 
    //setting them...
    static {
        System.setProperty( "javax.xml.parsers.SAXParserFactory", 
                            "com.sun.org.apache.xerces.internal." 
                                + "jaxp.SAXParserFactoryImpl" );
        System.setProperty( "javax.xml.parsers.DocumentBuilderFactory", 
                            "com.sun.org.apache.xerces.internal."
                                + "jaxp.DocumentBuilderFactoryImpl" );
    }
    
    CDKManager cdk;
    ICDKDebugManager cdkdebug;

    //Do not use SPRING OSGI for this manager
    //since we are only testing the implementations of the manager methods
    public CDKManagerTest() {
        cdk = new CDKManager();
    }
    
    public IBioclipseManager getManager() {
        return cdk;
    }

    @Test
    public void testLoadMoleculeFromCMLFile() throws IOException, 
                                          BioclipseException, 
                                          CoreException {

//        InputStream atpFile = getClass().getResourceAsStream("/testFiles/polycarpol.mol");
//        InputStream pdbFile = getClass().getResourceAsStream("/testFiles/1D66.pdb");
        String path = getClass().getResource("/testFiles/0037.cml").getPath();
        ICDKMolecule mol = cdk.loadMolecule( new MockIFile(path), new NullProgressMonitor() );

        Assert.assertNotNull(mol);
        Assert.assertNotSame(0, mol.getAtomContainer().getAtomCount());
        Assert.assertNotSame(0, mol.getAtomContainer().getBondCount());
    }

    @Test
    public void testLoadCMLFromFile2() throws IOException, 
                                          BioclipseException, 
                                          CoreException {

//        InputStream atpFile = getClass().getResourceAsStream("/testFiles/polycarpol.mol");
//        InputStream pdbFile = getClass().getResourceAsStream("/testFiles/1D66.pdb");
        String path = getClass().getResource("/testFiles/cs2a.cml").getPath();
        ICDKMolecule mol = cdk.loadMolecule( new MockIFile(path), new NullProgressMonitor() );

        Assert.assertNotNull(mol);
        Assert.assertNotSame(0, mol.getAtomContainer().getAtomCount());
        Assert.assertNotSame(0, mol.getAtomContainer().getBondCount());
    }

    
    @Test
    public void testLoadMoleculeFromSMILESFileDirectly() throws IOException, 
                                          BioclipseException, 
                                          CoreException {

        String path = getClass().getResource("/testFiles/nprods.smi").getPath();
        List<ICDKMolecule> mol = cdk.loadSMILESFile(
            new MockIFile(path), (IProgressMonitor)null
        );
        assertNotNull( mol );
        System.out.println("SMILES file size: " + mol.size());
        assertEquals(30, mol.size());
    }

    @Test
    public void testLoadMoleculeFromSMILESFile() throws IOException, 
                                          BioclipseException, 
                                          CoreException {

        String path = getClass().getResource("/testFiles/nprods.smi").getPath();
        List<ICDKMolecule> mols = cdk.loadMolecules(new MockIFile(path), (IChemFormat)SMILESFormat.getInstance(), null);
        
        System.out.println("SMILES file size: " + mols.size());
        assertEquals(30, mols.size());
        
        for (ICDKMolecule mol : mols){
        	System.out.println("Mol: " + mol.getName() + " SMILES: " +
        	    mol.getSMILES(
                    net.bioclipse.core.domain.IMolecule
                        .Property.USE_CACHED_OR_CALCULATED
                ));
        	if (mol.getName().equals("1")){
                ICDKMolecule smilesMol1 = cdk.fromSMILES("C(=O)N(Cc1ccco1)C(c1cc2ccccc2cc1)C(=O)NCc1ccccc1");
                double expm=cdk.calculateMass(smilesMol1);
                assertEquals(expm, cdk.calculateMass(mol));
                assertTrue(cdk.fingerPrintMatches(smilesMol1, mol));
        	}

        	if (mol.getName().equals("30")){
                ICDKMolecule smilesMol1 = cdk.fromSMILES("C(=O)N(Cc1ccc(o1)C)C(c1ccccc1)C(=O)NCS(=O)(=O)c1ccc(cc1)C");
                double expm=cdk.calculateMass(smilesMol1);
                assertEquals(expm, cdk.calculateMass(mol));
                assertTrue(cdk.fingerPrintMatches(smilesMol1, mol));
        	}
        }

    }
    
    @Test
    public void testloadMoleculesFromSMILESCheck() throws BioclipseException {
        String[] input = {"CC","CCC(CC)CC","CCC"};
        
        StringBuilder sb = new StringBuilder();
        for(String s: input) {
            sb.append( s );
            sb.append( "\n" );
        }
        
        IFile file = new MockIFile(
                           new ByteArrayInputStream(sb.toString().getBytes()))
                            .extension( "smi" );
        
        
        try {
            List<ICDKMolecule> molecules = cdk.loadSMILESFile(
                file, (IProgressMonitor)null
            );
            Assert.assertNotNull( molecules );
            Assert.assertEquals(3, molecules.size());
            List<String> inputList = new ArrayList<String>(Arrays.asList( input ));
            
            for(ICDKMolecule molecule:molecules) {
                String smiles = molecule.getSMILES(
                    net.bioclipse.core.domain.IMolecule
                        .Property.USE_CACHED_OR_CALCULATED
                );
                if(inputList.contains( smiles ))
                    inputList.remove( smiles );
            }
            Assert.assertEquals( 0, inputList.size() );
        } catch ( CoreException e ) {
            Assert.fail( e.getMessage() );
        } catch ( IOException e ) {
            Assert.fail( e.getMessage());
        }
    }
    
    @Test
    public void testLoadATP() throws IOException, 
                                     BioclipseException, 
                                     CoreException {

        String path = getClass().getResource("/testFiles/atp.mol")
                                .getPath();
        
        ICDKMolecule mol = cdk.loadMolecule( new MockIFile(path), new NullProgressMonitor() );

        System.out.println("mol: " + mol.toString());
    }

    @Test
    public void testLoadPolycarpol() throws IOException, 
                                            BioclipseException, 
                                            CoreException {

        String path = getClass().getResource("/testFiles/polycarpol.mol")
                                .getPath();
        
        ICDKMolecule mol = cdk.loadMolecule( new MockIFile(path), new NullProgressMonitor() );

        System.out.println("mol: " + mol.toString());
    }

    @Test
    public void testCreateSMILES() throws BioclipseException, 
                                          IOException, 
                                          CoreException {
        String path = getClass().getResource("/testFiles/0037.cml").getPath();
        
        ICDKMolecule mol = cdk.loadMolecule( new MockIFile(path), new NullProgressMonitor() );
        String smiles = mol.getSMILES(
            net.bioclipse.core.domain.IMolecule
                .Property.USE_CACHED_OR_CALCULATED
        );

        assertEquals("N#CC1CCCC(C)N1C(CO[Si](C)(C)C)C2=CC=CC=C2", smiles);
    }

    @Test
    public void testCreateMoleculeFromSMILES() throws BioclipseException {

        ICDKMolecule mol=cdk.fromSMILES("C1CCCCC1CCO");

        assertEquals(mol.getAtomContainer().getAtomCount(), 9);
        assertEquals(mol.getAtomContainer().getBondCount(), 9);
    }

    @Test
    public void testCreatingMoleculeIterator() 
                throws CoreException, 
                       FileNotFoundException {

        String path = getClass().getResource("/testFiles/test.sdf")
                                .getPath();
        
        List<IMolecule> molecules = new ArrayList<IMolecule>();

        for ( Iterator<net.bioclipse.cdk.domain.ICDKMolecule> iterator
                    = cdk.createMoleculeIterator( new MockIFile(path),
                                                  null );
              iterator.hasNext(); ) {

            molecules.add( iterator.next() );
        }

        assertEquals( 2, molecules.size() );
    }
    
    @Test
    public void testFingerPrintMatch() throws BioclipseException {
        SmilesGenerator generator = new SmilesGenerator();
        String indoleSmiles  = generator
                               .createSMILES( MoleculeFactory
                                              .makeIndole() );
        String pyrroleSmiles = generator
                               .createSMILES( MoleculeFactory
                                              .makePyrrole() );
        ICDKMolecule indole  = cdk.fromSMILES( indoleSmiles );
        ICDKMolecule pyrrole = cdk.fromSMILES( pyrroleSmiles );
        
        assertTrue( cdk.fingerPrintMatches(indole, pyrrole) );
    }
    
    @Test
    public void testSubStructureMatch() throws BioclipseException {
        SmilesGenerator generator = new SmilesGenerator();
        String indoleSmiles  = generator
                               .createSMILES( MoleculeFactory
                                              .makeIndole() );
        String pyrroleSmiles = generator
                               .createSMILES( MoleculeFactory.
                                              makePyrrole() );
        ICDKMolecule indole  = cdk.fromSMILES( indoleSmiles  );
        ICDKMolecule pyrrole = cdk.fromSMILES( pyrroleSmiles );
        
        assertTrue( cdk.subStructureMatches( indole, pyrrole ) );
    }
    
    @Test
    public void testStructureMatches() throws BioclipseException {
    	ICDKMolecule molecule = cdk.fromSMILES("CCCBr");
    	ICDKMolecule molecule2 = cdk.fromSMILES("CCCBr");
    	ICDKMolecule molecule3 = cdk.fromSMILES("C1CCBrC1");
    	assertTrue(cdk.areIsomorphic(molecule, molecule2));
    	Assert.assertFalse(cdk.areIsomorphic(molecule, molecule3));
   	}
    
    @Test
    public void testCDKMoleculeFromIMolecule() throws BioclipseException {
        SmilesGenerator generator = new SmilesGenerator();
        String indoleSmiles  = generator
                               .createSMILES( MoleculeFactory
                                              .makeIndole() );
        IMoleculeManager molecule = new MoleculeManager();
        IMolecule m = molecule.fromSmiles( indoleSmiles );
        ICDKMolecule cdkm = cdk.create( m );
        assertEquals( cdkm.getSMILES(
            net.bioclipse.core.domain.IMolecule
                .Property.USE_CACHED_OR_CALCULATED
        ), m.getSMILES(
            net.bioclipse.core.domain.IMolecule
                .Property.USE_CACHED_OR_CALCULATED
        ) );
    }
    
    @Test
    public void testSMARTSMatching() throws BioclipseException {
        String propaneSmiles = "CCC"; 
        
        ICDKMolecule propane  = cdk.fromSMILES( propaneSmiles  );
        
        assertTrue( cdk.smartsMatches(propane, propaneSmiles) );
    }

    @Test
    public void testLoadConformers() throws BioclipseException, IOException {
        MockIFile file = new MockIFile( 
            getClass().getResource("/testFiles/dbsmallconf.sdf")
                      .getPath() );

        List<ICDKMolecule> mols = cdk.loadConformers(file, null);
        assertNotNull( mols );
        assertEquals( 3, mols.size() );
        
        assertEquals( 3, mols.get( 0 ).getConformers().size() );
        assertEquals( 1, mols.get( 1 ).getConformers().size() );
        assertEquals( 2, mols.get( 2 ).getConformers().size() );
        
//        System.out.println(mols.get( 0 ).getConformers().get( 0 ).getSmiles());
//        System.out.println(mols.get( 0 ).getConformers().get( 1 ).getSmiles());
//        System.out.println(mols.get( 0 ).getConformers().get( 2 ).getSmiles());
//        System.out.println(mols.get( 1 ).getConformers().get( 0 ).getSmiles());
//        System.out.println(mols.get( 2 ).getConformers().get( 0 ).getSmiles());
//        System.out.println(mols.get( 2 ).getConformers().get( 1 ).getSmiles());
    }

    @Test
    public void testSave() throws BioclipseException, CDKException, CoreException, IOException {
        String propaneSmiles = "CCC"; 
        
        ICDKMolecule propane  = cdk.fromSMILES( propaneSmiles  );
        IChemModel chemmodel=propane.getAtomContainer().getBuilder().newChemModel();
        IMoleculeSet setOfMolecules=chemmodel.getBuilder().newMoleculeSet();
        setOfMolecules.addAtomContainer(propane.getAtomContainer());
        chemmodel.setMoleculeSet(setOfMolecules);
        
        IFile target=new MockIFile();
        cdk.save(chemmodel, target, (IChemFormat)MDLV2000Format.getInstance(), null);
        byte[] bytes=new byte[6];
        target.getContents().read(bytes);
        Assert.assertArrayEquals(new byte[]{10,32,32,67,68,75}, bytes);
    }

    @Test
    public void testSaveMolecule() throws BioclipseException, CDKException, CoreException, IOException {
        String propaneSmiles = "CCC"; 
        
        ICDKMolecule propane  = cdk.fromSMILES( propaneSmiles  );
        
        IFile target=new MockIFile();
        cdk.saveMolecule(propane, target, (IChemFormat)MDLV2000Format.getInstance());
        byte[] bytes=new byte[6];
        target.getContents().read(bytes);
        Assert.assertArrayEquals(new byte[]{10,32,32,67,68,75}, bytes);
    }

    @Test
    public void testSaveMoleculesSDF() throws BioclipseException, CDKException, CoreException, IOException {

        System.out.println("*************************");
        System.out.println("testSaveMoleculesSDF()");

        MoleculeManager molmg=new MoleculeManager();
        IMolecule mol1=molmg.fromSmiles("CCC");
        IMolecule mol2=molmg.fromSmiles("C1CCCCC1CCO");
        
        List<IMolecule> mols=new ArrayList<IMolecule>();
        mols.add(mol1);
        mols.add(mol2);
        
        IFile target=new MockIFile();
        cdk.saveMolecules(mols, target, (IChemFormat)SDFFormat.getInstance());

        List<ICDKMolecule> readmols = cdk.loadMolecules(target);
        assertEquals(2, readmols.size());
        
    	System.out.println("** Reading back created SDFile: ");
        for (ICDKMolecule cdkmol : readmols){
        	System.out.println("  - SMILES: " + cdk.calculateSMILES(cdkmol));
        }
        
        System.out.println("*************************");
        
    }
    
    @Test
    public void testSaveMoleculesSDFwithProps() throws BioclipseException, CDKException, CoreException, IOException {

        System.out.println("*************************");
        System.out.println("testSaveMoleculesSDFwithProps()");

        ICDKMolecule mol1=cdk.fromSMILES("CCC");
        ICDKMolecule mol2=cdk.fromSMILES("C1CCCCC1CCO");
        
        mol1.getAtomContainer().setProperty("wee", "how");
        mol2.getAtomContainer().setProperty("santa", "claus");
        
        List<IMolecule> mols=new ArrayList<IMolecule>();
        mols.add(mol1);
        mols.add(mol2);
        
        IFile target=new MockIFile();
        cdk.saveMolecules(mols, target, (IChemFormat)SDFFormat.getInstance());

        BufferedReader reader=new BufferedReader(new InputStreamReader(target.getContents()));

        System.out.println("#############################################");
        String line=reader.readLine();
        while(line!=null){
        	System.out.println(line);
            line=reader.readLine();
        }
        System.out.println("#############################################");
        
        List<ICDKMolecule> readmols = cdk.loadMolecules(target);
        assertEquals(2, readmols.size());

    	System.out.println("** Reading back created SDFile: ");
        for (ICDKMolecule cdkmol : readmols){
        	System.out.println("  - SMILES: " + cdk.calculateSMILES(cdkmol));
        	if (cdkmol.getAtomContainer().getAtomCount()==3){
            	assertEquals("how", cdkmol.getAtomContainer().getProperty("wee"));
        	}else{
            	assertEquals("claus", cdkmol.getAtomContainer().getProperty("santa"));
        	}
        }
        
        System.out.println("*************************");
        
    }
    
    
    @Test
    public void testSaveMoleculesCML() throws BioclipseException, CDKException, CoreException, IOException {

        System.out.println("*************************");
        System.out.println("testSaveMoleculesCML()");

        MoleculeManager molmg=new MoleculeManager();
        IMolecule mol1=molmg.fromSmiles("CCC");
        IMolecule mol2=molmg.fromSmiles("C1CCCCC1CCO");
        
        List<IMolecule> mols=new ArrayList<IMolecule>();
        mols.add(mol1);
        mols.add(mol2);
        
        IFile target=new MockIFile();
        cdk.saveMolecules(mols, target, (IChemFormat)CMLFormat.getInstance());

        List<ICDKMolecule> readmols = cdk.loadMolecules(target);
        assertEquals(2, readmols.size());

    	System.out.println("** Reading back created CML file: ");
        for (ICDKMolecule cdkmol : readmols){
        	System.out.println("  - SMILES: " + cdk.calculateSMILES(cdkmol));
//            System.out.println("Generated CML: \n");
//            System.out.println(cdkmol.getCML());
        }
        System.out.println("*************************");
        
    }

    @Test
    public void testSaveMoleculesCMLwithProps() throws BioclipseException, CDKException, CoreException, IOException {

        System.out.println("*************************");
        System.out.println("testSaveMoleculesCMLwithProps()");

        ICDKMolecule mol1=cdk.fromSMILES("CCC");
        ICDKMolecule mol2=cdk.fromSMILES("C1CCCCC1CCO");
        
        mol1.getAtomContainer().setProperty("wee", "how");
        mol2.getAtomContainer().setProperty("santa", "claus");
        
        List<IMolecule> mols=new ArrayList<IMolecule>();
        mols.add(mol1);
        mols.add(mol2);
        
        IFile target=new MockIFile();
        cdk.saveMolecules(mols, target, (IChemFormat)CMLFormat.getInstance());

        BufferedReader reader=new BufferedReader(new InputStreamReader(target.getContents()));

        System.out.println("#############################################");
        String line=reader.readLine();
        while(line!=null){
        	System.out.println(line);
            line=reader.readLine();
        }
        System.out.println("#############################################");
        
        List<ICDKMolecule> readmols = cdk.loadMolecules(target);
    	System.out.println("** Reading back created CML File: ");
        for (ICDKMolecule cdkmol : readmols){
        	System.out.println("  - SMILES: " + cdk.calculateSMILES(cdkmol));
        	if (cdkmol.getAtomContainer().getAtomCount()==3){
            	assertEquals("how", cdkmol.getAtomContainer().getProperty("wee"));
        	}else{
            	assertEquals("claus", cdkmol.getAtomContainer().getProperty("santa"));
        	}
        }
        
        System.out.println("*************************");
        
    }


    
    @Test
    public void testSaveMol2() throws BioclipseException, CDKException, CoreException, IOException {

    	String propaneSmiles = "CCC"; 
        
        ICDKMolecule propane  = cdk.fromSMILES( propaneSmiles  );

        IFile target=new MockIFile();
        cdk.saveMolecule(propane, target, (IChemFormat)Mol2Format.getInstance());
    	
    }

    @Test
    public void testCMLOK1() throws Exception {
        String filename = "testFiles/cs2a.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

        // test the resulting ChemFile content
        assertNotNull(chemFile);
        assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        assertNotNull(model);
        assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

        // test the molecule
        org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
        assertNotNull(mol);
        assertEquals(38, mol.getAtomCount());
        assertEquals(48, mol.getBondCount());
        assertTrue(GeometryTools.has3DCoordinates(mol));
        assertTrue(!GeometryTools.has2DCoordinates(mol));
    }

    
    @Test
    public void testLoadCMLFromFile3() throws IOException, 
                                          BioclipseException, 
                                          CoreException {

        String path = getClass().getResource("/testFiles/cs2a.cml").getPath();
        MockIFile mf=new MockIFile(path);
        
        ReaderFactory readerFactory=new ReaderFactory();
        CDKManagerHelper.registerSupportedFormats(readerFactory);

        //Create the reader
        ISimpleChemObjectReader reader 
            = readerFactory.createReader(mf.getContents());

        if (reader==null) {
            throw new BioclipseException("Could not create reader in CDK.");
        }

        IChemFile chemFile = new org.openscience.cdk.ChemFile();

        // Do some customizations...
        CDKManagerHelper.customizeReading(reader);

        //Read file
        try {
            chemFile=(IChemFile)reader.read(chemFile);
        } catch (CDKException e) {
        	e.printStackTrace();
        }

    }
    
    @Test public void testAddExplicitHydrogens() throws Exception {
        ICDKMolecule molecule = cdk.fromSMILES("C");
        assertEquals(1, molecule.getAtomContainer().getAtomCount());
        cdk.addExplicitHydrogens(molecule);
        assertEquals(5, molecule.getAtomContainer().getAtomCount());
        assertEquals(0, molecule.getAtomContainer().getAtom(0).getHydrogenCount());
    }

    @Test public void testBug691() throws Exception {
        ICDKMolecule molecule = cdk.fromSMILES("C(C1C(C(C(C(O1)O)O)O)O)O");
        assertEquals(12, molecule.getAtomContainer().getAtomCount());
        cdk.addExplicitHydrogens(molecule);
        assertEquals(24, molecule.getAtomContainer().getAtomCount());
        assertEquals(0, molecule.getAtomContainer().getAtom(0).getHydrogenCount());
    }

    @Test public void testAddImplicitHydrogens() throws Exception {
        ICDKMolecule molecule = cdk.fromSMILES("C");
        assertEquals(1, molecule.getAtomContainer().getAtomCount());
        cdk.addImplicitHydrogens(molecule);
        assertEquals(1, molecule.getAtomContainer().getAtomCount());
        assertEquals(4, molecule.getAtomContainer().getAtom(0).getHydrogenCount());
    }

    @Test public void testGenerate3DCoordinates() throws Exception {
        List<IMolecule> molecule = new ArrayList<IMolecule>();
        molecule.add( cdk.fromSMILES("CCC"));
        molecule.add( cdk.fromSMILES("C1CCCCC1"));
        assertEquals(3, ((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtomCount());
        assertEquals(6, ((ICDKMolecule)molecule.get( 1 )).getAtomContainer().getAtomCount());
        Assert.assertNull(((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtom(0).getPoint3d());
        Assert.assertNull(((ICDKMolecule)molecule.get( 1 )).getAtomContainer().getAtom(0).getPoint3d());
        ((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtom( 0 ).setPoint2d( new Point2d(0,0) );
        ((ICDKMolecule)molecule.get( 1 )).getAtomContainer().getAtom( 0 ).setPoint2d( new Point2d(0,0) );
        cdk.generate3dCoordinates(molecule);
        assertNotNull(((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtom(0).getPoint3d());
        assertNotNull(((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtom(0).getPoint2d());
        assertNotNull(((ICDKMolecule)molecule.get( 1 )).getAtomContainer().getAtom(0).getPoint3d());
        assertNotNull(((ICDKMolecule)molecule.get( 1 )).getAtomContainer().getAtom(0).getPoint2d());
    }

    @Test public void testGenerate3DCoordinatesSingle() throws Exception {
        List<IMolecule> molecule = new ArrayList<IMolecule>();
        molecule.add( cdk.fromSMILES("CCC"));
        assertEquals(3, ((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtomCount());
        Assert.assertNull(((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtom(0).getPoint3d());
        //2d coords should stay, we test that
        ((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtom( 0 ).setPoint2d( new Point2d(0,0) );
        cdk.generate3dCoordinates(molecule);
        assertNotNull(((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtom(0).getPoint3d());
        assertNotNull(((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtom(0).getPoint2d());
    }

    @Test public void testGenerate2DCoordinatesSingle() throws Exception {
        List<IMolecule> molecule = new ArrayList<IMolecule>();
        molecule.add(cdk.fromSMILES("CCCBr"));
        assertEquals(4, ((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtomCount());
        Assert.assertNull(((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtom(0).getPoint2d());
        //3d coords should stay, we test that.
        ((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtom( 0 ).setPoint3d( new Point3d(0,0,0) );
        List<ICDKMolecule> cdkMolecule = cdk.generate2dCoordinates(molecule);
        Assert.assertTrue(cdkMolecule.get(0) instanceof ICDKMolecule);
        assertNotNull(((ICDKMolecule)cdkMolecule.get(0)).getAtomContainer().getAtom(0).getPoint2d());
        assertNotNull(((ICDKMolecule)cdkMolecule.get(0)).getAtomContainer().getAtom(0).getPoint3d());
    }

    
    @Test public void testGenerate2DCoordinates() throws Exception {
        List<IMolecule> molecule = new ArrayList<IMolecule>();
        molecule.add(cdk.fromSMILES("CCCBr"));
        molecule.add( cdk.fromSMILES("C1CCCCC1"));
        assertEquals(4, ((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtomCount());
        assertEquals(6, ((ICDKMolecule)molecule.get( 1 )).getAtomContainer().getAtomCount());
        Assert.assertNull(((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtom(0).getPoint2d());
        Assert.assertNull(((ICDKMolecule)molecule.get( 1 )).getAtomContainer().getAtom(0).getPoint2d());
        //3d coords should stay, we test that.
        ((ICDKMolecule)molecule.get( 0 )).getAtomContainer().getAtom( 0 ).setPoint3d( new Point3d(0,0,0) );
        ((ICDKMolecule)molecule.get( 1 )).getAtomContainer().getAtom( 0 ).setPoint3d( new Point3d(0,0,0) );
        List<ICDKMolecule> cdkMolecule = cdk.generate2dCoordinates(molecule);
        Assert.assertTrue(cdkMolecule.get(0) instanceof ICDKMolecule);
        assertNotNull(((ICDKMolecule)cdkMolecule.get(0)).getAtomContainer().getAtom(0).getPoint2d());
        assertNotNull(((ICDKMolecule)cdkMolecule.get(0)).getAtomContainer().getAtom(0).getPoint3d());
        assertNotNull(((ICDKMolecule)cdkMolecule.get(1)).getAtomContainer().getAtom(0).getPoint2d());
        assertNotNull(((ICDKMolecule)cdkMolecule.get(1)).getAtomContainer().getAtom(0).getPoint3d());
    }
    
    
    @Test 
    public void testPerceiveAromaticity() throws Exception{
        String path = getClass().getResource("/testFiles/aromatic.mol").getPath();
        MockIFile mf=new MockIFile(path);
        ICDKMolecule mol = cdk.loadMolecule( mf, new NullProgressMonitor());
        Assert.assertFalse( mol.getAtomContainer().getAtom( 6 ).getFlag( CDKConstants.ISAROMATIC ) );
        ICDKMolecule molwitharomaticity = (ICDKMolecule)cdk.perceiveAromaticity( mol );
        Assert.assertTrue( molwitharomaticity.getAtomContainer().getAtom( 6 ).getFlag( CDKConstants.ISAROMATIC ) );
    }
    
    @Test public void testHas2d() throws Exception {
        ICDKMolecule molecule = cdk.fromSMILES("CCCBr");
        Assert.assertFalse(cdk.has2d(molecule));
        IMolecule cdkMolecule = cdk.generate2dCoordinates(molecule);
        Assert.assertTrue(cdk.has2d(cdkMolecule));
    }

    @Test public void testHas3d() throws Exception {
        ICDKMolecule molecule = cdk.fromSMILES("CCCBr");
        Assert.assertFalse(cdk.has3d(molecule));
        cdk.generate3dCoordinates(molecule);
        Assert.assertTrue(cdk.has3d(molecule));
    }

    @Test public void testFromCML() throws Exception {
        ICDKMolecule molecule = cdk.fromCml("<molecule id='m1'><atomArray atomID='a1 a2' x2='0.0 0.1' y2='1.2 1.3'/></molecule>");
        Assert.assertNotNull(molecule);
        Assert.assertEquals(2, molecule.getAtomContainer().getAtomCount());
    }

    @Test public void testFromString() throws Exception {
        ICDKMolecule molecule = cdk.fromCml("<molecule id='m1'><atomArray atomID='a1 a2' x2='0.0 0.1' y2='1.2 1.3'/></molecule>");
        Assert.assertNotNull(molecule);
        Assert.assertEquals(2, molecule.getAtomContainer().getAtomCount());
    }

    @Test public void testCreateSDFile_File_IMoleculeArray() throws Exception{
    	List<IMolecule> mol = new ArrayList<IMolecule>();
    	mol.add(cdk.fromSMILES("CCCBr"));
    	mol.add(cdk.fromSMILES("CCCCl"));
    	IFile file=new MockIFile();
    	cdk.saveSDFile(file, mol, null);
    	byte[] bytes=new byte[1000];
    	file.getContents().read(bytes);
    	StringBuffer sb=new StringBuffer();
        for(int i=0;i<bytes.length;i++){
        	sb.append((char)bytes[i]);
        }
        System.out.println(sb.toString());
        assertTrue(sb.toString().contains("$$$$"));
        assertTrue(sb.toString().contains("Cl"));
        assertTrue(sb.toString().contains("Br"));
    }
    
    
    @Test
    public void testExtractFromSDFile_IFile_int_int() throws FileNotFoundException, BioclipseException, InvocationTargetException{
        String path = getClass().getResource("/testFiles/test.sdf")
        .getPath();

        List<IMolecule> mol = cdk.extractFromSDFile( new MockIFile(path), 0, 1 );
        Assert.assertEquals( 2,mol.size() );
    }

    @Test
    public void testGetFormat() {
        Assert.assertEquals(
                MDLV2000Format.getInstance(),
                cdk.getFormat("MDLV2000Format")
        );
        Assert.assertEquals(
                Mol2Format.getInstance(),
                cdk.getFormat("Mol2Format")
        );
        Assert.assertEquals(
                CMLFormat.getInstance(),
                cdk.getFormat("CMLFormat")
        );
        Assert.assertEquals(
                SDFFormat.getInstance(),
                cdk.getFormat("SDFFormat")
        );
    }

    @Test
    public void testGuessFormatFromExtension() {
        Assert.assertEquals(
                MDLV2000Format.getInstance(),
                cdk.guessFormatFromExtension("file.mol")
        );
        Assert.assertEquals(
                MDLV2000Format.getInstance(),
                cdk.guessFormatFromExtension("file.mdl")
        );
        Assert.assertEquals(
                Mol2Format.getInstance(),
                cdk.guessFormatFromExtension("file.mol2")
        );
        Assert.assertEquals(
                CMLFormat.getInstance(),
                cdk.guessFormatFromExtension("file.cml")
        );
        Assert.assertEquals(
                SDFFormat.getInstance(),
                cdk.guessFormatFromExtension("file.sdf")
        );
    }

    @Test
    public void testGetFormats() {
        String formats = cdk.getFormats();
        Assert.assertTrue(formats.contains("Mol2Format"));
        Assert.assertTrue(formats.contains("CMLFormat"));
        Assert.assertTrue(formats.contains("MDLV2000Format"));
        Assert.assertTrue(formats.contains("SDFFormat"));
    }

    @Test public void testBug621() throws Exception {
        ICDKMolecule mol = cdk.fromSMILES("ClC(Cl)(Cl)Cl");
        String mf = cdk.molecularFormula(mol);
        Assert.assertFalse(mf.contains("H0"));
    }

    @Test public void testFragmentate() throws Exception {
        ICDKMolecule mol = cdk.fromSMILES("O=C(CC)[O-].[Na+]");
        List<IAtomContainer> fragments = cdk.partition(mol);
        Assert.assertEquals(2, fragments.size());
    }

    @Test public void testIsConnected() throws Exception {
        ICDKMolecule mol = cdk.fromSMILES("O=C(CC)[O-].[Na+]");
        Assert.assertFalse(cdk.isConnected(mol));
    }

    @Test public void testIsConnected2() throws Exception {
        ICDKMolecule mol = cdk.fromSMILES("O=C(CC)O");
        Assert.assertTrue(cdk.isConnected(mol));
    }

    @Test public void testTotalFormalCharge() throws Exception {
        ICDKMolecule mol = cdk.fromSMILES("O=C(CC)[O-].[Na+]");
        Assert.assertEquals(0, cdk.totalFormalCharge(mol));

        mol = cdk.fromSMILES("O=C(CC)[O-]");
        Assert.assertEquals(-1, cdk.totalFormalCharge(mol));

        mol = cdk.fromSMILES("O=C(CC(=O)[O-])[O-]");
        Assert.assertEquals(-2, cdk.totalFormalCharge(mol));
    }

    @Test public void testSingleTanimoto() throws Exception {
        String path = getClass().getResource("/testFiles/aromatic.mol").getPath();
        MockIFile mf=new MockIFile(path);
        ICDKMolecule mol = cdk.loadMolecule( mf, new NullProgressMonitor());
        float similarity = cdk.calculateTanimoto( mol,mol );
        Assert.assertEquals( 1, similarity, 0.0001 );
        path = getClass().getResource("/testFiles/atp.mol").getPath();
        mf=new MockIFile(path);
        ICDKMolecule mol2 = cdk.loadMolecule( mf, new NullProgressMonitor());
        float similarity2 = cdk.calculateTanimoto( mol,mol2 );
        Assert.assertEquals( 0.1972, similarity2, 0.0001 );
    }

    @Test public void testMultipleTanimoto() throws Exception {
        List<Float> expected= new ArrayList<Float>();
        expected.add((float)1);
        expected.add((float)0.19720767);
        List<Float> actuals= new ArrayList<Float>();
        String path = getClass().getResource("/testFiles/aromatic.mol").getPath();
        MockIFile mf=new MockIFile(path);
        ICDKMolecule mol = cdk.loadMolecule( mf, new NullProgressMonitor());
        actuals.add(cdk.calculateTanimoto( mol,mol ));
        path = getClass().getResource("/testFiles/atp.mol").getPath();
        mf=new MockIFile(path);
        ICDKMolecule mol2 = cdk.loadMolecule( mf, new NullProgressMonitor());
        actuals.add(cdk.calculateTanimoto( mol,mol2 ));
        Assert.assertEquals( expected, actuals );
    }

    @Test public void testGetMDLMolfileString() throws Exception {
        ICDKMolecule mol = cdk.fromSMILES("O=C(CC)[O-].[Na+]");

        String fileContent = cdk.getMDLMolfileString(mol);

        Assert.assertNotNull(fileContent);
        Assert.assertTrue(fileContent.contains("V2000"));
    }

    @Test public void testGetSetProperty() throws Exception {
        ICDKMolecule mol = cdk.fromSMILES("CC");
        Assert.assertNull(cdk.setProperty(mol, "foo", "bar"));
        Assert.assertEquals("bar", cdk.getProperty(mol, "foo"));
    }

    @Test public void testCalculateTanimoto_BitSet_BitSet() throws Exception {
        BitSet b1 = new BitSet(5); b1.set(5); b1.set(4);
        BitSet b3 = new BitSet(5); b3.set(3); b3.set(4);
        BitSet b4 = new BitSet(5); b4.set(3);
        Assert.assertEquals(1.0, cdk.calculateTanimoto(b1, b1), 0.0);
        Assert.assertEquals(0.0, cdk.calculateTanimoto(b1, b4), 0.0);
        Assert.assertNotSame(1.0, cdk.calculateTanimoto(b1, b3));
        Assert.assertNotSame(0.0, cdk.calculateTanimoto(b1, b3));
    }

    @Test public void testCalculateTanimoto_IMolecule_BitSet() throws Exception {
        ICDKMolecule mol = cdk.fromSMILES("CC");
        BitSet b3 = mol.getFingerprint(true);
        Assert.assertEquals(1.0, cdk.calculateTanimoto(mol, b3), 0.0);
    }
    
    @Test public void testLoadReaction_InputStream_IProgressMonitor_IChemFormat() throws Exception{
        String path = getClass().getResource("/testFiles/0002.stg01.rxn").getPath();
        IFile file = new MockIFile( path );
        ICDKReaction reaction = cdk.loadReactions( file.getContents(), new NullProgressMonitor(), (IChemFormat)MDLRXNFormat.getInstance()).get( 0 );

        Assert.assertNotNull(reaction);
        Assert.assertSame(1, reaction.getReaction().getReactantCount());
        Assert.assertSame(1, reaction.getReaction().getProductCount());

    }
    
    @Test public void testLoadReaction_IFile_IProgressMonitor() throws Exception{
        String path = getClass().getResource("/testFiles/reaction.1.cml").getPath();
        IFile file = new MockIFile( path );
        ICDKReaction reaction = cdk.loadReactions( file, new NullProgressMonitor()).get( 0 );

        Assert.assertNotNull(reaction);
        Assert.assertSame(1, reaction.getReaction().getReactantCount());
        Assert.assertSame(1, reaction.getReaction().getProductCount());
        
    }

    @Test public void testRemoveImplicitHydrogens() throws Exception {
        ICDKMolecule mol = cdk.fromSMILES("CC");
        cdk.removeImplicitHydrogens(mol);
        for (IAtom atom : mol.getAtomContainer().atoms()) {
            Assert.assertEquals(0, atom.getHydrogenCount().intValue());
        }
    }

    @Test public void testRemoveExplicitHydrogens() throws Exception {
        ICDKMolecule mol = cdk.fromSMILES("[H]C([H])([H])[H]");
        Assert.assertEquals(5, mol.getAtomContainer().getAtomCount());
        cdk.removeExplicitHydrogens(mol);
        Assert.assertEquals(1, mol.getAtomContainer().getAtomCount());
    }

    @Test
    public void testMolecularFormulaCharged() throws BioclipseException {
        ICDKMolecule m = cdk.fromSMILES( "[O-]" );
        assertEquals( "[HO]-", cdk.molecularFormula(m) );
    }

}
