/* *****************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.domain;

import java.util.ArrayList;
import java.util.List;


/**
 * @author jonalv
 *
 */
public class TextProperty extends Property {

    private List<TextAnnotation> annotations;

    public TextProperty() {
        super();
        annotations = new ArrayList<TextAnnotation>();
    }

    public TextProperty(String name) {
        super( name );
        annotations = new ArrayList<TextAnnotation>();
    }
    
    public TextProperty(TextProperty textProperty) {
        super( textProperty );
    }

    public boolean hasValuesEqualTo( BaseObject obj ) {
        
        if( !super.hasValuesEqualTo(obj) ) {
            return false;
        }
        if( !(obj instanceof Property) ) {
            return false;
        }
        return true;
    }

    public List<TextAnnotation> getAnnotations() {
        return annotations;
    }

    public void addAnnotation( TextAnnotation annotation ) {
        annotations.add( annotation );
        if ( annotation.getProperty() != this ) {
            annotation.setProperty( this );
        }
    }
    
    public String toString() {
        return "{TextProperty : name = " + getName() + "}"; 
    }
}
