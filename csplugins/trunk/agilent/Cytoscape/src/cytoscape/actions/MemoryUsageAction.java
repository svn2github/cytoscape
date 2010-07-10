/*
  File: MemoryUsageAction.java

  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.actions;


import cytoscape.util.CytoscapeAction;
import cytoscape.util.MemoryReporter;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;


public class MemoryUsageAction extends CytoscapeAction {
	/**
	 * Creates a new MemoryUsageAction object and hooks it into the menu system.
	 */
	public MemoryUsageAction() {
		super("Memory Usage...");
		setPreferredMenu("Help");
	}

	/**
	 *  Show memory usage freport.
	 */
	public void actionPerformed(final ActionEvent e) {
		final StringBuilder msg =
			new StringBuilder("used:           " + roundToNearestMiB(MemoryReporter.getUsedMemory()) + "MiB\n");
		final long maximum = MemoryReporter.getMaxMemory();
		if (maximum == -1L)
			msg.append("maximum:   not available!");
		else
			msg.append("maximum:   " + roundToNearestMiB(maximum) + "MiB\n");
		msg.append("committed:  " + roundToNearestMiB(MemoryReporter.getCommittedMemory()) + "MiB\n");

		JOptionPane.showMessageDialog(null, msg.toString(), "Memory Usage",
		                              JOptionPane.INFORMATION_MESSAGE);
	}

	private long roundToNearestMiB(final long noOfBytes) {
		return (noOfBytes + 512L * 1024L) / (1024L * 1024L);
	}
}
