/*
 * Created on Jun 13, 2005
 *
 * extends AWTEvent, forwards the drop event and its location. 
 */
package phoebe;

import java.awt.*;
import java.awt.datatransfer.Transferable;


/**
 * 
 * @author ajk
 *
 */

/**
 * forwards the drop event and its location
 */
public class PhoebeCanvasDropEvent  extends AWTEvent {
	private final static long serialVersionUID = 1213746928492377L;
	
	Transferable transferable;
	Point location;
	
	
	/**
	 * constructor for PhoebeCanvasDropEvent
	 * @param source the source canvas of the event
	 * @param t the transferable created by the drop event
	 * @param location the location of the drop, in terms of canvas coordinates
	 */
	public PhoebeCanvasDropEvent (PhoebeCanvasDroppable source, Transferable t, Point location)
	{
		super (source, -1); // from Graphic Java, p 303
		this.transferable = t;
		this.location = location;
		
	}

	/**
	 * @return Returns the location.
	 */
	public Point getLocation() {
		return location;
	}
	/**
	 * @return Returns the transferable.
	 */
	public Transferable getTransferable() {
		return transferable;
	}
}
