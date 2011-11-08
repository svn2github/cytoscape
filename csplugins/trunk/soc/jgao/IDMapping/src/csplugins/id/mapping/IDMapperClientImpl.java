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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;

/**
 *
 * @author gjj
 */
public class IDMapperClientImpl implements IDMapperClient {
    protected IDMapper mapper = null;

    protected String connectionString;
    protected final String classString;
    protected final String id;
    protected final String displayName;
    protected boolean selected;
    protected final ClientType clientType;

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

        public IDMapperClientImpl build()
            throws IDMapperException, ClassNotFoundException {
            return new IDMapperClientImpl(this);
        }
    }

    private IDMapperClientImpl(Builder builder) 
            throws IDMapperException, ClassNotFoundException {

        //Class.forName(classString);
        //mapper = BridgeDb.connect(connectionString);

        String defId = builder.id==null?""+clientNo+"-"
                +System.currentTimeMillis():builder.id;

        ClientType defClientType = builder.clientType;
        if (defClientType==null) {
            Class.forName(builder.classString);
            mapper = BridgeDb.connect(builder.connectionString);
            defClientType = ClientType.getClientType(mapper);
        }

        this.id = defId;
        this.displayName = builder.displayName;
        this.classString = builder.classString;
        this.connectionString = builder.connectionString;
        this.selected= builder.selected;
        this.clientType = defClientType;
        
        clientNo++;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public IDMapper getIDMapper() {
        if (mapper==null) {
            try {
                Class.forName(getClassString());

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    public void run() {
                        try {
                            mapper = BridgeDb.connect(getConnectionString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                //TODO: how to set waiting time?
                if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdown();
                }

                if (mapper == null) {
                    System.err.println("Failed to connect to " + this.toString());
                    return null;
                }

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

//        for (DataSource ds : dss) {
//            if (ds.getFullName()==null) {
//                String sysCode = ds.getSystemCode();
//                DataSource.register(sysCode, sysCode);
//            }
//        }
    }
    
    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString)
            throws IDMapperException {
        this.connectionString = connectionString;
        mapper = null;
    }
    
    public String getClassString() {
        return classString;
    }

    @Override
    public String toString() {
        return this.getDisplayName();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
