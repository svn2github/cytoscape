/*
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
package org.cytoscape.coreplugin.cpath.model;

import java.util.Vector;


/**
 * Contains a Single Option for the MaxHits Pull-Down Menu.
 *
 * @author Ethan Cerami
 */
public class MaxHitsOption {
	/**
	 * Get All Description.
	 */
	public static final String GET_ALL_DESCRIPTION = "No Limit";

	/**
	 * Default Number of Hits.
	 */
	public static final MaxHitsOption DEFAULT_NUM_HITS = new MaxHitsOption(10);

	/**
	 * Max Number of Hits.
	 */
	private int maxHits;

	/**
	 * Description of Option (as displayed in pull-down menu).
	 */
	private String description;

	/**
	 * Constructor.
	 *
	 * @param maxHits MaxHits int value.
	 */
	public MaxHitsOption(int maxHits) {
		this.maxHits = maxHits;

		if (maxHits == Integer.MAX_VALUE) {
			this.description = GET_ALL_DESCRIPTION;
		} else {
			this.description = "Limit to " + maxHits;
		}
	}

	/**
	 * Gets Max Number of Hits.
	 *
	 * @return max hits integer value.
	 */
	public int getMaxHits() {
		return maxHits;
	}

	/**
	 * Gets Description of Option (as displayed in pull-down menu).
	 *
	 * @return Option description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets Description of Option (as displayed in pull-down menu).
	 *
	 * @return Option description.
	 */
	public String toString() {
		return this.description;
	}

	/**
	 * Gets All Options for the Pull-Down Menu.
	 *
	 * @return Vector of MaxHitsOption Objects.
	 */
	public static Vector getAllOptions() {
		Vector allOptions = new Vector();
		allOptions.add(MaxHitsOption.DEFAULT_NUM_HITS);
		allOptions.add(new MaxHitsOption(20));
		allOptions.add(new MaxHitsOption(50));
		allOptions.add(new MaxHitsOption(100));
		allOptions.add(new MaxHitsOption(500));
		allOptions.add(new MaxHitsOption(1000));
		allOptions.add(new MaxHitsOption(5000));
		allOptions.add(new MaxHitsOption(Integer.MAX_VALUE));

		return allOptions;
	}

	/**
	 * Clone Object.
	 *
	 * @return Cloned Object.
	 */
	public Object clone() {
		MaxHitsOption option = new MaxHitsOption(maxHits);

		return option;
	}
}
