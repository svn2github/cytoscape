package oiler.util;

import java.util.Iterator;
import java.util.Set;

/**
 * An interface for all maps where
 * the keys and values are ints.
 * @author Samad Lotia
 */
public interface IntIntMap
{
	/**
	 * Clear the map.
	 */
	public void clear();

	/**
	 * Returns true if the map contains the key.
	 */
	public boolean containsKey(int key);

	/**
	 * Returns true if the map contains the value.
	 */
	public boolean containsValue(int value);

	public boolean equals(Object that);

	/**
	 * Returns the set of all entries in the map.
	 */
	public Set<Entry> entrySet();

	/**
	 * Returns an iterator of all entries in the map.
	 */
	public Iterator<Entry> entryIterator();

	/**
	 * Gets the value associated with the key.
	 */
	public int get(int key);

	/**
	 * Returns true if the map is empty.
	 */
	public boolean isEmpty();

	/**
	 * Returns the set of all keys.
	 */
	public IntSet keySet();

	/**
	 * Creates a (key,value) pair.
	 * @return the old value associated with key, or
	 * <code>KEY_NOT_FOUND</code> if they key was
	 * not previous associated with any value.
	 */
	public int put(int key, int value);

	/**
	 * Creates a (key,value) pair for each key
	 * and value in <code>other</code>.
	 */
	public boolean putAll(IntIntMap other);

	/**
	 * Removes a (key,value) pair.
	 */
	public int remove(int key);

	/**
	 * Returns the number of (key,value)
	 * pairs.
	 */
	public int size();

	/**
	 * Returns all the values in the map.
	 */
	public IntCollection values();

	/**
	 * An interface for all entries in the map.
	 */
	public interface Entry
	{
		public int key();
		public int value();
		public int hashCode();
		public boolean equals(Object o);
	}
}
