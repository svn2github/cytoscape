package ucsd.rmkelley.EdgeRandomization;
import java.io.*;
import java.util.*;
import edu.umd.cs.piccolo.activities.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import giny.view.NodeView;
import giny.model.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import phoebe.PNodeView;
import phoebe.PGraphView;
import cytoscape.data.Semantics;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*; 
import java.awt.BorderLayout;
import java.awt.event.*;
import cytoscape.layout.*;

public class EdgeRandomizationOptions{
  public int iterations;
  public Vector directedTypes;
  public CyNetwork currentNetwork;
}
