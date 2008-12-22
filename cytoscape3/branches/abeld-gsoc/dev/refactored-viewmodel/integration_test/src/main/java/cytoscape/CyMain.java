/*
  File: CyMain.java

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
package cytoscape;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.GraphObject;

import org.cytoscape.viewmodel.*;
import org.cytoscape.vizmap.*;

import org.cytoscape.presentation.NetworkPresentationFactory;
import org.cytoscape.presentation.TextPresentation;

/**
 * This is the main startup class for Cytoscape. This parses the command line
 * and implements CyInitParams so that it can be used to initialize cytoscape.
 *
 * <p>
 * Look and Feel is modified for jgoodies 2.1.4 by Kei Ono
 * </p>
 */
public class CyMain{

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public static void main(String[] args) throws Exception {
	    System.out.println("hello world! -- main");
		CyMain app = new CyMain(args);
	}

	/**
	 * Creates a new CyMain object.
	 *
	 * @param args  DOCUMENT ME!
	 *
	 * @throws Exception  DOCUMENT ME!
	 */
	public CyMain() throws Exception {
		this( new String[]{});
	    System.out.println("hello world! -- empty");
	}

    public CyMain(String[] args, CyNetworkFactory f) throws Exception {
	    System.out.println("hello world! -- with args");
	    System.out.println("NetworkFactory:"+f);
	}
    public CyMain(CyNetworkFactory f, CyNetworkViewFactory vf,
		  NetworkPresentationFactory pf, VisualStyleCatalog vsc,
		  VisualPropertyCatalog vpCatalog) throws Exception {
	    System.out.println("hello world! -- factories");
	    System.out.println("NetworkFactory:"+f);
	    CyNetwork network = f.getInstance();
	    CyNode n1 = network.addNode();
	    n1.attrs().set("name", "node 1");
	    CyNode n2 = network.addNode();
	    n2.attrs().set("name", "node 2");
	    CyEdge e1 = network.addEdge(n1, n2, false);
	    System.out.println("nodes:"+network.getNodeCount()+" edges:"+network.getEdgeCount());
	    CyNetworkView view = vf.getNetworkViewFor(network);
	    dumpViewmodelState(view);
	    // mutate model, see whether viewmodel updates
	    CyNode n3 = network.addNode();
	    CyEdge e2 = network.addEdge(n1, n3, false);
	    dumpViewmodelState(view);
	    System.out.println(view.getCyNodeView(n3));
	    System.out.println(view.getCyEdgeView(e2));

	    // create visual style, add an example MappingCalculator, dump values
	    VisualStyle myStyle = vsc.createVisualStyle();
	    VisualProperty nodeLabel = vpCatalog.getVisualProperty("NODE_LABEL");
	    MappingCalculator nodeLabelCalculator = new PassthroughMappingCalculator("name", nodeLabel,
										     String.class);
	    myStyle.setMappingCalculator(nodeLabelCalculator);
	    
	    myStyle.apply(view);
	    // TODO: add passthroughMapping to copy id to nodeLabel
	    TextPresentation p = pf.getTextPresentationFor(view);
	    System.out.println(p.render());
	}

    public void dumpViewmodelState(CyNetworkView view){
	    System.out.println("----------------------------------------------------");
	    System.out.println("dumping state of:"+view);
	    for (View<?extends GraphObject>v: view.getAllViews()){
		System.out.println("one View:"+v);
		System.out.println("    source:"+v.getSource());
	    }
	    System.out.println("----------------------------------------------------");
    }

    public CyMain(CyNetworkFactory f) throws Exception {
	    System.out.println("hello world! -- with only NetworkFactory");
	    System.out.println("NetworkFactory:"+f);
	    CyNetwork network = f.getInstance();
	    CyNode n1 = network.addNode();
	    CyNode n2 = network.addNode();
	    CyEdge e1 = network.addEdge(n1, n2, false);
	    System.out.println("nodes:"+network.getNodeCount()+" edges:"+network.getEdgeCount());
	}


    public CyMain(String[] args) throws Exception {
	    System.out.println("hello world! -- with naked args");
	}

}
