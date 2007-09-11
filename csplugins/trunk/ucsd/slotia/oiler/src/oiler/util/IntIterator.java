package oiler.util;

/**
 * An iterator for <code>int</code>s.
 *
 * @author Samad Lotia
 */
public interface IntIterator
{
	/**
	 * Returns <code>true</code> if a call to <code>next()</code> will be successful
	 */
	public boolean hasNext();

	/**
	 * Returns the next int in the iteration.
	 */
	public int next();

	/**
	 * Return the number of times <tt>next()</tt> can be called successfully.
	 * It will throw <code>UnsupportedOperationException</code> if the method is
	 * not supported.
	 */
	public int numRemaining();
}
