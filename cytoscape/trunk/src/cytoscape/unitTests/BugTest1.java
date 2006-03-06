
/*
  File: BugTest1.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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

package cytoscape.unitTests;


import cytoscape.Cytoscape;

import giny.model.Edge;
import giny.model.Node;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class BugTest1 extends TestCase
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public static Test suite ()
    {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(BugTest1.class);
    }

    public static void main (String[] args)
    {
        junit.textui.TestRunner.run (suite ());
    }

    public void testBug ()
    {
        Node n1 = Cytoscape.getCyNode ("S", true);
        Node n2 = Cytoscape.getCyNode ("M", true);
        Node n3 = Cytoscape.getCyNode ("A", true);
        Node n4 = Cytoscape.getCyNode ("Z", true);

        Cytoscape.getRootGraph ().createEdge (n1, n2, false); // S-M
        int  edge_idx = Cytoscape.getRootGraph ().createEdge (n1, n3, false); // S-A
        Edge del1 = Cytoscape.getRootGraph ().getEdge (edge_idx);
        edge_idx = Cytoscape.getRootGraph ().createEdge (n2, n3, false); // M-A
        Edge del2 = Cytoscape.getRootGraph ().getEdge (edge_idx);
        // Now delete S-A & M-A:
        Cytoscape.getRootGraph ().removeEdge (del1);
        Cytoscape.getRootGraph ().removeEdge (del2);
        edge_idx = Cytoscape.getRootGraph ().createEdge (n1, n4, false);
	// ****** THIS SUBEDGE RETURNED HAS A NULL SOURCE AND TARGET!!: *****
        Edge subedge = Cytoscape.getRootGraph ().getEdge (edge_idx);
        Node src    = subedge.getSource ();
        Node target = subedge.getTarget ();
        Assert.assertNotNull (src); // null!
        Assert.assertNotNull (target); // null!
    }
}
