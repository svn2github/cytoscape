package oiler.util;

import java.util.Set;

/**
 * A generic interface for sets with <code>int</code>s as elements.
 * @author Samad Lotia
 */
public interface IntSet extends IntCollection
{
	/**
	 * Creates a new Set with all the elements in the set.
	 */
	public Set<Integer> toSet();
}
