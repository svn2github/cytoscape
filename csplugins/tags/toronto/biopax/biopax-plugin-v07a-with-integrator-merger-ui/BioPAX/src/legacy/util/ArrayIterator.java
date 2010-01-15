
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package legacy.util;

import legacy.IndexIterator;

import java.util.NoSuchElementException;


/**
 * <b>This class is in a very unfinished state; development effort on this
 * class has been suspended until further notice; don't use this class
 * unless you're the author.
 * </b><p>
 * Creates an <code>IndexIterator</code> object out of an array of integers.
 */
public class ArrayIterator implements IndexIterator {
	private final int[] m_indices;
	private final int m_last; // Exclusive (non-inclusive) last index.
	private int m_iter; // Index of next item to be returned.

	/**
	 * Defines an <code>IndexIterator</code> by returning values
	 * <code>indices[0]</code>, <code>indices[1]</code>, ...
	 * <nobr><code>indices[indices.length - 1]</code></nobr> as
	 * sequential <code>next()</code> return values.
	 * No copy of the <code>indices</code> array is made.
	 *
	 * @throws NullPointerException if <nobr><code>indices == null</code></nobr>.
	 */
	public ArrayIterator(int[] indices) {
		this(indices, 0, indices.length);
	}

	/**
	 * Defines an <code>IndexIterator</code> by returning values
	 * <code>indices[beginIndex]</code>,
	 * <nobr><code>indices[beginIndex + 1]</code></nobr>, ...
	 * <nobr><code>indices[beginIndex + length - 1]</code></nobr> as
	 * sequential <code>next()</code> return values.
	 * No copy of the <code>indices</code> array is made.
	 *
	 * @throws NullPointerException           if <nobr><code>indices == null</code></nobr>.
	 * @throws IllegalArgumentException       if <nobr><code>length < 0</code></nobr>.
	 * @throws ArrayIndexOutOfBoundsException if <nobr><code>beginIndex < 0</code></nobr> or if
	 *                                        <nobr><code>beginIndex + length > indices.length</code></nobr>.
	 */
	public ArrayIterator(int[] indices, int beginIndex, int length) {
		if (indices == null) {
			throw new NullPointerException("indices is null");
		}

		if (length < 0) {
			throw new IllegalArgumentException("length < 0");
		}

		if (beginIndex < 0) {
			throw new ArrayIndexOutOfBoundsException("beginIndex < 0");
		}

		if ((beginIndex + length) > indices.length) {
			throw new ArrayIndexOutOfBoundsException("beginIndex + length > indices.length");
		}

		m_indices = indices;
		m_last = beginIndex + length;
		m_iter = beginIndex;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int numRemaining() {
		return m_last - m_iter;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int next() {
		if (m_iter != m_last) {
			return m_indices[m_iter++];
		} else {
			throw new NoSuchElementException();
		}
	}
}
