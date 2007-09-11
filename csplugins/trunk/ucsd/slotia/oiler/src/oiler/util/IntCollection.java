package oiler.util;

/**
 * An interface for a collection of <code>int</code>s.
 * @author Samad Lotia
 */
public interface IntCollection extends IntIterable
{
	/**
	 * Returns the number of elements in the receiver.
	 */
	public int size();
	
	/**
	 * Returns true if the receiver is empty.
	 */
	public boolean isEmpty();

	/**
	 * Returns true if the receiver contains the element.
	 */
	public boolean contains(int element);

	/**
	 * Returns an iterator for all elements in the receiver.
	 */
	public IntIterator iterator();

	/**
	 * Returns a new array with all elements in the receiver.
	 */
	public int[] toArray();

	/**
	 * Adds an element to the collection.
	 * @return true if the receiver was changed.
	 */
	public boolean add(int element);

	/**
	 * Removes an element to the collection.
	 * @return true if the receiver was changed.
	 */
	public boolean remove(int element);

	/**
	 * Adds all elements in <code>collection</code>.
	 * @return true if the receiver was modified.
	 */
	public boolean addAll(IntCollection collection);

	/**
	 * Returns true if all elements of <code>collection</code>
	 * exist in the receiver.
	 */
	public boolean containsAll(IntCollection collection);

	/**
	 * Removes all elements in <code>collection</code>.
	 * @return true if the receiver was modified.
	 */
	public boolean removeAll(IntCollection collection);

	/**
	 * Clears the receiver of all elements.
	 */
	public void clear();

	public boolean equals(Object that);
	public int hashCode();
}
