// WindowFactory:  create and return a CytoscapeWindow, or a subclass
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape;
//------------------------------------------------------------------------------
import y.base.*;
import y.view.*;
import java.awt.event.WindowListener;
import cytoscape.data.*;
import cytoscape.data.servers.*;
import cytoscape.dialogs.*;
import cytoscape.layout.*;
//------------------------------------------------------------------------------
public class WindowFactory {
  
//------------------------------------------------------------------------------
static public CytoscapeWindow create (WindowListener windowListener,
                                      CytoscapeConfig config,
                                      Graph2D graph, 
                                      ExpressionData expressionData,
                                      BioDataServer bioDataServer,
                                      GraphObjAttributes nodeAttributes,
                                      GraphObjAttributes edgeAttributes,
                                      String geometryFilename,
                                      String expressionDataFilename,
                                     String title) throws Exception
{
  if (biomodulesWindowNeeded (nodeAttributes))
    return new BioModulesWindow (windowListener, config,
                                 graph, expressionData,
                                 bioDataServer,
                                 nodeAttributes,
                                 edgeAttributes,
                                 geometryFilename,
                                 expressionDataFilename,
                                 title);
  else 
    return new CytoscapeWindow (windowListener, config,
                                graph, expressionData,
                                bioDataServer,
                                nodeAttributes,
                                edgeAttributes,
                                geometryFilename,
                                expressionDataFilename,
                                title);
}
//------------------------------------------------------------------------------
static public CytoscapeWindow create (WindowListener windowListener, 
                                      Graph2D graph, 
                                      ExpressionData expressionData,
                                      BioDataServer bioDataServer,
                                      GraphObjAttributes nodeAttributes,
                                      GraphObjAttributes edgeAttributes,
                                      String geometryFilename,
                                      String expressionDataFilename,
                                      String title) throws Exception

{
  if (biomodulesWindowNeeded (nodeAttributes))
    return new BioModulesWindow (windowListener,
                                 graph, expressionData,
                                 bioDataServer,
                                 nodeAttributes,
                                 edgeAttributes,
                                 geometryFilename,
                                 expressionDataFilename,
                                 title);
  else 
    return new CytoscapeWindow (windowListener,
                                graph, expressionData,
                                bioDataServer,
                                nodeAttributes,
                                edgeAttributes,
                                geometryFilename,
                                expressionDataFilename,
                                title);

}
//------------------------------------------------------------------------------
private static boolean biomodulesWindowNeeded (GraphObjAttributes nodeAttributes)
{
  return nodeAttributes.hasAttribute ("wef");
}
//------------------------------------------------------------------------------
}
