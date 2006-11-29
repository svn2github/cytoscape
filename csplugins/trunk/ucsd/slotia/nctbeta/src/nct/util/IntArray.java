package nct.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import nct.util.IntIterator;
import nct.util.IntIteratorWrapper;

/**
 * Resizeable array implementation for ints.
 *
 * <p>Note: time complexity for each method is presented
 * in Big-Oh notation where "n" represents the number of
 * elements in the array.</p>
 *
 * <p>Performance issues: this class places greater emphasis
 * on reducing space over time costs. Its internal
 * array always stores exactly as many elements are neeed,
 * never any more. This reduces time efficiency, as every
 * <tt>add()</tt> or <tt>remove()</tt> requires a resize.</p>
 *
 * @author Samad Lotia
 */
public class IntArray implements Iterable<Integer>
{
	protected int[] elements;

	/**
	 * Constructs an empty array with no elements.
	 */
	public IntArray()
	{
		elements = new int[0];
	}

	/**
	 * Add an element to the end of the list.
	 * The index of the new element is the size of the array
	 * before <tt>add()</tt> was called.
	 *
	 * <p>Time complexity: O(n) to resize the array.</p>
	 *
	 * @return <tt>true</tt> if add was successful
	 */
	public boolean add(int element)
	{
		return add(elements.length, element);
	}

	/**
	 * Add an element at the specified index in the list.
	 *
	 * <p>Time complexity: O(n) to resize the array.</p>
	 *
	 * @param index An index greater than zero. If <tt>index</tt> is
	 *              an index larger than the size of the array, the elements
	 *              between the end of the array before adding and the element
	 *              right before <tt>index</tt> will be padded with zeros. For example
	 *              assume there is an IntArray with four elements with indices
	 *              from 0 to 4. If <tt>add()</tt> was called with <tt>index</tt> 8,
	 *              the array will now have indices 0 to 8, where 5 to 7 are filled
	 *              with zeros.
	 *
	 * <p><tt>
	 * before:<br>
	 *   indices: 0 | 1 | 2 | 3 | 4<br>
	 *   -----------+---+---+---+--<br>
	 *   values : B | A | L | Q | N<br>
	 * <br>
	 * after add(8, A):<br>
	 *   indices: 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8<br>
	 *   -----------+---+---+---+---+---+---+---+--<br>
	 *   values : B | A | L | Q | N | 0 | 0 | 0 | A<br>
	 * </tt></p>
	 *
	 * @return <tt>true</tt> if add was successful.
	 *         <tt>false</tt> if index was less than 0
	 */
	public boolean add(int index, int element)
	{
		if (index < 0)
			return false;

		int[] newarray;
		if (index >= elements.length)
		{
			newarray = new int[index + 1];
			System.arraycopy(elements, 0, newarray, 0, elements.length);
		}
		else
		{
			newarray = new int[elements.length + 1];
			System.arraycopy(elements, 0, newarray, 0, index);
			System.arraycopy(elements, index, newarray, index + 1, elements.length - index);
		}
			
		newarray[index] = element;
		elements = newarray;
		return true;
	}

	/**
	 * Clear the array so that it no longer contains any elements.
	 *
	 * <p>Time complexity: O(1)</p>
	 */
	public void clear()
	{
		elements = new int[0];
	}

	/**
	 * Get the value at <tt>index</tt>.
	 * This method performs an index check. If the index is out
	 * of bounds, it will throw <tt>IndexOutOfBoundsException</tt>.
	 *
	 * <p>Time complexity: O(1)</p>
	 *
	 * @param index A valid index in range from 0 
	 *              to <tt>size()-1</tt>, inclusive.
	 */
	public int get(int index)
	{
		if (index < 0 || index >= elements.length)
			throw new IndexOutOfBoundsException("index requested: " + index + ", size of array: " + elements.length);

		return elements[index];
	}

	/**
	 * Get the value at <tt>index</tt>.
	 * @param index A valid index in range from 0 
	 *              to <tt>size()-1</tt>, inclusive.
	 *
	 * <p>Time complexity: O(1)</p>
	 *
	 * <p><i>WARNING:</i> This method does not perform index
	 * checking. If <tt>index</tt> is out of bounds, this method's
	 * return value is undefined. <i>MAKE SURE</i> valid indices
	 * are being passed to this method! Use this only if <tt>get()</tt>
	 * is a bottleneck in performance.</p>
	 */
	public int getQuick(int index)
	{
		return elements[index];
	}

