// FileReadingAbstractions.java
//-------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.readers;
import java.io.FileNotFoundException;
import y.view.Graph2D;
import cytoscape.GraphObjAttributes;
import cytoscape.CytoscapeConfig;

public class FileReadingAbstractions {

    public static Graph2D loadGMLBasic (String filename,
					GraphObjAttributes edgeAttributes) {
	GMLReader reader = new GMLReader(filename);
	return loadBasic(reader,edgeAttributes);
    }

    public static Graph2D loadInteractionBasic (String filename,
						GraphObjAttributes edgeAttributes) {
	InteractionsReader reader = new InteractionsReader (filename);
	return loadBasic(reader,edgeAttributes);
    }
    public static Graph2D loadBasic(GraphReader reader,
				    GraphObjAttributes edgeAttributes) {
	reader.read ();
	GraphObjAttributes interactionEdgeAttributes = reader.getEdgeAttributes ();
	edgeAttributes.add (interactionEdgeAttributes);
	edgeAttributes.addNameMap (interactionEdgeAttributes.getNameMap ());
	return reader.getGraph();
    }

    public static void initAttribs(CytoscapeConfig config,
				   Graph2D graph,
				   GraphObjAttributes nodeAttributes,
				   GraphObjAttributes edgeAttributes) {
	String [] nodeAttributeFilenames = config.getNodeAttributeFilenames ();
	if (nodeAttributeFilenames != null)
	    for (int i=0; i < nodeAttributeFilenames.length; i++) {
		try {
		    nodeAttributes.readAttributesFromFile (nodeAttributeFilenames [i]);
		} catch (NumberFormatException nfe) {
		    continue;
		} catch (IllegalArgumentException iae) {
		    continue;
		} catch (FileNotFoundException fnfe) {
		    continue;
		}
	    }

	
	String [] edgeAttributeFilenames = config.getEdgeAttributeFilenames ();
	if (edgeAttributeFilenames != null)
	    for (int i=0; i < edgeAttributeFilenames.length; i++) {
		try {
		    edgeAttributes.readAttributesFromFile (edgeAttributeFilenames [i]);
		} catch (NumberFormatException nfe) {
		    continue;
		} catch (IllegalArgumentException iae) {
		    continue;
		} catch (FileNotFoundException fnfe) {
		    continue;
		}
	    }
	if (nodeAttributes != null)
	    addNameMappingToAttributes (graph.getNodeArray (), nodeAttributes);
    }

    /**
     * add node-to-canonical name mapping (and the same for edges), so that
     * node attributes can be retrieved by simply knowing the y.base.node,
     * which is the basic view of data in this program.  for example:
     *
     *  NodeCursor nc = graph.selectedNodes (); 
     *  for (nc.toFirst (); nc.ok (); nc.next ()) {
     *    Node node = nc.node ();
     *    String canonicalName = nodeAttributes.getCanonicalName (node);
     *    HashMap attribBundle = nodeAttributes.getAllAttributes (canonicalName);
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

}
