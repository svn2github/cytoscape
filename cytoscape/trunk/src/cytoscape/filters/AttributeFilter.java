package cytoscape.filters;

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/


import y.base.*;
import y.view.*;

import cytoscape.undo.UndoableGraphHider;

// List, Iterator, Hashtable, etc.
import java.util.*;

import cytoscape.data.*;
import cytoscape.*;
/**
 * Old javadoc comments from copied code (-owo 2003.01.28):
 * 
 * @author namin@mit.edu
 * @version 2002-03-02
 */
public class AttributeFilter extends Filter {
    /**
     * Matching value.
     */
    String matchingValue;

    double matchingDouble;
    boolean doubleFlag=false;

    int matchingInt;
    boolean intFlag=false;

    String attributeSelected;
    GraphObjAttributes nodeAttributes;

    public AttributeFilter(Graph2D graph,
			   GraphObjAttributes nodeAttributes,
			   String attributeSelected,
			   String matchingValue) {
	super(graph);
	this.nodeAttributes = nodeAttributes;
	this.matchingValue = matchingValue;
	this.attributeSelected = attributeSelected;
	prepClassHandling();
    }

    private void prepClassHandling() {
	if(attributeSelected==null) return;
	if(nodeAttributes.getClass(attributeSelected).equals(Double.class)) {
	    try {
		matchingDouble = (new Double(matchingValue)).doubleValue();
		doubleFlag=true;
	    } catch (NumberFormatException e) {}
	}
	if(nodeAttributes.getClass(attributeSelected).equals(Integer.class)) {
	    try {
		matchingInt = (new Integer(matchingValue)).intValue();
		intFlag=true;
	    } catch (NumberFormatException e) {}
	}
    }

    public NodeList get(NodeList hidden) {
	NodeList flagged = new NodeList();

	for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
	    Node node = nc.node();
	    // if no attribute is selected, then select no nodes.
	    if(attributeSelected==null) {
		flagged.add(node);
		continue;
	    }
	    // otherwise: test to see if the attribute matches.
	    String geneName = nodeAttributes.getCanonicalName(node);
	    Object value = nodeAttributes.get(attributeSelected,geneName);

            if(value instanceof List) {
                Iterator valueIt = ((List)value).iterator();
		boolean matched=false;
                while(valueIt.hasNext()) {
                    Object subValue = valueIt.next();
		    
		    if (doubleFlag&&(subValue!=null)) {
			if(((Double)subValue).doubleValue()==matchingDouble)
			    matched=true;
		    }
		    else if (intFlag&&(subValue!=null)) {
			if(((Integer)subValue).intValue()==matchingInt)
			    matched=true;
		    }
		    else if (subValue instanceof String) {
			String strval = (String)subValue;
			String strmatch = matchingValue;
			if(strval.equals(strmatch))
			    matched=true;
		    }
		    else {
			if(subValue!=null)
			    System.out.println(subValue.getClass().toString());
		    }
                }
		if(!matched) flagged.add(node);
	    }
	    else {
		if (doubleFlag&&(value!=null)) {
		    if(((Double)value).doubleValue()!=matchingDouble)
			flagged.add(node);
		}
		else if (intFlag&&(value!=null)) {
		    if(((Integer)value).intValue()!=matchingInt)
			flagged.add(node);
		}
		else if (value instanceof String) {
		    String strval = (String)value;
		    String strmatch = matchingValue;
		    if(!(strval.equals(strmatch)))
			flagged.add(node);
		}
		else {
		    if(value!=null)
			System.out.println(value.getClass().toString());
		    flagged.add(node);
		}

	    }



	}
	return flagged;
    }

}


