/*
 *  Created on 3. December 2007
 */
package de.layclust.postprocessing;

import de.layclust.datastructure.ConnectedComponent;


/**
 * @author sita
 *
 */
public interface IPostProcessing {
	
	public void initPostProcessing(ConnectedComponent cc);

	public void run();
	
}
