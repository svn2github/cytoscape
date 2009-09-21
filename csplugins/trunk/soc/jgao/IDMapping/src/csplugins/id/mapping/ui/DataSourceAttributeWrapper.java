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

package csplugins.id.mapping.ui;

import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author gjj
 */
class DataSourceAttributeWrapper implements Comparable {
        private String value;
        private DsAttr da;

        static private Map<String, DataSourceAttributeWrapper> dataSourceWrappers
                = new HashMap();
        static private Map<String, DataSourceAttributeWrapper> attributeWrappers
                = new HashMap();

        static private DataSourceAttributeWrapper separator =
                new DataSourceAttributeWrapper("==Below are attributes==",DsAttr.SEPARATOR);

        enum DsAttr{
            DATASOURCE, ATTRIBUTE, SEPARATOR;
        };

        static DataSourceAttributeWrapper getSeparator() {
            return separator;
        }

        static DataSourceAttributeWrapper getInstance(String value, DsAttr da) {
            if (value==null) {
                return null;
            }

            DataSourceAttributeWrapper wrapper;
            if (da==DsAttr.DATASOURCE) {
                wrapper = dataSourceWrappers.get(value);
            } else if (da==DsAttr.ATTRIBUTE) {
                wrapper = attributeWrappers.get(value);
            } else {
                wrapper = separator;
            }

            if (wrapper==null) {
                wrapper = new DataSourceAttributeWrapper(value, da);
                if (da==DsAttr.DATASOURCE) {
                    dataSourceWrappers.put(value, wrapper);
                } else if (da==DsAttr.ATTRIBUTE) {
                    attributeWrappers.put(value, wrapper);
                }
            }

            return wrapper;
        }

        private DataSourceAttributeWrapper(String value, DsAttr da) {
            this.value = value;
            this.da = da;
        }

        DsAttr getDsAttr() {
            return da;
        }

        String value() {
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
