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
package net.bioclipse.structuredb.domain;

import static org.junit.Assert.*;

import org.junit.Test;


public class UserTest {

    @Test
    public void testHasValuesEqualTo() {
        
        User user1 = new User( "username", "password", true);
        User user2 = new User(user1);
        User user3 = new User( "another username", "password", false);
        
        assertTrue(  user1.hasValuesEqualTo(user2) );
        assertFalse( user1.hasValuesEqualTo(user3) );
    }
    
    @Test
    public void testDoubleReferences() {
        DBMolecule dBMolecule = new DBMolecule();
        
        User user = new User();
        
        user.addCreatedBaseObject( dBMolecule );
    
        assertTrue( user.getCreatedBaseObjects().contains(dBMolecule) );
        assertTrue( dBMolecule.getCreator() == user );
    }
}
