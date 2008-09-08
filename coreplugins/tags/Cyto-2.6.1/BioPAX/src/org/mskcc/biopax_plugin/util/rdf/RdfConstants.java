// $Id: RdfConstants.java,v 1.2 2006/06/15 22:06:02 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.biopax_plugin.util.rdf;

import org.jdom.Namespace;


/**
 * RDF (Resource Description Framework) Constants.
 *
 * @author Ethan Cerami.
 */
public class RdfConstants {
	/**
	 * RDF Root Name.
	 */
	public static final String RDF_ROOT_NAME = "RDF";

	/**
	 * RDF ID Attribute
	 */
	public static final String ID_ATTRIBUTE = "ID";

	/**
	 * RDF Resource Attribute
	 */
	public static final String RESOURCE_ATTRIBUTE = "resource";

	/**
	 * RDF About Attribute
	 */
	public static final String ABOUT_ATTRIBUTE = "about";

	/**
	 * RDF Datatype Attribute
	 */
	public static final String DATATYPE_ATTRIBUTE = "datatype";

	/**
	 * RDF Namespace URI
	 */
	public static final String RDF_NAMESPACE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	/**
	 * RDF Namespace Prefix
	 */
	public static final String RDF_NAMESPACE_PREFIX = "rdf";

	/**
	 * RDF Namespace Object.
	 */
	public static final Namespace RDF_NAMESPACE = Namespace.getNamespace(RdfConstants.RDF_NAMESPACE_PREFIX,
	                                                                     RdfConstants.RDF_NAMESPACE_URI);
}
