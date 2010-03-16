/*
  File: Interaction.java

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
package cytoscape.data;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Interaction.java:  protein-protein or protein-DNA: parse text file, encapsulate
 */
public class Interaction {
	private String source;
	private List<String> targets = new ArrayList<String>();
	private String interactionType;

	/**
	 * Creates a new Interaction object.
	 *
	 * @param source  DOCUMENT ME!
	 * @param target  DOCUMENT ME!
	 * @param interactionType  DOCUMENT ME!
	 */
	public Interaction(final String source, final String target, final String interactionType) {
		this.source = source;
		this.interactionType = interactionType;
		this.targets.add(target);
	} // ctor (3 args)

	/**
	 * Creates a new Interaction object.
	 *
	 * @param rawText  DOCUMENT ME!
	 */
	public Interaction(String rawText) {
		this(rawText, " ");
	}

	/**
	 * Creates a new Interaction object.
	 *
	 * @param rawText  DOCUMENT ME!
	 * @param delimiter  DOCUMENT ME!
	 */
	public Interaction(String rawText, String delimiter) {
		final StringTokenizer strtok = new StringTokenizer(rawText, delimiter);
		int counter = 0;

		while (strtok.hasMoreTokens()) {
			if (counter == 0)
				source = strtok.nextToken().trim();
			else if (counter == 1)
				interactionType = strtok.nextToken().trim();
			else {
				targets.add(strtok.nextToken().trim());
			}

			counter++;
		}
	} // ctor (String)

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getSource() {
		return source;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getType() {
		return interactionType;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int numberOfTargets() {
		return targets.size();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String[] getTargets() {
		return targets.toArray(new String[0]);
	} // getTargets

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(interactionType);
		sb.append("::");
		sb.append(source);
		sb.append("::");

		final int targetSize = targets.size();
		for (int i = 0; i < targetSize; i++) {
			sb.append(targets.get(i));

			if (i < (targetSize - 1))
				sb.append(",");
		}

		return sb.toString();
	}
} // Interaction
