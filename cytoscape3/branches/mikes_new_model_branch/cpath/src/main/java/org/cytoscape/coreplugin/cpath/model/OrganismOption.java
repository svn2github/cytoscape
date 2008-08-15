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
 * Contains a Single Option for the Organism Pull-Down Menu.
 *
 * @author Ethan Cerami
 */
public class OrganismOption {
	/**
	 * All Organisms Option.
	 */
	public static final OrganismOption ALL_ORGANISMS = new OrganismOption(-9999, "All Organisms");

	/**
	 * NCBI Taxonomy ID.
	 */
	private int taxonomyId;

	/**
	 * Organism Species Name.
	 */
	private String speciesName;

	/**
	 * Constructor.
	 *
	 * @param taxonomyId  NCBI Taxonomy ID.
	 * @param speciesName Organism Species Name.
	 */
	public OrganismOption(int taxonomyId, String speciesName) {
		this.taxonomyId = taxonomyId;
		this.speciesName = speciesName;
	}

	/**
	 * Gets NCBI Taxonomy ID.
	 *
	 * @return NCBI Taxonomy ID.
	 */
	public int getTaxonomyId() {
		return taxonomyId;
	}

	/**
	 * Gets Organism Species Name.
	 *
	 * @return Organism Species Name.
	 */
	public String getSpeciesName() {
		return speciesName;
	}

	/**
	 * Gets Description of Option (as displayed in pull-down menu).
	 *
	 * @return Option description.
	 */
	public String toString() {
		return this.speciesName;
	}

	/**
	 * Gets All Options for the Pull-Down Menu.
	 *
	 * @return Vector of OrganismOption Objects.
	 */
	public static Vector getAllOptions() {
		Vector items = new Vector();
		items.add(ALL_ORGANISMS);
		items.add(new OrganismOption(6239, "Caenorhabditis elegans"));
		items.add(new OrganismOption(7227, "Drosophila melanogaster"));
		items.add(new OrganismOption(562, "Escherichia coli"));
		items.add(new OrganismOption(9606, "Homo Sapiens"));
		items.add(new OrganismOption(85962, "Helicobacter pylori 26695"));
		items.add(new OrganismOption(10090, "Mus Musculus"));
		items.add(new OrganismOption(4932, "Saccharomyces cerevisiae"));
		items.add(new OrganismOption(10116, "Rattus norvegicus"));

		return items;
	}

	/**
	 * Clone Object.
	 *
	 * @return Cloned Object.
	 */
	public Object clone() {
		OrganismOption option = new OrganismOption(taxonomyId, speciesName);

		return option;
	}
}
