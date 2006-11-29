package nct.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import nct.util.IntIterator;

/**
 * Wraps an IntIterator with an Iterator<Integer>.
 * This is used to convert IntIterators to Iterator<Integer>'s.
 *
 * <p><i>NOTE:</i> <tt>Iterator<Integer></tt> incurs a higher
 * time and space cost because ints are wrapped into Integer
 * objects. However <tt>Iterator<Integer></tt> is more safer since
 * excessive calls to <tt>next()</tt> will throw
 * <tt>NoSuchElementException</tt></p>
 *
 * <p>Why is there a separate class to wrap IntIterators?
 * Typically conversions are done by static methods.
 * However by having this class, methods that return Iterator<Integer>
 * have the option to override <tt>remove()</tt> if its implementation
 * is needed. This is not possible by a static method that does
 * the conversion.</p>
 *
 * @author Samad Lotia
 */
class IntIteratorWrapper implements Iterator<Integer>
{
	protected IntIterator intIterator;

	public IntIteratorWrapper(IntIterator intIterator)
	{
		this.intIterator = intIterator;
	}

	public boolean hasNext()
	{
		return intIterator.hasNext();
	}

	public Integer next()
	{
		if (intIterator.hasNext())
			return new Integer(intIterator.next());
		else
			throw new NoSuchElementException();
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
