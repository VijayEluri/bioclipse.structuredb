/* $Revision: 7636 $ $Author: nielsout $ $Date: 2007-09-02 11:46:10 +0100 (su, 02 sep 2007) $
 * 
 * Copyright (C) 2007  Niels Out <nielsout@users.sf.net>
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
package net.bioclipse.cdk.jchempaint.widgets;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.Transform;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.renderer.IJava2DRenderer;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;
import org.openscience.cdk.validate.ProblemMarker;

/**
 * A Java2D based 2D renderer for IChemObjects.
 * 
 * @author      nielsout, Arvid Berg
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.module  render
 */
public class SWTRenderer implements IJava2DRenderer {

	private Renderer2DModel rendererModel;
	private AffineTransform affine;

	protected LoggingTool logger;
	IsotopeFactory isotopeFactory;
	float fscale = 16;
	
	Map<java.awt.Color,Color> cleanUp= new HashMap<java.awt.Color,Color>();

	public SWTRenderer(Renderer2DModel model) {
		this.rendererModel = model;
		logger = new LoggingTool(this);
	}
	
	public SWTRenderer(Renderer2DModel model, float fscale){
	    this(model);
	    this.fscale = fscale;
	}
	
	public void paintChemModel(IChemModel model, GC graphics) {
		// TODO Auto-generated method stub

	}

	public void paintMoleculeSet(IMoleculeSet moleculeSet, GC graphics) {
		// TODO Auto-generated method stub

	}

	public void paintReaction(IReaction reaction, GC graphics) {
		// TODO Auto-generated method stub

	}

