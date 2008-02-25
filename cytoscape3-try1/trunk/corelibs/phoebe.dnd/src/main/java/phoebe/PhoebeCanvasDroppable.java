/*
 * Created on Jun 13, 2005
 *
 * interface for maintaining listeners for PhoebeCanvasDropEvents.  
 */
package phoebe;

import java.awt.event.*;
import java.util.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;

/**
 * @author Allan Kuchinsky
 *
 *
 */

 /**
 * interface for maintaining listeners for PhoebeCanvasDropEvents.  
 */
public interface PhoebeCanvasDroppable {

	/**
	 * 
	 * adds a PhoebeCanvasDropListener to the listener store
	 * @param l the PhoebeCanvasDropListener to be added
	 */
	public void addPhoebeCanvasDropListener (PhoebeCanvasDropListener l);
	
    /**
     * removes a PhoebeCanvasDropListener from the listener store
     * @param l the PhoebeCanvasDropListener to be deleted
     */
	public void removePhoebeCanvasDropListener (PhoebeCanvasDropListener l);

}
