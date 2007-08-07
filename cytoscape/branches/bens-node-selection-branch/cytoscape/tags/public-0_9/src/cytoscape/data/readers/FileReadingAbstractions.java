// FileReadingAbstractions.java
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

public class FileReadingAbstractions {

    public static Graph2D loadGMLBasic (String filename,
					GraphObjAttributes edgeAttributes) {
	GMLReader reader = new GMLReader(filename);
	return loadBasic(reader,edgeAttributes);
    }

    public static Graph2D loadIntrBasic (String filename,
					 GraphObjAttributes edgeAttributes) {
	InteractionsReader reader = new InteractionsReader (filename);
	return loadBasic(reader,edgeAttributes);
    }
    public static Graph2D loadBasic(GraphReader reader,
				    GraphObjAttributes edgeAttributes) {
	reader.read ();
	GraphObjAttributes newEdgeAttributes = reader.getEdgeAttributes ();
	edgeAttributes.add (newEdgeAttributes);
	edgeAttributes.addNameMap (newEdgeAttributes.getNameMap ());
	return reader.getGraph();
    }

    public static void initAttribs(CytoscapeConfig config,
				   Graph2D graph,
				   GraphObjAttributes nodeAttributes,
				   GraphObjAttributes edgeAttributes) {
	String [] nAFilenames = config.getNodeAttributeFilenames ();
	if (nAFilenames != null)
	    for (int i=0; i < nAFilenames.length; i++) {
		try {
		    nodeAttributes.readAttributesFromFile (nAFilenames[i]);
		} catch (NumberFormatException nfe) {
		    System.err.println (nfe.getMessage ());
                    nfe.printStackTrace ();
		} catch (IllegalArgumentException iae) {
		    System.err.println (iae.getMessage ());
                    iae.printStackTrace ();
		} catch (FileNotFoundException fnfe) {
		    System.err.println (fnfe.getMessage ());
                    fnfe.printStackTrace ();
		}
	    }

	
	String [] eAFilenames = config.getEdgeAttributeFilenames ();
	if (eAFilenames != null)
	    for (int i=0; i < eAFilenames.length; i++) {
		try {
		    edgeAttributes.readAttributesFromFile (eAFilenames [i]);
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

}
