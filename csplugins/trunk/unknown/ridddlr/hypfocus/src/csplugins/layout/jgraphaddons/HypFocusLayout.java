/*
 * @(#)CircleLayoutAlgorithm.java 1.0 18-MAY-2004
 * 
 * Copyright (c) 2004, Gaudenz Alder All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. - Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. - Neither the name of JGraph nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package csplugins.layout.jgraphaddons;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.util.*;
import giny.view.*;
import cytoscape.layout.*;

import cern.colt.map.*;
import cern.colt.list.*;

import java.util.Iterator;


/**
 * @author Gaudenz Alder
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class HypFocusLayout extends AbstractLayout {

  public static double array_x[];
  public static double array_y[];
	
  public HypFocusLayout ( CyNetworkView networkView ) {
    super( networkView );
  }


  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  public Object construct () {

  initialize();
  int[] nodes = networkView.getNetwork().getNodeIndicesArray();
  
  

  final double SQUEEZE = 5;
  double radius = 500;
  double mid2x = 1000;
  double mid2y = 1000;


  //if initializing hypfocus
  if (networkView.getSelectedNodes().size() == 0) // if there are no selected nodes
  {
    JOptionPane.showMessageDialog(networkView.getComponent(),
                        "No nodes selected: initializing.");

    array_x = new double[nodes.length];
    array_y = new double[nodes.length];


    System.out.println("nodes.length " + nodes.length);
    for (int i = 0; i < nodes.length; i++ )
    {
      array_x[i] = networkView.getNodeDoubleProperty( nodes[i], CyNetworkView.NODE_X_POSITION);
      array_y[i] = networkView.getNodeDoubleProperty( nodes[i], CyNetworkView.NODE_Y_POSITION);
      //System.out.println(i + " " + "nodes[i]: " + nodes[i]);
    }
  }
  
  else
  {
        CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
        int[] selectedNodeInx = graphView.getSelectedNodeIndices();
        
	mid2x = array_x[0];
	mid2y = array_y[0];

        int centernode = nodes.length + selectedNodeInx[0];
        System.out.println("nodes.length: " + nodes.length 
                + " selectedNodeInx[[0]: " + selectedNodeInx[0]
                + " centernode: " + centernode);
        mid2x = array_x[centernode];
	mid2y = array_y[centernode];
  }

  int r = (int)currentSize.getHeight();
  radius = r/2;
  double phi = 2 * Math.PI / nodes.length;



  for (int i = 0; i < nodes.length; i++ )
  {

    double relx = array_x[i] - mid2x;
    double rely = array_y[i] - mid2y;
    //double relx = networkView.getNodeDoubleProperty( nodes[i], CyNetworkView.NODE_X_POSITION) - mid2x;
    //double rely = networkView.getNodeDoubleProperty( nodes[i], CyNetworkView.NODE_Y_POSITION) - mid2y;

    double euc_dist = Math.sqrt( relx*relx + rely*rely);

    double ratio = radius / Math.sqrt (euc_dist*euc_dist + radius*radius/SQUEEZE/SQUEEZE);

    relx *= ratio;
    rely *= ratio;
    double current_x = radius + relx;
    double current_y = radius + rely;

    networkView.setNodeDoubleProperty( nodes[i], CyNetworkView.NODE_X_POSITION, 	current_x );
    networkView.setNodeDoubleProperty( nodes[i], CyNetworkView.NODE_Y_POSITION, 	current_y );
  }


     
  java.util.Iterator nod = networkView.getNodeViewsIterator();
  while ( nod.hasNext() )
  {
      ( ( NodeView )nod.next() ).setNodePosition( true );
  }
  return null;
  }

	
}
