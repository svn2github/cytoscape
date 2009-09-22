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

package csplugins.id.mapping.util;

import org.bridgedb.Xref;

/**
 *
 * @author gjj
 */
public class XrefWrapper {
    private String value;
    private DataSourceWrapper dataSource;

    public XrefWrapper(final Xref xref) {
        this(xref.getId(),DataSourceWrapper.getInstance(
                xref.getDataSource().getFullName(),
                DataSourceWrapper.DsAttr.DATASOURCE));
    }

    public XrefWrapper(final String value, DataSourceWrapper dataSource) {
        this.value = value;
        this.dataSource = dataSource;
    }

    public String getValue() {
        return value;
    }

    public DataSourceWrapper getDataSource() {
        return dataSource;
    }

    /**
     * @return short string representation for this Xref, for example En:ENSG000001 or X:1004_at
     *   This string representation is not meant to be stored or parsed, it is there mostly for
     *   debugging purposes.
     */
    public String toString() { return dataSource.toString()+":"+value;  }

    /**
     * hashCode calculated from id and datasource combined.
     * @return the hashCode
     */
    public int hashCode()
    {
        return toString().hashCode();
    }

    /**
     * @return true if both the id and the datasource are equal.
     * @param o Object to compare to
     */
    public boolean equals(Object o)
    {
            if (o == null) return false;
            if(!(o instanceof XrefWrapper)) return false;
            XrefWrapper ref = (XrefWrapper)o;
            return value.equals(ref.value) && dataSource.equals(ref.dataSource);
    }
}
