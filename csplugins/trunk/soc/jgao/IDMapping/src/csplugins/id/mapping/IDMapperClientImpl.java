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
public class IDMapperClientImpl implements IDMapperClient {
    protected IDMapper mapper = null;
    protected String connectionString;
    protected String classString;
    protected String id;
    protected String displayName;
    protected boolean selected;

    protected static int clientNo = 0;

    public IDMapperClientImpl(String connectionString, String classString)
            throws ClassNotFoundException, IDMapperException{
        this(connectionString, classString, null);
        id = ""+clientNo;
        displayName = ""+clientNo;
    }

    public IDMapperClientImpl(String connectionString, String classString,
            String displayName)
            throws ClassNotFoundException, IDMapperException {
        this(connectionString, classString, displayName, null);
        id = ""+clientNo;
    }

    public IDMapperClientImpl(String connectionString, String classString,
            String displayName, String id)
            throws ClassNotFoundException, IDMapperException {
        this(connectionString, classString, displayName, id, true);
    }

    public IDMapperClientImpl(String connectionString, String classString,
            String displayName, String id, boolean selected)
            throws ClassNotFoundException, IDMapperException {
        this.classString = classString;
        Class.forName(classString);

        this.connectionString = connectionString;
        mapper = BridgeDb.connect(connectionString);

        this.id = id;
        this.displayName = displayName;
        this.selected = selected;
        
        clientNo++;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public IDMapper getIDMapper() {
        return mapper;
    }
    
    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString)
            throws IDMapperException {
        this.connectionString = connectionString;
        mapper = BridgeDb.connect(connectionString);
    }
    
    public String getClassString() {
        return classString;
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
