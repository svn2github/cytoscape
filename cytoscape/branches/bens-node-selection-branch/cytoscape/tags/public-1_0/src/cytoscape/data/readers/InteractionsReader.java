// InteractionsReader:  from semi-structured text file, into an array of Interactions

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

//-----------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.readers;
//-----------------------------------------------------------------------------------------
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;

import y.base.Node;
import y.base.Edge;
import y.view.Graph2D;

import cytoscape.GraphObjAttributes;
import cytoscape.data.Interaction;
import cytoscape.data.*;
import cytoscape.data.servers.*;
import cytoscape.data.readers.*;
//-----------------------------------------------------------------------------------------
public class InteractionsReader implements GraphReader {
  String filename;
  Vector allInteractions = new Vector ();
  GraphObjAttributes edgeAttributes = new GraphObjAttributes ();
  Graph2D graph;
  BioDataServer dataServer;
  String species;
//-----------------------------------------------------------------------------------------
public InteractionsReader (BioDataServer dataServer, String species, String filename)
{
  this.filename = filename;
  this.dataServer = dataServer;
  this.species = species;
}
//-----------------------------------------------------------------------------------------
public void read ()
{
  String rawText;
  try {
    if (filename.trim().startsWith ("jar://")) {
      TextJarReader reader = new TextJarReader (filename);
      reader.read ();
      rawText = reader.getText ();
      }
    else {
      TextFileReader reader = new TextFileReader (filename);
      reader.read ();
      rawText = reader.getText ();
      }
    }
  catch (Exception e0) {
    System.err.println ("-- Exception while reading interaction file " + filename);
    System.err.println (e0.getMessage ());
    return;
    }

  String delimiter = " ";
  if (rawText.indexOf ("\t") >= 0)
    delimiter = "\t";
  StringTokenizer strtok = new StringTokenizer (rawText, "\n");

  Vector interactions = new Vector ();
  while (strtok.hasMoreElements ()) {
    String newLine = (String) strtok.nextElement ();
    allInteractions.addElement (new Interaction (newLine, delimiter));
    }

  //for (int i=0; i < interactions.size (); i++) {
  //  Interaction inter = (Interaction) allInteractions.elementAt (i);
  //  System.out.println (inter);
  //  }

  createGraphFromInteractionData ();

}  // readFromFile
//-------------------------------------------------------------------------------------------
public int getCount ()
{
  return allInteractions.size ();
}
//-------------------------------------------------------------------------------------------
public Interaction [] getAllInteractions ()
{
  Interaction [] result = new Interaction [allInteractions.size ()];

  for (int i=0; i < allInteractions.size (); i++) {
    Interaction inter = (Interaction) allInteractions.elementAt (i);
    result [i] = inter;
    }

  return result;

}
//-------------------------------------------------------------------------------------------
protected String canonicalizeName (String name)
{
  String canonicalName = name;
  // System.out.println ("canonicalize, dataServer = " + dataServer);
  if (dataServer != null) {
    canonicalName = dataServer.getCanonicalName (species, name);
    //System.out.println (" -- canonicalizeName from server: " + canonicalName);
    }

  return canonicalName;

} // canonicalizeName
//-------------------------------------------------------------------------------------------
protected void createGraphFromInteractionData ()
{
  graph = new Graph2D ();
  Interaction [] interactions = getAllInteractions ();

  Hashtable nodes = new Hashtable ();

    //---------------------------------------------------------------------------
    // loop through all of the interactions -- which are triples of the form:
    //           source; target0, target1...; interaction type
    // using a hash to avoid duplicate node creation, add a node to the graph
    // for each source and target
    // in addition,
    //---------------------------------------------------------------------------

  for (int i=0; i < interactions.length; i++) {
    Interaction interaction = interactions [i];
    //System.out.println ("source: " + interaction.getSource ());
    String nodeName = canonicalizeName (interaction.getSource ());
    //System.out.println ("canonicalized: " + nodeName);    
    if (!nodes.containsKey (nodeName)) {
      Node node = graph.createNode (0.0, 0.0, 70.0, 30.0, nodeName);
      nodes.put (nodeName, node);
      }
    String [] targets = interaction.getTargets ();
    for (int t=0; t < targets.length; t++) {
      String targetNodeName = canonicalizeName (targets [t]);
      if (!nodes.containsKey (targetNodeName)) {
        Node targetNode = graph.createNode (0.0, 0.0, 70.0, 30.0, targetNodeName);
        nodes.put (targetNodeName, targetNode);
        } // if target node is previously unknown
      } // for t
    } // i


    //---------------------------------------------------------------------------
    // now loop over the interactions again, this time creating edges between
    // all sources and each of their respective targets.
    // for each edge, save the source-target pair, and their interaction type,
    // in edgeAttributes -- a hash of a hash of name-value pairs, like this:
    //   edgeAttributes ["interaction"] = interactionHash
    //   interactionHash [sourceNode::targetNode] = "pd"
    //---------------------------------------------------------------------------

  for (int i=0; i < interactions.length; i++) {
    Interaction interaction = interactions [i];
    String nodeName = canonicalizeName (interaction.getSource ());
    String interactionType = interaction.getType ();
    Node sourceNode = (Node) nodes.get (nodeName);
    String [] targets = interaction.getTargets ();
    for (int t=0; t < targets.length; t++) {
      String targetName = canonicalizeName (targets [t]);
      Node targetNode = (Node) nodes.get (targetName);
      Edge edge = graph.createEdge (sourceNode, targetNode);
      String edgeName = nodeName + " (" + interactionType + ") " + targetName;
      int previousMatchingEntries = edgeAttributes.countIdentical(edgeName);
      if (previousMatchingEntries > 0)
        edgeName = edgeName + "_" + previousMatchingEntries;
      edgeAttributes.add ("interaction", edgeName, interactionType);
      edgeAttributes.addNameMapping (edgeName, edge);
      } // for t
   } // for i
  
} // createGraphFromInteractionData
//-------------------------------------------------------------------------------------------
public Graph2D getGraph ()
{
  return graph;

} // createGraph
//------------------------------------------------------------------------------------
public GraphObjAttributes getEdgeAttributes ()
{
  return edgeAttributes;

} // getEdgeAttributes
//------------------------------------------------------------------------------------
} // InteractionsReader



