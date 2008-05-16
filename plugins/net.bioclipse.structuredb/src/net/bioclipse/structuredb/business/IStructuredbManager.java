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
package net.bioclipse.structuredb.business;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.structuredb.domain.Folder;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;

/**
 * @author jonalv
 */
@PublishedClass ("Handles structure databases")
public interface IStructuredbManager extends IBioclipseManager {

    /**
     * Creates a local database instance.
     *
     * @param databaseName the name of the created database
     * @throws IllegalArgumentException if a database with the given
     *                                  name already exists
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Creates a local structure database " +
                                        "with the given name if no such " +
                                        "database already exists." )
    public void createLocalInstance( String databaseName )
        throws IllegalArgumentException;

    /**
     * Removes a local database instance with a given name if such a database
     * exists, otherwise does nothing.
     *
     * @param databaseName name of the database instance to be deleted
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Removes a local database with the " +
                                       "given name if such exists, otherwise " +
                                       "does nothing" )
    public void removeLocalInstance( String databaseName );

    /**
     * Retrieves all Structures with a given name from a database with a
     * given name.
     *
     * @param databaseName
     * @param structureName
     * @return
     */
    @PublishedMethod ( params = "String databaseName, String name",
                       methodSummary = "Fetches all structures by a given " +
                                       "name from a database with a given name")
    public List<Structure> allStructuresByName( String databaseName,
                                                String structureName );

    /**
     * Retrieves a folder with a given name from a database with a given name.
     *
     * @param databaseName
     * @param folderName
     * @return a folder
     */
    @PublishedMethod ( params = "String databaseName, String folderName",
                       methodSummary = "Fetches a folder by a given name" +
                                       "from a database with a given name" )
    public Folder folderByName( String databaseName,
                                String folderName );

    /**
     * Retrieves a user with a given username from a database with a given name.
     *
     * @param databaseName
     * @param username
     * @return
     */
    @PublishedMethod ( params = "String databaseName, String username",
                       methodSummary = "Fetches a user with a given username " +
                                       "from a database with a given name")
    public User userByName( String databaseName,
                            String username );

    /**
     * Creates a structure with the given name from the given cdkmolecule and
     * persists it in the database with the given name
     *
     * @param databaseName
     * @param moleculeName
     * @param cdkMolecule
     * @return the structure
     * @throws BioclipseException
     */
    @PublishedMethod ( params = "String databaseName, String moleculeName, " +
                                "ICDKMolecule cdkMolecule",
                       methodSummary = "Creates a structure with the given " +
                                       "name from the given cdkmolecule and " +
                                       "saves it in the database with the " +
                                       "given name" )
    public Structure createStructure( String databaseName,
                                      String moleculeName,
                                      ICDKMolecule cdkMolecule )
                                      throws BioclipseException;

    /**
     * Creates a folder with the given name and persists it in the database
     * with the given name.
     *
     * @param databaseName
     * @param folderName
     * @return the folder
     * @throws IllegalArgumentException
     */
    @PublishedMethod ( params = "String databaseName, String folderName",
                       methodSummary = "Creates a folder with the given name " +
                                       "and saves it in the database with " +
                                       "the given name" )
    public Folder createFolder( String databaseName,
                                String folderName )
                                throws IllegalArgumentException;

    /**
     * Creates a user with the given username, password and sudoer flag and
     * persists it in the database with the given name.
     *
     * @param databaseName
     * @param username
     * @param password
     * @param sudoer
     * @return the user
     * @throws IllegalArgumentException
     */
    @PublishedMethod ( params = "String databaseName, String username, " +
                                "String password, boolean administrator",
                       methodSummary = "Creates a user with the given " +
                                       "username and password and with " +
                                       "administrator rights if that " +
                                       "variable is true")
    public User createUser( String databaseName,
                            String username,
                            String password,
                            boolean sudoer ) throws IllegalArgumentException;

    /**
     * Retrieves all structures from a database with a given name.
     *
     * @param databaseName
     * @return
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Fetches all structures from a " +
                                       "database with a given name")
    public List<Structure> allStructures( String databaseName );

    /**
     * Retrieves all folders from a database with a given name.
     *
     * @param databaseName
     * @return
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Fetches all folders from a database " +
                                       "with a given name")
    public List<Folder> allFolders( String databaseName );

    /**
     * Retrieves all users from a database with a given name.
     *
     * @param databaseName
     * @return
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Fetches all users from a database " +
                                       "with a given name")
    public List<User> allUsers( String databaseName );

    /**
     * Persists all structures in a sdf file in the specified database in
     * a folder named after the file.
     *
     * @param path
     * @throws BioclipseException
     */
    @PublishedMethod ( params = "String databaseName, String filePath",
                       methodSummary = "Saves all structures in a given sdf " +
                                       "file in the database with the given " +
                                       "name. The strucutres are stored in " +
                                       "library named after the name of the " +
                                       "sdf file")
    public void addStructuresFromSDF(String databaseName, String filePath) 
                throws BioclipseException;
    
    /**
     * Persists all structures in a sdf file in the specified database in
     * a folder named after the file with a progressmonitor
     * 
     * @param databaseName
     * @param filePath
     * @param monitor
     * @throws BioclipseException
     */
    public void addStructuresFromSDF( String databaseName, 
                                      String filePath, 
                                      IProgressMonitor monitor ) 
                throws BioclipseException;
    
    /**
     * @return a list of names of the names of all databases
     */
    @PublishedMethod ( methodSummary = "Returns a list with the names of all " +
    		                               "structuredb database instances." )
    public List<String> listDatabaseNames();

    /**
     * Return a list of all structures in the database identified by the given 
     * database name that might be a substructure of the given molecule. 
     * 
     * @param databaseName
     * @param molecule
     * @return
     * @throws BioclipseException 
     */
    @PublishedMethod (params = "String databaseName, ICDKMolecule molecule",
                      methodSummary = "Lists all structures in the database " +
                      		            "identified by the given database name " +
                      		            "that might be a substructure of the " +
                      		            "given molecule")
    public List<Structure> allStructureFingerprintSearch(String databaseName,
                                                         ICDKMolecule molecule) 
                           throws BioclipseException;
}
