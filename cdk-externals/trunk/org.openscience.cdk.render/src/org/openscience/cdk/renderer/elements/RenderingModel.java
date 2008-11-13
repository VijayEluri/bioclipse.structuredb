/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
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
package org.openscience.cdk.renderer.elements;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @cdk.module  render
 */
public class RenderingModel implements IRenderingElement,
                                       Iterable<IRenderingElement> {

    Collection<IRenderingElement> elements = new ArrayList<IRenderingElement>();
    double scalex = 10;
    double scaley = 10;
    
    
    public Iterator<IRenderingElement> iterator() {
        return elements.iterator();
    }
    
    public void add(IRenderingElement element) {
        elements.add(element);
    }

    public void setScale( double[] scale ) {
        if( scale !=null && scale.length !=2) return;
        scalex = Math.min( scale[0],scale[1]);
        scaley = scalex;
    }
    
    public double[] getDimensions(IAtomContainer ac, Dimension size) {
        
        Rectangle2D aSize = getExtent( ac );
        double[] scale = new double[]{ (size.getWidth()/(aSize.getWidth())),
                             (size.getHeight()/(aSize.getHeight()))};
        setScale( scale );
        return scale;
    }

    public double getScale(IAtomContainer ac, Dimension size) {
        double[] scale = getDimensions( ac, size );
        return Math.min(scale[0],scale[1]);
    }
    
    Rectangle2D getExtent(IAtomContainer ac) {
        double xmin = Double.POSITIVE_INFINITY, xmax = Double.NEGATIVE_INFINITY, ymin =
                Double.POSITIVE_INFINITY , ymax = Double.NEGATIVE_INFINITY;

        for ( IAtom atom : ac.atoms() ) {
            double x = atom.getPoint2d().x;
            double y = atom.getPoint2d().y;
            xmin = Math.min( xmin, x );
            xmax = Math.max( xmax, x );
            ymin = Math.min( ymin, y );
            ymax = Math.max( ymax, y );
        }
        return new Rectangle2D.Double(xmin,ymin,xmax-xmin,ymax-ymin);
    }
    
    public void accept( IRenderingVisitor v ) {
        
        v.visitModel( this );
    }
    
    public Point2D center(IAtomContainer ac, Dimension size) {
        // translation needed to center ac
        
        double scale = getScale( ac, size );
        Rectangle2D extent = getExtent( ac );
        double aWidth = extent.getWidth()*scale;
        double aHeight = extent.getHeight()*-scale;
        double xDiff = size.getWidth()-aWidth;
        double yDiff = size.getHeight()-aHeight;
        return new Point2D.Float( (float)(-(extent.getX()*scale)+xDiff/2),
                                  (float)(-(extent.getY()*-scale)+yDiff/2));
    }
}