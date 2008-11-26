package org.openscience.cdk.renderer.visitor;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingVisitor;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement;

/**
 * @cdk.module render
 */
public abstract class AbstractAWTRenderingVisitor implements IRenderingVisitor {
	
	private final double scale;
	private final double dx;
	private final double dy;
	
	public AbstractAWTRenderingVisitor(double scale, double dx, double dy) {
		this.scale = scale;
		this.dx = dx;
		this.dy = dy;
	}
	
	public int tX(double x) {
		return (int) (this.dx + (this.scale * x));
	}
	
	public int tY(double y) {
		return (int) (this.dy + (this.scale * y));
	}

	public Rectangle2D getTextBounds(TextElement text, Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D bounds = fm.getStringBounds(text.text, g);
		
		double widthPad = 3;
		double heightPad = 1;
		
		double w = bounds.getWidth() + widthPad;
		double h = bounds.getHeight() + heightPad;
		return new Rectangle2D.Double(tX(text.x) - w / 2, tY(text.y) - h / 2, w, h);
	}
	
	public Point getTextBasePoint(TextElement textElement, Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D stringBounds = fm.getStringBounds(textElement.text, g);
		int baseX = (int) (tX(textElement.x) - (stringBounds.getWidth() / 2));
		
		// correct the baseline by the ascent
		int baseY = (int) (tY(textElement.y) + (fm.getAscent() - stringBounds.getHeight() / 2));
		return new Point(baseX, baseY);
	}

	public abstract void visitElementGroup(ElementGroup elementGroup);

	public abstract void visitLine(LineElement lineElement);
	
	public abstract void visitOval(OvalElement ovalElement);

	public abstract void visitText(TextElement textElement);

	public abstract void visitWedge(WedgeLineElement wedgeElement);

}