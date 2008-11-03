/*
  File: CyLayouts.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.layout;


import java.util.Collection;
import java.util.Map;


public interface CyLayouts {

	String PREF_MENU_KEY = "preferredMenu";
	String PREF_MENU_DEFAULT = "Cytoscape Layouts";
	String DEFAULT_LAYOUT_NAME = "grid";

	void addLayout(CyLayoutAlgorithm layout, Map props);

	void removeLayout(CyLayoutAlgorithm layout, Map props);

	CyLayoutAlgorithm getLayout(String name);

	Collection<CyLayoutAlgorithm> getAllLayouts(); 

	CyLayoutAlgorithm getDefaultLayout(); 

	String getMenuName(CyLayoutAlgorithm layout); 
}
