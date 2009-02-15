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

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents an entry in the database cross reference.<br>
 *
 * @author kono
 *
 */
public class DBReference {
	private String abbreviation;
	private String name;
	private String urlSyntax;
	private String genericUrl;
	private String object;
	private List<String> synonyms;

	/**
	 * Creates a new DBReference object.
	 *
	 * @param abbreviation  DOCUMENT ME!
	 * @param name  DOCUMENT ME!
	 * @param genericUrl  DOCUMENT ME!
	 */
	public DBReference(String abbreviation, String name, String genericUrl) {
		this(abbreviation, name, null, genericUrl, null);
	}

	/**
	 * Creates a new DBReference object.
	 *
	 * @param abbreviation  DOCUMENT ME!
	 * @param name  DOCUMENT ME!
	 * @param urlSyntax  DOCUMENT ME!
	 * @param genericUrl  DOCUMENT ME!
	 * @param object  DOCUMENT ME!
	 */
	public DBReference(String abbreviation, String name, String urlSyntax, String genericUrl,
	                   String object) {
		this.abbreviation = abbreviation;
		this.name = name;
		this.urlSyntax = urlSyntax;
		this.genericUrl = genericUrl;
		this.synonyms = null;
		this.object = object;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getAbbreviation() {
		return abbreviation;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getFullName() {
		return name;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws MalformedURLException DOCUMENT ME!
	 */
	public URL getGenericURL() throws MalformedURLException {
		return new URL(genericUrl);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getObject() {
		return object;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<String> getSynonyms() {
		return synonyms;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param entry DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws MalformedURLException DOCUMENT ME!
	 */
	public URL getQueryURL(String entry) throws MalformedURLException {
		final String queryURL = urlSyntax + entry;

		return new URL(queryURL);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param synonym DOCUMENT ME!
	 */
	public void setSynonym(List<String> synonym) {
		this.synonyms = synonym;
	}
}
