package oiler.util;

import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/**
 * Resizable array implementation for ints.
 *
 * @author Samad Lotia
 */
public class IntArray extends AbstractIntList implements IntList
{
	protected int[] elements;
	protected int modifications = 0; // number of modifications made to the array

	/**
	 * Constructs an empty array with no elements.
	 */
	public IntArray()
	{
		this(0);
	}

	/**
	 * Constructs an array of zeros with
	 * initial capacity.
	 */
	public IntArray(final int initialCapacity)
	{
		if (initialCapacity < 0)
			throw new IllegalArgumentException("initialCapacity < 0");

		elements = new int[initialCapacity];
	}


	/**
	 * Constructs an array with elements from <code>array</code>.
	 */
	public IntArray(final int[] array)
	{
		if (array == null)
			throw new IllegalArgumentException("array == null");

		elements = new int[array.length];
		System.arraycopy(array, 0, elements, 0, array.length);
	}

	/**
	 * Constructs an array with elements from <code>collection</code>.
	 */
	public IntArray(final IntCollection collection)
	{
		if (collection == null)
			throw new IllegalArgumentException("collection == null");
		elements = new int[collection.size()];
		final IntIterator e = collection.iterator();
		for (int i = 0; e.hasNext(); i++)
			elements[i] = e.next();
	}

	/**
	 * Add an element at the specified index in the list.
	 *
	 * @param index An index greater than zero. If <tt>index</tt> is
	 *              an index larger than the size of the array, the elements
	 *              between the end of the array before adding and the element
	 *              right before <tt>index</tt> will be padded with zeros. For example
	 *              assume there is an IntArray with four elements with indices
	 *              0 to 4. If <tt>add()</tt> was called with <tt>index</tt> 8,
	 *              the array will now have indices 0 to 8, where 5 to 7 are filled
	 *              with zeros.
	 *
	 * <p><pre>
	 * <i>before:</i>
	 *   indices: 0 | 1 | 2 | 3 | 4
	 *   -----------+---+---+---+--
	 *   values : B | A | L | Q | N
	 *
	 * <i>after add(8, A):</i>
	 *   indices: 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8
	 *   -----------+---+---+---+---+---+---+---+--
	 *   values : B | A | L | Q | N | 0 | 0 | 0 | A
	 *
	 * <i>after add(2, A):</i>
	 *   indices: 0 | 1 | 2 | 3 | 4 | 5
	 *   -----------+---+---+---+---+--
	 *   values : B | A | A | L | Q | N
	 * </pre></p>
	 */
	public boolean add(final int index, final int element)
	{
		modifications++;
		elements = IntArrays.add(elements, index, element);
		return true;
	}

	/**
	 * Clear the array so that it no longer contains any elements.
	 */
	public void clear()
	{
		modifications++;
		elements = null; // throw away elements
		elements = new int[0];
	}

	/**
	 * Get the value at <tt>index</tt>.
	 * This method performs an index check. If the index is out
	 * of bounds, it will throw <tt>IndexOutOfBoundsException</tt>.
	 *
	 * @param index A valid index in range from 0 
	 *              to <tt>size()-1</tt>, inclusive.
	 * @throws IndexOutOfBoundsException Thrown if <tt>index</tt> is out of bounds
	 */
	public int get(final int index)
	{
		if (index < 0 || index >= elements.length)
			throw new IndexOutOfBoundsException("index requested: " + index + ", size of array: " + elements.length);

		return elements[index];
	}


	/**
	 * Remove an element from the array at <code>index</code>.
	 *
	 *
	 * <p>Time complexity: O(n) for resizing the array</p>
	 *
	 * @param index The index of the element to be removed.
	 *              If the index is out of bounds,
	 *              <tt>IndexOutOfBoundsException</tt> is thrown.
	 * @return The element that was removed.
	 * @throws IndexOutOfBoundsException Thrown if <tt>index</tt> is out of bounds
	 */
	public int removeAtIndex(final int index)
	{
		final int element = elements[index];
		elements = IntArrays.remove(elements, index);
		modifications++;
		return element;
	}
	
	/**
	 * Set the value at <code>index</code>.
	 *
	 * <p>Time complexity: O(1)</p>
	 *
	 * This method performs an index check. If the index is out
	 * of bounds, it will throw an IndexOutOfBoundsException.
	 * @param index A valid index in range from 0 
	 *              to size()-1, inclusive.
	 * @throws IndexOutOfBoundsException Thrown if <tt>index</tt> is out of bounds
	 */
	public int set(final int index, final int element)
	{
		if (index < 0 || index >= elements.length)
			throw new IndexOutOfBoundsException("index requested: " + index + ", size of array: " + elements.length);

		final int oldElement = elements[index];
		elements[index] = element;
		modifications++;
		return oldElement;
	}
	
	/**
	 * Returns the number of elements in the array.
	 */
	public int size()
	{
		return elements.length;
	}

	public IntIterator iterator()
	{
		return new IntIterator()
		{
			private int index = 0;
			private final int expectedModifications = modifications;

			public int numRemaining()
			{
				return elements.length - index;
			}

			public int next()
			{
				if (index >= size())
					throw new NoSuchElementException();
				if (expectedModifications != modifications)
					throw new ConcurrentModificationException();
				return elements[index++];
			}

			public boolean hasNext()
			{
				return index < elements.length;
			}
		};
	}
}
