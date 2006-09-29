/*
 File: LabelPlacerGraphic.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package cytoscape.visual.ui;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.beans.*;
import cytoscape.visual.LabelPosition;
import giny.view.Label;

/**
 * A drag and drop graphic that allows users to set the placement
 * of node labels.
 */
public class LabelPlacerGraphic extends JPanel implements PropertyChangeListener {

    private LabelPosition lp; 

    // indices of the closest points
    private int bestLabelX = 1;
    private int bestLabelY = 1;
    private int bestNodeX = 1;
    private int bestNodeY = 1;

    // dimensions of panel 
    private int xy = 500; 
    private int center = xy/2;

    // dimensions for node box
    private int nxy = (int)(0.4*xy);

    // locations of node points
    private int[] npoints = {center-nxy/2,center,center+nxy/2};

    // dimensions for label box
    private int lx = (int)(0.24*xy);
    private int ly = (int)(0.1*xy);

    // locations for label points
    private int[] lxpoints = {0,lx/2,lx};
    private int[] lypoints = {0,ly/2,ly};

    // diameter of a point
    private int dot = (int)(0.02*xy);

    // x/y positions for label box, initially offset
    private int xPos = dot; 
    private int yPos = dot;

    // mouse drag state
    private boolean beenDragged = false;
    private boolean canDrag = false;
    private boolean canOffsetDrag = false;

    // click offset
    private int xClickOffset = 0;
    private int yClickOffset = 0;

    // the x and y offsets for the label rendering
    private int xOffset;
    private int yOffset;

    // default text justify rule
    private int justify;
  
    // used to label box and node
    private Color transparentRed = new Color(1.0f,0.0f,0.0f,0.1f);
    private Color transparentBlue = new Color(0.0f,0.0f,1.0f,0.1f);

    /**
     * A gui for placing a label relative to a node.
     */
    public LabelPlacerGraphic(LabelPosition pos) {
        super();

	if (pos == null)
		lp = new LabelPosition(Label.NONE,Label.NONE,Label.JUSTIFY_CENTER,0.0,0.0);
	else
		lp = pos;

	setPreferredSize(new Dimension(xy,xy));
	setBackground(Color.white);

	addMouseListener(new MouseClickHandler());
	addMouseMotionListener(new MouseDragHandler());

	applyPosition();

	repaint();
    }
   
    /**
     * The method that handles the rendering of placement gui.
     */
    public void paint(Graphics g) {
    	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                                 RenderingHints.VALUE_ANTIALIAS_ON);

    	// clear the screen
    	g.setColor(Color.white);
    	g.clearRect(0,0,xy,xy);

	// draw the node box
	int x = center - nxy/2;
	int y = center - nxy/2;

	g.setColor(transparentBlue);
	g.fillOval(x,y,nxy,nxy);

    	((Graphics2D)g).setStroke( new BasicStroke(3) );
    	g.setColor(Color.blue);
	g.drawLine(x,y,x+nxy,y);
	g.drawLine(x+nxy,y,x+nxy,y+nxy);
	g.drawLine(x+nxy,y+nxy,x,y+nxy);
	g.drawLine(x,y+nxy,x,y);
	g.drawString("NODE",center-nxy/12,center-nxy/6);

	// draw the node box points
    	g.setColor(Color.black);
	for ( int i = 0; i < npoints.length; i++ )
		for ( int j = 0; j < npoints.length; j++ )
			g.fillOval(npoints[i]-dot/2,npoints[j]-dot/2,dot,dot);


	// draw the base box if any offsets are used
	if ( xOffset != 0 || yOffset != 0 ) {
		g.setColor(Color.lightGray);
		g.drawLine(xPos,yPos,xPos+lx,yPos);
		g.drawLine(xPos+lx,yPos,xPos+lx,yPos+ly);
		g.drawLine(xPos+lx,yPos+ly,xPos,yPos+ly);
		g.drawLine(xPos,yPos+ly,xPos,yPos);
	}

	// draw the label box
    	g.setColor(transparentRed);
	g.fillRect(xOffset+xPos,yOffset+yPos,lx,ly);

    	g.setColor(Color.red);
	g.drawLine(xOffset+xPos,yOffset+yPos,xOffset+xPos+lx,yOffset+yPos);
	g.drawLine(xOffset+xPos+lx,yOffset+yPos,xOffset+xPos+lx,yOffset+yPos+ly);
	g.drawLine(xOffset+xPos+lx,yOffset+yPos+ly,xOffset+xPos,yOffset+yPos+ly);
	g.drawLine(xOffset+xPos,yOffset+yPos+ly,xOffset+xPos,yOffset+yPos);

	if ( justify == Label.JUSTIFY_LEFT ) {
		g.drawString("LABEL",xOffset+xPos+3,yOffset+yPos+ly/3);
		g.drawString("CLICK 'N DRAG",xOffset+xPos+3,yOffset+yPos+5*ly/6);
	} else if ( justify == Label.JUSTIFY_RIGHT ) {
		g.drawString("LABEL",xOffset+xPos+4*lx/5-10,yOffset+yPos+ly/3);
		g.drawString("CLICK 'N DRAG",xOffset+xPos+lx/3-10,yOffset+yPos+5*ly/6);
	} else { // center
		g.drawString("LABEL",xOffset+xPos+2*lx/5,yOffset+yPos+ly/3);
		g.drawString("CLICK 'N DRAG",xOffset+xPos+lx/5,yOffset+yPos+5*ly/6);
	}


