package oiler.util;

import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * A base class for all IntLists.
 * @author Samad Lotia
 */
public abstract class AbstractIntList extends AbstractIntCollection implements IntList
{
	public abstract int get(int index);

	/**
	 * Adds an element to the end of the list.
	 */
	public boolean add(int element)
	{
		return add(size(), element);
	}

	/**
	 * Throws UnsupportedOperationException for
	 * immutable types.
	 */
	public int set(int index, int element)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws UnsupportedOperationException for
	 * immutable types.
	 */
	public boolean add(int index, int element)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws UnsupportedOperationException for
	 * immutable types.
	 */
	public int removeAtIndex(int index)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Searches the list for the element
	 * and removes it.
	 * @return true if the list was modified.
	 */
	public boolean remove(int element)
	{
		final int index = indexOf(element);
		if (index < 0)
			return false;

		remove(index);
		return true;
	}

	/**
	 * Returns true if the element is in the list.
	 */
	public boolean contains(int element)
	{
		return indexOf(element) >= 0;
	}

	/**
	 * Returns the index of the element, or
	 * -1 if it is not in the list.
	 * If the element is in the list
	 * more than once, this method
	 * returns the lowest index.
	 */
	public int indexOf(int element)
	{
		for (int i = 0; i < size(); i++)
			if (get(i) == element)
				return i;
		return -1;
	}

	/**
	 * Returns the index of the element, or
	 * -1 if it is not in the list.
	 * If the element is in the list
	 * more than once, this method
	 * returns the highest index.
	 */
	public int lastIndexOf(int element)
	{
		for (int i = size() - 1; i >= 0; i--)
			if (get(i) == element)
				return i;
		return -1;
	}

	/**
	 * Converts the list to a List&lt;Integer&gt;.
	 */
	public List<Integer> toList()
	{
		final List<Integer> list = new ArrayList<Integer>(size());
		for (int i = 0; i < size(); i++)
			list.add(get(i));
		return list;
	}

	/**
	 * Returns an iterator for all elements in the list.
	 */
	public IntIterator iterator()
	{
		return new IntIterator()
		{
			private int index = 0;

			public int numRemaining()
			{
				return size() - index;
			}

			public int next()
			{
				if (index >= size())
					throw new NoSuchElementException();
				return get(index++);
			}

			public boolean hasNext()
			{
				return index < size();
			}
		};
	}

	/**
	 * Returns true if <code>o</code> is an IntList
	 * and if it contains the same elements
	 * at the same indices.
	 */
	public boolean equals(final Object o)
	{
		if (o == null)
			return false;
		if (!(o instanceof IntList))
			return false;

		IntList that = (IntList) o;
		if (that.size() != size())
			return false;
		for (int i = 0; i < that.size(); i++)
			if (get(i) != that.get(i))
				return false;
		return true;
	}
}
