package oiler.util;

/**
 * Base class for collections.
 * @author Samad Lotia
 */
public abstract class AbstractIntCollection implements IntCollection
{
	// Query methods

	public abstract IntIterator iterator();
	public abstract int size();

	public boolean isEmpty()
	{
		return size() == 0;
	}

	/**
	 * Gets the receiver's iterator and returns true if
	 * the iterator returns an <code>int</code> equal
	 * to <code>element</code>.
	 */
	public boolean contains(final int element)
	{
		final IntIterator e = iterator();
		while (e.hasNext())
			if (e.next() == element)
				return true;
		return false;
	}

	/**
	 * Gets the receiver's iterator and constructs
	 * an array with all of the <code>int</code>s
	 * the iterator returns.
	 */
	public int[] toArray()
	{
		final int[] result = new int[size()];
		final IntIterator e = iterator();
		for (int i = 0; e.hasNext(); i++)
			result[i] = e.next();
		return result;
	}

	// Bulk query methods

	/**
	 * Gets <code>collection</code>'s iterator and
	 * returns true if the receiver returns true
	 * for each <code>int</code> the iterator
	 * returns.
	 */
	public boolean containsAll(final IntCollection collection)
	{
		final IntIterator e = collection.iterator();
		while (e.hasNext())
			if (!contains(e.next()))
				return false;
		return true;
	}

	public abstract boolean equals(final Object that);

	// Algorithm from <i>The Art of Computer Programming, Volume 3</i> by
	// Donald E. Knuth, chapter 6.4.
	public int hashCode()
	{
		int hash = size();
		IntIterator e = iterator();
		while (e.hasNext())
			hash = ((hash << 5) ^ (hash >> 27)) ^ e.next();
		return (hash & 0x7FFFFFFF);
	}

	// Modification methods -- throws UnsupportedOperationException

	/**
	 * Throws <code>UnsupportedOperationException</code>
	 * for immutable types.
	 */
	public boolean add(int element)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws <code>UnsupportedOperationException</code>
	 * for immutable types.
	 */
	public boolean remove(int element)
	{
		throw new UnsupportedOperationException();
	}

	// Bulk modification methods

	/**
	 * Gets <code>collection</code>'s iterator and
	 * returns true if the receiver added any of
	 * the <code>int</code>s the iterator
	 * returns.
	 */
	public boolean addAll(final IntCollection collection)
	{
		final IntIterator e = collection.iterator();
		boolean result = false;
		while (e.hasNext())
			if (add(e.next()))
				result = true;
		return result;
	}

	/**
	 * Gets <code>collection</code>'s iterator and
	 * returns true if the receiver removed any of
	 * the <code>int</code>s the iterator
	 * returns.
	 */
	public boolean removeAll(final IntCollection collection)
	{
		final IntIterator e = collection.iterator();
		boolean result = false;
		while (e.hasNext())
			if (remove(e.next()))
				result = true;
		return result;
	}

	public abstract void clear();
}
