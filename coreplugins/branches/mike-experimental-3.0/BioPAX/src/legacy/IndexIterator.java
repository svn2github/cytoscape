
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

package legacy;

import java.util.NoSuchElementException;


/**
 * <b>This class is in a very unfinished state; development effort on this
 * class has been suspended until further notice; don't use this class
 * unless you're the author.
 * </b><p>
 * An iterator over a set of indices.
 */
public interface IndexIterator {
	/**
	 * Returns the next index in the iteration.
	 *
	 * @return the next index in the iteration.
	 * @throws NoSuchElementException if the iteration has no more
	 *                                indices.
	 */
	public int next();

	/**
	 * Returns an integer I such that <code>next()</code> will successfully
	 * return a value no more and no less that I times.  Negative values are
	 * never returned.
	 */
	public int numRemaining();
}
