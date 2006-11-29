package nct.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashSet;

import nct.util.IntIterator;
import nct.util.IntIntHash;
import nct.util.IntIteratorWrapper;

/**
  * A Set object for containing ints.
  * Sets behave exactly like lists except no two integers with the same value
  * can exist in the same set.
  *
  * <p>Time complexity issues: IntSet internally represents the set as a hash
  * table, where ints with a defined value of IN_SET are in the set, whereas
  * ints not in the set are not defined in the hash or have the defined value
  * of NOT_IN_SET. Time complexity is described in Big-Oh notation, where n
  * is the number of total ints in the set and ints that were in the set but
  * have been removed. The methods add, contain, size, and remove are in constant
  * time; the methods clear, isEmpty, iterator, and toArray are in
  * linear time.</p>
  *
  * @author Samad Lotia
  */
public class IntSet extends IntIntHash implements Iterable<Integer>, Comparable<IntSet>
{
	private static final int IN_SET		= 7;
	private static final int NOT_IN_SET	= 26;

	protected int size = 0;
	
	public IntSet()
	{
		super();
	}

	/**
	 * Adds an int to the set.
	 *
	 * <p>Time complexity: O(1)</p>
	 *
	 * @return true if n was added to the set.
	 *         false if n was already in the set, and therefore not added.
	 */
	public boolean add(int n)
	{
		if (super.get(n) == IN_SET)
			return false;
		else
		{
			super.put(n, IN_SET);
			size++;
			return true;
		}
	}

	/**
	 * Clears the set so that it will no longer contain any ints.
	 *
	 * <p>Time complexity: O(n)</p>
	 */
	public void clear()
	{
		size = 0;
		super.empty();
	}

	/**
	 * Checks to see if an int is in the set.
	 *
	 * <p>Time complexity: O(1)</p>
	 *
	 * @return true if n is in the set.
	 *         false if n is not in the set.
	 */
	public boolean contains(int n)
	{
		return super.get(n) == IN_SET;
	}

	/**
	 * Checks to see if there are any ints in the set.
	 *
	 * <p>Time complexity: O(n)</p>
	 *
	 * @return true if the set does not contain any ints.
	 *         false if the set contains at least one int.
	 */
	public boolean isEmpty()
	{
		return size == 0;
	}

	/**
	 * Returns an iterator over all the ints contained in the set.
	 * <p><i>WARNING:</i> <tt>next()</tt> has undefined behavior if it
	 * is called when <tt>hasNext()</tt> returns <tt>false</tt>.</p>
	 */
	public IntIterator intIterator()
	{
		final int[] keys   = super.m_keys;
		final int[] values = super.m_vals;
		final int   size   = this.size;

		return new IntIterator()
		{
			int index = 0;
			int remaining = size;
			
			public int numRemaining()
			{
				return remaining;
			}

			public boolean hasNext()
			{
				return (remaining != 0);
			}

			public int next()
			{
				while (values[index] != IN_SET)
					index++;

				remaining--;
				return keys[index++];
			}
		};
	}

	/**
	 * An iterator for all elements in the set.
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
	 * Remove an int from the set.
	 *
	 * <p>Time complexity: O(1)</p>
	 *
	 * @return true if n was removed from the set.
	 *         false if n was not in the set and therefore not removed.
	 */
	public boolean remove(int n)
	{
		if (super.get(n) != IN_SET)
			return false;
		else
		{
			super.put(n, NOT_IN_SET);
			size--;
			return true;
		}
	}

	/**
	 * Return the total number of ints in the set.
	 *
	 * <p>Time complexity: O(1)</p>
	 */
	public int size()
	{
		return size;
	}

	/**
	 * Compares two IntSets.
	 *
	 * <p>Time complexity: O(n)</p>
	 *
	 * @return 0 if both sets have exactly the same ints.
	 *         Otherwise returns the difference in the size
	 *         of the sets; if they have the same size, returns
	 *         the int in the receiver's set that is not in
	 *         <tt>that</tt>'s set.
	 */
	public int compareTo(IntSet that)
	{
		boolean equals = true;
		
		if (this.size() != that.size())
			return this.size() - that.size();
		
		int i = 0;
		while (i < super.m_keys.length)
		{
			if (super.m_vals[i] == IN_SET)
				if (!that.contains(super.m_keys[i]))
					return super.m_keys[i];
			
			i++;
		}
		
		return 0;
	}

	/**
	 * Return a copy of all ints in the set.
	 *
	 * <p>Time complexity: O(n)</p>
	 */
	public int[] toArray()
	{
		int[] array       = new int[size];
		int   array_index = 0;
		
		for (int i = 0; i < super.m_vals.length; i++)
			if (super.m_vals[i] == IN_SET)
				array[array_index++] = super.m_keys[i];
		
		return array;
	}

	/**
	 * Return a copy set.
	 *
	 * <p>Time complexity: O(n)</p>
	 */
	public Set<Integer> toSet()
	{
		Set<Integer> set = new HashSet<Integer>();

		for (int i = 0; i < super.m_keys.length; i++)
			if (super.m_vals[i] == IN_SET)
				set.add(new Integer(super.m_keys[i]));

		return set;
	}
}
