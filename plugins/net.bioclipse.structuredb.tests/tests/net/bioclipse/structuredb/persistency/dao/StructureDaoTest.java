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
package net.bioclipse.structuredb.persistency.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.exception.CDKException;

import testData.TestData;
import net.bioclipse.structuredb.domain.Folder;
import net.bioclipse.structuredb.domain.Structure;

public class StructureDaoTest extends GenericDaoTest<Structure> {

    public StructureDaoTest() {
        super(Structure.class);
    }
    
    public void testPersistStructureWithFolder() {
        
        Folder folder = new Folder();
        IFolderDao folderDao 
            = (IFolderDao) applicationContext.getBean("folderDao");
        addCreatorAndEditor(folder);
        folderDao.insert(folder);
        
        Structure structure = new Structure();
        structure.setFolder(folder);
        addCreatorAndEditor(structure);
        dao.insert(structure);
        
        Structure loaded = dao.getById( structure.getId() );
        assertNotNull("The lodaded object shuold not be null", loaded);
        assertNotSame(structure, loaded);
        assertTrue( structure.hasValuesEqualTo(loaded) );
        assertTrue( structure.getFolder()
                             .hasValuesEqualTo(loaded.getFolder()) );
    }

    public void testPersistStructureWithFolderId() {
        
        Folder folder = new Folder();
        IFolderDao folderDao 
            = (IFolderDao) applicationContext.getBean("folderDao");
        addCreatorAndEditor(folder);
        folderDao.insert(folder);
        
        Structure structure = new Structure();

        addCreatorAndEditor(structure);
        ((IStructureDao)dao).insertInFolder( structure, folder.getId() );
        
        Structure loaded = dao.getById( structure.getId() );
        assertNotNull("The lodaded object should not be null", loaded);
        assertNotSame(structure, loaded);
        assertTrue( structure.hasValuesEqualTo(loaded) );
        assertTrue( loaded.getFolder().getId().equals( folder.getId() ) );
    }
    
    @Override
    public void testUpdate() {
        super.testUpdate();
        
        Folder folder = new Folder();
        IFolderDao folderDao 
            = (IFolderDao) applicationContext.getBean("folderDao");
        addCreatorAndEditor(folder);
        folderDao.insert(folder);
        
        Structure structure = new Structure();
        structure.setFolder(folder);
        addCreatorAndEditor(structure);
        dao.insert(structure);
        
        Structure loaded = dao.getById( structure.getId() );
        structure.setName("edited");
        dao.update(structure);
        Structure updated = dao.getById( structure.getId() );
        assertTrue( structure.hasValuesEqualTo(updated) );
    }
    
    @SuppressWarnings("serial")
    public void testGetByName() throws CDKException {
        final Structure structure1 = new Structure( "structure",
                                                    TestData
                                                    .getCycloOctan() );
        final Structure structure2 = new Structure( "structure", 
                                                    TestData
                                                    .getCycloPropane() );
        addCreatorAndEditor(structure1);
        addCreatorAndEditor(structure2);
        dao.insert(structure1);
        dao.insert(structure2);
        
        List<Structure> structures = new ArrayList<Structure>() {{
            add(structure1);
            add(structure2);
        }};
        
        assertTrue( dao.getAll().containsAll(structures) );
        
        List<Structure> saved = ( (IStructureDao)dao ).getByName(
                                  structure1.getName() );
        assertTrue(  saved.containsAll(structures) );
        assertFalse( saved.contains(object1)       );
        assertFalse( saved.contains(object2)       );
    }
    
    public void testAllStructureIterator() {
        List<Structure> structures = new ArrayList<Structure>() {
            {
                add( object1 );
                add( object2 );
            }
        };
        Iterator<Structure> iterator 
            = ( (IStructureDao)dao ).allStructuresIterator();
        assertTrue( iterator.hasNext() );
        int numberof = 0;
        while( iterator.hasNext() ) {
            assertTrue( structures.contains( iterator.next() ) );
            numberof++;
        }
        assertEquals( 2, numberof );
    }
    
    public void testNumberOfStructures() {
        assertEquals( 2, ((IStructureDao)dao).numberOfStructures() );
    }
    
    public void testFingerPrintSearch() {
        Iterator<Structure> iterator
            = ( (IStructureDao)dao ).fingerPrintSubsetSearch( 
              ((Structure)object1).getFingerPrint() );
        boolean found = false;
        while( iterator.hasNext() ) {
            if( iterator.next().equals( object1 )) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
}
