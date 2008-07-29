/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.structuredb.persistency.dao;

import java.util.Iterator;
import java.util.List;

import net.bioclipse.structuredb.domain.Structure;

/**
 * Definition of the methods shared by all daos
 * 
 * @author jonalv
 *
 * @param <T> The domain object the dao should work with
 */
public interface IGenericDao<T> {
    
    /** persist the object using INSERT statement*/
    public void insert(T instance);
    
    /** saves changes to the object using UPDATE statement */
    public void update(T instance);

    /** load a persisted object identified by the id*/
    public T getById(String id);

    /** removes an object identified by the id */
    public void delete(String id);
    
    /** loads all instances of the dao's type */
    public List<T> getAll();
}
