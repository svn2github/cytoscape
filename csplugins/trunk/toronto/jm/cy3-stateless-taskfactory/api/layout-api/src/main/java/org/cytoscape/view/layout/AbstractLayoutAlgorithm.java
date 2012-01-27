/*
  File: AbstractLayoutAlgorithm.java

  Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.view.layout;


import org.cytoscape.work.undo.UndoSupport;


/**
 * The AbstractLayoutAlgorithm provides a basic implementation of a layout TaskFactory.
 * @CyAPI.Abstract.Class
 */
abstract public class AbstractLayoutAlgorithm implements CyLayoutAlgorithm<LayoutTaskContext> {

	/**
	 * The UndoSupport object use for allowing undo of layouts.
	 */
	protected final UndoSupport undo;
//	private ViewChangeEdit undoableEdit;
	
	protected final boolean supportsSelectedOnly;
	private final String humanName;
	private final String computerName;

	/**
	 * The Constructor.
	 * @param undo the UndoSupport object used for allowing undo of layouts.
	 * @param computerName a computer readable name used to construct property strings.
	 * @param humanName a user visible name of the layout.
	 * @param supportsSelectedOnly indicates whether only selected nodes should be laid out.
	 */
	public AbstractLayoutAlgorithm(final UndoSupport undo, final String computerName, final String humanName, boolean supportsSelectedOnly) {
		this.undo = undo;
		this.computerName = computerName;
		this.humanName = humanName;
		this.supportsSelectedOnly = supportsSelectedOnly;
	}

	/**
	 * A computer readable name used to construct property strings.
	 * @return a computer readable name used to construct property strings.
	 */
	public String getName() {
		return computerName;
	}

	/**
	 * Used to get the user-visible name of the layout.
	 * @return the user-visible name of the layout.
	 */
	public String toString() {
		return humanName;
	}
}
