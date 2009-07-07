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
package net.bioclipse.cml.tests;

import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cml.managers.IValidateCMLManager;
import net.bioclipse.core.ResourcePathTransformer;
import nu.xom.Elements;
import nu.xom.Nodes;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.IChemFormat;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLList;

public class AbstractValidateCMLManagerPluginTest {

    protected static IValidateCMLManager cml;
    protected static ICDKManager cdk;

    @Test
    public void testValidate_String() throws Exception {
        ICDKMolecule mol = cdk.fromSMILES("CNC");
        cdk.saveMolecule(mol, "/Virtual/testValidate.cml",
                (IChemFormat)CMLFormat.getInstance(), true);
        cml.validate("/Virtual/testValidate.cml");
    }
    
    @Test public void testFromString() throws Exception {
        CMLElement cmlElem = cml.fromString(
            "<molecule xmlns=\"http://www.xmlcml.org/schema\"/>"
        );
        Assert.assertTrue(cmlElem.getClass().getName().contains("CMLMolecule"));
    }
    
    @Test public void testParseFile_IFile() throws Exception{
        ICDKMolecule mol = cdk.fromSMILES("CNC");
        cdk.saveMolecule(mol, "/Virtual/testparse.cml",
                (IChemFormat)CMLFormat.getInstance(), true);
        Object o = cml.parseFile( ResourcePathTransformer.getInstance().transform("/Virtual/testparse.cml"));
        Assert.assertTrue(o instanceof CMLList );
    }

    @Test public void testParseFile_String() throws Exception{
        ICDKMolecule mol = cdk.fromSMILES("CNC");
        cdk.saveMolecule(mol, "/Virtual/testparse.cml",
                (IChemFormat)CMLFormat.getInstance(), true);
        Object o = cml.parseFile( "/Virtual/testparse.cml");
        Assert.assertTrue(o instanceof CMLList );
    }
}
