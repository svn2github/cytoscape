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

import cytoscape.layout.Tunable;

import java.util.HashSet;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;

/**
 *
 * @author gjj
 */
public class IDMapperClientImplTunables implements IDMapperClient {
    protected IDMapper mapper = null;

    protected Tunable connectionString;
    protected Tunable classString;
    protected Tunable id;
    protected Tunable displayName;
    protected Tunable selected;
    protected Tunable clientType;

    protected IDMapperClientProperties props;

    protected static int clientNo = 0;

    protected static final String CLIENT_ID = "id";
    protected static final String CLIENT_DISPLAYNAME = "display-name";
    protected static final String CLASS_STRING = "class-name";
    protected static final String CONNECTION_STRING = "connection-string";
    protected static final String SELECTED = "selected";
    protected static final String CLIENT_TYPE = "client-type";

    public static class Builder {
        private String connectionString;
        private String classString;

        // optional parameters
        private String displayName = null;
        private String id = null;
        private boolean selected = true;
        private ClientType clientType = null;

        public Builder(String connectionString, String classString) {
            if (connectionString==null || classString==null) {
                throw new IllegalArgumentException();
            }
            this.connectionString = connectionString;
            this.classString = classString;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public Builder clientType(ClientType clientType) {
            this.clientType = clientType;
            return this;
        }

        public IDMapperClientImplTunables build() {
            return new IDMapperClientImplTunables(this);
        }
    }

    private IDMapperClientImplTunables(Builder builder) {

        //Class.forName(classString);
        //mapper = BridgeDb.connect(connectionString);

        String defId = builder.id==null?""+clientNo+"-"
                +System.currentTimeMillis():builder.id;

        ClientType defClientType = builder.clientType;
        if (defClientType==null) {
            try {
                Class.forName(builder.classString);
                mapper = BridgeDb.connect(builder.connectionString);
                defClientType = ClientType.getClientType(mapper);
            } catch (Exception e) {
                defClientType = ClientType.OTHER;
                e.printStackTrace();
            }
        }

        props = new IDMapperClientProperties(defId);

        initilizeTunables(builder.connectionString,
                builder.classString,
                builder.displayName==null?builder.connectionString:builder.displayName,
                defId,
                builder.selected,
                defClientType);

        props.saveProperties();
        
        clientNo++;
    }

    public IDMapperClientImplTunables(IDMapperClientProperties props, String newPropsId) {
        this.props = props;

        String defId = ""+clientNo+"-"+System.currentTimeMillis();
        this.initilizeTunables("", "", defId, defId, true, ClientType.OTHER);

        if (newPropsId!=null) {
            this.props = new IDMapperClientProperties(newPropsId, props);
            this.props.initializeProperties();
            props.release(); // release the old
        }

        //Class.forName(getClassString());
        //mapper = BridgeDb.connect(getConnectionString());

        props.saveProperties();

        clientNo++;
    }

    private void initilizeTunables(String connectionString, String classString,
            String displayName, String id, boolean selected, ClientType clientType) {
        this.id = new Tunable(CLIENT_ID, "ID for client", Tunable.STRING, id);
        props.add(this.id);

        this.displayName = new Tunable(CLIENT_DISPLAYNAME,
                    "Display name for client", Tunable.STRING, displayName);
        props.add(this.displayName);

        this.classString = new Tunable(CLASS_STRING,
                    "ID mapper class name", Tunable.STRING, classString);
        props.add(this.classString);

        this.connectionString = new Tunable(CONNECTION_STRING,
                    "Connection string of ID mapper", Tunable.STRING,
                    connectionString);
        props.add(this.connectionString);

        this.selected = new Tunable(SELECTED,
                    "Is this client selected", Tunable.BOOLEAN,
                    selected);
        props.add(this.selected);

        this.clientType = new Tunable(CLIENT_TYPE,
                    "Client type", Tunable.STRING|Tunable.NOINPUT, clientType.name());
        props.add(this.clientType);

        props.initializeProperties(); // save to props or set to tunables
    }

    public String getId() {
        return (String)id.getValue();
    }

    public String getDisplayName() {
        return (String)displayName.getValue();
    }

    public ClientType getClientType() {
        return ClientType.valueOf((String)clientType.getValue());
    }

    public IDMapper getIDMapper() {
        if (mapper==null) {
            try {
                Class.forName(getClassString());
                mapper = BridgeDb.connect(getConnectionString());
                preprocess(mapper);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return mapper;
    }

    /**
     * set fullname of datasource as syscode if it is null
     * in this plugin, fullname represents the datasource
     * @param mapper
     */
    private static void preprocess(final IDMapper mapper) {
        if (mapper==null)
            return;

        IDMapperCapabilities caps = mapper.getCapabilities();
        Set<DataSource> dss = new HashSet();
        try {
            dss.addAll(caps.getSupportedSrcDataSources());
            dss.addAll(caps.getSupportedTgtDataSources());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        for (DataSource ds : dss) {
            if (ds.getFullName()==null) {
                String sysCode = ds.getSystemCode();
                DataSource.register(sysCode, sysCode);
            }
        }
    }
    
    public String getConnectionString() {
        return (String)connectionString.getValue();
    }

    public void setConnectionString(String connectionString)
            throws IDMapperException {
        this.connectionString.setValue(connectionString);
        mapper = null;
        props.saveProperties(this.connectionString);
    }
    
    public String getClassString() {
        return (String)classString.getValue();
    }

    @Override
    public String toString() {
        return this.getDisplayName();
    }

    public IDMapperClientProperties getProps() {
        return props;
    }

    public boolean isSelected() {
        return (Boolean)selected.getValue();
    }

    public void setSelected(boolean selected) {
        this.selected.setValue(selected);
        props.saveProperties(this.selected);
    }

    public void close() {
        props.release();
    }
}