	public void paintReactionSet(IReactionSet reactionSet, GC graphics) {
		// TODO Auto-generated method stub

	}
	/**
	 * Paint the given Molecule on the graphics object at location specified by bounds.
	 * 
	 *@param  atomCon The molecule to be drawn
	 *@param  graphics GC to draw on
	 *@param  bounds
	 */
	public void paintMolecule(IAtomContainer atomCon, GC graphics) {
		
		
		if (affine == null) {
			//System.out.println("paintMolecule should be called first time with the bounds of the graphical object..");
			logger.warn("Cannot paintMolecule without transform Matrix");
			return;			
		}
		double[] m=new double[6];
		affine.getMatrix(m);
		Transform tf=new Transform(graphics.getDevice(),
				(float)m[0],	(float)m[2],
				(float)m[1],	(float)m[3],
				(float)m[4],	(float)m[5] 	       );
//		graphics.transform(affine);
		graphics.setTransform(tf);
		createFont(graphics,.4f,true );
//		System.out.println("transform matrix:" + graphics.getTransform());
		
		if (rendererModel.getUseAntiAliasing()) {
		    graphics.setAdvanced( true );
		    graphics.setAntialias(SWT.ON );
		    graphics.setInterpolation(SWT.HIGH);
//			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		else {
		    graphics.setAdvanced( true );
		    graphics.setAntialias( SWT.OFF );
		}
		// set basic shape form for bonds
		
		//Color bondColor=new Color(graphics.getDevice(),0,0,0);
		//graphics.setForeground(bondColor);
		graphics.setLineAttributes(new LineAttributes((float) (rendererModel.getBondWidth()/rendererModel.getBondLength()),
				SWT.CAP_ROUND, SWT.JOIN_ROUND));
//		graphics.setStroke(new BasicStroke(
//			(float) (rendererModel.getBondWidth()/rendererModel.getBondLength()),
//			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
//		);

		IRingSet ringSet = getRingSet(atomCon);
	
		// draw bonds
		paintBonds(atomCon, ringSet, graphics);

		// draw atom symbols
		paintAtoms(atomCon, graphics);
//		System.out.println("transform matrix:" + graphics.getTransform());
		
	}
	/**
	 * Paint the given Molecule on the graphics object at location specified by bounds.
	 * 
	 *@param  atomCon The molecule to be drawn
	 *@param  graphics GC to draw on
	 *@param  bounds
	 */
	public void paintMolecule(IAtomContainer atomCon, GC graphics,
			Rectangle2D bounds) {
		//rendererModel.setShowAromaticity(true);
		
		// calculate the molecule boundaries via the atomContainer
		Rectangle2D molBounds = createRectangle2D(atomCon); 
		if (molBounds == null || molBounds.isEmpty()) {
			logger.debug("empty atomCon? -> no molBounds -> no drawing ");
			//System.out.println("empty atomCon? -> no molBounds -> no drawing ");
			return;
		}
		AffineTransform transformMatrix = createScaleTransform(molBounds,bounds);
		affine = transformMatrix;
		
		paintMolecule(atomCon, graphics);
		
	}
	protected IRingSet getRingSet(IAtomContainer atomContainer)
	{
	  IRingSet ringSet = atomContainer.getBuilder().newRingSet();
	  java.util.Iterator<IAtomContainer> molecules = null;
	  try
	  {
	    molecules = ConnectivityChecker.partitionIntoMolecules(atomContainer).molecules().iterator();
	  }

	  catch (Exception exception)
	  {
	    logger.warn("Could not partition molecule: ", exception.getMessage());
	    logger.debug(exception);
	    return ringSet;
	  }

	  while (molecules.hasNext())
	  {
	    SSSRFinder sssrf = new SSSRFinder((IMolecule)molecules.next());

	    ringSet.add(sssrf.findSSSR());
	  }

	  return ringSet;
	}
	/**
	 * Triggers paintAtom method for all the atoms in the given array of atoms.
	 *
	 * @param atomCon
	 * @param graphics
	 */
	public void paintAtoms(IAtomContainer atomCon, GC graphics)
	{
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			paintAtom(atomCon, atomCon.getAtom(i), graphics);
		}
	}
	/**
	 *  triggers the
	 *  paintColouredAtoms method if the atom has got a certain color and triggers
	 *  the paintAtomSymbol method if the symbol of the atom is not C
	 *  
	 * @param container
	 * @param atom
	 * @param graphics
	 */
	public void paintAtom(IAtomContainer container, IAtom atom, GC graphics)
	{
		//System.out.println("paintAtom Symbol:" + atom.getSymbol() + " atom:" + atom);
				
	

		boolean drawSymbol = false; //paint all Atoms for the time being
		boolean isRadical = (container.getConnectedSingleElectronsCount(atom) > 0);
		if (atom instanceof IPseudoAtom)
		{
			drawSymbol = false;
//			if (atom instanceof FragmentAtom) {
//				paintFragmentAtom((FragmentAtom)atom, atomBackColor, graphics,
//						alignment, isRadical);
//			} else {
			//	paintPseudoAtomLabel((IPseudoAtom) atom, atomBackColor, graphics, alignment, isRadical);
//			}
			//System.out.println("call paintPseudoAtomLabel here?");
			return;
		} else if (!atom.getSymbol().equals("C"))
		{
			/*
			 *  only show element for non-carbon atoms,
			 *  unless (see below)...
			 */
			drawSymbol = true;
		} else if (getRenderer2DModel().getKekuleStructure())
		{
			// ... unless carbon must be drawn because in Kekule mode
			drawSymbol = true;
		} else if (atom.getFormalCharge() != 0)
		{
			// ... unless carbon is charged
			drawSymbol = true;
		} else if (container.getConnectedBondsList(atom).size() < 1)
		{
			// ... unless carbon is unbonded
			drawSymbol = true;
		} else if (getRenderer2DModel().getShowEndCarbons() && (container.getConnectedBondsList(atom).size() == 1))
		{
			drawSymbol = true;
		} else if (atom.getProperty(ProblemMarker.ERROR_MARKER) != null)
		{
			// ... unless carbon is unbonded
			drawSymbol = true;
		} else if (atom.getMassNumber() != null)
		{
			try
			{
				if (atom.getMassNumber() != IsotopeFactory.getInstance(container.getBuilder()).
						getMajorIsotope(atom.getSymbol()).getMassNumber())
				{
					drawSymbol = true;
				}
			} catch (Exception exception) {
                logger.debug("Could not get an instance of IsotopeFactory");
            }

		} else if (isRadical) 
			drawSymbol = true;

		if (drawSymbol == true) {
			int alignment = GeometryTools.getBestAlignmentForLabelXY(container, atom);
			//System.out.println("alignment: " + alignment);
			paintAtomSymbol(atom, graphics, alignment, isRadical);
		}
	}
	
	private void drawTextUpsideDown(GC graphics, TextLayout layout, int x, int y){
	    
	    Rectangle bounds = layout.getBounds();
	    Rectangle doubleBounds = new Rectangle(
                                               bounds.x,
                                               bounds.y,
                                               bounds.width*4,
                                               bounds.height*4);
	    if(rendererModel.getIsCompact()) {
	        int size = Math.max( bounds.width, bounds.height );
	        graphics.setBackground( graphics.getForeground() );
	        graphics.fillOval( x, y, size,size);
	    } else {
	        ImageData alphaMask =
                    drawText( graphics.getDevice(), layout, doubleBounds );
            alphaMask = superFlip( alphaMask, true, graphics.getForeground() );
            graphics.setInterpolation( SWT.HIGH );
            Image image = new Image( graphics.getDevice(), alphaMask );
            graphics.drawImage( image, 0, 0, doubleBounds.width,
                                doubleBounds.height, x, y, bounds.width,
                                bounds.height );

            image.dispose();
	    }
	}
	
	private ImageData drawText(Device device, TextLayout layout, Rectangle rect) {
	    Image image = new Image(device,rect);
	    if(layout.getFont() == getFont( FontSize.NORMAL ))
	        layout.setFont( getFont( FontSize.DOUBLE_N ) );
	    else
	        layout.setFont( getFont( FontSize.DOUBLE_S ) );
	    GC imageGC = new GC(image);
	        imageGC.setForeground( device.getSystemColor( SWT.COLOR_WHITE ) );
	        imageGC.setBackground( device.getSystemColor( SWT.COLOR_BLACK ) );
	        imageGC.setAdvanced( true );
	        imageGC.setInterpolation(SWT.HIGH);
	        imageGC.setAntialias( SWT.OFF);
	        
	        imageGC.fillRectangle( rect);
	        layout.draw( imageGC, 0, 0 );
      imageGC.dispose();
	    ImageData imageData = image.getImageData();
	    image.dispose();
	    return imageData;
	}
	
	static ImageData superFlip(ImageData srcData, boolean vertical,Color color) {
	    
      ImageData newImageData = (ImageData) srcData.clone();
      
      for (int srcY = 0; srcY < srcData.height; srcY++) {
        for (int srcX = 0; srcX < srcData.width; srcX++) {
          int destX = 0, destY = 0;
          if (vertical) {
            destX = srcX;
            destY = srcData.height - srcY - 1;
          } else {
            destX = srcData.width - srcX - 1;
            destY = srcY;
          }          
          int red = srcData.palette.getRGB( srcData.getPixel( srcX, srcY)).red;
          newImageData.setAlpha( destX, destY, red );
          newImageData.setPixel( destX, destY, srcData.palette.getPixel(
                                                             color.getRGB() ) );          
        }
      }

      return newImageData;
    }
	
  static ImageData flip(ImageData srcData, boolean vertical) {
      int bytesPerPixel = srcData.bytesPerLine / srcData.width;
      int destBytesPerLine = srcData.width * bytesPerPixel;
      byte[] newData = new byte[srcData.data.length];
      for (int srcY = 0; srcY < srcData.height; srcY++) {
        for (int srcX = 0; srcX < srcData.width; srcX++) {
          int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
          if (vertical) {
            destX = srcX;
            destY = srcData.height - srcY - 1;
          } else {
            destX = srcData.width - srcX - 1;
            destY = srcY;
          }
          destIndex = (destY * destBytesPerLine)
              + (destX * bytesPerPixel);
          srcIndex = (srcY * srcData.bytesPerLine)
              + (srcX * bytesPerPixel);
          System.arraycopy(srcData.data, srcIndex, newData, destIndex,
              bytesPerPixel);
        }
      }
      // destBytesPerLine is used as scanlinePad to ensure that no padding is
      // required
      return new ImageData(srcData.width, srcData.height, srcData.depth,
          srcData.palette, destBytesPerLine, newData);
    }

	
	public void paintAtomSymbol(IAtom atom, GC graphics, int alignment, boolean isRadical)
	{
		Color saveColor = graphics.getForeground();
		String symbol = "";

		if (atom.getSymbol() != null) {
			symbol = atom.getSymbol();
		}
		//symbol = "Mg"; //to test if a certain symbol is spaced out right 
		
		createFont(graphics,.4f,false);
		Font fontAtom=getFont(FontSize.NORMAL);

		Font fontSmall =getFont(FontSize.SMALL); 
		
		graphics.setFont(fontAtom);

//		FontRenderContext frc = graphics.getFontRenderContext();
		TextLayout layoutAtom = new TextLayout(graphics.getDevice());
		layoutAtom.setText(symbol);
		layoutAtom.setFont(fontAtom);//, frc);
		Rectangle2D boundsAtom = toJava2DRect(layoutAtom.getBounds());
		
		//btest has to be substracted to get the text on the exact right position
		//FIXME: get right value from graphics object? (width of line? or so)
		float btest = (float) (rendererModel.getBondWidth()/rendererModel.getBondLength());
		double atomSymbolX = atom.getPoint2d().x - boundsAtom.getWidth()/2 - 0.5 * btest; 
		double atomSymbolY = atom.getPoint2d().y - boundsAtom.getHeight() /2 - boundsAtom.getY();
		double atomSymbolW = boundsAtom.getWidth();
		double atomSymbolH = boundsAtom.getHeight() - boundsAtom.getY();
		double atomSymbolDNext = (double)layoutAtom.getBounds().width;//getWidth();//distance from X,Y to 'next' character
		double marginc = (atomSymbolDNext - atomSymbolW)/2;//margin used in calculations
		double margind = marginc * 1.4;//slightly larger margin for drawing
		double atomSymbolHOffset = boundsAtom.getY();
		
		
	//	System.out.println("the marginc is now: " + marginc + " margind: " + margind);

		//bounds around Atom Symbol
		boundsAtom.setRect(boundsAtom.getX() + atomSymbolX - margind,
				boundsAtom.getY() + atomSymbolY - margind,
				boundsAtom.getWidth() + 2 * margind,
				boundsAtom.getHeight() + 2 * margind);
		
			
		Color atomColor = toSWTColor(graphics, getRenderer2DModel().getAtomColor(atom, java.awt.Color.BLACK));
		Color otherColor = toSWTColor(graphics,getRenderer2DModel().getForeColor());
		
		Color bgColor = toSWTColor(graphics,getRenderer2DModel().getBackColor());
		paintColouredAtomBackground( atom, bgColor, graphics );
		if (atom == getRenderer2DModel().getHighlightedAtom()){
			bgColor = toSWTColor(graphics,getRenderer2DModel().getHoverOverColor());
		  paintColouredAtomBackground( atom, bgColor, graphics );
		}
		
		
		
//		graphics.fill(boundsAtom);// draw atom symbol background
		
//		graphics.fillRectangle(	(int)boundsAtom.getX(), (int)boundsAtom.getY(),
//				(int)boundsAtom.getWidth(),(int) boundsAtom.getHeight());
	
		double massnumberW = 0;
		//double formalChargW = 0;
		double hydroGenW = 0;
		double hydroGenCountW = 0;
		double hydroGenCountH = 0;
		double hydroGenH = 0;;
		
		if (atom.getMassNumber() != null && isotopeFactory != null) {
			IIsotope majorIsotope = isotopeFactory.getMajorIsotope(atom.getSymbol());
			if (majorIsotope != null && atom.getMassNumber() != majorIsotope.getMassNumber())
			{
			
				graphics.setFont(fontSmall);
				String textMass = Integer.toString(atom.getMassNumber());
//				FontRenderContext frcMass = graphics.getFontRenderContext();
				TextLayout layoutMass = new TextLayout(graphics.getDevice());
				layoutMass.setFont(fontSmall);
				layoutMass.setText(textMass);
//						textMass, fontSmall, frcMass);
				
				Rectangle2D boundsMass = new Rectangle2D.Double(
						layoutMass.getBounds().x,
						layoutMass.getBounds().y,
						layoutMass.getBounds().width,
						layoutMass.getBounds().height);
//				Rectangle boundsMass=layoutMass.getBounds();
//				massnumberW = layoutMass.getAdvance();
				massnumberW=layoutMass.getWidth();
				//terrible way of getting the MassNumber on the right X location for 'every?' number
				//I would expect this to be 'screenAtomX - boundsMass.getWidth()- margin - marginSmall' but that didn't work 100% correct
				//float massNumberX = (float)(atomSymbolX - layoutMass.getAdvance() + tempWA - (float)boundsMass.getX() - marginSmall);
				double massNumberX = atomSymbolX - massnumberW;//-margin ?
				double massNumberY = atomSymbolY + boundsAtom.getHeight() - boundsMass.getHeight() / 2;

				boundsMass.setRect(boundsMass.getX() + massNumberX - margind,
						boundsMass.getY() + massNumberY - margind,
						boundsMass.getWidth() + 2 * margind,
						boundsMass.getHeight() + 2 * margind);

				//bgColor = Color.green;
				graphics.setForeground(bgColor);

//				graphics.fillRectangle((int)boundsMass.getX(),(int)boundsMass.getY(),
//						(int)boundsMass.getWidth(),(int)boundsMass.getHeight());// draw Mass number background
//				graphics.setFont(fontSmall);
				graphics.setForeground(otherColor);
//				layoutMass.draw(graphics, (float)massNumberX, (float)massNumberY);// draw Mass Number
				// FIXME : draw text
//				layoutMass.draw(graphics, (int)massNumberX, (int)massNumberY);
				drawTextUpsideDown( graphics, layoutMass,(int)massNumberX, (int)massNumberY);
				layoutMass.dispose();
			}
		}
		
		if (atom.getHydrogenCount() != null && atom.getHydrogenCount() > 0) {
			graphics.setFont(fontAtom);
			String hChar = "H";
//			FontRenderContext frcMass = graphics.getFontRenderContext();
			TextLayout layoutH = new TextLayout(graphics.getDevice());
			layoutH.setText(hChar);
			layoutH.setFont(fontAtom);
//			hChar, fontAtom, frcMass);
			Rectangle2D boundsHydro = toJava2DRect(layoutH.getBounds());
			
			hydroGenW = layoutH.getWidth();

			hydroGenH = boundsHydro.getHeight();	
			graphics.setFont(fontSmall);
			String hCount = atom.getHydrogenCount().toString();
//			frcMass = graphics.getFontRenderContext();
			TextLayout layoutHC = new TextLayout(graphics.getDevice());
					layoutHC.setText(hCount);
					layoutHC.setFont(fontSmall);//, frcMass);
			Rectangle2D boundsHydroC = toJava2DRect(layoutHC.getBounds());
			
			hydroGenCountW = layoutHC.getWidth();
			hydroGenCountH = boundsHydroC.getHeight();

			double hydroGenX = atomSymbolX;
			double hydroGenY = atomSymbolY;//'H' at same height as atom Symbol
			//TODO: add margins
			
//			System.out.println("test layoutAtom.getLeading(): " + layoutAtom.getLeading() + " layoutatom.getAscent(): " + layoutAtom.getAscent() + 
//					" layoutH.getAscent(): " + layoutH.getAscent() + " layoutH.getBaseline()" + layoutH.getBaseline() + " layoutAtom.getBaseline()" + layoutAtom.getBaseline());
			
			switch (alignment) {
				case -2: //H below atomSymbol
					hydroGenY = hydroGenY - hydroGenH + atomSymbolHOffset - 2 * marginc; break;
				case -1: //left alignment 
					hydroGenX -= (hydroGenW + Math.max(hydroGenCountW, massnumberW)); break;
				case 2: //H above atomSymbol != correct
					hydroGenY += atomSymbolH + marginc + atomSymbolHOffset + 0.5 * boundsHydroC.getHeight() - boundsHydroC.getX(); 
					//FIXME: add height of formalCharge to this
					break;
				default: //right alignment = correct		
					hydroGenX += atomSymbolDNext; break;	

			}
				
			boundsHydro.setRect(boundsHydro.getX() + hydroGenX - margind,
					boundsHydro.getY() + hydroGenY - margind,
					boundsHydro.getWidth() + 2 * margind,
					boundsHydro.getHeight() + 2 * margind);
		
			double hydroGenCX = hydroGenX + hydroGenW;
			double hydroGenCY = hydroGenY - 0.5 * boundsHydroC.getHeight();//1,2,3,etc. 'subscript'

			boundsHydroC.setRect(boundsHydroC.getX() + hydroGenCX - margind,
					boundsHydroC.getY() + hydroGenCY - margind,
					boundsHydroC.getWidth() + 2 * margind,
					boundsHydroC.getHeight() + 2 * margind);
			
			graphics.setForeground(bgColor);
			
//			graphics.fill(boundsHydro);// draw 'H' background
//			graphics.fill(boundsHydroC);// draw '1/2/3' hydrogen Count background
//			fill(graphics,boundsHydro);
//			fill(graphics,boundsHydroC);

			graphics.setForeground(otherColor);
			// FIXME : draw text
			graphics.setFont(fontAtom);
//			layoutH.draw(graphics, (int)hydroGenX, (int)hydroGenY);// draw the 'H'
			drawTextUpsideDown( graphics, layoutH, (int)hydroGenX, (int)hydroGenY );
			graphics.setFont(fontSmall);
//			layoutHC.draw(graphics, (int)hydroGenCX, (int)hydroGenCY);// draw the hydrogen Count
			drawTextUpsideDown( graphics, layoutHC, (int)hydroGenCX, (int)hydroGenCY);
//			layoutH.dispose();
//			layoutHC.dispose();
			
			
		}
			
		if (atom.getFormalCharge() != null && atom.getFormalCharge() != 0) {

			graphics.setFont(fontSmall);
			String textFormal = "";
			double marginRight = 0;//margin on the right (=to hide part of bonds)
			if (atom.getFormalCharge() != 1 && atom.getFormalCharge() != -1)
				textFormal += Integer.toString(Math.abs(atom.getFormalCharge()));
			
			if (atom.getFormalCharge() > 0) {
				textFormal += "+";
				marginRight = margind;
			}
//			FontRenderContext frcFormal = graphics.getFontRenderContext();
			
			double formalChargeX = atomSymbolX + atomSymbolDNext;
			double formalChargeY = atomSymbolY;
			double formalChargeH = 0;
			
			//System.out.println("alignment: " + alignment);

			if (alignment == 1) //right alignment
				formalChargeX += hydroGenW + hydroGenCountW;//should hydroGenCountW be included here?
			else if (alignment == 2) { //top alignment
				formalChargeY += hydroGenH + hydroGenCountH;
			}
			double formalChargeY2 = formalChargeY;

			double formalChargeW = 0;
			TextLayout layoutFormalC = layoutAtom;//have to initialize it so *something*
			if (textFormal != "") { //draw amount and optional '+'-symbol
				layoutFormalC = new TextLayout(graphics.getDevice());
						layoutFormalC.setText(textFormal);
						layoutFormalC.setFont(fontSmall);//, frcFormal);
				//TextLayout layoutBase = new TextLayout(baseString, fontSmall, frcFormal);
				Rectangle2D boundsFormalC = toJava2DRect(layoutFormalC.getBounds());
		
				formalChargeH = boundsFormalC.getHeight();
				 
				formalChargeY += boundsAtom.getHeight() - boundsFormalC.getHeight() / 2;
			
				boundsFormalC.setRect(boundsFormalC.getX() + formalChargeX - margind,
					boundsFormalC.getY() + formalChargeY - margind,
					boundsFormalC.getWidth() + 2 * margind + marginRight,
					boundsFormalC.getHeight() + 2 * margind);
		
				graphics.setForeground(bgColor);
				fill(graphics,boundsFormalC);// draw Formal Charge background
//				graphics.setFont(fontSmall);
				graphics.setForeground(otherColor);
				formalChargeW = layoutFormalC.getWidth();
			} 
			if (atom.getFormalCharge() < 0) { //draw the 'minus' symbol
				double formalChargeX2 = formalChargeX + formalChargeW + marginc;
				String textFormal2 = "_";
				TextLayout layoutFormal = new TextLayout(graphics.getDevice());
				layoutFormal.setText(textFormal2);
				layoutFormal.setFont(fontSmall);//, frcFormal);
				Rectangle2D boundsFormalC = toJava2DRect(layoutFormal.getBounds());
				double posiY = Math.max(boundsFormalC.getHeight(), formalChargeH) + boundsFormalC.getY();
				formalChargeY2 += boundsAtom.getHeight() + posiY / 2;//
			
				boundsFormalC.setRect(boundsFormalC.getX() + formalChargeX2 - margind,
					boundsFormalC.getY() + formalChargeY2 - 5 * margind,
					boundsFormalC.getWidth() + 2 * margind,
					boundsFormalC.getHeight() + 8 * margind);

				graphics.setForeground(bgColor);
				fill(graphics,boundsFormalC);// draw Formal Charge background
//				graphics.setFont(fontSmall);
				graphics.setForeground(otherColor);
				// FIXME : draw text
//				layoutFormal.draw(graphics, (int)formalChargeX2, (int)formalChargeY2);// draw Formal Charge
				drawTextUpsideDown(graphics, layoutFormal, (int)formalChargeX2, (int)formalChargeY2);
//				layoutFormal.dispose();
			}
			if (textFormal != "") {
			    // FIXME : draw text
//				layoutFormalC.draw(graphics, (int)formalChargeX, (int)formalChargeY);// draw Formal Charge
				drawTextUpsideDown(graphics,layoutFormalC, (int)formalChargeX, (int)formalChargeY);
			}
//			layoutFormalC.dispose();
		}

		
		graphics.setFont(fontAtom);
		graphics.setForeground(atomColor);
		
		// FIXME : draw text
		//layoutAtom.draw(graphics, (int)atomSymbolX, (int)atomSymbolY);// draw atom symbol 4
		drawTextUpsideDown(graphics, layoutAtom, (int)atomSymbolX, (int)atomSymbolY);
		
	/*	String test = "MgH";
		frc = graphics.getFontRenderContext();
		TextLayout layoutTest = new TextLayout(test, fontAtom, frc);
		double CHwidth = layoutTest.getAdvance();
		double tempv = CHwidth - atomSymbolW - hydroGenW;
		
		layoutTest.draw(graphics, (float)atomSymbolX, (float)atomSymbolY);// draw CH test		
		System.out.println("symbol width: " + atomSymbolW + " hydroGenW: " + hydroGenW +
				" total: " + (atomSymbolW + hydroGenW) + " CHwidth: " + CHwidth);
		System.out.println("difference: " + tempv + " dif2: " + (hydroGenW - atomSymbolW) + " dif3: " + ((hydroGenW - atomSymbolW) - tempv));
		*/
		
		graphics.setForeground(saveColor);
		
//		fontAtom.dispose();
//		fontSmall.dispose();
//		layoutAtom.dispose();
		
	}
	
	
	
	public  Color toSWTColor(GC graphics,java.awt.Color color) {
	    if(color==null) color=java.awt.Color.BLACK;
		Color otherColor=cleanUp.get(color);
		if(otherColor==null){
			otherColor = new Color(graphics.getDevice(),
				color.getRed(),
				color.getGreen(),
				color.getBlue());
			cleanUp.put(color,otherColor);
		}
		return otherColor;
//		color=java.awt.Color.CYAN;
//		return new Color(graphics.getDevice(),
//				100,
//				0,
//				0);
	}
	enum FontSize{
	    NORMAL,
	    SMALL,
	    DOUBLE_N,
	    DOUBLE_S;
	}
	Font normalAtomFont, smallAtomFont;
	Font smallAtomFontD,normalAtomFontD;
	
	private Font getFont(FontSize fontSize){
	    switch (fontSize) {
        case NORMAL:
            return normalAtomFont;
        case SMALL:
            return smallAtomFont;
        case DOUBLE_N:
            return normalAtomFontD;
        case DOUBLE_S:
            return smallAtomFontD;
        default:
            return normalAtomFont;
        }
	}
	private void createFont(GC graphics,float smallFontFactor,boolean force){
	    
	    if (force || normalAtomFont == null || smallAtomFont==null) {
          if(normalAtomFont!=null) normalAtomFont.dispose();
          if(smallAtomFont != null) smallAtomFont.dispose();
          
          
//        float[] transmatrix = { 1f / fscale, 0f, 0f, -1f / fscale};
//        AffineTransform trans = new AffineTransform(transmatrix);
//          double scale=trans.getScaleY();
          int fontSize =(int) Math.abs( fscale );// (int) Math.abs(16*1-scale);
          normalAtomFont = new Font(graphics.getDevice(), "Arial", fontSize,
              SWT.NORMAL);
          normalAtomFontD = new Font(graphics.getDevice(), "Arial", fontSize*4,
                                     SWT.NORMAL);
            // the graphics objects has a transform which is 'reversed' to go
            // from world coordinates
            // to screencoordinates, so transform the characters back to show
            // them 'up-side-up'.
                        
            // fontAtom = fontAtom.deriveFont(trans);

            // FIXME: add this 0.4 in the RendererModel

            // Font fontSmall = fontAtom.deriveFont(sizeSmall);
            // font for upper/lower text such as Massnumber, Charges,
            // HydrogenCount etc..

            FontData fd = normalAtomFont.getFontData()[0];
            fd.setHeight((int) (fd.getHeight() * smallFontFactor));
            smallAtomFont = new Font(graphics.getDevice(), fd);
            smallAtomFontD = new Font(graphics.getDevice(), "Arial",
                                            (int)(fontSize*smallFontFactor*4+.5),
                                            SWT.NORMAL);
            }         
	}
	public void dispose(){
		for(Color c:cleanUp.values())
			c.dispose();
		cleanUp.clear();
		for(FontSize fSize:FontSize.values())
		    if(getFont(fSize)!=null)getFont(fSize).dispose();
	}
	public static Rectangle2D toJava2DRect(Rectangle rect){
		return new Rectangle2D.Double(rect.x,rect.y,rect.width,rect.height);
	}
	public static void fill(GC graphics,Rectangle2D rect){
		graphics.fillRectangle((int)rect.getX(), 
				(int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
	}	
	public static void draw(GC graphics,Rectangle2D rect){
		graphics.drawRectangle((int)rect.getX(), 
				(int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
	}
	public static void fill(GC graphics,Ellipse2D rect){
	    graphics.fillOval((int)rect.getX(), 
	        (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
	  } 
	  public static void draw(GC graphics,Ellipse2D rect){
	    graphics.drawOval((int)rect.getX(), 
	        (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
	  }
	/**
	 *  Paints a rectangle of the given color at the position of the given atom.
	 *  For example when the atom is highlighted.
	 *
	 *@param  atom      The atom to be drawn
	 *@param  color     The color of the atom to be drawn
	 */
	public void paintColouredAtomBackground(org.openscience.cdk.interfaces.IAtom atom, Color color, GC graphics)
	{
		double x = atom.getPoint2d().x;
		double y = atom.getPoint2d().y;
		//System.out.println("painting paintColouredAtomBackground now at " + x + " / " + y);
		//FIXME: right size for this AtomRadius (currently estimate)
		double atomRadius = rendererModel.getHighlightRadiusModel();
		graphics.setForeground(color);
		graphics.setBackground( color );
	
		Rectangle2D shape = new Rectangle2D.Double(); 
		shape.setFrame(x - (atomRadius / 2), y - (atomRadius / 2), atomRadius, atomRadius);
		Ellipse2D shape2 = new Ellipse2D.Double(  );
		shape2.setFrameFromCenter( x, y, x - (atomRadius / 2), y - (atomRadius / 2) );
		if(rendererModel.getIsCompact())
			draw(graphics,shape2);
		else
			fill(graphics,shape2);
	}
	/**
	 *  A ring is defined aromatic if all atoms are aromatic, -or- all bonds are
	 *  aromatic.
	 *  copied from AbstractRenderer2D
	 */
	public boolean ringIsAromatic(IRing ring)
	{
		boolean isAromatic = true;		
		for (int i = 0; i < ring.getAtomCount() && isAromatic; i++)
		{
			if (!ring.getAtom(i).getFlag(CDKConstants.ISAROMATIC))
			{
				isAromatic = false;			
			}
		}
		if (!isAromatic)
		{
			isAromatic = true;
			Iterator<IBond> bonds = ring.bonds().iterator();
			while (bonds.hasNext() )
				if (!bonds.next().getFlag(CDKConstants.ISAROMATIC))
					return false;
		}
		return isAromatic;
	}
	
	public static double distance2points(Point2d a, Point2d b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}
	public static double distance2points(double x0, double y0, double x1, double y1) {
		return Math.sqrt(Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2));
	}
	/**
	 *  Paints the inner bond of a double bond that is part of a ring.
	 *
	 *@param  bond       The bond to be drawn
	 *@param  ring       The ring the bond is part of
	 *@param  bondColor  Color of the bond
	 */
	public void paintInnerBond(org.openscience.cdk.interfaces.IBond bond, IRing ring, Color bondColor, GC graphics)
	{
		Point2d center = GeometryTools.get2DCenter(ring);
		//System.out.println("  paintInnerBond (=working) now at " + center);
		//next few lines draw a green and pink line just for debugging, to be removed later
	/*	graphics.setColor(Color.green);
		Line2D line = new Line2D.Double(
				bond.getAtom(0).getPoint2d().x,				bond.getAtom(0).getPoint2d().y,
				center.x,				center.y						);
		graphics.draw(line);
		Line2D line2 = new Line2D.Double(
				bond.getAtom(1).getPoint2d().x,				bond.getAtom(1).getPoint2d().y,
				center.x,				center.y			);
		graphics.draw(line2);*/
		Point2d a = bond.getAtom(0).getPoint2d();
		Point2d b = bond.getAtom(1).getPoint2d();
	
		//TODO: put distanceconstant in the renderermodel
		double distanceconstant = 0.15; //distance between inner and outerbond (in world coordinates)

		double distance = distance2points(a,b);
		double u = ((center.x - a.x)*(b.x - a.x) + (center.y - a.y)*(b.y - a.y)) / (Math.pow(distance, 2));
		double px = a.x + u*(b.x - a.x);
		double py = a.y + u*(b.y - a.y);
		//System.out.println("distancea and b: " + distance + " u: " + u + " px: " + px + " py " + py);
		
		Point2d z = new Point2d(px, py);

	/*	graphics.setColor(Color.pink);
		Line2D linepink = new Line2D.Double(
				z.x,
				z.y,
				center.x,
				center.y
			);
		graphics.draw(linepink);*/

		double ae = distance2points(a ,z) / distance2points(center, z) * distanceconstant;
		double af = Math.sqrt(Math.pow(ae,2) + Math.pow(distanceconstant,2));
		
		double pfx = a.x + af*(center.x - a.x);
		double pfy = a.y + af*(center.y - a.y);
		
		double bh = distance2points(b, z) / distance2points(center, z) * distanceconstant;
		double bi = Math.sqrt(Math.pow(bh, 2) + Math.pow(distanceconstant,2));
		
		double pix = b.x + bi*(center.x - b.x);
		double piy = b.y + bi*(center.y - b.y);

		graphics.setForeground(bondColor);
		
		graphics.drawLine((int)pfx,(int)pfy,(int)pix,(int)piy);
	}

	/**
	 *  Triggers the suitable method to paint each of the given bonds and selects
	 *  the right color.
	 *
	 *@param  ringSet   The set of rings the molecule contains
	 */
	public void paintBonds(IAtomContainer atomCon, IRingSet ringSet, GC graphics)
	{
		Color bondColor;
		IRing ring;
		Iterator<IBond> bonds = atomCon.bonds().iterator();
		List<IRing> painted_rings = new ArrayList<IRing>();
	
		logger.debug("Painting bonds...");
		//System.out.println("--doing paintBonds now");
		while (bonds.hasNext())
		{
			IBond currentBond = bonds.next();
			
			
			if (currentBond == rendererModel.getHighlightedBond() && 
					(rendererModel.getSelectedPart()==null || !rendererModel.getSelectedPart().contains(currentBond)))
			{
				bondColor = toSWTColor(graphics,rendererModel.getHoverOverColor());
				for (int j = 0; j < currentBond.getAtomCount(); j++)
				{
					paintColouredAtomBackground(currentBond.getAtom(j), bondColor, graphics);
					
				}
			}
			bondColor = toSWTColor(graphics, rendererModel.getColorHash().get(currentBond));
			if (bondColor == null)
			{
				bondColor = toSWTColor(graphics,rendererModel.getForeColor());
			}
			ring = RingSetManipulator.getHeaviestRing(ringSet, currentBond);
			if (ring != null)
			{
				//System.out.println("found a ring, ringIsAromatic(ring) " + ringIsAromatic(ring) + " ,getShowAromaticity: "
//						+ rendererModel.getShowAromaticity());

				logger.debug("Found ring to draw");
				if (ringIsAromatic(ring) && rendererModel.getShowAromaticity())
				{
					logger.debug("Ring is aromatic");
					if (!painted_rings.contains(ring))
					{
						paintRingRing(ring, bondColor, graphics);
						painted_rings.add(ring);
					}
					paintSingleBond(currentBond, bondColor, graphics);
				} else
				{
					logger.debug("draw Ring as *not* aromatic");
					paintRingBond(currentBond, ring, bondColor, graphics);
				}
			} else
			{
				//System.out.println("no ring found!");

				logger.debug("Drawing a non-ring bond");
				paintBond(currentBond, bondColor, graphics);
			}
		}
	}
	/**
	 *  Triggers the paint method suitable to the bondorder of the given bond that
	 *  is part of a ring with CDK's grey inner bonds.
	 *
	 *@param  bond       The Bond to be drawn.
	 */
	public void paintRingBond(org.openscience.cdk.interfaces.IBond bond, IRing ring, Color bondColor, GC graphics)
	{
		Point2d center = GeometryTools.get2DCenter(ring);
		//System.out.println(" painting paintRingBond now at " + center + " getOrder: " + bond.getOrder() + " bond: " + bond);


		if (bond.getOrder() == IBond.Order.SINGLE)
		{
			// Added by rstefani (in fact, code copied from paintBond)
			if (bond.getStereo() != CDKConstants.STEREO_BOND_NONE && bond.getStereo() != CDKConstants.STEREO_BOND_UNDEFINED)
			{
				// Draw stero information if available
				if (bond.getStereo() >= CDKConstants.STEREO_BOND_UP)
				{
					paintWedgeBond(bond, bondColor, graphics);
				} else
				{
					paintDashedWedgeBond(bond, bondColor, graphics);
				}
			} else
			{
				// end code by rstefani
				//System.out.println("  singlebond in ring");
				paintSingleBond(bond, bondColor, graphics);
			}
		} else if (bond.getOrder() == IBond.Order.DOUBLE)
		{
			
			paintSingleBond(bond, bondColor, graphics);
			paintInnerBond(bond, ring, bondColor, graphics);
		} else if (bond.getOrder() == IBond.Order.TRIPLE)
		{
			paintTripleBond(bond, bondColor, graphics);
		} else
		{
			logger.warn("Drawing bond as single even though it has order: ", bond.getOrder());
			//System.out.println("Drawing bond as single even though it has order: " + bond.getOrder());

			paintSingleBond(bond, bondColor, graphics);
		}
	}
	/**
	 *  Paints the given bond as a wedge bond.
	 *
	 *@param  bond       The singlebond to be drawn
	 *@param  bondColor  Color of the bond
	 */
	public void paintWedgeBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, GC graphics)
	{
		//System.out.print("painting paintWedgeBond now for: " + bond);
		double wedgeWidth = rendererModel.getBondWidth() /10;
		//perhaps introduce a new setting instead of using getBondWidth here
		//System.out.println(" wedgeWidth: " + wedgeWidth);
		
		double x0, x1, y0, y1;

		if (bond.getStereo() == CDKConstants.STEREO_BOND_UP)
		{ //FIXME: check if this is correct, I think the difference between STEREO_BOND_UP and the 
			//other is which is the 'startpoint', please tell me (Nout) if this is correct. 
			x0 = bond.getAtom(0).getPoint2d().x;
			x1 = bond.getAtom(1).getPoint2d().x;
			y0 = bond.getAtom(0).getPoint2d().y;
			y1 = bond.getAtom(1).getPoint2d().y;
		} else
		{
			x1 = bond.getAtom(0).getPoint2d().x;
			x0 = bond.getAtom(1).getPoint2d().x;
			y1 = bond.getAtom(0).getPoint2d().y;
			y0 = bond.getAtom(1).getPoint2d().y;
		}
		double angle;
		if ((x1 - x0) == 0) {
			angle = Math.PI / 2;
		} else {
			angle = Math.atan((y1 - y0) / (x1 - x0));
		}
		float newxup = (float)(x1 - Math.sin(angle) * wedgeWidth);
		float newyup = (float)(y1 + Math.cos(angle) * wedgeWidth);
		
		float newxdown = (float)(x1 + Math.sin(angle) * wedgeWidth);
		float newydown = (float)(y1 - Math.cos(angle) * wedgeWidth);
		
//		GeneralPath p = new GeneralPath(); //create a triangle with GenaralPath
		Path pp=new Path(graphics.getDevice());
//		p.moveTo((float)x0, (float)y0);
			pp.moveTo((float)x0, (float)y0);
//		p.lineTo(newxup, newyup);
			pp.lineTo(newxup, newyup);
//		p.lineTo(newxdown, newydown);
			pp.lineTo(newxdown, newydown);
//		p.closePath();
			pp.close();
		graphics.setForeground(bondColor);
		graphics.setBackground( bondColor );
        //fill(graphics,pp);
		graphics.fillPath(pp);
	}
	/**
	 *  Paints the given bond as a dashed wedge bond.
	 *
	 *@param  bond       The single bond to be drawn
	 *@param  bondColor  Color of the bond
	 */
	public void paintDashedWedgeBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, GC graphics)
	{
		//System.out.println("painting paintDashedWedgeBond now for: " + bond);
		double wedgeWidth = rendererModel.getBondWidth() /10;
		double bondWidth = rendererModel.getBondWidth() / 40;
		double x0, x1, y0, y1;

		if (bond.getStereo() == CDKConstants.STEREO_BOND_DOWN)
		{ //FIXME: check if this is correct, I think the difference between STEREO_BOND_UP and the 
			//other is which is the 'startpoint', please tell me (Nout) if this is correct. 
			x0 = bond.getAtom(0).getPoint2d().x;
			x1 = bond.getAtom(1).getPoint2d().x;
			y0 = bond.getAtom(0).getPoint2d().y;
			y1 = bond.getAtom(1).getPoint2d().y;
		} else
		{
			x1 = bond.getAtom(0).getPoint2d().x;
			x0 = bond.getAtom(1).getPoint2d().x;
			y1 = bond.getAtom(0).getPoint2d().y;
			y0 = bond.getAtom(1).getPoint2d().y;
		}
		double angle;
		if ((x1 - x0) == 0) {
			angle = Math.PI / 2;
		} else {
			angle = Math.atan((y1 - y0) / (x1 - x0));
		}
		float newxup = (float)(x1 - Math.sin(angle) * wedgeWidth);
		float newyup = (float)(y1 + Math.cos(angle) * wedgeWidth);
		
		float newxdown = (float)(x1 + Math.sin(angle) * wedgeWidth);
		float newydown = (float)(y1 - Math.cos(angle) * wedgeWidth);
		
		double bondLength = distance2points(bond.getAtom(0).getPoint2d(), bond.getAtom(1).getPoint2d());
		int numberOfLines = (int) (bondLength / bondWidth / 3);

		//System.out.println("lines: " + numberOfLines);
		
		graphics.setForeground(bondColor);
		
		double xl, xr, yl, yr;
		Line2D.Double line = new Line2D.Double();
		for (int i = 0; i < numberOfLines - 2; i++) { //do not show last line because atom symbol will be drawn on that place
			double t = (double)i / numberOfLines;
			xl = x0 + t * (newxup - x0);
			xr = x0 + t * (newxdown - x0);
			yl = y0 + t * (newyup - y0);
			yr = y0 + t * (newydown - y0);
			//System.out.println(i + " : " + t + " from " + xl + " ; " + yl + " to: " + xr + " ; " + yr);
			line.setLine(xl, yl, xr, yr);
//			draw(graphics,line);
			graphics.drawLine((int)xl,(int)yl,(int)xr,(int)yr);
		}
	}

	/**
	 *  Draws the ring in an aromatic ring.
	 */
	public void paintRingRing(IRing ring, Color bondColor, GC graphics)
	{
		Point2d center = GeometryTools.get2DCenter(ring);
		//System.out.println(" painting a Ringring now at " + center);
		
		double[] minmax = GeometryTools.getMinMax(ring);
		double width = (minmax[2] - minmax[0]) * 0.7;
		double height = (minmax[3] - minmax[1]) * 0.7;
		
		//make a circle
		if (width > height)
			width = height;
		else if (height > width)
			height = width;
		
		double[] coords = { (center.x - (width / 2.0)), (center.y - (height / 2.0)) };
		//offset is the width of the ring
		double offset = (0.05 * Math.max(width, height));
		double offsetX2 = 2 * offset;

		// Fill outer oval.
		graphics.setForeground(bondColor);
		
		graphics.fillOval((int)coords[0],(int)coords[1],(int)width,(int)height);
		
		// Erase inner oval.
		graphics.setForeground(toSWTColor(graphics,rendererModel.getBackColor()));
		
		graphics.fillOval((int)(coords[0] + offset),(int)( coords[1] + offset), 
				(int)(width - offsetX2), (int)(height - offsetX2));
		
		// Reset drawing colour.
		graphics.setForeground(bondColor);
	}
	/**
	 *  Triggers the paint method suitable to the bondorder of the given bond.
	 *
	 *@param  bond       The Bond to be drawn.
	 */
	public void paintBond(IBond bond, Color bondColor, GC graphics)
	{

		//System.out.println("      paintBond, getstereo: " + bond.getStereo() + " getorder: " + bond.getOrder() + " x,y: " + bond.getAtom(0).getPoint2d().x + "," +
//				bond.getAtom(0).getPoint2d().y);
		
		if (!GeometryTools.has2DCoordinates(bond)) {
			return;
		}
		
		if (!rendererModel.getShowExplicitHydrogens()) {
			if (bond.getAtom(0).getSymbol().equals("H")) return;
			if (bond.getAtom(1).getSymbol().equals("H")) return;
		}

		if (bond.getStereo() != CDKConstants.STEREO_BOND_NONE && bond.getStereo() != CDKConstants.STEREO_BOND_UNDEFINED)
		{
			// Draw stereo information if available
			if (bond.getStereo() >= CDKConstants.STEREO_BOND_UP)
			{
				paintWedgeBond(bond, bondColor, graphics);
			} else
			{
				paintDashedWedgeBond(bond, bondColor, graphics);
			}
		} else
		{
			// Draw bond order when no stereo info is available
			if (bond.getOrder() == CDKConstants.BONDORDER_SINGLE)
			{
				paintSingleBond(bond, bondColor, graphics);
			} else if (bond.getOrder() == CDKConstants.BONDORDER_DOUBLE)
			{
				paintDoubleBond(bond, bondColor, graphics);
			} else if (bond.getOrder() == CDKConstants.BONDORDER_TRIPLE)
			{
				paintTripleBond(bond, bondColor, graphics);
//			} else if (bond.getOrder() == 8.)
//			{
//				paintAnyBond(bond, bondColor, graphics);
			} else
			{
				
				//System.out.println("       painting single bond because order = " + bond.getOrder());
				// paint all other bonds as single bonds
				paintSingleBond(bond, bondColor, graphics);
			}
		}
	}
	/**
	 *  Paints the given 'Any'  bond.
	 *
	 *@param  bond The given 'Any'  bond to be drawn
	 */
	public void paintAnyBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, GC graphics)
	{
		//TODO: rewrite this old code:
		
		/*if (GeometryTools.has2DCoordinates(bond))
		{
			int[] screencoords=getScreenCoordinates(GeometryTools.getBondCoordinates(bond, r2dm.getRenderingCoordinates()));
			int dashlength=4;
			int spacelength=4;
            if ((screencoords[0] == screencoords[2]) && (screencoords[1] == screencoords[3]))
            {
                    graphics.drawLine(screencoords[0], screencoords[1], screencoords[2], screencoords[3]);
                    return;
            }
            double linelength = Math.sqrt((screencoords[2] - screencoords[0]) * (screencoords[2] - screencoords[0]) + (screencoords[3] - screencoords[1]) * (screencoords[3] - screencoords[1]));
            double xincdashspace = (screencoords[2] - screencoords[0]) / (linelength / (dashlength + spacelength));
            double yincdashspace = (screencoords[3] - screencoords[1]) / (linelength / (dashlength + spacelength));
            double xincdash = (screencoords[2] - screencoords[0]) / (linelength / (dashlength));
            double yincdash = (screencoords[3] - screencoords[1]) / (linelength / (dashlength));
            int counter = 0;
            for (double i = 0; i < linelength - dashlength; i += dashlength + spacelength)
            {
                    graphics.drawLine((int) (screencoords[0] + xincdashspace * counter), (int) (screencoords[1] + yincdashspace * counter), (int) (screencoords[0] + xincdashspace * counter + xincdash), (int) (screencoords[1] + yincdashspace * counter + yincdash));
                    counter++;
            }
            if ((dashlength + spacelength) * counter <= linelength)
            {
                    graphics.drawLine((int) (screencoords[0] + xincdashspace * counter), (int) (screencoords[1] + yincdashspace * counter), screencoords[2], screencoords[3]);
            }
		}*/
	}
	/**
	 *  Paints the given double bond.
	 *
	 *@param  bond       The double bond to be drawn
	 */
	public void paintDoubleBond(IBond bond, Color bondColor, GC graphics)
	{
		if (GeometryTools.has2DCoordinates(bond))
		{
			double[] tempc = new double[] { bond.getAtom(0).getPoint2d().x, bond.getAtom(0).getPoint2d().y,
					bond.getAtom(1).getPoint2d().x, bond.getAtom(1).getPoint2d().y};
			
			double[] coords = GeometryTools.distanceCalculator(tempc, rendererModel.getBondDistance());
		
			Line2D line = new Line2D.Double(
					coords[0], coords[1], coords[6], coords[7]
				);
			paintOneBond(line, bondColor, graphics);

			Line2D line2 = new Line2D.Double(
					coords[2], coords[3], coords[4], coords[5]
				);
			paintOneBond(line2, bondColor, graphics);
			
		}
	}
	/**
	 *  Paints the given triple bond.
	 *
	 *@param  bond       The triple bond to be drawn
	 */
	public void paintTripleBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, GC graphics)
	{
		//System.out.println("painting paintTripleBond now at " + bond.getAtom(0).getPoint2d());

		paintSingleBond(bond, bondColor, graphics);
		double[] tempc = new double[] { bond.getAtom(0).getPoint2d().x, bond.getAtom(0).getPoint2d().y,
				bond.getAtom(1).getPoint2d().x, bond.getAtom(1).getPoint2d().y};
		
		double[] coords = GeometryTools.distanceCalculator(tempc, 
                                               rendererModel.getBondDistance());

		Line2D line = new Line2D.Double(
				coords[0], coords[1], coords[6], coords[7]
			);
		paintOneBond(line, bondColor, graphics);

		Line2D line2 = new Line2D.Double(
				coords[2], coords[3], coords[4], coords[5]
			);
		paintOneBond(line2, bondColor, graphics);
		
		//TODO: rewrite this old code:

	/*	int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond,r2dm.getRenderingCoordinates()), (r2dm.getBondWidth() / 2 + r2dm.getBondDistance()));

		int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
		paintOneBond(newCoords1, bondColor, graphics);

		int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
		paintOneBond(newCoords2, bondColor, graphics);*/
	}

	public void paintSingleBond(IBond bond, Color bondColor, GC graphics)
	{
		//System.out.println("  painting paintSingleBond " + bond.getAtom(0).getPoint2d() + " // " + bond.getAtom(1).getPoint2d());
		if (GeometryTools.has2DCoordinates(bond))
		{
			Line2D line = new Line2D.Double(
					bond.getAtom(0).getPoint2d().x,
					bond.getAtom(0).getPoint2d().y,
					bond.getAtom(1).getPoint2d().x,
					bond.getAtom(1).getPoint2d().y
				);
			paintOneBond(line, bondColor, graphics);
		}
	}
	
	/**
	 *  Really paints the bond. It is triggered by all the other paintbond methods
	 *  to draw a polygon as wide as bond width.
	 *
	 *@param  coords
	 *@param  bondColor  Color of the bond
	 */
	public void paintOneBond(Line2D line, Color bondColor, GC graphics)
	{
		// draw the shapes
//		graphics.setForeground(bondColor);
		graphics.setLineAttributes(new LineAttributes((float)(rendererModel.getBondWidth()/rendererModel.getBondLength()), 
				SWT.CAP_ROUND, SWT.JOIN_ROUND));
		
//		graphics.setStroke(new BasicStroke(
//				(float) (rendererModel.getBondWidth()/rendererModel.getBondLength()), 
//				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
//		);
		graphics.setForeground(bondColor);
		graphics.drawLine((int)line.getX1(),(int)line.getY1(),(int)line.getX2(),(int)line.getY2());
	}
	
	private AffineTransform createScaleTransform(Rectangle2D contextBounds, Rectangle2D rendererBounds) {
		AffineTransform affinet = new AffineTransform();
		
		//scale
		double factor = rendererModel.getZoomFactor() * (1.0 - rendererModel.getMargin() * 2.0);
	    double scaleX = factor * rendererBounds.getWidth() / contextBounds.getWidth();
	    double scaleY = factor * rendererBounds.getHeight() / contextBounds.getHeight();

	    double scale = Math.min( scaleX, scaleY );
	    affinet.scale( scale, -scale );	    
	    //translate
	    
		//System.out.println("scale: " + scale);
	    double dx = -contextBounds.getX() * scale + 0.5 * (rendererBounds.getWidth() - contextBounds.getWidth() * scale);
	    double dy = -contextBounds.getY() * scale - 0.5 * (rendererBounds.getHeight() + contextBounds.getHeight() * scale);
	    //System.out.println("dx: " + dx + " dy:" +dy);						
	    affinet.translate(dx / scale, dy / scale);
	    
		return affinet;
	}
	/**
	 *  Returns model coordinates from screencoordinates provided by the graphics translation
	 *   
	 * @param ptSrc the point to convert
	 * @return Point2D in real world coordinates
	 */
	public Point2d getCoorFromScreen(int screenX, int screenY) {
		Point2D ptSrc = new Point2D.Double(screenX, screenY);
		Point2D ptDst = new Point2D.Double();
		try {
			affine.inverseTransform(ptSrc, ptDst);
		}
		catch (Exception exception) {
			//System.out.println("Unable to reverse affine transformation");
			System.exit(0);
		}
		return new Point2d(ptDst.getX(), ptDst.getY());
	}
	/**
	 * 
	 * @param container
	 * @param ptSrc in real world coordinates (ie not screencoordinates)
	 */
	public static void showClosestAtomOrBond(IAtomContainer container, Point2d ptSrc) {
		IAtom atom = GeometryTools.getClosestAtom( ptSrc.x, ptSrc.y, container);
		double Atomdist = atom.getPoint2d().distance(ptSrc);
		//System.out.println("closest Atom distance: " + Atomdist + " Atom:" + atom);
		
		IBond bond = GeometryTools.getClosestBond( ptSrc.x, ptSrc.y, container);
		Point2d bondCenter = GeometryTools.get2DCenter(bond.atoms());
		double Bonddist = bondCenter.distance(ptSrc);
		//System.out.println("closest Bond distance: " + Bonddist + " Bond: " + bond);
	}
	
	public Rectangle2D createRectangle2D(IAtomContainer atomCon) {
		if (atomCon == null || atomCon.getAtomCount() == 0)
			return null;
		
		float xmin, xmax = (float)atomCon.getAtom(0).getPoint2d().x;
		xmin = xmax;
		float ymin, ymax = (float)atomCon.getAtom(0).getPoint2d().y;
		ymin = ymax;
		float y,x;
		for (int i = 1; i < atomCon.getAtomCount(); i++) {
			y = (float)atomCon.getAtom(i).getPoint2d().y;
			x = (float)atomCon.getAtom(i).getPoint2d().x;
			if (x < xmin)
				xmin = x;
			else if (x > xmax)
				xmax = x;
			if (y < ymin)
				ymin = y;
			else if (y > ymax)
				ymax = y;
		}
    	float margin = 1; //1 is ~enough margin to make symbols + text appear on screen	
		Rectangle2D result = new Rectangle2D.Float();
		result.setRect(xmin - margin, ymin - margin, (xmax - xmin) + 2 * margin, (ymax - ymin) + 2 * margin);
		return result;
	}
	
	public Renderer2DModel getRenderer2DModel() {
		return this.rendererModel;
	}

	public void setRenderer2DModel(Renderer2DModel model) {
		this.rendererModel = model;
	}
	public void paintMolecule(IAtomContainer atomCon, Graphics2D graphics) {
		// TODO Auto-generated method stub
		logger.fatal("Not a java2D renderer");
	}
	public void paintMolecule(IAtomContainer atomCon, Graphics2D graphics,
			Rectangle2D bounds) {
		// TODO Auto-generated method stub
		logger.fatal("Not a java2D renderer");
		
	}
	/*
	 * Utility method for copying 3D x,y to 2D coordinates 
	 */
	public static IAtomContainer generate2Dfrom3D( IAtomContainer atomContainer ) {

	    try {
	        atomContainer = (IAtomContainer) atomContainer.clone();

	        // For each molecule,
	        for ( int i = 0; i < atomContainer.getAtomCount(); i++ ) {
	            IAtom atom = atomContainer.getAtom( i );
	            Point3d p3 = atom.getPoint3d();
	            Point2d p2 = new Point2d();
	            p2.x = p3.x;
	            p2.y = p3.y;
	            atom.setPoint3d( null );
	            atom.setPoint2d( p2 );
	        }
	    } catch ( CloneNotSupportedException e ) {
	        return null;
	    }
	    return atomContainer;
	}

	
}
