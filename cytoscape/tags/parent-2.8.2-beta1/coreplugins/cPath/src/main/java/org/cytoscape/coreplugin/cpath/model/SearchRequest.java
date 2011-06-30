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


/**
 * Encapsulates a Search Request which will be sent to cPath.
 *
 * @author Ethan Cerami
 */
public class SearchRequest {
	/**
	 * Search Query Terms.
	 */
	private String query;

	/**
	 * Organism Selection.
	 */
	private OrganismOption organism;

	/**
	 * MaxHits Selection.
	 */
	private MaxHitsOption maxHits;

	/**
	 * Constructor.
	 * By default, MaxHits is set to the default value,
	 * and we search all organisms.
	 */
	public SearchRequest() {
		this.query = null;
		this.organism = OrganismOption.ALL_ORGANISMS;
		this.maxHits = MaxHitsOption.DEFAULT_NUM_HITS;
	}

	/**
	 * Gets Search Query.
	 *
	 * @return Search Query Terms.
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Sets Search Query.
	 *
	 * @param query Search Query Terms.
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Gets Organism Option.
	 *
	 * @return Organism Option.
	 */
	public OrganismOption getOrganism() {
		return organism;
	}

	/**
	 * Sets Organism Option.
	 *
	 * @param organism Organism Option.
	 */
	public void setOrganism(OrganismOption organism) {
		this.organism = organism;
	}

	/**
	 * Gets Max Hits Option.
	 *
	 * @return MaxHits Option.
	 */
	public MaxHitsOption getMaxHitsOption() {
		return maxHits;
	}

	/**
	 * Set Max Hits Option.
	 *
	 * @param maxHits MaxHits Option.
	 */
	public void setMaxHits(MaxHitsOption maxHits) {
		this.maxHits = maxHits;
	}

	/**
	 * Clone the Search Query Object.
	 *
	 * @return Cloned object.
	 */
	public Object clone() {
		SearchRequest clone = new SearchRequest();
		clone.setMaxHits((MaxHitsOption) maxHits.clone());
		clone.setOrganism((OrganismOption) organism.clone());
		clone.setQuery(new String(query));

		return clone;
	}

	/**
	 * Gets Search Query Description.
	 *
	 * @return Query Description.
	 */
	public String toString() {
		if ((query != null) && (query.length() > 0)) {
			StringBuffer text = new StringBuffer(query);
			String species = organism.getSpeciesName();
			text.append(" [" + species + "]");

			return text.toString();
		} else if (organism != null) {
			return organism.getSpeciesName();
		} else {
			return "Query not specified";
		}
	}
}
