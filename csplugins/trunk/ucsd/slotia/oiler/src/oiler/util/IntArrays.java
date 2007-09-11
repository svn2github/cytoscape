package oiler.util;

import java.util.List;
import java.util.ArrayList;

/**
 * A singleton class that operates on <code>int[]</code>s.
 *
 * <p>This class is useful if one does not want the overhead
 * and convenienceof an <code>IntArray</code> but needs a dynamically
 * resizeable array of <code>int</code>s.</p>
 *
 * @author Samad Lotia
 */
public class IntArrays
{
	protected IntArrays() {}

	/**
	 * Add an element to the end of the list.
	 * The index of the new element is the size of the array
	 * before <code>add()</code> was called.
	 */
	public static int[] add(final int[] array, final int element)
	{
		return add(array, array.length, element);
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
	public static int[] add(final int[] array, final int index, final int element)
	{
		if (index < 0)
			return array;

		int[] newarray;

		// If index is not within the array's index range...
		if (index >= array.length)
		{
			// The new element will be at a higher index than the other elements
			newarray = new int[index + 1];
			// Copy all the old elements to newarray
			System.arraycopy(array, 0, newarray, 0, array.length);
		}
		else
		{
			// The new element will be somewhere inside the array's index range
			newarray = new int[array.length + 1];
			// Copy all the old elements before index to newarray
			System.arraycopy(array, 0, newarray, 0, index);
			// Copy all the old elements after index to newarray
			System.arraycopy(array, index, newarray, index + 1, array.length - index);
		}

		newarray[index] = element;
		return newarray;
	}

	/**
	 * Find the index of an element.
	 *
	 * @return Index of <tt>element</tt> in the array or -1 if the
	 *         element does not exist in the array. If there are
	 *         multiple elements with the same value, this will
	 *         return the lowest index.
	 */
	public static int indexOf(final int[] array, final int element)
	{
		for (int i = 0; i < array.length; i++)
			if (array[i] == element)
				return i;

		return -1;
	}

	/**
	 * Determines if an array has an element.
	 *
	 * @return <code>true</code> if the array has <code>element</code>
	 */
	public static boolean contains(final int[] array, final int element)
	{
		return (indexOf(array, element) != -1);
	}

	/**
	 * Find the index of an element.
	 *
	 * @return Index of <tt>element</tt> in the array or -1 if the
	 *         element does not exist in the array. If there are
	 *         multiple elements with the same value, this will
	 *         return the highest index.
	 */
	public static int lastIndexOf(final int[] array, final int element)
	{
		for (int i = array.length - 1; i >= 0; i--)
			if (array[i] == element)
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
	 * @throws IndexOutOfBoundsException Thrown if <tt>index</tt> is out of bounds
	 */
	public static int[] remove(final int[] array, final int index)
	{
		if (index < 0 || index >= array.length)
			throw new IndexOutOfBoundsException("index requested: " + index + ", size of array: " + array.length);
		
		final int[] newarray = new int[array.length - 1];
		// Copy the elements before index to newarray
		System.arraycopy(array, 0, newarray, 0, index);
		// Copy the elements after index to newarray
		System.arraycopy(array, index + 1, newarray, index, array.length - index - 1);

		return newarray;
	}

	/**
	 * Converts an <code>int[]</code> to a <code>List<Integer></code>.
	 */
	public static List<Integer> toList(final int[] array)
	{
		final List<Integer> list = new ArrayList<Integer>(array.length);
		for (int i = 0; i < array.length; i++)
			list.add(array[i]);
		return list;
	}

	/**
	 * Returns a printable String of the array.
	 */
	public static String toString(final int[] array)
	{
		final StringBuilder result = new StringBuilder("[ ");
		for (int i = 0; i < array.length; i++)
		{
			result.append(array[i]);
			result.append(" ");
		}
		result.append("]");
		return result.toString();
	}
}
