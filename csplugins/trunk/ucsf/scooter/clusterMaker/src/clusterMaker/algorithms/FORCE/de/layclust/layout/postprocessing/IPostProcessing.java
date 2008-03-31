/*
 *  Created on 3. December 2007
 */
package de.layclust.layout.postprocessing;

import de.layclust.layout.data.ConnectedComponent;


/**
 * @author sita
 *
 */
public interface IPostProcessing {
	
	public void initPostProcessing(ConnectedComponent cc);

	public void run();
	
}
