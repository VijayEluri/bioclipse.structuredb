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
package net.bioclipse.structuredb.internalbusiness;

import java.util.List;

import org.openscience.cdk.AtomContainer;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.structuredb.domain.Library;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;

public class StructuredbInstanceManager 
	   extends AbstractStructuredbInstanceManager 
	   implements IStructuredbInstanceManager {

	public Library createLibrary(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public Structure createStructure(String name, ICDKMolecule cdkMolecule) {
		// TODO Auto-generated method stub
		return null;
	}

	public Structure createStructure(String name, AtomContainer atomContainer) {
		// TODO Auto-generated method stub
		return null;
	}

	public User createUser(String username, String password, boolean sudoer) {
		// TODO Auto-generated method stub
		return null;
	}

	public void delete(Library library) {
		// TODO Auto-generated method stub
		
	}

	public void delete(User user) {
		// TODO Auto-generated method stub
		
	}

	public void delete(Structure structure) {
		// TODO Auto-generated method stub
		
	}

	public List<Library> retrieveAllLibraries() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Structure> retrieveAllStructures() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> retrieveAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	public Library retrieveLibraryByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Structure> retrieveStructureByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public User retrieveUserByName(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public void update(Library library) {
		// TODO Auto-generated method stub
		
	}

	public void update(User user) {
		// TODO Auto-generated method stub
		
	}

	public void update(Structure structure) {
		// TODO Auto-generated method stub
		
	}

}
