// FileReadingAbstractions.java

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
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//----------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//----------------------------------------------------------------------------
package cytoscape.data.readers;
import java.io.FileNotFoundException;
import y.view.Graph2D;
import cytoscape.GraphObjAttributes;
import cytoscape.CytoscapeConfig;
import cytoscape.data.*;
import cytoscape.data.servers.*;
import cytoscape.data.readers.*;
//----------------------------------------------------------------------------
public class FileReadingAbstractions {

//----------------------------------------------------------------------------
public static Graph2D loadGMLBasic (String filename, 
                                    GraphObjAttributes edgeAttributes) 
{
  GMLReader reader = new GMLReader (filename);
  return loadBasic (reader, edgeAttributes);
}
//----------------------------------------------------------------------------
public static Graph2D loadIntrBasic (BioDataServer dataServer,
                                     String species,
                                     String filename, 
                                     GraphObjAttributes edgeAttributes) 
{
  InteractionsReader reader = new InteractionsReader (dataServer, species, filename);
  return loadBasic (reader, edgeAttributes);

}
//----------------------------------------------------------------------------
public static Graph2D loadBasic (GraphReader reader, GraphObjAttributes edgeAttributes) 
{
  reader.read ();
  GraphObjAttributes newEdgeAttributes = reader.getEdgeAttributes ();
  edgeAttributes.add (newEdgeAttributes);
  edgeAttributes.addNameMap (newEdgeAttributes.getNameMap ());
  edgeAttributes.addClassMap (newEdgeAttributes.getClassMap ());
  return reader.getGraph();

}
//----------------------------------------------------------------------------
public static void initAttribs (BioDataServer dataServer,
                                String species,
                                CytoscapeConfig config,
                                Graph2D graph,
                                GraphObjAttributes nodeAttributes,
                                GraphObjAttributes edgeAttributes)
{
  String [] edgeAttributeFilenames = config.getEdgeAttributeFilenames ();
  String [] nodeAttributeFilenames = config.getNodeAttributeFilenames ();
  initAttribs (dataServer, species, graph, nodeAttributes, edgeAttributes,
               edgeAttributeFilenames, nodeAttributeFilenames);

}
//----------------------------------------------------------------------------
public static void initAttribs (BioDataServer dataServer,
                                String species,
                                Graph2D graph,
                                GraphObjAttributes nodeAttributes,
                                GraphObjAttributes edgeAttributes,
                                String [] nodeAttributeFilenames,
                                String [] edgeAttributeFilenames)
{
  if (nodeAttributeFilenames != null)
    for (int i=0; i < nodeAttributeFilenames.length; i++) {
       try {
         nodeAttributes.readAttributesFromFile (dataServer, species, nodeAttributeFilenames[i]);
         } 
       catch (NumberFormatException nfe) {
         System.err.println (nfe.getMessage ());
          nfe.printStackTrace ();
         } 
      catch (IllegalArgumentException iae) {
        System.err.println (iae.getMessage ());
        iae.printStackTrace ();
        }
       catch (FileNotFoundException fnfe) {
         System.err.println (fnfe.getMessage ());
         fnfe.printStackTrace ();
         }
       }

   if (edgeAttributeFilenames != null)
     for (int i=0; i < edgeAttributeFilenames.length; i++) {
       try {
         edgeAttributes.readAttributesFromFile (edgeAttributeFilenames [i]);
         } 
       catch (Exception excp) {
         System.err.println (excp.getMessage ());
         excp.printStackTrace ();
         } 
        } // for i

    if (nodeAttributes != null)
      addNameMappingToAttributes (graph.getNodeArray (), nodeAttributes);

    // no need to add name mapping for edge attributes -- it has already been
    // done, at the time when the interactions file (or gml file) was read


} // initAttribs
//----------------------------------------------------------------------------
    /**
     * add node-to-canonical name mapping (and the same for edges), so that
     * node attributes can be retrieved by simply knowing the y.base.node,
     * which is the basic view of data in this program.  for example:
     *
     *  NodeCursor nc = graph.selectedNodes (); 
     *  for (nc.toFirst (); nc.ok (); nc.next ()) {
     *    Node node = nc.node ();
     *    String canonicalName = nodeAttributes.getCanonicalName (node);
     *    HashMap bundle = nodeAttributes.getAllAttributes (canonicalName);
     *    }
     * 
     * 
     */
protected static void addNameMappingToAttributes (Object [] graphObjects,
                                                  GraphObjAttributes attributes)
{
  for (int i=0; i < graphObjects.length; i++) {
    Object graphObj = graphObjects [i];
    String canonicalName = graphObj.toString ();
    attributes.addNameMapping (canonicalName, graphObj);
   }

} // addNameMappingToAttributes
//----------------------------------------------------------------------------
} // FileReadingAbstractions


