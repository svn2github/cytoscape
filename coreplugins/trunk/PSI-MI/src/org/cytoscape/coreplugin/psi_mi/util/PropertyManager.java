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
package org.cytoscape.coreplugin.psi_mi.util;

import java.util.Properties;


/**
 * Centralized Property Manager.
 *
 * @author Ethan Cerami
 */
public class PropertyManager extends Properties {
	/**
	 * Property:  Database Location
	 */
	public static final String DB_LOCATION = new String("dataservice.db_location");

	/**
	 * Property:  Database User Name.
	 */
	public static final String DB_USER = new String("dataservice.db_user");

	/**
	 * Property:  Database Password.
	 */
	public static final String DB_PASSWORD = new String("dataservice.db_password");

	/**
	 * Property:  SeqHound Location.
	 */
	public static final String SEQ_HOUND_LOCATION = new String("dataservice.seqhound_location");

	/**
	 * Property:  CPath Read Location.
	 */
	public static final String CPATH_READ_LOCATION = new String("dataservice.cpath_read_location");

	/**
	 * Property:  DataService Proxy Host.
	 */
	public static final String DATASERVICE_PROXY_HOST = new String("dataservice.proxy_host");

	/**
	 * Property:  DataService Proxy Port.
	 */
	public static final String DATASERVICE_PROXY_PORT = new String("dataservice.proxy_port");

	/**
	 * Property:  CPath Write Location.
	 */
	public static final String CPATH_WRITE_LOCATION = new String("dataservice.cpath_write_location");

	/**
	 * Property:  NCBI Location.
	 */
	public static final String NCBI_LOCATION = new String("dataservice.ncbi_location");

	/**
	 * Property:  Log Config File.
	 */
	public static final String LOG_CONFIG_FILE = new String("LOG_CONFIG_FILE");

	/**
	 * Singelton Property Manager.
	 */
	private static PropertyManager manager;

	/**
	 * Private Constructor.
	 */
	private PropertyManager() {
	}

	/**
	 * Gets Instance of Singleton.
	 *
	 * @return PropertyManager.
	 */
	public static PropertyManager getInstance() {
		if (manager == null) {
			manager = new PropertyManager();
			manager.bootStrap();
		}

		return manager;
	}

	/**
	 * Initializes Property Manager with Hard Coded Default Values.
	 */
	private void bootStrap() {
		manager.setProperty(DB_USER, "tomcat");
		manager.setProperty(DB_PASSWORD, "kitty");
		manager.setProperty(DB_LOCATION, "localhost");
		manager.setProperty(LOG_CONFIG_FILE, "config/config-JDBC.properties");
		manager.setProperty(CPATH_READ_LOCATION, "http://cbio.mskcc.org/cpath/webservice.do");
		manager.setProperty(CPATH_WRITE_LOCATION, "http://cbio.mskcc.org/ds/xmlrpc");
		manager.setProperty(SEQ_HOUND_LOCATION, "http://zaphod.mshri.on.ca/cgi-bin/seqhound/seqrem");
		manager.setProperty(NCBI_LOCATION, "http://www.ncbi.nlm.nih.gov:80/entrez/query.fcgi");
	}
}
