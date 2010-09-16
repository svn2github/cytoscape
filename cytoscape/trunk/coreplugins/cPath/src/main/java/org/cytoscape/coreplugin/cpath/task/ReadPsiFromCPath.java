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
package org.cytoscape.coreplugin.cpath.task;

import cytoscape.Cytoscape;

import cytoscape.data.ImportHandler;

import cytoscape.data.readers.GraphReader;

import org.cytoscape.coreplugin.cpath.model.CPathException;
import org.cytoscape.coreplugin.cpath.model.EmptySetException;
import org.cytoscape.coreplugin.cpath.protocol.CPathProtocol;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Reads PSI Interactions from cPath.
 *
 * @author Ethan Cerami
 */
public class ReadPsiFromCPath {
	/**
	 * Parameter Not Specified.
	 */
	public static final int NOT_SPECIFIED = -1;
	private String uri;

	/**
	 * Gets ArrayList of Interactions by Keyword
	 *
	 * @param keyword    Keyword String.
	 * @param taxonomyId TaxonomyID.
	 * @param maxHits    MaxHits.
	 * @return ArrayList of Interactions.
	 * @throws CPathException    cPath Connection Error.
	 * @throws EmptySetException No Matching Interactions Found.
	 */
	public GraphReader getInteractionsByKeyword(String keyword, int taxonomyId, int maxHits)
	    throws CPathException, EmptySetException {
		return process(CPathProtocol.COMMAND_GET_BY_KEYWORD, keyword, taxonomyId, 0, maxHits);
	}

	/**
	 * Gets ArrayList of Interactions by Keyword
	 *
	 * @param keyword    Keyword String.
	 * @param taxonomyId TaxonomyID.
	 * @param startIndex StartIndex.
	 * @param maxHits    MaxHits.
	 * @return GraphReader Object.
	 * @throws CPathException    cPath Connection Error.
	 * @throws EmptySetException No Matching Interactions Found.
	 */
	public GraphReader getInteractionsByKeyword(String keyword, int taxonomyId, int startIndex,
	                                            int maxHits)
	    throws CPathException, EmptySetException {
		return process(CPathProtocol.COMMAND_GET_BY_KEYWORD, keyword, taxonomyId, startIndex,
		               maxHits);
	}

	/**
	 * Gets Total Number of Interactions for specified search parameters.
	 *
	 * @param keyword    Search Keyword.
	 * @param taxonomyId Taxonomy ID.
	 * @return number of interactions.
	 * @throws CPathException    Data Service Error.
	 * @throws EmptySetException No Results Found.
	 */
	public int getInteractionsCount(String keyword, int taxonomyId)
	    throws CPathException, EmptySetException {
		CPathProtocol cpath = new CPathProtocol();
		cpath.setCommand(CPathProtocol.COMMAND_GET_BY_KEYWORD);
		cpath.setFormat(CPathProtocol.FORMAT_COUNT_ONLY);
		cpath.setQuery(keyword);

		if (taxonomyId != NOT_SPECIFIED) {
			cpath.setOrganism(taxonomyId);
		}

		uri = cpath.getURI();

		String value = cpath.connect();
		int count;

		try {
			count = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new CPathException("Error Occurred while connecting "
			                         + "to the cPath Web Service (Details:  Invalid data "
			                         + "returned.  Double check that you are using the correct "
			                         + "cPath URL.)");
		}

		return count;
	}

	/**
	 * Gets ArrayList of Interactions by Keyword
	 *
	 * @param keyword Keyword String.
	 * @param maxHits MaxHits.
	 * @return ArrayList of Interactions.
	 * @throws CPathException    Indicates error connecting to data service.
	 * @throws EmptySetException No Matching Interactions Found.
	 */
	public GraphReader getInteractionsByKeyword(String keyword, int maxHits)
	    throws CPathException, EmptySetException {
		return process(CPathProtocol.COMMAND_GET_BY_KEYWORD, keyword, NOT_SPECIFIED, 0, maxHits);
	}

	/**
	 * Gets URI of Last Query.
	 *
	 * @return URI String.
	 */
	public String getLastQueryURI() {
		return uri;
	}

	/**
	 * Process Service.
	 */
	private GraphReader process(String command, String query, int taxonomyId, int startIndex,
	                            int maxHits) throws CPathException {
		CPathProtocol cpath = new CPathProtocol();
		cpath.setCommand(command);
		cpath.setFormat(CPathProtocol.FORMAT_XML);
		cpath.setQuery(query);
		cpath.setStartIndex(startIndex);
		cpath.setMaxHits(maxHits);

		if (taxonomyId != NOT_SPECIFIED) {
			cpath.setOrganism(taxonomyId);
		}

		try {
			URL url = new URL(cpath.getURI());
			GraphReader reader = Cytoscape.getImportHandler().getReader(url);

			return reader;
		} catch (MalformedURLException e) {
			throw new CPathException("Could not parse URL", e);
		}
	}
}
