/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.persistency.dao;

import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.ChoiceAnnotation;


/**
 * @author jonalv
 *
 */
public class ChoiceAnnotationDao 
       extends GenericDao<ChoiceAnnotation> 
       implements IChoiceAnnotationDao {

    @Override
    public void insert(ChoiceAnnotation annotation) {
        getSqlMapClientTemplate().update( "BaseObject.insert", 
                                          annotation );
        getSqlMapClientTemplate().update( "Annotation.insert",
                                          annotation );
        getSqlMapClientTemplate().update( "ChoiceAnnotation.insert",
                                          annotation );
    }
    
    @Override
    public void update(ChoiceAnnotation annotation) {
        getSqlMapClientTemplate().update( "BaseObject.update", 
                                          annotation );
        getSqlMapClientTemplate().update( "Annotation.update", 
                                          annotation );
        getSqlMapClientTemplate().update( "ChoiceAnnotation.update", 
                                          annotation );
    }    
    
    public ChoiceAnnotationDao() {
        super( ChoiceAnnotation.class );
    }
}
