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

import cytoscape.CyNetwork;

import java.util.ArrayList;
import java.util.Observable;


/**
 * Encapsulates a Complete History of All Search Requests and Search
 * Responses executed by a user.
 *
 * @author Ethan Cerami
 */
public class SearchBundleList extends Observable {
	/**
	 * List of All Search Buncldes.
	 */
	private ArrayList searchBundles = new ArrayList();

	/**
	 * Adds a new Search Request / Response Pair.
	 *
	 * @param bundle SearchBundle Object.
	 */
	public void add(SearchBundle bundle) {
		searchBundles.add(bundle);
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Gets Total Number of Search Bundles.
	 *
	 * @return Number of Search Bundles.
	 */
	public int getNumSearchBundles() {
		return searchBundles.size();
	}

	/**
	 * Gets the Search Bundle at the Specified Index.
	 *
	 * @param index Index Value.
	 * @return SearchBundle Object.
	 */
	public SearchBundle getSearchBundleByIndex(int index) {
		return (SearchBundle) searchBundles.get(index);
	}

	/**
	 * Gets the Search Bundle with the Specified Bundle ID.
	 *
	 * @param bundleId Unique Bundle Identifier.
	 * @return SearchBundle Object.
	 */
	public SearchBundle getSearchBundleByBundleId(int bundleId) {
		for (int i = 0; i < searchBundles.size(); i++) {
			SearchBundle bundle = (SearchBundle) searchBundles.get(i);
			int id = bundle.getId();

			if (bundleId == id) {
				return bundle;
			}
		}

		return null;
	}

	/**
	 * Gets the Search Bundle with the Specified CyNewtork ID.
	 *
	 * @param networkId Unique CyNetwork Identifier.
	 * @return SearchBundle Object.
	 */
	public SearchBundle getSearchBundleByCynetworkId(int networkId) {
		for (int i = 0; i < searchBundles.size(); i++) {
			SearchBundle bundle = (SearchBundle) searchBundles.get(i);
			SearchResponse response = bundle.getResponse();
			CyNetwork network = response.getCyNetwork();

			if (network != null) {
				int id = Integer.parseInt(network.getIdentifier());

				if (networkId == id) {
					return bundle;
				}
			}
		}

		return null;
	}
}