	// draw the label box points
    	g.setColor(Color.black);
	for ( int i = 0; i < lxpoints.length; i++ )
		for ( int j = 0; j < lypoints.length; j++ ) {
			if ( i == bestLabelX && j == bestLabelY && !beenDragged)
				g.setColor(Color.yellow);
			g.fillOval(xPos+lxpoints[i]-dot/2,yPos+lypoints[j]-dot/2,dot,dot);
			if ( i == bestLabelX && j == bestLabelY )
				g.setColor(Color.black);
		}

    }


    private class MouseClickHandler extends MouseAdapter {
        /**
	 * Only allows dragging if we're in the label box.
	 * Also sets the offset from where the click is and where
	 * the box is, so the box doesn't appear to jump around
	 * too much.
	 */
        public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		// click+drag within normal box
		if ( x >= xPos && x <= xPos + lx && 
		     y >= yPos && y <= yPos + ly ) {
		     canDrag = true;
		     xClickOffset = x - xPos;
		     yClickOffset = y - yPos;
		// click+drag within offset box
		} else if ( x >= xPos+xOffset && x <= xPos+xOffset + lx && 
		            y >= yPos+yOffset && y <= yPos+yOffset + ly ) {
		     canOffsetDrag = true;
		     xClickOffset = x - xPos+xOffset;
		     yClickOffset = y - yPos+yOffset;
		}
	}

	/**
	 * Finds the closest points once the dragging is finished.
	 */
        public void mouseReleased(MouseEvent e) {
		if ( beenDragged ) {
			xPos = e.getX();
			yPos = e.getY();
			// adjust if drag happened in offset box
			if ( canOffsetDrag ) {
				xPos += xOffset;
				yPos += yOffset;
			}

			findClosestPoint();
			repaint();
			beenDragged = false;
			canDrag = false;
			canOffsetDrag = false;
		}
        }
    }


    private class MouseDragHandler extends MouseMotionAdapter {
        /**
	 * Handles redrawing for dragging.
	 */
        public void mouseDragged(MouseEvent e) {
		// dragging within normal box
		if ( canDrag ) {
		     	xPos = e.getX() - xClickOffset;
		     	yPos = e.getY() - yClickOffset;
			beenDragged = true;
    			repaint();
		// dragging within offset box
		} else if ( canOffsetDrag ) {
		     	xPos = e.getX() - xClickOffset + xOffset;
		     	yPos = e.getY() - yClickOffset + yOffset;
			beenDragged = true;
    			repaint();
		}
        }
    }

    /**
     * Finds the points on the Label box and Node box that are closest.
     */
    private void findClosestPoint() {
        // These points need to be set back to the actual x/y positions of
	// the label box so that it's the points that are being checked, and
	// not the points + offset.
        xPos -= xClickOffset;
        yPos -= yClickOffset;

	double best = Double.POSITIVE_INFINITY;

	// loop over each point in the node box
    	for ( int i = 0; i < npoints.length; i++ ) {
    		for ( int j = 0; j < npoints.length; j++ ) {
			Point nodePoint = new Point(npoints[i]-dot/2,npoints[j]-dot/2);

			// loop over each point in the label box
			for ( int a = 0; a < lxpoints.length; a++) {
				for ( int b = 0; b < lypoints.length; b++) {
					Point labelPoint = new Point(xPos+lxpoints[a]-dot/2,yPos+lypoints[b]-dot/2);

					double dist = labelPoint.distance((Point2D)nodePoint); 
					if ( dist < best ) {
						best = dist;
						bestLabelX = a;
						bestLabelY = b;
						bestNodeX = i;
						bestNodeY = j;
					}
				}
			}
		}
	}

	xPos = npoints[bestNodeX] - lxpoints[bestLabelX];
	yPos = npoints[bestNodeY] - lypoints[bestLabelY];

	lp.setLabelAnchor( bestLabelX + (3*bestLabelY) );
	lp.setTargetAnchor( bestNodeX + (3*bestNodeY) );

	firePropertyChange("LABEL_POSITION_CHANGED",null,lp);
    }

    /**
     * Applies the new LabelPosition to the graphic.
     */
    private void applyPosition() {

	xOffset = (int)lp.getOffsetX();
	yOffset = (int)lp.getOffsetY();
	justify = lp.getJustify();

	int nodeAnchor = lp.getTargetAnchor(); 
	if ( nodeAnchor != Label.NONE ) {
		bestNodeX = nodeAnchor % 3;		
		bestNodeY = (int)nodeAnchor / 3;		
	}

	int labelAnchor = lp.getLabelAnchor(); 
	if ( labelAnchor != Label.NONE ) {
		bestLabelX = labelAnchor % 3;		
		bestLabelY = (int)labelAnchor / 3;		
	}

	if ( nodeAnchor != Label.NONE || 
	     labelAnchor != Label.NONE ) { 
		xPos = npoints[bestNodeX] - lxpoints[bestLabelX];
		yPos = npoints[bestNodeY] - lypoints[bestLabelY];
	}
    }

    /**
     * Handles all property changes that the panel listens for.
     */
    public void propertyChange(PropertyChangeEvent e) {
	String type = e.getPropertyName();
	if ( type.equals("LABEL_POSITION_CHANGED") ) {
		lp = (LabelPosition)e.getNewValue();
		applyPosition();
		repaint();
	}
    }
}
