/* $Revision: $ $Author:  $ $Date: $
 *
 * Copyright (C) 2007  Gilleain Torrance
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.controller;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;

/**
 * Adds an atom on the given location on mouseclick
 * 
 * @author Gilleain Torrance
 * @cdk.module control
 */
public class AddRingModule extends ControllerModuleAdapter {

	public AddRingModule(IChemModelRelay chemModelRelay) {
		super(chemModelRelay);
	}

	public void mouseClickedDown(Point2d worldCoord) {

		IAtom closestAtom = chemModelRelay.getClosestAtom(worldCoord);
		int ringSize = chemModelRelay.getController2DModel().getRingSize();
		if (closestAtom == null) {
		    chemModelRelay.addRing(ringSize);
		} else {
			chemModelRelay.addRing(closestAtom, ringSize);
		}
		chemModelRelay.updateView();
	}

	public void setChemModelRelay(IChemModelRelay relay) {
		this.chemModelRelay = relay;
	}

}