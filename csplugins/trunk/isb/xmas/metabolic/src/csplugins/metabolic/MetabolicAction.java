package csplugins.metabolic;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;
import cytoscape.plugin.jar.*;

import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import java.beans.*;
import java.util.*;

import javax.swing.JMenuItem;

import edu.umd.cs.piccolo.PNode;

import phoebe.*;

import giny.model.*;
import giny.view.*;

public class MetabolicAction {

  public MetabolicAction () {
  }

 public static JMenuItem shadows ( Object[] args, PNode node ) {
    final CyNetworkView net_view = ( CyNetworkView )args[0];
    //CyNetwork network = net_view.getNetwork();
    final PNodeView nv = ( PNodeView )node; 

     return  new JMenuItem( new AbstractAction( "Alias Shadows" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                NodeView shadow = new ShadowNode(  nv.getRootGraphIndex(),
                                                   ( PGraphView )net_view );
              
              } } ); } } );
  }


}
