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
package cytoscape.data.ontology;

import cytoscape.data.ontology.readers.DBCrossReferenceReader;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Database cress reference.<br>
 * <p>
 * This class manages the relationships between databases. The cross reference
 * file is available at:<br>
 *
 * http://www.geneontology.org/doc/GO.xrf_abbs
 *
 * </p>
 *
 * @version 0.9
 * @since Cytoscape 2.4
 * @author kono
 *
 */
public class DBCrossReferences {
	/*
	 * Map to store the cross reference.
	 */
	private Map<String, DBReference> crossRefMap;

	/**
	 * Creates a new DBCrossReferences object.
	 */
	public DBCrossReferences() {
		this.crossRefMap = new HashMap<String, DBReference>();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public void load() throws IOException {
		DBCrossReferenceReader xrefReader = new DBCrossReferenceReader();
		xrefReader.readResourceFile();
		this.crossRefMap = xrefReader.getXrefMap();
	}

	/**
	 * Add a database reference object
	 *
	 * @param db
	 */
	public void setDBReference(DBReference db) {
		crossRefMap.put(db.getAbbreviation(), db);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Set getDBNames() {
		return crossRefMap.keySet();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param abbreviation DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public DBReference getDBReference(String abbreviation) {
		return crossRefMap.get(abbreviation);
	}
}