	/**
	 * Find the index of an element.
	 *
	 * <p>Time complexity: O(n)</p>
	 *
	 * @return Index of <tt>element</tt> in the array; if there are
	 *         multiple elements with the same value, this will
	 *         return the lowest index or -1 if the element
	 *         does not exist in the array.
	 */
	public int indexOf(int element)
	{
		for (int i = 0; i < elements.length; i++)
			if (elements[i] == element)
				return i;

		return -1;
	}

	/**
	 * A low-cost iterator for all the elements in the array.
	 * <i>WARNING:</i> The behavior of <tt>next()</tt> is
	 * undefined if <tt>hasNext()</tt> returns <tt>false</tt>.
	 */
	public IntIterator intIterator()
	{
		return new IntIterator()
		{
			private int index = 0;

			public boolean hasNext()
			{
				return index < elements.length;
			}

			public int next()
			{
				return elements[index++];
			}

			public int numRemaining()
			{
				return elements.length - index;
			}
		};
	}

	/**
	 * An iterator for all elements in the array.
	 * This iterator packages all elements into an <tt>Integer</tt>
	 * object. This incurs a higher time and space cost than
	 * <tt>IntIterator</tt>.
	 *
	 * <p>This method returns <tt>NoSuchElementException</tt> if
	 * <tt>next()</tt> is called when <tt>hasNext()</tt> returns
	 * <tt>false</tt>.</p>
	 *
	 * <p><tt>remove()</tt> is an invalid operation.</p>
	 */
	public Iterator<Integer> iterator()
	{
		return new IntIteratorWrapper(this.intIterator());
	}
	
	/**
	 * Find the index of an element.
	 *
	 * <p>Time complexity: O(n)</p>
	 *
	 * @return Index of the element in the array; if there are
	 *         multiple elements with the same value, this will
	 *         return the highest index. Returns -1 if the element
	 *         does not exist in the array.
	 */
	public int lastIndexOf(int element)
	{
		for (int i = elements.length - 1; i >= 0; i--)
			if (elements[i] == element)
				return i;

		return -1;
	}

	/**
	 * Remove a element from the array at <tt>index</tt>.
	 *
	 * <p>Time complexity: O(n) for resizing the array</p>
	 *
	 * @param index The index of the element to be removed.
	 *              If the index is out of bounds,
	 *              <tt>IndexOutOfBoundsException</tt> is thrown.
	 * @return The element that was removed.
	 */
	public int remove(int index)
	{
		if (index < 0 || index >= elements.length)
			throw new IndexOutOfBoundsException("index requested: " + index + ", size of array: " + elements.length);
		
		int element = elements[index];

		int[] newarray = new int[elements.length - 1];
		System.arraycopy(elements, 0, newarray, 0, index);
		System.arraycopy(elements, index + 1, newarray, index, elements.length - index - 1);

		elements = newarray;
		return element;
	}
	
	/**
	 * Set the value at <tt>index</tt>.
	 *
	 * <p>Time complexity: O(1)</p>
	 *
	 * This method performs an index check. If the index is out
	 * of bounds, it will throw an IndexOutOfBoundsException.
	 * @param index A valid index in range from 0 
	 *              to <tt>size()-1</tt>, inclusive.
	 */
	public void set(int index, int element)
	{
		if (index < 0 || index >= elements.length)
			throw new IndexOutOfBoundsException("index requested: " + index + ", size of array: " + elements.length);

		elements[index] = element;
	}
	
	/**
	 * Set the value at <tt>index</tt>.
	 * @param index A valid index in range from 0 
	 *              to <tt>size()-1</tt>, inclusive.
	 *
	 * <p>Time complexity: O(1)</p>
	 *
	 * <p><i>WARNING:</i> This method does not perform index
	 * checking. If <tt>index</tt> is out of bounds, this method's
	 * return value is undefined. <i>MAKE SURE</i> valid indices
	 * are being passed to this method! Use this only if <tt>set()</tt>
	 * is a bottleneck in performance.</p>
	 */

	public void setQuick(int index, int element)
	{
		elements[index] = element;
	}

	/**
	 * @return The number of elements in the array
	 */
	public int size()
	{
		return elements.length;
	}

	/**
	 * Return a copy of all elements in the array.
	 * <p>Time complexity: O(n)</p>
	 */
	public int[] toArray()
	{
		int[] array = new int[elements.length];
		System.arraycopy(elements, 0, array, 0, elements.length);
		return array;
	}
}
