/*******************************************************************************
 * Copyright (c) 2005-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Kuhn <shk3@users.sf.net> - original implementation
 *     Carl <carl_marak@users.sf.net>  - converted into table
 *     Ola Spjuth                      - minor fixes
 *     Egon Willighagen                - made into a SWT widget
 *     Arvid Berg                      - rewrite of rendering
 *******************************************************************************/
package net.bioclipse.cdk.jchempaint.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.IJava2DRenderer;
import org.openscience.cdk.renderer.RendererModel;

/**
 * SWT widget that views molecules using CDK's JChemPaint viewing engine.
 */
public class JChemPaintSWTWidget extends Canvas {
    
    private SWTRenderer renderer;
    private IAtomContainer molecule;
    
    protected boolean generated = false;
    private final static int compactSize = 200;
    boolean notSet = true;
    /**
     * The constructor.
     */
    public JChemPaintSWTWidget(Composite parent, int style) {
        super(parent, style);
        
        this.setBackground( getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
        
        renderer = new SWTRenderer(new RendererModel());
        Dimension screenSize = new Dimension(this.getSize().x, this.getSize().y);

        renderer.getRenderer2DModel().setDrawNumbers(true);
        renderer.getRenderer2DModel().setBondDistance( 2 );
        setCompactedNess(screenSize);
        renderer.getRenderer2DModel().setBondWidth(10);
        renderer.getRenderer2DModel().setForeColor(Color.BLACK);
        renderer.getRenderer2DModel().setBackColor( Color.WHITE );
        renderer.getRenderer2DModel().setHoverOverColor( Color.LIGHT_GRAY );
        
        
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent event) {
                JChemPaintSWTWidget.this.widgetDisposed(event);
            }
        });
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event) {
                JChemPaintSWTWidget.this.paintControl(event);
            }
        });
        addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent event) {
                JChemPaintSWTWidget.this.controlResized(event);
            }
        });
    }

    public void setInput(IAtomContainer molecule) throws IllegalArgumentException {
        if (!GeometryTools.has2DCoordinates(molecule)) {
            //throw new IllegalArgumentException("The AtomContainer does not contain 2D coordinates.");
            this.molecule = null;
        }
        this.molecule = molecule;
    }
    
    public RendererModel getRendererModel() {
        return renderer.getRenderer2DModel();
    }
    
    public IJava2DRenderer getRenderer(){
    	return renderer;
    }
    
    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        return new Point(200, 200);
    }
    
    private void widgetDisposed(DisposeEvent event) {
        renderer.dispose();
        molecule = null;
        renderer = null;
    }
    
    private void controlResized(ControlEvent event) {
    	int xsize = this.getSize().x;
        int ysize = this.getSize().y;
        Dimension newDimensions=new Dimension(xsize,ysize);
        if(molecule != null)
            updateOnReize(newDimensions);
    	setCompactedNess(newDimensions);
    }
    protected void updateOnReize(Dimension newSize){
        if(newSize.getWidth() == 0 || newSize.getHeight() == 0 ) {
            notSet = true;
            return;
        }  
        GeometryTools.translateAllPositive(molecule);
        GeometryTools.scaleMolecule(molecule, newSize, 0.8);          
        GeometryTools.center(molecule, newSize);
//            GeometryTools.translateAllPositive(molecule, coordinates);
//            GeometryTools.scaleMolecule(molecule, oldDimensions, 0.8, coordinates);          
//            GeometryTools.center(molecule, oldDimensions, coordinates);

        
        //renderer.getRenderer2DModel().setBackgroundDimension(newSize);
//            Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, oldDimensions.getWidth(), oldDimensions.getHeight());
//            renderer.paintMolecule(
//                molecule, 
//                (Graphics2D)graphics,
//                rect
//            );
        notSet = false;
    }
    private void paintControl(PaintEvent event) {
        if(notSet)
            updateOnReize( new Dimension(this.getSize().x,this.getSize().y) );
      int y = 0;
      
      if(generated ){
          String text = "Generated from 3D coordinates";
          // FIXME : dispose font
          event.gc.setFont( new Font(event.gc.getDevice(),"Arial",34,SWT.NORMAL) );
          Point p = event.gc.textExtent( text );
         
          y = p.y;
       
          event.gc.setForeground( new org.eclipse.swt.graphics.Color( 
                                            event.gc.getDevice(),220,220,255) );
          event.gc.drawText( text,0,0);
      }          
    	renderer.paintMolecule(molecule,event.gc,
    	                       new Rectangle2D.Double(
    	                                              0,
    	                                              y,
    	                                              this.getSize().x,
    	                                              this.getSize().y-y));
    }

	protected void setCompactedNess(Dimension dimensions) {
        if (dimensions.height < compactSize ||
            dimensions.width < compactSize) {
            renderer.getRenderer2DModel().setIsCompact(true);
        } else {
            renderer.getRenderer2DModel().setIsCompact(false);
        }
	}
}