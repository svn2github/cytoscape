
/*
  File: ZoomSelectedAction.java 
  
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

//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.awt.geom.Rectangle2D;


import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import java.util.List;
import java.util.Iterator;

import giny.view.*;
import cytoscape.giny.*;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

//-------------------------------------------------------------------------
public class ZoomSelectedAction extends CytoscapeAction {
       
    public ZoomSelectedAction ()  {
        super();
    }
    
    public void actionPerformed(ActionEvent e) {
        
      zoomSelected();
    }

  public static void zoomSelected () {
      CyNetworkView view = Cytoscape.getCurrentNetworkView();
        List selected_nodes = view.getSelectedNodes();

        if ( selected_nodes.size() == 0 ) {return;}

        Iterator selected_nodes_iterator = selected_nodes.iterator();
        double bigX;
        double bigY;
        double smallX;
        double smallY;
        double W;
        double H;
        
        NodeView first = ( NodeView )selected_nodes_iterator.next();
        bigX = first.getXPosition();
        smallX = bigX;
        bigY = first.getYPosition();
        smallY = bigY;
    
        while ( selected_nodes_iterator.hasNext() ) {
          NodeView nv = ( NodeView )selected_nodes_iterator.next();
          double x = nv.getXPosition();
          double y = nv.getYPosition();

          if ( x > bigX ) {
            bigX = x;
          } else if ( x < smallX ) {
            smallX = x;
          }

          if ( y > bigY ) {
            bigY = y;
          } else if ( y < smallY ) {
            smallY = y;
          }
        }
        
        PBounds zoomToBounds;
        if (selected_nodes.size() == 1) {
          zoomToBounds = new PBounds( smallX - 100 , smallY - 100 , ( bigX - smallX + 200 ), ( bigY - smallY + 200 ) );
        } else {
          zoomToBounds = new PBounds( smallX  , smallY  , ( bigX - smallX + 100 ), ( bigY - smallY + 100 ) );
        }
        PTransformActivity activity =  ( ( PhoebeNetworkView )view).getCanvas().getCamera().animateViewToCenterBounds( zoomToBounds, true, 500 );
    }
}
