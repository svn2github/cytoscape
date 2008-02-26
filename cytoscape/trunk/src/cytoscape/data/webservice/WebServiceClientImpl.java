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
package cytoscape.data.webservice;

import cytoscape.data.webservice.WebServiceClientManager.ClientType;
import cytoscape.util.ModuleProperties;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;


/**
 * Abstract class for all web service clients.
 * All clients MUST extend this class.
 *
 * @author Keiichiro Ono
 * @since Cytoscape 2.6
 * @version 0.5
 */
public abstract class WebServiceClientImpl implements WebServiceClient, CyWebServiceEventListener {
	protected Object stub;
	protected String displayName;
	protected String clientID;
	protected ClientType[] types;
	protected ModuleProperties props;
	protected static final String DEF_NAME = "default";
	protected static final String DEF_DISPLAY_NAME = "Default Web Service Cilent";
	protected List<Method> availableMethods = null;

	/**
	 * Creates a new WebServiceClientImpl object.
	 *
	 * @param serviceName  DOCUMENT ME!
	 * @param displayName  DOCUMENT ME!
	 */
	public WebServiceClientImpl(final String serviceName, final String displayName) {
		this(serviceName, displayName, new ClientType[] { ClientType.ATTRIBUTE });
	}

	/**
	 * Creates a new WebServiceClientImpl object.
	 *
	 * @param serviceName  DOCUMENT ME!
	 * @param displayName  DOCUMENT ME!
	 * @param types  DOCUMENT ME!
	 */
	public WebServiceClientImpl(final String serviceName, final String displayName,
	                            ClientType[] types) {
		this(serviceName, displayName, types, null);
	}

	/**
	 * Creates a new WebServiceClientImpl object.
	 *
	 * @param serviceName  DOCUMENT ME!
	 * @param displayName  DOCUMENT ME!
	 * @param types  DOCUMENT ME!
	 * @param props  DOCUMENT ME!
	 */
	public WebServiceClientImpl(final String serviceName, final String displayName,
	                            final ClientType[] types, final ModuleProperties props) {
		this.clientID = serviceName;
		this.displayName = displayName;
		this.types = types;
		this.props = props;

		WebServiceClientManager.getCyWebServiceEventSupport().addCyWebServiceEventListener(this);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param methodName DOCUMENT ME!
	 * @param parameterTypes DOCUMENT ME!
	 * @param parameters DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public Object execute(String methodName, Class[] parameterTypes, Object[] parameters)
	    throws Exception {
		Method method = stub.getClass().getMethod(methodName, parameterTypes);
		Object ret = method.invoke(stub, parameters);

		return ret;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<Method> getAllServices() {
		if (availableMethods == null) {
			availableMethods = new ArrayList<Method>();

			final Class stubClass = stub.getClass();
			final Method[] methods = stubClass.getMethods();

			for (Method m : methods) {
				if (m.toString().startsWith("public")) {
					availableMethods.add(m);
				}
			}
		}

		return availableMethods;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getClientID() {
		return clientID;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public ClientType[] getClientType() {
		return types;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ct DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isCompatibleType(ClientType ct) {
		for (ClientType t : types) {
			if (t.equals(ct)) {
				return true;
			}
		}

		return false;
	}

	/**
	 *  Client stub will be returned from this.
	 *  All services are accessible thorough this stub.
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getClientStub() {
		return stub;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public ModuleProperties getProps() {
		return props;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param props DOCUMENT ME!
	 */
	public void setProps(ModuleProperties props) {
		this.props = props;
	}

	/**
	 *  Execute the service through event handling system.
	 *
	 * @param e DOCUMENT ME!
	 */
	public abstract void executeService(CyWebServiceEvent e) throws CyWebServiceException;
}
