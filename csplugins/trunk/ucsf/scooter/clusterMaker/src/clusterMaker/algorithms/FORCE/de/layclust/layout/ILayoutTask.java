/* 
* Created on 4. October 2007
 * 
 */
package de.layclust.layout;

/**
 * This is an interface for a thread to run the layouting phase for one cost matrix.
 * 
 * @author sita
 */
public interface ILayoutTask extends Runnable{
	
	/**
	 * This method initialises the task with the path to where the cost matrix is saved.
	 * @param cm_path Path of the input cost matrix.
	 */
	public void initialiseLayoutTask(String cm_path);
	
}
