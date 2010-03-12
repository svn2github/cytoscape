
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

package example;

import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape; 
import cytoscape.CytoscapeInit; 
import cytoscape.actions.LoadNetworkTask;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

public class OneHundredKAbbreviatedAction extends AbstractOneHundredKAction {

	public OneHundredKAbbreviatedAction() {
		super("Abbreviated Demo Test");

		testScenarios.add( new TestScenario( "1 network with 100,000 nodes and edges: ", "/100K_1_net_of_100K.nnf"));
		testScenarios.add( new TestScenario( "50 networks, each with 10 total nodes and edges with one overview: ", "/abbrev_50nets_of_10.nnf"));

		testScenarios.add( new TestScenario( "500 networks, each with 1 node: ","/abbrev_500nets_of_1.nnf"));

	}
}
