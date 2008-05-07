/*
 *  $RCSfile$
 *  $Author: miguelrojasch $
 *  $Date: 2006-10-02 12:52:54 +0200 (Mon, 02 Oct 2006) $
 *  $Revision: 1979 $
 *
 *  Copyright (C) 1997-2005  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package net.bioclipse.cdk10.jchempaint.ui.editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.eclipse.swt.widgets.Display;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulator;

/**
 *  This class implements an editing JChemPaintPanel.
 *
 *@author        steinbeck
 *@cdk.created       16. Februar 2005
 *@cdk.module    jchempaint
 *@see           JChemPaintViewerOnlyPanel
 */
public class DrawingPanel extends JPanel
{
    private JChemPaintModel jchemPaintModel;
    private Renderer2D r2d;
    private Display display;

    /**
     *  Constructor for the DrawingPanel object
     * @param display
     *
     */
    public DrawingPanel(Display display)
    {
        super();
        this.display = display;
    }

    /**
     * Set the JChemPaintModel
     *
     * @param model  the JChemPaintModel
     */
    public void setJChemPaintModel(JChemPaintModel model)
    {
        this.jchemPaintModel = model;
        r2d = new Renderer2D(jchemPaintModel.getRendererModel());
        r2d.setRenderer2DModel(jchemPaintModel.getRendererModel());
    }

    public void updateRingSetInRenderer(){
        r2d.redoSSSR(AtomContainerSetManipulator.getAllAtomContainers(jchemPaintModel.getChemModel().getMoleculeSet()));
    }
    /**
     *  Draws bonds, atoms; takes care of highlighting.
     *
     *@param  g  the Graphics object to paint to
     */
    public void paint(Graphics g)
    {
        super.paint(g);
        this.setBackground(jchemPaintModel.getRendererModel().getBackColor());
        if (jchemPaintModel == null) return;
        Graphics2D g2d = (Graphics2D) g;
        r2d.paintChemModel(jchemPaintModel.getChemModel(), g2d);
    }


    /**
     *  Gets the preferredSize attribute of the JChemPaintPanel object
     *
     *@return    The preferredSize value
     */
    public Dimension getPreferredSize()
    {
        if (jchemPaintModel == null) return new Dimension(794,1123);
        return jchemPaintModel.getRendererModel().getBackgroundDimension();
    }

    public Display getDisplay() {
        return display;
    }

    public void setAtomColorer(IAtomColorer colorer){
        r2d.getRenderer2DModel().setAtomColorer( colorer );
    }


    public Renderer2D getRenderer2D() {
        return r2d;
    }




}

