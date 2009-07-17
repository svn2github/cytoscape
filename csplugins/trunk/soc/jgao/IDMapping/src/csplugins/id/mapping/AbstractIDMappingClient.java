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

import cytoscape.util.ModuleProperties;

import java.util.Set;
import java.util.Vector;
import java.util.Collections;
import java.io.Serializable;

import org.bridgedb.Xref;
import org.bridgedb.IDMapper;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;

/**
 * Abstract class for all ID mapping clients.
 * All clients MUST extend this class.
 *
 * @author gjj
 *
 */
public abstract class AbstractIDMappingClient implements Serializable, IDMappingClient {
	// Default ID
	protected static final String DEF_NAME = "default";

	// Default Display Name
	protected static final String DEF_DISPLAY_NAME = "Default Web Service Cilent";

	// Stub object.
	protected IDMapper idMapper;

	// Client ID.  This should be unique.
	protected String clientID;

	// Display Name for this client.
	protected String displayName;

	// Properties for this client.  Will be used by Tunable.
	protected ModuleProperties props;

	/**
	 * Creates a new WebServiceClientImpl object.
	 */
	public AbstractIDMappingClient() {
		this(DEF_NAME, DEF_DISPLAY_NAME);
	}

	/**
	 * Creates a new WebServiceClientImpl object.
	 *
	 * @param serviceName  DOCUMENT ME!
	 * @param displayName  DOCUMENT ME!
	 */
	public AbstractIDMappingClient(final String serviceName, final String displayName) {
		this(serviceName, displayName, null);
	}

	/**
	 * Creates a new WebServiceClientImpl object.
	 *
	 * @param serviceName  DOCUMENT ME!
	 * @param displayName  DOCUMENT ME!
	 * @param props  DOCUMENT ME!
	 */
	public AbstractIDMappingClient(final String serviceName, final String displayName,
	                           final IDMapper idMapper) {
		this(serviceName, displayName, idMapper, null);
	}

	/**
	 * Creates a new WebServiceClientImpl object.
	 *
	 * @param serviceName  DOCUMENT ME!
	 * @param displayName  DOCUMENT ME!
	 * @param props  DOCUMENT ME!
	 */
	public AbstractIDMappingClient(final String serviceName, final String displayName,
	                               final IDMapper idMapper, final ModuleProperties props) {
		this.clientID = serviceName;
		this.displayName = displayName;
		this.props = props;
		this.idMapper = idMapper;

		//WebServiceClientManager.getCyWebServiceEventSupport().addCyWebServiceEventListener(this);
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
	 *  Client stub will be returned from this.
	 *  All services are accessible thorough this stub.
	 *
	 * @return  DOCUMENT ME!
	 */
	public IDMapper getIDMapper() {
		return idMapper;
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
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDescription() {
            StringBuilder desc = new StringBuilder(this.getDisplayName());
            desc.append("\nCapacities:\n");

            desc.append(">> Supported source ID types:\n");
            IDMapperCapabilities capabilities = idMapper.getCapabilities();

            Set<DataSource> dss = null;
            try {
                dss = capabilities.getSupportedSrcDataSources();
            } catch (IDMapperException ex) {
                ex.printStackTrace();
            }

            if (dss!=null) {
                Vector<String> vec = new Vector(dss.size());
                for (DataSource ds : dss) {
                    vec.add(getDescription(ds));
                }

                Collections.sort(vec);
                for (String str : vec) {
                    desc.append("\t"+str+"\n");
                }
            }

            desc.append(">> Supported target ID types:\n");
            dss = null;
            try {
                dss = capabilities.getSupportedTgtDataSources();
            } catch (IDMapperException ex) {
                ex.printStackTrace();
            }

            if (dss!=null) {
                Vector<String> vec = new Vector(dss.size());
                for (DataSource ds : dss) {
                    vec.add(getDescription(ds));
                }

                Collections.sort(vec);
                for (String str : vec) {
                    desc.append("\t"+str+"\n");
                }
            }

            desc.append(">> Is free-text search supported?\n");
            desc.append(capabilities.isFreeSearchSupported()? "\tYes":"\tNo");
            desc.append("\n");

            return desc.toString();
	}

        private String getDescription(DataSource dataSource) {
            StringBuilder desc = new StringBuilder();
            String sysName = dataSource.getSystemCode();
            if (sysName!=null) {
                desc.append(sysName);
            }
            desc.append("\t");

            String fullName = dataSource.getFullName();
            if (fullName!=null) {
                desc.append(fullName);
            }
            desc.append("\t");

            Xref example = dataSource.getExample();
            if (example!=null) {
                String id = example.getId();
                if (id!=null) {
                    desc.append(id);
                }
            }

            return desc.toString();
        }
	
	public void setIDMapper(final IDMapper idMapper) {
		this.idMapper = idMapper;
	}

    @Override
    public String toString() {
        return this.getDisplayName();
    }

}
