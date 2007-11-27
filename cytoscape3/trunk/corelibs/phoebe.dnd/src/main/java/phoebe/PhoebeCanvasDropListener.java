/*
 * Created on Jun 13, 2005
 *
 * an interface for responding to PhoebeCanvasDropEvents.  
 */
package phoebe;

import java.awt.event.*;
import java.util.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;

/**
 * @author Allan Kuchinsky
 *
 */

/**
 * an interface for responding to PhoebeCanvasDropEvents.  
 */
public interface PhoebeCanvasDropListener extends EventListener {


	/**
	 * method for responding to a drop
	 * @param event the PhoebeCanvasDropEvent
	 */
	void itemDropped(PhoebeCanvasDropEvent event);

}
