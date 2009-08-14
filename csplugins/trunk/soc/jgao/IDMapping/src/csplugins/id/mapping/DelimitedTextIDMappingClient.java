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

import cytoscape.util.ModuleProperties;
import cytoscape.util.ModulePropertiesImpl;

import java.net.MalformedURLException;
import java.net.URL;

import org.bridgedb.IDMapperException;
import org.bridgedb.file.IDMapperText;

/**
 *
 * @author gjj
 */
public class DelimitedTextIDMappingClient extends FileIDMappingClient {

    protected static final String TEXT_CLIENT_PROPERTY_NAME 
            = CLIENT_PROPERTY_NAME+".Text";

    public static final String DATA_SOURCE_DELIMITER_CHANGED
            = "DATA_SOURCE_DELIMITER_CHANGED";
    public static final String ID_DELIMITER_CHANGED
            = "ID_DELIMITER_CHANGED";
    public static final String TRANSITIVITY_CHANGED
            = "TRANSITIVITY_CHANGED";

    public DelimitedTextIDMappingClient(final URL url,
            final char[] dataSourceDelimiters) throws IDMapperException {
        this(url, dataSourceDelimiters, null);
    }

    public DelimitedTextIDMappingClient(final URL url,
            final char[] dataSourceDelimiters,
            final char[] idDelimiters) throws IDMapperException {
        this(url, dataSourceDelimiters, idDelimiters, false);
    }

    public DelimitedTextIDMappingClient(final URL url,
            final char[] dataSourceDelimiters,
            final char[] idDelimiters,
            final boolean transitivity) throws IDMapperException {
        this (new IDMapperText(url,
                               dataSourceDelimiters,
                               idDelimiters,
                               transitivity));
    }

    public void setDataSourceDelimiters(final char[] dataSourceDelimiters) {
        IDMapperText mapper = (IDMapperText)idMapper;
        char[] old = mapper.getDataSourceDelimiters();
        mapper.setDataSourceDelimiters(dataSourceDelimiters);
        pcs.firePropertyChange(DATA_SOURCE_DELIMITER_CHANGED, old,
                dataSourceDelimiters);
    }

    public void setIDDelimiters(final char[] idDelimiters) {
        IDMapperText mapper = (IDMapperText)idMapper;
        char[] old = mapper.getIDDelimiters();
        mapper.setIDDelimiters(idDelimiters);
        pcs.firePropertyChange(ID_DELIMITER_CHANGED, old,
                idDelimiters);
    }

    public void setTransitivity(final boolean transitivity) {
        IDMapperText mapper = (IDMapperText)idMapper;
        boolean old = mapper.getTransitivity();
        mapper.setTransitivity(transitivity);
        pcs.firePropertyChange(ID_DELIMITER_CHANGED, old,
                transitivity);
    }

    public DelimitedTextIDMappingClient(final IDMapperText idMapper) {
        this(idMapper, null);

        // set ModuleProperties
        ModuleProperties properties = new ModulePropertiesImpl(
                ""+clientNo, TEXT_CLIENT_PROPERTY_NAME);

        properties.add(new Tunable("type", "Client type", Tunable.STRING, "Text"));
        String url = idMapper.getURL().toString();
        properties.add(new Tunable("url", "URL of the text file", Tunable.STRING,
                url));

        String dsDel = new String(idMapper.getIDDelimiters());
        properties.add(new Tunable("data_source_delimiter",
                "Delimiters between data sources", Tunable.STRING, dsDel));

        if (idMapper.getIDDelimiters()!=null) {
            String idDel = new String(idMapper.getIDDelimiters());
            properties.add(new Tunable("id_delimiter",
                    "Delimiters between identifiers", Tunable.STRING, idDel));
        }

        properties.add(new Tunable("transitivity", "Transitivity support",
                Tunable.BOOLEAN, new Boolean(idMapper.getTransitivity())));

        properties.add(new Tunable("selected", "is this client selected",
                Tunable.BOOLEAN, new Boolean(isSelected())));



        properties.initializeProperties();

        setProps(properties);

    }

    public DelimitedTextIDMappingClient(final ModuleProperties properties)
            throws IDMapperException, MalformedURLException {
        this(propertiesToIDMapper(properties), properties);
        Tunable t = properties.get("selected");
        setSelected(t==null || (Boolean)t.getValue());
    }

    protected  DelimitedTextIDMappingClient(final IDMapperText idMapper,
            final ModuleProperties properties) {
        super(idMapper.getURL().toString(),
                idMapper.getURL().toString(),
                idMapper, properties);
    }

    private static IDMapperText propertiesToIDMapper(
            final ModuleProperties properties)  
            throws IDMapperException, MalformedURLException {
        Tunable t = properties.get("url");
        assertNull(t, "Illegal properties: no URL.");
        URL url = new URL(t.getValue().toString());

        t = properties.get("data_source_delimiter");
        assertNull(t, "Illegal properties: no data source delimiter.");
        char[] dsDel = t.getValue().toString().toCharArray();

        char[] idDel = null;
        t = properties.get("id_delimiter");
        if (t!=null) {
            idDel = t.getValue().toString().toCharArray();
        }

        boolean transitivity = false;
        t = properties.get("transitivity");
        if (t!=null) {
            transitivity = (Boolean)t.getValue();
        }

        return new IDMapperText(url, dsDel, idDel, transitivity);
    }

    private static void assertNull(Object obj, String msg) {
        if (obj==null) {
            throw new java.lang.IllegalArgumentException(msg);
        }
    }
}
