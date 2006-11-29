package nct.util;

/**
 * An iteration over a list of ints
 *
 * @author Samad Lotia
 */
public interface IntIterator
{

	/**
	 * @return <tt>true</tt> if a call to <tt>next()</tt> will be successful
	 */
	public boolean hasNext();

	/**
	 * @return the next int in the iteration.  If <tt>hasNext()</tt> returns
	 * <tt>false</tt> before <tt>next()</tt> is called, the behavior of this
	 * iterator becomes undefined.
	 */
	public int next();

	/**
	 * @return the number of times <tt>next()</tt> can be called successfully.
	 * This method's implementation is optional. It will throw
	 * <tt>UnsupportedOperationException</tt> if the method is
	 * not supported.
	 */
	public int numRemaining();
}
