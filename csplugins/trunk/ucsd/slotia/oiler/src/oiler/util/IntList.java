package oiler.util;

import java.util.List;

/**
 * A generic interface for lists with <code>int</code>s as elements.
 * @author Samad Lotia
 */
public interface IntList extends IntCollection
{
	/**
	 * Add an element to the list at the specified index.
	 * How a list handles an index that is not in range
	 * is implementation-specific.
	 */
	public boolean add(int index, int element);

	/**
	 * Return an element at the specified index.
	 */
	public int get(int index);

	/**
	 * Returns the lowest index where the element is found in the list,
	 * or -1 if the element is not in the list.
	 */
	public int indexOf(int element);

	/**
	 * Returns the highest index where the element is found in the list,
	 * or -1 if the element is not in the list.
	 */
	public int lastIndexOf(int element);

	/**
	 * Removes an element at the specified index from the list and
	 * returns the element.
	 */
	public int removeAtIndex(int index);

	/**
	 * Sets the element at a specified index.
	 */
	public int set(int index, int element);

	/**
	 * Creates a new List with all the elements in the list.
	 */
	public List<Integer> toList();
}
