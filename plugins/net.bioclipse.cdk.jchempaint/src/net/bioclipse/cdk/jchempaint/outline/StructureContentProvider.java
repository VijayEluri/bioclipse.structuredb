/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth
 *
 ******************************************************************************/
package net.bioclipse.cdk.jchempaint.outline;

import java.io.IOException;
import java.util.Iterator;

import net.bioclipse.cdk.domain.CDKChemObject;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.PeriodicTableElement;
import org.openscience.cdk.config.ElementPTFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMoleculeSet;

public class StructureContentProvider implements ITreeContentProvider {

    public static class CDKAtomChemObject extends CDKChemObject<IAtom> {

        public CDKAtomChemObject(String name, IAtom chemobj) {
            super( name, chemobj );
        }

        public CDKAtomChemObject(IAtom chemObject) {
            super(chemObject);
        }

    }

    public static class CDKBondChemObject extends CDKChemObject<IBond> {

        public CDKBondChemObject(String name, IBond chemobj) {
            super( name, chemobj );
        }

        public CDKBondChemObject(IBond chemObject) {
            super(chemObject);
        }
    }
    //Use logging
    private static final Logger logger = Logger.getLogger(StructureContentProvider.class);

    public StructureContentProvider() {}

    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof Container) {
            Container container=(Container)parentElement;
            if (container.getChildren()!=null
                    && container.getChildren().size()>0)
                return container.getChildren().toArray(new CDKChemObject[0]);
            else
                return new Object[0];
        }
        else if (parentElement instanceof CDKChemObject) {

            CDKChemObject<?> chemobj=(CDKChemObject<?>)parentElement;

            if (!(chemobj.getChemobj() instanceof IAtomContainer)) {
                return new Object[0];
            }
            IAtomContainer ac = (IAtomContainer) chemobj.getChemobj();

            Container atoms=new Container("Atoms");
            for (IAtom atom:ac.atoms()){
                atoms.addChild( createCDKChemObject( atom ) );
            }

            Container bonds=new Container("Bonds");
            for (IBond bond:ac.bonds()){
                bonds.addChild( createCDKChemObject( bond ) );
            }

            Object[] retobj=new Object[2];
            retobj[0]=atoms;
            retobj[1]=bonds;

            return retobj;
        }

        return new Object[0];
    }

    public static CDKChemObject<IAtom> createCDKChemObject( IAtom atom ) {
        String symbol = atom.getSymbol();
        ElementPTFactory efac;
        try {
            efac = ElementPTFactory.getInstance();
        } catch ( IOException e) {
            logger.error("Error while opening element info file", e);
            return new CDKAtomChemObject("unknown" + " (" + symbol + ")", atom);
        }
        PeriodicTableElement elem = efac.getElement(symbol);
        if (elem == null) {
            return new CDKAtomChemObject("unknown" + " (" + symbol + ")", atom);
        }
        String name = efac.getName(elem);
        if (name == null) name = "unknown";
        CDKChemObject<IAtom> co
          = new CDKAtomChemObject(name + " (" + symbol + ")", atom);
        return co;
    }

    public static CDKChemObject<IBond> createCDKChemObject( IBond bond ) {

        StringBuilder sb = new StringBuilder();
        char separator
          = bond.getOrder() == CDKConstants.BONDORDER_DOUBLE   ? '='
          : bond.getOrder() == CDKConstants.BONDORDER_TRIPLE   ? '#'
          : bond.getFlag(CDKConstants.ISAROMATIC) ? '~' : '-';
        for (Iterator<IAtom> it=bond.atoms().iterator(); it.hasNext();) {
            sb.append(it.next().getSymbol());
            if (it.hasNext()) {
                sb.append(separator);
            }
        }
        sb.append(   bond.getOrder() == CDKConstants.BONDORDER_DOUBLE
                       ? " (double)"
                   : bond.getOrder() == CDKConstants.BONDORDER_TRIPLE
                       ? " (triple)"
                   : bond.getFlag(CDKConstants.ISAROMATIC)
                       ? " (aromatic)"
                       : "" );
        CDKChemObject<IBond> co=new CDKBondChemObject(sb.toString(), bond);
        return co;
    }

    public Object getParent(Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasChildren(Object element) {
        return (getChildren(element).length > 0);
    }

    public Object[] getElements(Object inputElement) {

        if (inputElement instanceof IChemModel) {
            IChemModel model = (IChemModel) inputElement;

            IMoleculeSet ms=model.getMoleculeSet();
            if (ms==null || ms.getAtomContainerCount()<=0)
            {
                logger.debug("No AtomContainers in ChemModel.");
                return new Object[0];
            }

            CDKChemObject<?>[] acs=new CDKChemObject[ms.getAtomContainerCount()];
            for (int i=0; i<ms.getAtomContainerCount(); i++){
                acs[i]=new CDKChemObject<IAtomContainer>("AC_" + i, ms.getAtomContainer(i));
            }

            if (acs.length>1)
                return acs;
            else if (acs.length==1){
                return getChildren(acs[0]);
            }

        }

        return new IChemObject[0];
    }

    public void dispose() {
        // TODO Auto-generated method stub

    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // do nothing
    }

}
