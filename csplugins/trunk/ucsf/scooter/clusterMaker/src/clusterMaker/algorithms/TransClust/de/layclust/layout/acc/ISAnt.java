package de.layclust.layout.acc;

/**
 * This is the interface for all ants that are able to work with the class 
 * StackPlayground.
 * 
 * 
 * @author Nils Kleinbölting
 *
 */
public interface ISAnt {
	/**
	 * Lets the ant perform a step on the grid. 
	 *
	 */
	public void makeStep();
	/**
	 * Sets the current position of the ant.
	 * 
	 * @param pos new position.
	 */
	public void setPosition(int[] pos);
	/**
	 * Returns the current position of the ant.
	 * 
	 * @return current position.
	 */
	public int[] getPosition();
}
