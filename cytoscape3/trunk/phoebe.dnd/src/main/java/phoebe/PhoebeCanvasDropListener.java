/*
 * Created on Jun 13, 2005
 *
 * an interface for responding to PhoebeCanvasDropEvents.  
 */
package phoebe;

import java.util.EventListener;

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
