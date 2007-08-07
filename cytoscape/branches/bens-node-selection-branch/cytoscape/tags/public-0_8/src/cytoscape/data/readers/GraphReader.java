// GraphReader
//-----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-----------------------------------------------------------------------------
package cytoscape.data.readers;
//-----------------------------------------------------------------------------
import cytoscape.GraphObjAttributes;
import y.view.Graph2D;
//-----------------------------------------------------------------------------
public interface GraphReader {
    public void read();
    public Graph2D getGraph ();
    public GraphObjAttributes getEdgeAttributes ();
}


