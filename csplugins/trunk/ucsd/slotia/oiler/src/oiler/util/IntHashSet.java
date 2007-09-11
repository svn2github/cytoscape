package oiler.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashSet;

/**
  * A set for <code>int</code>s.
  * Sets behave exactly like lists except no two integers with the same value
  * can exist in the same set.
  *
  * @author Samad Lotia
  */
public class IntHashSet extends AbstractIntCollection implements IntSet 
{
	/**
	 * A dummy value to put into the map.
	 * It is the Abjadi value of the author's
	 * first name (Suad + Meem + Daal).
	 */
	protected static final int IN_SET = 90 + 40 + 4;

	protected final IntIntHashMap map;
	
	public IntHashSet()
	{
		map = new IntIntHashMap();
	}

	public IntHashSet(final int capacity)
	{
		map = new IntIntHashMap(capacity);
	}

	public IntHashSet(final IntCollection other)
	{
		map = new IntIntHashMap(other.size());
		IntIterator iterator = other.iterator();
		while (iterator.hasNext())
			add(iterator.next());
	}

	/**
	 * Adds an int to the set.
	 *
	 * @return true if n was added to the set,
	 *         false if n was already in the set.
	 */
	public boolean add(int n)
	{
		return map.put(n, IN_SET) == IN_SET;
	}

	/**
	 * Empties the set so that it will no longer contain any ints.
	 */
	public void clear()
	{
		map.clear();
	}

	/**
	 * Checks to see if an int is in the set.
	 *
	 * @return true if n is in the set.
	 *         false if n is not in the set.
	 */
	public boolean contains(int n)
	{
		return map.get(n) == IN_SET;
	}

	/**
	 * Returns an iterator over all the ints contained in the set.
	 */
	public IntIterator iterator()
	{
		return map.keysIterator();
	}

	/**
	 * Remove an int from the set.
	 *
	 * @return true if n was removed from the set.
	 *         false if n was not in the set and therefore not removed.
	 */
	public boolean remove(int n)
	{
		return map.remove(n) != IntIntHashMap.KEY_NOT_FOUND;
	}

	/**
	 * Return the total number of ints in the set.
	 */
	public int size()
	{
		return map.size;
	}

	/**
	 * Tests equality of two IntSets.
	 */
	public boolean equals(final Object o)
	{
		if (o == null)
			return false;
		if (!(o instanceof IntSet))
			return false;
		IntSet that = (IntSet) o;
		if (this.size() != that.size())
			return false;
		
		final IntIterator elementsIterator = iterator();
		while (elementsIterator.hasNext())
		{
			final int element = elementsIterator.next();
			if (!that.contains(element))
				return false;
		}
		return true;
	}

	/**
	 * Return a copy set.
	 *
	 * <p>Time complexity: O(n)</p>
	 */
	public Set<Integer> toSet()
	{
		final Set<Integer> set = new HashSet<Integer>();
		final IntIterator elementsIterator = iterator();

		while (elementsIterator.hasNext())
		{
			final int element = elementsIterator.next();
			set.add(new Integer(element));
		}
		return set;
	}
}
