// GMLReader.java
//-------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.readers;
//-----------------------------------------------------------------------------------------
import y.base.Node;
import y.base.Edge;
import y.view.Graph2D;
import y.io.YGFIOHandler;
import y.io.GMLIOHandler;
//-------------------------------------------------------------------------------------
public class GMLReader {
  private String filename;
//-------------------------------------------------------------------------------------
public GMLReader (String filename)
{
  this.filename = filename;
}
//-------------------------------------------------------------------------------------
public Graph2D read ()
{
  GMLIOHandler ioh  = new GMLIOHandler ();
  Graph2D graph = new Graph2D ();
  ioh.read (graph, filename);
  return graph;

} // read
//------------------------------------------------------------------------------
} // class GMLReader
