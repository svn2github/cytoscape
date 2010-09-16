
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

package cytoscape.geom.spacial;


/**
 * A spacial index for objects in two dimensions, with support for
 * insertions and deletions.
 */
public interface MutableSpacialIndex2D extends SpacialIndex2D {
	/**
	 * Empties this structure of all entries.
	 */
	public void empty();

	/**
	 * Inserts a new data entry into this structure; the entry's extents
	 * are specified by the input parameters.  "Extents" is a short way
	 * of saying "minimum bounding rectangle".  The minimum bounding rectangle
	 * of an entry is axis-aligned, meaning that its sides are parallel to the
	 * axes of the data space.
	 * @param objKey a user-defined unique identifier used to refer to the entry
	 *   being inserted in later operations; this identifier must be
	 *   non-negative.
	 * @param xMin the minimum X coordinate of the entry's extents rectangle.
	 * @param yMin the minimum Y coordinate of the entry's extents rectangle.
	 * @param xMax the maximum X coordinate of the entry's extents rectangle.
	 * @param yMax the maximum Y coordinate of the entry's extents rectangle.
	 * @exception IllegalStateException if objKey is already used for an
	 *   existing entry in this structure.
	 * @exception IllegalArgumentException if objKey is negative,
	 *   if xMin is not less than or equal to xMax, or
	 *   if yMin is not less than or equal to yMax.
	 */
	public void insert(int objKey, float xMin, float yMin, float xMax, float yMax);

	/**
	 * Deletes the specified data entry from this structure.
	 * @param objKey a user-defined identifier that was potentially used in a
	 *   previous insertion.
	 * @return true if and only if objKey existed in this structure prior to this
	 *   method invocation.
	 */
	public boolean delete(int objKey);
}
