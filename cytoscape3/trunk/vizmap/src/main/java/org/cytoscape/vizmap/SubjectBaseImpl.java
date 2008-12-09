/*
 File: SubjectBase.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.vizmap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import java.util.ArrayList;
import java.util.List;


/**
 * Abstract Base Class for Subject in the Subject / Observer Pattern. Also Known
 * as Publisher / Subscriber Pattern.
 *
 * A Subject class notifies all its subscribers whenever its state changes.
 *
 * Note that this code duplicates some code in the AbstractCalculator class. May
 * be a good place to refactor in the future.
 */
public abstract class SubjectBaseImpl implements SubjectBase {
	/**
	 * An Array List of All Observers who want to be notified of changes.
	 */
	protected final List<ChangeListener> observers = new ArrayList<ChangeListener>();

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.SubjectBaseInf#addChangeListener(javax.swing.event.ChangeListener)
	 */
	public void addChangeListener(ChangeListener listener) {
		observers.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.SubjectBaseInf#removeChangeListener(javax.swing.event.ChangeListener)
	 */
	public void removeChangeListener(ChangeListener listener) {
		observers.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.SubjectBaseInf#fireStateChanged()
	 */
	public void fireStateChanged() {
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = observers.size() - 1; i >= 0; i--) {
			final ChangeListener listener = observers.get(i);
			final ChangeEvent changeEvent = new ChangeEvent(this);
			listener.stateChanged(changeEvent);
		}
	}
}
