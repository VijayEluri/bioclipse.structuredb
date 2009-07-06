/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     Egon Willighagen
 ******************************************************************************/
package net.bioclipse.cdk.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.FormatFactory;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.cml.CMLCoreModule;
import org.openscience.cdk.io.cml.CMLStack;
import org.openscience.cdk.io.cml.MDMoleculeConvention;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.IChemFormatMatcher;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.formats.MDLV3000Format;
import org.openscience.cdk.io.formats.PDBFormat;
import org.openscience.cdk.io.formats.SDFFormat;
import org.openscience.cdk.io.listener.PropertiesListener;

public class CDKManagerHelper {

    /**
     * Register all formats that we support for reading in Bioclipse.
     * 
     * @param fac
     */
    public static void registerSupportedFormats(ReaderFactory fac) {
        IResourceFormat[] supportedFormats = {
            PDBFormat.getInstance(),
            SDFFormat.getInstance(),
            CMLFormat.getInstance(),
            MDLV2000Format.getInstance(),
            MDLV3000Format.getInstance()
        };
        for (IResourceFormat format : supportedFormats) {
            if (!fac.getFormats().contains(format)) fac.registerFormat((IChemFormatMatcher)format);
        }
    }
    
    /**
     * Register all formats known to the CDK.
     */
    public static void registerAllFormats(FormatFactory fac) {
        try {
            InputStream iStream = org.openscience.cdk.ioformats.Activator.class.getResourceAsStream("/io-formats.set");
            BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
            int formatCount = 0;
            while (reader.ready()) {
                // load them one by one
                String formatName = reader.readLine();
                formatCount++;
                try {
                    Class formatClass = org.openscience.cdk.io.Activator.class.getClassLoader().loadClass(formatName);
                    Method getinstanceMethod = formatClass.getMethod("getInstance", new Class[0]);
                    IChemFormatMatcher format = (IChemFormatMatcher)getinstanceMethod.invoke(null, new Object[0]);
                    fac.registerFormat(format);
//                    System.out.println("Loaded IO format: " + format.getClass().getName());
                } catch (ClassNotFoundException exception) {
//                    System.out.println("Could not find this ChemObjectReader: "+ formatName);
                } catch (Exception exception) {
//                    System.out.println("Could not load this ChemObjectReader: "+ formatName);
                }
            }
        } catch ( IOException e ) {
            System.out.println("Error loading all formats");
        }
    }

    public static void customizeReading(ISimpleChemObjectReader reader) {
        System.out.println("customingIO, reader found: " + reader.getClass().getName());
        System.out.println("Found # IO settings: " + reader.getIOSettings().length);
        if (reader instanceof PDBReader) {
            Properties customSettings = new Properties();
            customSettings.setProperty("DeduceBonding", "false");

            PropertiesListener listener = new PropertiesListener(customSettings);
            reader.addChemObjectIOListener(listener);
        }

        if (reader instanceof CMLReader) {
            ((CMLReader)reader).registerConvention(
                "md:mdMolecule",
                new MDMoleculeConvention((IChemFile)null)
            );
            System.out.println("****** CmlReader, registered MDMoleculeConvention");
            
            ((CMLReader)reader).registerConvention(
                "bioclipse:atomType",
                new CMLCoreModule((IChemFile)null) {
                    
                    List<String> atomTypes = new ArrayList<String>();

                    @Override
                    protected void newAtomData() {
                        super.newAtomData();
                        atomTypes = new ArrayList<String>();
                    };
                    
                    @Override
                    protected void storeAtomData() {
                        super.storeAtomData();

                        boolean hasAtomType = false;
                        if (atomTypes.size() == atomCounter) {
                            hasAtomType = true;
                        } else {
                            logger.debug("No atom types: " + elid.size(),
                                         " != " + atomCounter);
                        }
                        if (hasAtomType) {
                            for (int i = 0; i < atomCounter; i++) {
                                currentAtom = currentMolecule.getAtom(i);
                                currentAtom.setAtomTypeName(atomTypes.get(i));
                            }
                        }                        
                    }
                    
                    @Override
                    public void endElement(CMLStack xpath, String uri,
                                            String name, String raw) {
                        if (xpath.endsWith("atom", "atomType")) {
                            while ((atomTypes.size()+1) < atomCounter)
                                atomTypes.add(null);
                            atomTypes.add(currentChars);
                        } else {
                            super.endElement( xpath, uri, name, raw );
                        }
                    }
                }
            );
        }

    }


}
