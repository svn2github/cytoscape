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

import java.util.Collections;
import java.util.Set;
import java.util.Vector;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

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

    protected IDMapperClientProperties props;

    protected static int clientNo = 0;

    protected static final String CLIENT_ID = "id";
    protected static final String CLIENT_DISPLAYNAME = "display-name";
    protected static final String CLASS_STRING = "class-name";
    protected static final String CONNECTION_STRING = "connection-string";
    protected static final String SELECTED = "selected";

    public IDMapperClientImplTunables(String connectionString, String classString)
            throws ClassNotFoundException, IDMapperException{
        this(connectionString, classString, ""+clientNo);
    }

    public IDMapperClientImplTunables(String connectionString, String classString,
            String displayName)
            throws ClassNotFoundException, IDMapperException {
        this(connectionString, classString, displayName, ""+clientNo);
    }

    public IDMapperClientImplTunables(String connectionString, String classString,
            String displayName, String id)
            throws ClassNotFoundException, IDMapperException {
        this(connectionString, classString, displayName, id, true);
    }

    public IDMapperClientImplTunables(String connectionString, String classString,
            String displayName, String id, boolean selected)
            throws ClassNotFoundException, IDMapperException {
        Class.forName(classString);
        mapper = BridgeDb.connect(connectionString);

        props = new IDMapperClientProperties(id);

        initilizeTunables(connectionString, classString, displayName, id,
                selected);

        props.saveProperties();
        
        clientNo++;
    }

    public IDMapperClientImplTunables(IDMapperClientProperties props)
            throws ClassNotFoundException, IDMapperException {
        this.props = props;

        this.initilizeTunables("", "", ""+clientNo, ""+clientNo, true);

        Class.forName(getClassString());
        mapper = BridgeDb.connect(getConnectionString());

        props.saveProperties();

        clientNo++;
    }

    public void initilizeTunables(String connectionString, String classString,
            String displayName, String id, boolean selected) {
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

        props.initializeProperties(); // save to props or set to tunables
    }

    public String getId() {
        return (String)id.getValue();
    }

    public String getDisplayName() {
        return (String)displayName.getValue();
    }

    public IDMapper getIDMapper() {
        return mapper;
    }
    
    public String getConnectionString() {
        return (String)connectionString.getValue();
    }

    public void setConnectionString(String connectionString)
            throws IDMapperException {
        this.connectionString.setValue(connectionString);
        mapper = BridgeDb.connect(connectionString);
        props.saveProperties(this.connectionString);
    }
    
    public String getClassString() {
        return (String)classString.getValue();
    }

    public String getDescription() {
        StringBuilder desc = new StringBuilder(this.getDisplayName());
        desc.append("\nCapacities:\n");

        desc.append(">> Supported source ID types:\n");
        IDMapperCapabilities capabilities = mapper.getCapabilities();

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
            int i=0;
            for (DataSource ds : dss) {
                i++;
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
        if (dataSource==null) {
            System.err.print("wrong");
        }
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
