/*
 * Created on Sep 16, 2005
 *
 */
package cytoscape.editor.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;

import cytoscape.visual.Arrow;
import cytoscape.visual.ShapeNodeRealizer;

/**
 * 
 * Specialized Icon for Cytoscape editor palette entry.  Renders icon based upon
 * input shape, size, color.
 * 
 * @author Allan Kuchinsky
 * @version 1.0
 */
public class CytoShapeIcon implements Icon {

	

    private Color _color;
	  private Image _image =  null;
	  private byte _shape;
	  private Arrow _arrowType = null;
	  
	  public static final int WIDTH = 32;
	  public static final int HEIGHT = 32;
	
	/**
	 * 
	 */
	public CytoShapeIcon(byte shape, Color color) {
		_color = color;
		_shape = shape;
		_image = null;
		_arrowType = null;
	}
	
	public CytoShapeIcon(Image img) {
		_image = img;
		_arrowType = null;
	}
	
	public CytoShapeIcon (Arrow arrowType)
	{
		_arrowType = arrowType;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {

		return 32;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {

		return 32;
	}
	
	 /**


	/* (non-Javadoc)
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		


	    int width = getIconWidth();
	    int height = getIconHeight();

		if (_image != null)
		{
			g.drawImage(_image, x, y, c);
			return;
			
		}
		
		if (_arrowType != null)
		{
			g.setColor(Color.BLACK);
			if (_arrowType == Arrow.BLACK_DELTA)
			{
				g.fillPolygon(new int[] { x, x + ((3 * width) / 4),  x + ((3 * width) / 4),
				  x + width, x + ((3 * width) / 4), x + ((3 * width) / 4), x },
				  new int [] { y + ((7 * height) / 16), y + ((7 * height) / 16),
						y + ((5 * height) / 16), y + height / 2, 
						y + ((11 * height) / 16), y + ((9 * height) / 16),
						y + ((9 * height) / 16) }
				, 7);
				}
			else if (_arrowType == Arrow.COLOR_DELTA)
			{
				g.setColor(Color.BLUE);
				g.fillPolygon(new int[] { x, x + ((3 * width) / 4),  x + ((3 * width) / 4),
				  x + width, x + ((3 * width) / 4), x + ((3 * width) / 4), x },
				  new int [] { y + ((7 * height) / 16), y + ((7 * height) / 16),
						y + ((5 * height) / 16), y + height / 2, 
						y + ((11 * height) / 16), y + ((9 * height) / 16),
						y + ((9 * height) / 16) }
				, 7);
				}
			else if (_arrowType == Arrow.BLACK_CIRCLE)
			{
				g.fillRect(x, y + ((7 * height) / 16), (13 * (width / 16)), height / 8);
				g.fillOval(x + 5 * width / 8, y + ((5 * height) / 16), 6 * width / 16, 6* height / 16);
			}
			
			else if (_arrowType == Arrow.BLACK_T)
			{
				g.fillRect(x, y + ((7 * height) / 16), (15 * (width / 16)), height / 8);
				g.fillRect(x + (15 * (width / 16)), y + ((5 * height) / 16), width / 16, height * 6 / 16);
			}
			else if (_arrowType == Arrow.NONE)
			{
				g.fillRect(x, y + ((7 * height) / 16), (15 * (width / 16)), height / 8);
			}			return;
		}
	    
	    g.setColor(_color);

	    if (_shape == ShapeNodeRealizer.TRIANGLE) {
	    	g.fillPolygon( new int[] { x, x + width / 2, x + width },
	    			new int [] {       y + height, y, y + height} , 
					3);
	    	g.setColor(Color.BLACK);
	    	g.drawPolygon( new int[] { x, x + width / 2, x + width },
	    			new int [] {       y + height, y, y + height} , 
					3);

	    } else if ( _shape == ShapeNodeRealizer.ROUND_RECT ) {
	    	g.fillRoundRect(x, y, width, height, width / 2, height / 2);
	    	g.setColor(Color.BLACK);
	    	g.drawRoundRect(x, y, width, height, width / 2, height / 2);

	    } else if (_shape == ShapeNodeRealizer.DIAMOND) {
	    	g.fillPolygon( new int[] { x, x + width / 2, x + width, x + width / 2 },
	    			new int [] {       y + height / 2, y, y + height / 2, y + height} , 
					4);	 
	    	g.setColor(Color.BLACK);
	    	g.drawPolygon( new int[] { x, x + width / 2, x + width, x + width / 2 },
	    			new int [] {       y + height / 2, y, y + height / 2, y + height} , 
					4);	 

	    } else if (_shape == ShapeNodeRealizer.ELLIPSE) {
	        g.fillOval(x, y, width, height);
	        g.setColor(Color.BLACK);
	        g.drawOval(x, y, width, height);
	        
	        
	    } else if (_shape == ShapeNodeRealizer.HEXAGON) {
	    	g.fillPolygon( new int[] { x, x + width / 4, x + ((3 * width) / 4), x + width,
	    			x + ((3 * width) / 4), x + width / 4},
	    			new int [] {       y + height / 2, y, y, y + height / 2, y + height, y + height} , 
					6);	    	
	    	g.setColor(Color.BLACK);
	    	g.drawPolygon( new int[] { x, x + width / 4, x + ((3 * width) / 4), x + width,
	    			x + ((3 * width) / 4), x + width / 4},
	    			new int [] {       y + height / 2, y, y, y + height / 2, y + height, y + height} , 
					6);	    	
	    	
	    	
	    } else if (_shape == ShapeNodeRealizer.OCTAGON) {
	    	g.fillPolygon( new int[] { x, x + width / 4, x + ((3 * width) / 4), x + width,
	    			x+ width, x + ((3 * width) / 4), x + width / 4, x},
	    			new int [] { y + height / 4, y, y, y + height / 4, 
	    			y + (3 * (height / 4)), y + height, y + height, 
	    	y + (3 * (height / 4))}, 
					8);	 
	    	g.setColor(Color.BLACK);
	    	g.drawPolygon( new int[] { x, x + width / 4, x + ((3 * width) / 4), x + width,
	    			x+ width, x + ((3 * width) / 4), x + width / 4, x},
	    			new int [] { y + height / 4, y, y, y + height / 4, 
	    			y + (3 * (height / 4)), y + height, y + height, 
	    	y + (3 * (height / 4))}, 
					8);
	    	
	    } else if (_shape == ShapeNodeRealizer.PARALLELOGRAM) {
	    	g.fillPolygon( new int[] { x, x + ((3 * width) / 4), x + width,
	    			 x + width / 4},
	    			new int [] {y, y, y + height, y + height} , 
					4);	
	    	g.setColor(Color.BLACK);
	    	g.drawPolygon( new int[] { x, x + ((3 * width) / 4), x + width,
	    			 x + width / 4},
	    			new int [] {y, y, y + height, y + height} , 
					4);	
	    	
	    	
	    } else if (_shape == ShapeNodeRealizer.RECT) {
	    	g.fillRect(x, y, width, height);
	    	g.setColor(Color.BLACK);
	    	g.drawRect(x, y, width, height);

	    }
	}

}
