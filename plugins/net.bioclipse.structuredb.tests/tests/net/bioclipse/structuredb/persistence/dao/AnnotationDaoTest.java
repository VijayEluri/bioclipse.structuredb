/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.persistence.dao;

import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.persistency.dao.IAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.IDBMoleculeDao;

/**
 * @author jonalv
 *
 */
public class AnnotationDaoTest extends GenericDaoTest<Annotation> {

    
    public AnnotationDaoTest() {
        super(Annotation.class);
    }
    
    @Override
    public void testDelete() {
        
        IDBMoleculeDao dBMoleculeDao 
            = (IDBMoleculeDao) applicationContext.getBean("dBMoleculeDao");
        
        DBMolecule dBMolecule = new DBMolecule();
        dBMolecule.setCreator(testUser);
        dBMolecule.setLastEditor(testUser);
        dBMoleculeDao.insert(dBMolecule);
        object1.addDBMolecule(dBMolecule);
        dao.update(object1);
        assertTrue( dBMolecule.getAnnotations().contains( object1 ) );
        super.testDelete();
        
        dBMolecule = dBMoleculeDao.getById( dBMolecule.getId() );
        assertFalse( dBMolecule.getAnnotations().contains( object1 ) );
    }
    
    public void testGetByName() {

        IDBMoleculeDao dBMoleculeDao 
            = (IDBMoleculeDao) applicationContext.getBean("dBMoleculeDao");

        DBMolecule s = new DBMolecule();
        dBMoleculeDao.insert( s );
        Annotation annotation = new Annotation("label");
        addCreatorAndEditor(annotation);
        dao.insert(annotation);
        assertEquals( annotation, 
                      ( (IAnnotationDao)dao ).getByName(annotation.getName()) );
        annotation.addDBMolecule( s );
        dao.update( annotation );
        assertEquals( 1, 
                      ( (IAnnotationDao)dao ).getByName( annotation.getName() )
                                        .getDBMolecules().size() );
    }
    
    public void testGetStructures() {
        DBMolecule dBMolecule       = new DBMolecule();
        DBMolecule unusedStructure = new DBMolecule();
        IDBMoleculeDao dBMoleculeDao = (IDBMoleculeDao) applicationContext.getBean("dBMoleculeDao");
        dBMoleculeDao.insert( dBMolecule );
        dBMoleculeDao.insert( unusedStructure );
        
        object1.addDBMolecule( dBMolecule );
        dao.update( object1 );
        Annotation loaded = dao.getById( object1.getId() );
        assertEquals( 1, loaded.getDBMolecules().size() );
        assertEquals( dBMolecule, object1.getDBMolecules().get( 0 ) );
    }
}
