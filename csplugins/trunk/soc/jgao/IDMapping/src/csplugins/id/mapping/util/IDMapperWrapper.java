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

import csplugins.id.mapping.IDMapperClientManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;

/**
 *
 * @author gjj
 */
public class IDMapperWrapper {
    public static Map<XrefWrapper, Set<XrefWrapper>> mapID(Set<XrefWrapper> srcXrefs,
            Set<DataSourceWrapper> tgtDataSources) {
        // separate xrefs
        Set<Xref> idXrefs = new HashSet();
        Set<XrefWrapper> attrXrefs = new HashSet();
        for (XrefWrapper xref : srcXrefs) {
            DataSourceWrapper ds = xref.getDataSource();
            if (ds.getDsAttr()==DataSourceWrapper.DsAttr.DATASOURCE) {
                idXrefs.add(new Xref(xref.getValue(),DataSource.getByFullName(ds.value())));
            } else if (ds.getDsAttr()==DataSourceWrapper.DsAttr.ATTRIBUTE) {
                attrXrefs.add(xref);
            }
        }

        // separate datasources
        Set<DataSource> idTypes = new HashSet();
        Set<DataSourceWrapper> attrTypes = new HashSet();
        for (DataSourceWrapper ds : tgtDataSources) {
            if (ds.getDsAttr()==DataSourceWrapper.DsAttr.DATASOURCE) {
                idTypes.add(DataSource.getByFullName(ds.value()));
            } else if (ds.getDsAttr()==DataSourceWrapper.DsAttr.ATTRIBUTE) {
                attrTypes.add(ds);
            }
        }

        Map<XrefWrapper, Set<XrefWrapper>> result = new HashMap();
        
        IDMapperStack idMapperStack = IDMapperClientManager.selectedIDMapperStack();

        // mapping id to id
        if (!idXrefs.isEmpty() && !idTypes.isEmpty()) {
            Map<Xref,Set<Xref>> mappingId2Id = null;
            try {
                 mappingId2Id = idMapperStack.mapID(idXrefs, idTypes.toArray(new DataSource[0]));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (mappingId2Id!=null) {
                for (Map.Entry<Xref,Set<Xref>> entry : mappingId2Id.entrySet()) {
                    Xref srcXref = entry.getKey();
                    Set<Xref> tgtXrefs = entry.getValue();

                    XrefWrapper srcXrefWrapper = new XrefWrapper(srcXref);
                    Set<XrefWrapper> tgtXrefWrappers = result.get(srcXrefWrapper);
                    if (tgtXrefWrappers==null) {
                        tgtXrefWrappers = new HashSet(tgtXrefs.size());
                        result.put(srcXrefWrapper, tgtXrefWrappers);
                    }
                    
                    for (Xref tgtXref : tgtXrefs) {
                        tgtXrefWrappers.add(new XrefWrapper(tgtXref));
                    }                    
                }
            }
        }

        // mapping id to attribute
        if (!idXrefs.isEmpty() && !attrTypes.isEmpty()) {
            for (Xref srcXref : idXrefs) {
                XrefWrapper srcXrefWrapper = new XrefWrapper(srcXref);
                Set<XrefWrapper> tgtXrefWrappers = result.get(srcXrefWrapper);
                if (tgtXrefWrappers==null) {
                    tgtXrefWrappers = new HashSet();
                    result.put(srcXrefWrapper, tgtXrefWrappers);
                }

                for (DataSourceWrapper dsw : attrTypes) {
                    String attrType = dsw.value();
                    Set<String> tgtAttrValues;
                    try {
                        tgtAttrValues = idMapperStack.getAttributes(srcXref, attrType);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        continue;
                    }

                    for (String tgtAttrValue : tgtAttrValues) {
                        tgtXrefWrappers.add(new XrefWrapper(tgtAttrValue, dsw));
                    }
                }
            }
        }

        // mapping attribute to id
        if (!attrXrefs.isEmpty() && !idTypes.isEmpty()) {
            for (XrefWrapper xrw : attrXrefs) {
                String srcAttrValue = xrw.getValue();
                String srcAttrType = xrw.getDataSource().value();
                Map<Xref,String> mapTgtXrefs;
                try {
                    mapTgtXrefs = idMapperStack.freeAttributeSearch(srcAttrValue, srcAttrType, -1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }

                Set<Xref> tgtXrefs = new HashSet();
                for (Map.Entry<Xref,String> entry : mapTgtXrefs.entrySet()) {
                    Xref tgtXref = entry.getKey();
                    String tgtType = entry.getValue();
                    if (idTypes.contains(tgtXref.getDataSource())) {
                            //&& tgtType.equals(srcAttrValue)) { // TODO: should we require exact match
                        tgtXrefs.add(entry.getKey());
                    }
                }

                Set<XrefWrapper> tgtXrefWrappers = result.get(xrw);
                if (tgtXrefWrappers==null) {
                    tgtXrefWrappers = new HashSet(tgtXrefs.size());
                    result.put(xrw, tgtXrefWrappers);
                }

                for (Xref tgtXref : tgtXrefs) {
                    tgtXrefWrappers.add(new XrefWrapper(tgtXref));
                }
            }
        }

        // mapping attribute to attribute is not supported

        return result;
    }
}
