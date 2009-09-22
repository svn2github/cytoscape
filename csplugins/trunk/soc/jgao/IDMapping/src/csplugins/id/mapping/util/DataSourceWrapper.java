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

import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author gjj
 */
public class DataSourceWrapper implements Comparable {
        private String value;
        private DsAttr da;

        static private Map<String, DataSourceWrapper> dataSourceWrappers
                = new HashMap();
        static private Map<String, DataSourceWrapper> attributeWrappers
                = new HashMap();

        public enum DsAttr{
            DATASOURCE, ATTRIBUTE;
        };
//
//        public static DataSourceWrapper getInstance(String value) {
//            if (value==null)
//                return null;
//
//            DataSourceWrapper dsw = dataSourceWrappers.get(value);
//            if (value!=null) {
//                return dsw;
//            }
//
//            return attributeWrappers.get(value);
//        }

        public static DataSourceWrapper getInstance(String value, DsAttr da) {
            if (value==null || da==null) {
                return null;
            }

            DataSourceWrapper wrapper = null;
            if (da==DsAttr.DATASOURCE) {
                wrapper = dataSourceWrappers.get(value);
            } else if (da==DsAttr.ATTRIBUTE) {
                wrapper = attributeWrappers.get(value);
            }

            if (wrapper==null) {
                wrapper = new DataSourceWrapper(value, da);
                if (da==DsAttr.DATASOURCE) {
                    dataSourceWrappers.put(value, wrapper);
                } else if (da==DsAttr.ATTRIBUTE) {
                    attributeWrappers.put(value, wrapper);
                }
            }

            return wrapper;
        }

        private DataSourceWrapper(String value, DsAttr da) {
            this.value = value;
            this.da = da;
        }

        public DsAttr getDsAttr() {
            return da;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

        public int compareTo(Object obj) {
            return this.toString().compareTo(obj.toString());
        }
    }
