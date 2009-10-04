package de.layclust.layout.acc;

/**
 * Interface for all Ant-classes.
 * @author Nils Kleinbölting
 *
 */

public interface IAnt {

	/**
	 * Lets the ant perform a step on the grid: if it carries no item it decides to pick up an item, 
	 * moves and decides if it drops the item on the new position.
	 *
	 */
	public void makeStep();
	
	/**
	 * Lets the ant drop the item it carries (depending on the implementation the item is dropped
	 * on the current position or the ant moves until it finds a good place).
	 *
	 */
	public void drop();
	
	/**
	 * Returns true if spreadMode is set, false otherwise.
	 * @return spreadMode
	 */
	public boolean isSpreadMode();

	/**
	 * Sets spreadMode on (true) or off (false). When spreadMode is on a modified neighbourhood-
	 * function is used, where the number of the neighbouring items is not important, so that the items
	 * are spread on the grid in a sorted fashion.
	 * @param spreadMode
	 */
	public void setSpreadMode(boolean spreadMode);
	
	public void setPosition(int[] pos);
	public int[] getPosition();
}
