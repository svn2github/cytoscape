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
package csplugins.layout.algorithms;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.util.*;
import giny.view.*;

import cern.colt.map.*;
import cern.colt.list.*;

import csplugins.layout.AbstractLayout;
/**
 * @author Gaudenz Alder
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CircleGraphLayoutAlgorithm extends AbstractLayout {
	  
  public CircleGraphLayoutAlgorithm ( CyNetworkView networkView ) {
    super( networkView );
  }


  /* (non-Javadoc)
  * @see java.lang.Runnable#run()
  */
  public Object construct () {

    initialize();
    java.util.Iterator nodeIter = networkView.getNetwork().nodesIterator();
    int nodeCount = networkView.getNetwork().getNodeCount();

    int r = (int)currentSize.getHeight();
    // Compute angle step
    double phi = 2 * Math.PI / nodeCount;
    // Arrange vertices in a circle
    for (int i = 0; i < nodeCount; i++) {
      int node = ((CyNode)nodeIter.next()).getRootGraphIndex();
        networkView.getNodeView(node).setXPosition( r + r * Math.sin(i * phi) );
        networkView.getNodeView(node).setYPosition( r + r * Math.cos(i * phi) );
      }
    /* 
      java.util.Iterator nod = networkView.getNodeViewsIterator();
      while ( nod.hasNext() ) {
        ( ( NodeView )nod.next() ).setNodePosition( true );
      }
      */
    return null;
  }
  
}
