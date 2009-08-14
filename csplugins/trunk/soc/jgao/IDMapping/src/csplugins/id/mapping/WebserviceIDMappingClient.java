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

package csplugins.id.mapping;

import csplugins.id.mapping.IDMappingClientManager.ClientType;

import cytoscape.util.ModuleProperties;

import org.bridgedb.webservice.IDMapperWebservice;

/**
 *
 * @author gjj
 */
public abstract class WebserviceIDMappingClient extends AbstractIDMappingClient {
    // Default ID
	protected static final String WS_NAME = "webservice";

	// Default Display Name
	protected static final String WS_DISPLAY_NAME = "File ID Mapping Cilent";

    /**
     * Creates a new WebServiceClientImpl object.
     */
    public WebserviceIDMappingClient() {
        this(WS_NAME, WS_DISPLAY_NAME);
    }

    /**
     * Creates a new WebServiceClientImpl object.
     *
     * @param serviceName  DOCUMENT ME!
     * @param displayName  DOCUMENT ME!
     */
    public WebserviceIDMappingClient(final String serviceName, final String displayName) {
        this(serviceName, displayName, null);
    }

    /**
     * Creates a new WebServiceClientImpl object.
     *
     * @param serviceName  DOCUMENT ME!
     * @param displayName  DOCUMENT ME!
     * @param props  DOCUMENT ME!
     */
    public WebserviceIDMappingClient(final String serviceName, final String displayName,
                               final IDMapperWebservice idMapper) {
        this(serviceName, displayName, idMapper, null);
    }

    /**
     * Creates a new WebServiceClientImpl object.
     *
     * @param serviceName  DOCUMENT ME!
     * @param displayName  DOCUMENT ME!
     * @param props  DOCUMENT ME!
     */
    public WebserviceIDMappingClient(final String serviceName, final String displayName,
                               final IDMapperWebservice idMapper, final ModuleProperties props) {
        super(serviceName, displayName, idMapper, props);

    }

    /**
     *
     * @return
     */
    public ClientType getClientType() {
        return ClientType.WEBSERVICE;
    }
}
