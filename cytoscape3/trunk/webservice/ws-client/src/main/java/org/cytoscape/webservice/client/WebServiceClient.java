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
package org.cytoscape.webservice.client;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Web service client wrapper for Cytoscape.
 * <p>
 * Usually, developers first use code generator to create Java code
 * from WSDL.  The generated code should be wrapped by this interface
 * to be used in Cytoscape framework.
 * </p>
 * All web service clients <strong>must</strong> implement this method.
 *
 * @param <S> Client stub object.  This is service dependent.  For example, NCBI's eUtils
 *  stub has the class EUtilsServiceSoap.
 *
 * @author kono
 * @version 0.5
 * @since Cytoscape 2.6
 */
public interface WebServiceClient<S> {
	/**
	 *  Returns client ID.
	 *  This ID should be unique.
	 *
	 * @return  client ID as String.
	 */
	public String getClientID();

	/**
	 *  Returns display name of this client.
	 *  This is more human readable name for this client.
	 *
	 * @return  display name for this client.
	 */
	public String getDisplayName();

	/**
	 *  Returns client type.
	 *  A client can have multiple types.
	 *
	 *  For example, NCBI Client can be used as network import
	 *  and attribute import, so this array contains both types.
	 *
	 * @see org.cytoscape.webservice.client.ClientType
	 *
	 * @return  Array of client types.
	 */
	public ClientType[] getClientType();

	/**
	 *  Return true if the given ClientType is compatible with this client.
	 *
	 *  If a client is used as network importer, isCompatibleType(ClientType.NETWORK)
	 *  returns true, but isCompatibleType(ClientType.ATTRIBUTE) returns false.
	 *
	 * @param ct Client type.
	 *
	 * @return  true if compatible.
	 */
	public boolean isCompatibleType(ClientType ct);

	/**
	 *  Get client stub object.
	 *  All services available from this client will be
	 *  accessed through this stub.  This will be used when developer wants
	 *  to access "raw" API of this service.
	 *
	 * @return Client stub.  This object type depends on service.
	 */
	public S getClientStub();

	/**
	 * Set stub to this client.
	 *
	 * @param stub client stub used in this client.
	 */
	public void setClientStub(S clientStub);

	/**
	 * Returns all available methods accessible through client stub.
	 *
	 * @return Collection of methods available through stub.
	 */
	public Collection<Method> getAccessibleMethods();

	/**
	 * Get description for this client.
	 *
	 * @return  Description as a string.  Users should write parser for this return value.
	 */
	public String getDescription();

	/**
	 *  Set description for this service.
	 *
	 * @param description Description as a String.
	 */
	public void setDescription(String description);
}
