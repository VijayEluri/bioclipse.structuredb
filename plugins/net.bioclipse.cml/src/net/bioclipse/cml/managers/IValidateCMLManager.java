/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.bioclipse.cml.managers;

import java.io.IOException;

import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.TestMethods;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;

import org.eclipse.core.resources.IFile;
import org.xmlcml.cml.base.CMLElement;

@TestClasses(
    "net.bioclipse.cml.tests.APITest," +
    "net.bioclipse.cml.tests.JavaValidateCMLManagerPluginTest"
)
public interface IValidateCMLManager extends IBioclipseManager {

    @Recorded
    public void validate( IFile input ) throws IOException;

    @Recorded
    @PublishedMethod(params = "String filename",
         methodSummary = "Checks if the file indicated by filename in " +
         		"workspace is valid CML")
    @TestMethods("testValidate_String")
    public String validate( String filename ) throws IOException,
                                             BioclipseException;

    @PublishedMethod(
        params = "String cmlString",
        methodSummary = "Converts a CML String into a CMLElement"
    )
    @TestMethods("testFromString")
    public CMLElement fromString(String cmlString) throws BioclipseException;

    @PublishedMethod(
                     params = "IFile input",
                     methodSummary = "Parses a file into a CMLElement"
    )
    public CMLElement parseFile(IFile input) throws BioclipseException;

    @PublishedMethod(
                     params = "String path",
                     methodSummary = "Parses a file into a CMLElement"
    )
    public CMLElement parseFile(String path) throws BioclipseException;

}
