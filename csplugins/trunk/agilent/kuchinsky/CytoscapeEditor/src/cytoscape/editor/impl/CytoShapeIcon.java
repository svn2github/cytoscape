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

	
//      public static final int TRIANGLE = 0;
//	  public static final int DIAMOND = 1;
//	  public static final int ELLIPSE = 2;
//	  public static final int HEXAGON = 3;
//	  public static final int OCTAGON = 4;
//	  public static final int PARALELLOGRAM = 5;
//	  public static final int RECTANGLE = 6;
//	  public static final int ROUNDED_RECTANGLE = 7;
//	  public static final int IMAGE = -1;
//	  
//	  private int _shape;

//    public static final byte RECT = (byte)0;
//    public static final byte ROUND_RECT = (byte)1;
//    public static final byte RECT_3D = (byte)2;
//    public static final byte TRAPEZOID = (byte)3;
//    public static final byte TRAPEZOID_2 = (byte)4;
//    public static final byte TRIANGLE = (byte)5;
//    public static final byte PARALLELOGRAM = (byte)6;
//    public static final byte DIAMOND = (byte)7;
//    public static final byte ELLIPSE = (byte)8;
//    public static final byte OCTAGON = (byte)10;
    
    private Color _color;
	  private Image _image =  null;
	  private byte _shape;
	  
	  public static final int WIDTH = 32;
	  public static final int HEIGHT = 32;
	
	/**
	 * 
	 */
	public CytoShapeIcon(byte shape, Color color) {
		_color = color;
		_shape = shape;
		_image = null;
	}
	
	public CytoShapeIcon(Image img) {
		_image = img;
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
	    
	    g.setColor(_color);

	    if (_shape == ShapeNodeRealizer.TRIANGLE) {
	    	g.fillPolygon( new int[] { x, x + width / 2, x + width },
	    			new int [] {       y + height, y, y + height} , 
					3);

	    } else if ( _shape == ShapeNodeRealizer.ROUND_RECT ) {
	    	g.fillRoundRect(x, y, width, height, width / 2, height / 2);

	    } else if (_shape == ShapeNodeRealizer.DIAMOND) {
	    	g.fillPolygon( new int[] { x, x + width / 2, x + width, x + width / 2 },
	    			new int [] {       y + height / 2, y, y + height / 2, y + height} , 
					4);	    	

	    } else if (_shape == ShapeNodeRealizer.ELLIPSE) {
	        g.fillOval(x, y, width, height);
	    } else if (_shape == ShapeNodeRealizer.HEXAGON) {
	    	g.fillPolygon( new int[] { x, x + width / 4, x + ((3 * width) / 4), x + width,
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
	    } else if (_shape == ShapeNodeRealizer.PARALLELOGRAM) {
	    	g.fillPolygon( new int[] { x, x + ((3 * width) / 4), x + width,
	    			 x + width / 4},
	    			new int [] {y, y, y + height, y + height} , 
					4);	    	
	    } else if (_shape == ShapeNodeRealizer.RECT) {
	    	g.fillRect(x, y, width, height);

	    }
	}

}
