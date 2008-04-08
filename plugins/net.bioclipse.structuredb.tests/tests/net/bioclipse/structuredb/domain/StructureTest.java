/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/

package net.bioclipse.structuredb.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;

import testData.TestData;


public class StructureTest {

	@Test
	public void testHasValuesEqualTo() throws CDKException {
		
		AtomContainer cycloPropane = TestData.getCycloPropane();
		AtomContainer cycloOctan   = TestData.getCycloOctan();
		
		Structure structure1 = new Structure( "cycloPropane", cycloPropane );
		Structure structure2 = new Structure(structure1);
		Structure structure3 = new Structure( "cycloOctane", cycloOctan );
		
		assertTrue(  structure1.hasValuesEqualTo(structure2) );
		assertFalse( structure1.hasValuesEqualTo(structure3) );
	}
	
	@Test
	public void testDoubleReferences() throws CDKException {
		
		AtomContainer testMolecule = TestData.getCycloPropane();
		
		Folder folder     = new Folder();
		Folder library2    = new Folder();
		Structure structure = new Structure("Cyclopropane", testMolecule);
		
		structure.setFolder( folder );
		
		assertTrue( structure.getFolder() == folder );
		assertTrue( folder.getStructures().contains(structure) );
		
		structure.setFolder( library2 );
		
		assertTrue(  structure.getFolder() == library2           );
		assertTrue(  library2.getStructures().contains(structure) );
		assertFalse( folder.getStructures().contains(structure)  );
	}
	
}
