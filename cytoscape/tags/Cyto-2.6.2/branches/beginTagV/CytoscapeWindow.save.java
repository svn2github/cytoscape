// CytoscapeWindow.java:  a yfiles, GUI tool for exploring genetic networks
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape;
//------------------------------------------------------------------------------
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.JOptionPane;

import java.io.File;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Vector;
import java.util.Enumeration;

import y.base.*;
import y.view.*;

import y.layout.Layouter;
import y.layout.GraphLayout;

import y.layout.circular.CircularLayouter;
import y.layout.hierarchic.HierarchicLayouter;
import y.layout.organic.OrganicLayouter;
import y.layout.random.RandomLayouter;

import y.io.YGFIOHandler;
import y.io.GMLIOHandler;

import y.algo.GraphHider;

import cytoscape.data.*;
import cytoscape.data.readers.*;
import cytoscape.data.servers.*;
import cytoscape.dialogs.*;
import cytoscape.layout.*;
import cytoscape.vizmap.*;
//-----------------------------------------------------------------------------------
public class CytoscapeWindow extends JPanel 
             implements VizChooserClient, ActivePathViewer {

  protected static final int DEFAULT_WIDTH = 700;
  protected static final int DEFAULT_HEIGHT = 700;

  protected WindowListener windowListener;
  protected Graph2D graph;
  protected String geometryFilename;
  protected String expressionDataFilename;

  protected JFrame mainFrame;
  protected JMenuBar menuBar;
  protected JMenu opsMenu;
  protected JToolBar toolbar;
  protected JButton activePathToolbarButton;

  protected JMenu expressionConditionsMenu;

  protected Cursor defaultCursor = Cursor.getPredefinedCursor (Cursor.DEFAULT_CURSOR);
  protected Cursor busyCursor = Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR);

  protected Layouter layouter;

  protected Graph2DView graphView;
  protected ViewMode disabledEditingMode = new DisabledEditMode ();
  protected ViewMode nodeAttributesPopupMode = createNodeAttributesPopupMode ();
  protected boolean viewModesInstalled = false;

  protected BioDataServer bioDataServer;
  protected String bioDataServerName;
  protected GraphObjAttributes nodeAttributes = new GraphObjAttributes ();
  protected GraphObjAttributes edgeAttributes = new GraphObjAttributes ();

  protected ExpressionData expressionData = null;
  protected String currentCondition = "none";

  protected final String goModeMenuLabel = "Show GeneOntology Info";
  protected final String expressionModeMenuLabel = "Show mRNA Expression";
  protected NodeViz nodeViz;
  protected EdgeViz edgeViz;

  protected ActivePathsFinderBridge finder = null;
  protected ActivePath [] activePaths;
  protected String [] conditionNames;

  protected VizChooser theVizChooser = new VizChooser();

  protected GraphHider graphHider; // = new GraphHider (graph);
  protected Vector subwindows = new Vector ();

  protected String windowTitle;
   // selected nodes can be displayed in a new window.  this next variable
   // provides a title for that new window
  protected String titleForCurrentSelection = null;
  protected CytoscapeConfig config;
  protected static boolean activePathsFindingIsAvailable;
  protected static boolean activePathsNativeLibraryLoaded = false;
  protected HashMap nodeToNameMap = new HashMap ();
  protected HashMap nameToNodeMap = new HashMap ();
//------------------------------------------------------------------------------
public CytoscapeWindow (WindowListener windowListener,
                        CytoscapeConfig config,
                        Graph2D graph, 
                        ExpressionData expressionData,
                        BioDataServer bioDataServer,
                        GraphObjAttributes nodeAttributes,
                        GraphObjAttributes edgeAttributes,
                        String geometryFilename,
                        String expressionDataFilename,
                        String title)
   throws Exception
{
  this.windowListener = windowListener;
  this.graph = graph;
  this.geometryFilename = geometryFilename;
  this.expressionDataFilename = expressionDataFilename;
  this.bioDataServer = bioDataServer;
  this.expressionData = expressionData;

  if (nodeAttributes != null)
    this.nodeAttributes = nodeAttributes;
  if (edgeAttributes != null)
    this.edgeAttributes = edgeAttributes;

  nodeViz = new NodeViz (config.getProperties ());
  edgeViz = new EdgeViz (config.getProperties ());

  if (title == null)
    this.windowTitle = "";
  else
    this.windowTitle = title;
  this.config = config;

  activePathsFindingIsAvailable = loadActivePathsNativeLibrary ();

  initializeWidgets ();
  displayGraph ();

  if (expressionData != null) 
    incorporateExpressionData ();

  mainFrame.setVisible (true);

  PluginLoader pluginLoader = new PluginLoader (this, config, nodeAttributes, edgeAttributes);
  pluginLoader.load ();

  if (config != null && config.activePathParametersPresent ()) {
    System.out.println ("----- getting active path parameters");
    runActivePathsFinder (config.getActivePathParameters ());
    }

} // ctor
//------------------------------------------------------------------------------
protected void updateGraph ()
{
  graphView.paintImmediately (0, 0, graphView.getWidth(), graphView.getHeight());
  
}
//------------------------------------------------------------------------------
public JFrame getMainFrame ()
{
  return mainFrame;
}
//------------------------------------------------------------------------------
public JMenuBar getMenuBar ()
{
  return menuBar;
}
//------------------------------------------------------------------------------
public void setMenuBar (JMenuBar newMenuBar)
{
  mainFrame.setJMenuBar (newMenuBar);
}
//------------------------------------------------------------------------------
protected boolean loadActivePathsNativeLibrary ()
// todo: move this to ActivePathsWindow, a needed subclass of CytoscapeWindow
{
  boolean result = true;

  if (!activePathsNativeLibraryLoaded) {
    String propertyName = "java.library.path";
    String libraryPath = System.getProperty (propertyName);
    String libraryBaseName = "ActivePaths";
    String libraryFullName = "lib" + libraryBaseName + ".so";
    File libraryFile = new File (libraryPath, libraryFullName);
    if (libraryFile.canRead ()) {
      System.out.print ("loading " + libraryFile.toString () + "...");
      System.out.flush ();
      System.loadLibrary (libraryBaseName);
      System.out.println (" done");
      activePathsNativeLibraryLoaded = true;
      result =  true;
      }
    else {
      System.out.println ("could not find " + libraryFile.toString ());
      result = false;
      }
   } // if !activePathsNativeLibraryLoaded

  return result;

} // loadActivePathsNativeLibrary
//------------------------------------------------------------------------------
protected void initializeWidgets ()
{
  setLayout (new BorderLayout ());  
  graphView = new Graph2DView ();
  add (graphView, BorderLayout.CENTER);
  graphView.setPreferredSize (new Dimension (DEFAULT_WIDTH, DEFAULT_HEIGHT));

  toolbar = createToolBar ();
  add (toolbar, BorderLayout.NORTH);

  mainFrame = new JFrame (windowTitle);
  mainFrame.addWindowListener (windowListener);
 //mainFrame.addWindowListener (new WindowAdapter () {
 //   public void windowClosing (WindowEvent e) {System.out.println ("bye!");System.exit (0);}}
 //   );

    
  mainFrame.setJMenuBar (createMenuBar ());
  mainFrame.setContentPane (this);
  mainFrame.pack ();
  setInteractivity (true);

} // initializeWidgets
//------------------------------------------------------------------------------
protected String getCanonicalNodeName (String nodeName)
{
 String canonicalName = nodeName;

 if (bioDataServer != null) try {
   canonicalName = bioDataServer.getCanonicalName (nodeName);
   }
  catch (Exception ignore) {;}

  return canonicalName;

} // getCanonicalNodeName
//------------------------------------------------------------------------------
protected void displayGraph ()
{
  if (graph == null) graph = new Graph2D ();

  OrganicLayouter ol = new OrganicLayouter ();
  ol.setActivateDeterministicMode (true);
  ol.setPreferredEdgeLength(80);
  layouter = ol;
  graphView.setGraph2D (graph);
  graphHider = new GraphHider (graph);
  applyLayout (false);

  setNodeNames ();
  String initialExpressionCondition = "none";
  applyAllVizMappings (nodeViz);

  graphView.fitContent ();
  graphView.setZoom (graphView.getZoom ()*0.9);
  updateGraph ();

} // displayGraph
//------------------------------------------------------------------------------
public GraphObjAttributes getNodeAttributes ()
{
  return nodeAttributes;
}
//------------------------------------------------------------------------------
private void setNodeNames ()
{
  if (bioDataServer == null) return;

  Node [] nodes = graphView.getGraph2D().getNodeArray();

  for (int i=0; i < nodes.length; i++) {
    Node node = nodes [i];
    NodeRealizer r = graphView.getGraph2D().getRealizer(node);
    String defaultName = r.getLabelText ();
    nodeToNameMap.put (node, defaultName);
    nameToNodeMap.put (defaultName, node);
    String newName = defaultName;
    try {
      String [] synonyms = bioDataServer.getSynonyms (defaultName);
      if (synonyms.length > 0)
        newName = synonyms [0];
       r.setLabelText (newName);
       }
     catch (Exception ignoreForNow) {;}
     } // for i

} // setNodeNames
//------------------------------------------------------------------------------
protected void incorporateExpressionData ()
{
  if (expressionData != null) {
    nodeViz.setExtremeValues (expressionData.getExtremeValues());       
    nodeViz.setVizChooserExpressionValues (expressionData.getExtremeValues());
    currentCondition = "none";

    expressionConditionsMenu = new JMenu ("Expression");
    expressionConditionsMenu.setToolTipText ("Draw nodes using mRNA expression data");
    ButtonGroup group = new ButtonGroup ();
    ExpressionMenuListener expressionMenuListener =
      createExpressionMenuListener ();
    conditionNames = expressionData.getConditionNames ();
    JMenuItem noneRadioItem = new JRadioButtonMenuItem ("None");
    noneRadioItem.setSelected (true);
    group.add (noneRadioItem);
    noneRadioItem.addActionListener (expressionMenuListener);
    expressionConditionsMenu.add (noneRadioItem);
    expressionConditionsMenu.addSeparator ();
    for (int i=0; i < conditionNames.length; i++) {
      JMenuItem radioItem = new JRadioButtonMenuItem (conditionNames [i]);
      radioItem.setSelected (false);
      group.add (radioItem);
      radioItem.addActionListener (expressionMenuListener);
      expressionConditionsMenu.add (radioItem);
      } // for i

    if (activePathsFindingIsAvailable) {
      opsMenu.add (createFindActivePathsAction ());
        // before adding a new 'Expression' and 'Ops' menubar menu, delete
        // any by the same name which might be there already.
      MenuElement [] menus = menuBar.getSubElements ();
      for (int i=0; i < menus.length; i++) {
        JMenu jmenu = (JMenu) menus [i];
        boolean deleteThis =  jmenu.getText().equalsIgnoreCase ("expression") ||
                              jmenu.getText().equalsIgnoreCase ("ops");
        if (deleteThis)
          menuBar.remove (jmenu.getComponent ());
        } // for i
      } // if activePathsFindingAvailable

    menuBar.add (expressionConditionsMenu);
    mainFrame.setJMenuBar (menuBar);
    } // if expressionData

} // incorporateExpressionData
//------------------------------------------------------------------------------
public JMenu getOperationsMenu ()
{
  return opsMenu;
}
//------------------------------------------------------------------------------
protected void setInteractivity (boolean newState)
{
  if (newState == true) { // turn interactivity ON
    if (!viewModesInstalled) {
      graphView.addViewMode (disabledEditingMode);
      graphView.addViewMode (nodeAttributesPopupMode);
      viewModesInstalled = true;
      }
    graphView.setViewCursor (defaultCursor);
    setCursor (defaultCursor); 
    }
  else {  // turn interactivity OFF
    if (viewModesInstalled) {
      graphView.removeViewMode (disabledEditingMode);
      graphView.removeViewMode (nodeAttributesPopupMode); 
      viewModesInstalled = false;
      }
    graphView.setViewCursor (busyCursor);
    setCursor (busyCursor);
    }

} // setInteractivity
//------------------------------------------------------------------------------
/**
 *  render nodes (border & fill) and edges with using the current
 *  attribute/appearance mappings
 */
public void applyAllVizMappings (NodeViz nodeViz)
{
  System.out.println ("---------- applyAllVizMappings");
  DefaultBackgroundRenderer renderer = new DefaultBackgroundRenderer (graphView);
  renderer.setColor (nodeViz.getDefaultBackgroundColor());
  graphView.setBackgroundRenderer (renderer);

  Node[] nodes = graphView.getGraph2D().getNodeArray();
  for (int i=0;i<nodes.length;i++) {
    Node node = nodes[i];
    attributesBasedNodeRendering (node);
    }

  EdgeCursor cursor = graphView.getGraph2D().edges();
  cursor.toFirst ();
  for (int i=0; i < cursor.size(); i++){
    Edge edge = cursor.edge();
    Node source = edge.source ();
    Node target = edge.target ();
    String sourceNodeName = 
          getCanonicalNodeName (graphView.getGraph2D().getLabelText (source));
    String targetNodeName =  
          getCanonicalNodeName (graphView.getGraph2D().getLabelText (target));
    String edgeName = sourceNodeName + "::" + targetNodeName;
    String interactionType = (String) edgeAttributes.getValue ("interaction", edgeName);
    Color edgeColor = edgeViz.getEdgeColor (interactionType);
    EdgeRealizer er = graphView.getGraph2D().getRealizer(edge);
    er.setLineColor (edgeColor);
    String targetDecoration = edgeViz.getTargetDecoration (interactionType);
    if (targetDecoration.equals ("arrow")) {
      er.setArrow (Arrow.STANDARD);
      }
    String sourceDecoration = edgeViz.getSourceDecoration (interactionType);
    if (sourceDecoration.equals ("arrow")) {
      er.setSourceArrow (Arrow.STANDARD);
      }
    cursor.cyclicNext();
    }

  updateGraph ();
  
} // applyAllVizMappings
//------------------------------------------------------------------------------
/**
 * use expression data/appearacne mapping to render nodes and node borders
 */
public void applyExpressionVizMappings (NodeViz nodeViz)
{
  displayNodesWithExpressionValues (currentCondition);
  displayBordersWithExpressionValues (currentCondition);
}
//------------------------------------------------------------------------------
public void displayPath (ActivePath activePath, boolean clearOthersFirst,
                         String pathTitle)
{
  titleForCurrentSelection = pathTitle;
  selectNodesByName (activePath.getNodes (), clearOthersFirst);
}
//------------------------------------------------------------------------------
public void displayPath (ActivePath activePath, String pathTitle)
{
  displayPath (activePath, true, pathTitle);
}
//------------------------------------------------------------------------------
protected HashMap combinePaths (ActivePath [] activePaths)
{
  HashMap result = new HashMap ();

  for (int i=0; i < activePaths.length; i++) {
    String [] nodes = activePaths [i].getNodes ();
    int n;
    for (n=0; n < nodes.length; n++)
      result.put (nodes [n], null);
    } // for i

  return result;

} // oldCombinePaths
//------------------------------------------------------------------------------
protected String [] oldCombinePaths (ActivePath [] activePaths)
{
  int totalNodes = 0;
  for (int i=0; i < activePaths.length; i++)
    totalNodes += activePaths [i].numberOfNodes ();

  String [] result = new String [totalNodes];

  int counter = 0;
  for (int i=0; i < activePaths.length; i++) {
    String [] nodes = activePaths [i].getNodes ();
    int n;
    for (n=0; n < nodes.length; n++)
      result [counter + n] = nodes [n];
    counter += n;
    } // for i

  return result;

} // oldCombinePaths
//------------------------------------------------------------------------------
public void activePathFinderProgressCallback (int newValue)
// this version of the progress callback hides all nodes -except- those in the paths
{
  if (finder != null) {
    graphHider.unhideAll ();
    activePaths = finder.getPaths ();
    //String [] namesOfAllNodesInAllPaths = combinePaths (activePaths);
    HashMap uniqueNodeNames = combinePaths (activePaths);
    //System.out.println ("number of active paths: " + activePaths.length);
    //System.out.println ("number of nodes to show, combined: " + uniqueNodeNames.size ());
    //                    namesOfAllNodesInAllPaths.length);
    //showNodesByName (namesOfAllNodesInAllPaths);
    showNodesByName (uniqueNodeNames);
    finder.clearPaths ();
    } // if finder

} // activePathFinderProgressCallback
//------------------------------------------------------------------------------
public void selectActivePathFinderProgressCallback (int newValue)
// this version of the progress callback simply selects nodes in
// all of the paths, leaving the rest of the graph undisturbed
{
  if (finder != null) {
    String description = "searching for paths...";  
    activePaths = finder.getPaths ();
    for (int i=0; i < activePaths.length; i++) {
      if (i==0) 
        displayPath (activePaths [i], true, description);
      else
        displayPath (activePaths [i], false, description);
      } // for i
    finder.clearPaths ();
    } // if finder

} // selectActivePathFinderProgressCallback
//------------------------------------------------------------------------------
protected JMenuBar createMenuBar ()
{
  menuBar = new JMenuBar ();
  JMenu fileMenu = new JMenu ("File");

  JMenu loadSubMenu = new JMenu ("Load");
  fileMenu.add (loadSubMenu);
  JMenuItem mi = loadSubMenu.add (createLoadGMLFileAction ());
  mi.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_G, ActionEvent.CTRL_MASK));
  mi = loadSubMenu.add (createLoadInteractionFileAction ());
  mi.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_I, ActionEvent.CTRL_MASK));
  mi = loadSubMenu.add (createLoadExpressionMatrixAction ());
  mi.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_E, ActionEvent.CTRL_MASK));

  fileMenu.add (createSaveAsGMLAction ());
  mi = fileMenu.add (createCloseWindowAction ());
  mi.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_W, ActionEvent.CTRL_MASK));
  mi = fileMenu.add (createExitAction ());
  mi.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
  menuBar.add (fileMenu);

  JMenu layoutMenu = new JMenu ("Layout");
  layoutMenu.setToolTipText ("Apply new layout algorithm to graph");
  menuBar.add (layoutMenu);
  layoutMenu.add (createCircularLayoutAction ());
  layoutMenu.add (createHierarchicalLayoutAction ());
  layoutMenu.add (createOrganicLayoutAction ());
  layoutMenu.add (createRandomLayoutAction ());
  // layoutMenu.add (new GroupWiseLayoutAction ());
  layoutMenu.add (createLayoutAction ());

  JMenu selectiveDisplayMenu = new JMenu ("Select");
  selectiveDisplayMenu.setToolTipText ("Select nodes by different criteria");
  menuBar.add (selectiveDisplayMenu);
  selectiveDisplayMenu.add (createDeselectAllAction ());

  opsMenu = new JMenu ("Ops"); // always create the ops menu
  menuBar.add (opsMenu);

  if (bioDataServer != null) selectiveDisplayMenu.add (createGoIDSelectAction ());
  selectiveDisplayMenu.add (createAlphabeticalSelectionAction ());
  mi = selectiveDisplayMenu.add (createDisplaySelectedInNewWindowAction ());
  mi.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_S, ActionEvent.CTRL_MASK));
  
  return menuBar;

} // createMenuBar
//------------------------------------------------------------------------------
protected ExpressionMenuListener createExpressionMenuListener () {
  return new ExpressionMenuListener();
}
protected class ExpressionMenuListener implements ActionListener {

  public void actionPerformed (ActionEvent e) {
    String conditionName = e.getActionCommand ();
    if (conditionName.equalsIgnoreCase ("none"))
      currentCondition = null;
    else
      currentCondition = conditionName;
    displayNodesWithExpressionValues (conditionName);
    nodeViz.setNodeAttributeWhichControlsFillColor(conditionName);
    displayBordersWithExpressionValues(conditionName);
    nodeViz.setBorderAttributeWhichControlsFillColor(conditionName);
    if (theVizChooser.isShowing())
	theVizChooser.updateSummaryPanel("expression");
    }

} // inner class ExpressionMenuListener
//------------------------------------------------------------------------------
protected JToolBar createToolBar()
{
  JToolBar bar = new JToolBar ();
  bar.add (createZoomAction (0.9));
  bar.add (createZoomAction (1.1));
  bar.add (createZoomSelectedAction ());
  bar.add (createFitContentAction ());
  bar.add (createShowAllAction ());
  bar.addSeparator ();
  bar.add (createAppearanceControllerLauncherAction (nodeAttributes, edgeAttributes));
    
  return bar;

} // createToolBar
//------------------------------------------------------------------------------
protected void addActivePathToolbarButton ()
{
  if (activePathToolbarButton != null)
    toolbar.remove (activePathToolbarButton);

  activePathToolbarButton = toolbar.add (createActivePathControllerLauncherAction ());
}
//------------------------------------------------------------------------------
protected void displayNodesWithExpressionValues (String conditionName)
{
  Node [] nodes = graphView.getGraph2D().getNodeArray();

  for (int i=0; i < nodes.length; i++) {
    Node node = nodes [i];
    String geneName = getCanonicalNodeName (graphView.getGraph2D().getLabelText (node));
    if (conditionName.equalsIgnoreCase ("none")) {
      attributesBasedNodeRendering (node);
      }
    else {   
      mRNAMeasurement measurement = 
        expressionData.getMeasurement (geneName, conditionName);
      if (measurement == null)
        defaultNodeRendering (node);
      else {
	double[][] bins = nodeViz.getExpressionColorBins();
        expressionBasedNodeRendering (node, measurement, bins);
      }
    } // else: condition is not 'None'
  } // for i

  updateGraph ();

} // displayNodesWithExpressionValues
//------------------------------------------------------------------------------
protected void displayBordersWithExpressionValues (String conditionName)
{
  Node [] nodes = graphView.getGraph2D().getNodeArray();

  for (int i=0; i < nodes.length; i++) {
      Node node = nodes [i];
      String geneName = graphView.getGraph2D().getLabelText (node);
      if (conditionName.equalsIgnoreCase ("none")) {
	  attributesBasedBorderRendering (node);
      }
      else {   
	  mRNAMeasurement measurement = 
	      expressionData.getMeasurement (geneName, conditionName);
	  if (measurement == null)
	      defaultBorderRendering (node);
	  else{
	      double[][] bins = nodeViz.getSignificanceColorBins();
	      expressionBasedBorderRendering (node, measurement, bins);
	  }
      } // else: condition is not 'None'
    } // for i
  updateGraph ();

} // displayBordersWithExpressionValues
//------------------------------------------------------------------------------
protected void defaultNodeRendering (Node node)
{
  NodeRealizer r = graphView.getGraph2D().getRealizer(node);
  r.setFillColor (nodeViz.getDefaultNodeColor());

} // defaultNodeRendering
//------------------------------------------------------------------------------
protected void defaultBorderRendering (Node node)
{ 
  NodeRealizer r = graphView.getGraph2D().getRealizer(node);
  r.setLineColor (nodeViz.getDefaultBorderColor());

} // defaultBorderRendering
//------------------------------------------------------------------------------
/**
 * Use current attribute/appearance mapping to draw the node.
 * <p>
 * <b> todo: </b> <i> (pshannon, 2002/02/26)</i> expand this to handle all
 * aspects of the node's appearance: shape, size, border, as well as the
 * fill color present now.
 */
protected void attributesBasedNodeRendering (Node node)
{
  String nodeName = graphView.getGraph2D().getLabelText (node);
  Color fillColor = nodeViz.getNodeColor (nodeAttributes, nodeName);
  NodeRealizer r = graphView.getGraph2D().getRealizer(node);
  r.setFillColor (fillColor);
  r.setLineColor (nodeViz.getBorderColor (nodeAttributes, nodeName));
}
//------------------------------------------------------------------------------
protected void attributesBasedBorderRendering (Node node)
{
  String nodeName = graphView.getGraph2D().getLabelText (node);
  Color fillColor = nodeViz.getBorderColor (nodeAttributes, nodeName);
  NodeRealizer r = graphView.getGraph2D().getRealizer(node);
  r.setLineColor (fillColor);

}
//------------------------------------------------------------------------------
protected void expressionBasedNodeRendering (Node node, mRNAMeasurement measurement, 
                                             double [][] bins)
{
    double currentRatio = measurement.getRatio();
    double absoluteMaxRatio = nodeViz.getExtremeMaxRatioValue();
    double absoluteMinRatio = nodeViz.getExtremeMinRatioValue();
    double ratioHighClamp = nodeViz.getExpressionHighClamp();
    double ratioLowClamp  = nodeViz.getExpressionLowClamp();
    Color  ratioMaxColor  = nodeViz.getExpressionRatioMaxColor();
    Color  ratioMinColor  = nodeViz.getExpressionRatioMinColor();

    NodeRealizer r = graphView.getGraph2D().getRealizer(node);
    Color nodeColor;
    int binCount = bins.length;
    int selectedBin = 0;
    if (currentRatio>=absoluteMaxRatio||currentRatio>=ratioHighClamp)
	nodeColor = ratioMaxColor;
    if (currentRatio<=absoluteMinRatio||currentRatio<=ratioLowClamp)
	nodeColor = ratioMinColor;
    else{
	for (int i=binCount-1;i>=0;i--)
	    if (currentRatio>=bins[i][0]){
		selectedBin = i;
		break;
	    }
	    Color tempColor = new Color((int)bins[selectedBin][1],
					(int)bins[selectedBin][2],
					(int)bins[selectedBin][3]);
	    nodeColor = tempColor;
    }

    r.setFillColor (nodeColor);

} // expressionBasedNodeRendering
//------------------------------------------------------------------------------
protected void expressionBasedBorderRendering (Node node, mRNAMeasurement measurement, 
                                              double [][] bins)
{
    double currentRatio = measurement.getSignificance();
    double absoluteMaxRatio = nodeViz.getExtremeMaxSigValue();
    double absoluteMinRatio = nodeViz.getExtremeMinSigValue();
    double ratioHighClamp = nodeViz.getExpressionSigMaxValue();
    double ratioLowClamp  = nodeViz.getExpressionSigMinValue();
    Color  ratioMaxColor  = nodeViz.getExpressionSigMaxColor();
    Color  ratioMinColor  = nodeViz.getExpressionSigMinColor();

    NodeRealizer r = graphView.getGraph2D().getRealizer(node);
    Color nodeColor;
    int binCount = bins.length;
    int selectedBin = 0;
    if (currentRatio>=absoluteMaxRatio||currentRatio>=ratioHighClamp)
	nodeColor = ratioMaxColor;
    if (currentRatio<=absoluteMinRatio||currentRatio<=ratioLowClamp)
	nodeColor = ratioMinColor;
    else{
	for (int i=binCount-1;i>=0;i--)
	    if (currentRatio>=bins[i][0]){
		selectedBin = i;
		break;
	    }
	    Color tempColor = new Color((int)bins[selectedBin][1],
					(int)bins[selectedBin][2],
					(int)bins[selectedBin][3]);
	    nodeColor = tempColor;
    }

    r.setLineColor (nodeColor);
    r.setLineType(nodeViz.getBorderThickness());

} // expressionBasedBorderRendering
//------------------------------------------------------------------------------
public void selectNodesByName (String [] nodeNames)
{
  boolean clearAllSelectionsFirst = true;
  selectNodesByName (nodeNames, clearAllSelectionsFirst);
}
//------------------------------------------------------------------------------
public void showNodesByName (HashMap uniqueNodeNames)
{
  Graph2D g = graphView.getGraph2D ();
  Node [] nodes = graphView.getGraph2D().getNodeArray();
  String [] nodesToShow = (String []) uniqueNodeNames.keySet().toArray (new String [0]);
  
  graphHider.unhideAll ();

  for (int i=0; i < nodes.length; i++) {
    boolean matched = false;
    String nodeName = getCanonicalNodeName (graphView.getGraph2D().getLabelText (nodes [i]));
    if (!uniqueNodeNames.containsKey (nodeName)) {
      graphHider.hide (nodes [i]);
      }
    } // for i

  updateGraph ();

} // showNodesByName
//------------------------------------------------------------------------------
protected void showNodesByName (String [] nodeNames)
{
  Graph2D g = graphView.getGraph2D ();
  Node [] nodes = graphView.getGraph2D().getNodeArray();
  // System.out.println ("number of nodes in graph: " + nodes.length);
  
  graphHider.unhideAll ();
  for (int i=0; i < nodes.length; i++) {
    boolean matched = false;
    String graphNodeName = graphView.getGraph2D().getLabelText (nodes [i]);
    for (int n=0; n < nodeNames.length; n++) {
      if (nodeNames [n].equalsIgnoreCase (graphNodeName)) {
        matched = true;
        break;
        } // if equals
       } // for n
     if (!matched) 
       graphHider.hide (nodes [i]);
    } // for i

  updateGraph ();

} // showNodesByName
//------------------------------------------------------------------------------
protected void selectNodesByName (String [] nodeNames, boolean clearAllSelectionsFirst)
{
  Graph2D g = graphView.getGraph2D();
  Node [] nodes = graphView.getGraph2D().getNodeArray();

  for (int i=0; i < nodes.length; i++) {
    String graphNodeName = getCanonicalNodeName (graphView.getGraph2D().getLabelText (nodes [i]));
    NodeRealizer nodeRealizer = graphView.getGraph2D().getRealizer(nodes [i]);
    boolean matched = false;
    for (int n=0; n < nodeNames.length; n++)
      if (nodeNames [n].equalsIgnoreCase (graphNodeName)) {
        // System.out.println (graphNodeName);
        matched = true;
        break;
        }
    if (clearAllSelectionsFirst && !matched)
      nodeRealizer.setSelected (false);
    else if (matched)
      nodeRealizer.setSelected (true);
    } // for i

  updateGraph ();

} // selectNodesByName
//------------------------------------------------------------------------------
protected void deselectAllNodes ()
{
  Graph2D g = graphView.getGraph2D();
  Node [] nodes = graphView.getGraph2D().getNodeArray();

  for (int i=0; i < nodes.length; i++) {
    NodeRealizer nodeRealizer = graphView.getGraph2D().getRealizer(nodes [i]);
    nodeRealizer.setSelected (false);
    } // for i

  updateGraph ();

} // deselectAllNodes
//------------------------------------------------------------------------------
protected void selectNodesStartingWith (String key)
{
  setInteractivity (false);
  key = key.toLowerCase ();
  Graph2D g = graphView.getGraph2D();
  updateGraph ();

  Node [] nodes = graphView.getGraph2D().getNodeArray();

  for (int i=0; i < nodes.length; i++) {
    String nodeName = graphView.getGraph2D().getLabelText (nodes [i]);
    boolean matched = false;
    if (nodeName.toLowerCase().startsWith (key))
      matched = true;
    else if (bioDataServer != null) {
      try {
        String [] synonyms = bioDataServer.getSynonyms (nodeName);
        for (int s=0; s < synonyms.length; s++)
          if (synonyms [s].toLowerCase().startsWith (key)) {
            matched = true;
            break;
         } // for s
       }
      catch (Exception ignoreForNow) {;}
      } // else if: checking synonyms
    setNodeSelected (nodes [i], matched);
    } // for i

  setInteractivity (true);
  updateGraph ();

} // selectDisplyToNodesStartingWith ...
//------------------------------------------------------------------------------
protected void setNodeSelected (Node node, boolean visible)
{
  NodeRealizer r = graphView.getGraph2D().getRealizer(node);
  r.setSelected (visible);

} // setNodeSelected
//------------------------------------------------------------------------------
protected void updateEdgeVisibilityFromNodeVisibility ()
{
  for (EdgeCursor ec = graphView.getGraph2D().edges(); ec.ok(); ec.next()) {
    Edge e = ec.edge ();
    Node source = e.source ();
    boolean edgeShouldBeVisible = false;
    if (graphView.getGraph2D().getRealizer(source).isVisible ()) {
      Node target = e.target ();
      if (graphView.getGraph2D().getRealizer(target).isVisible ()) {
        edgeShouldBeVisible = true;
         //System.out.println ("===================== 2 visible nodes: " + e);
        } // if target node is visible
      } // if source node is visible
    EdgeRealizer er = graphView.getGraph2D().getRealizer(e);
    er.setVisible (edgeShouldBeVisible);
    // System.out.println ("making visible? " + e + " " + edgeShouldBeVisible);
    } // for each edge

} // updateEdgeVisibilityFromNodeVisibility
//------------------------------------------------------------------------------
protected void selectNodesSharingGoID (int goID)
{
  Graph2D g = graphView.getGraph2D();
  Node [] nodes = graphView.getGraph2D().getNodeArray();

  try {
    for (int n=0; n < nodes.length; n++) {
    String nodeName = graphView.getGraph2D().getLabelText (nodes [n]);
    int [] bioProcessIDs;
    int [] molFuncIDs;
    int [] cellularComponentIDs;
      bioProcessIDs = bioDataServer.getBioProcessIDs (nodeName);
      molFuncIDs = bioDataServer.getMolecularFunctionIDs (nodeName);
      cellularComponentIDs = bioDataServer.getCellularComponentIDs (nodeName);
      int [] allIDs = new int [bioProcessIDs.length +
                               molFuncIDs.length +
                               cellularComponentIDs.length];
      int d=0;  // destination (allIDs) index 
      for (int i=0; i < bioProcessIDs.length; i++)
        allIDs [d++] = bioProcessIDs [i];
      for (int i=0; i < molFuncIDs.length; i++)
        allIDs [d++] = molFuncIDs [i];
      for (int i=0; i < cellularComponentIDs.length; i++)
        allIDs [d++] = cellularComponentIDs [i];
      Vector allPaths = new Vector ();
      for (int i=0; i < allIDs.length; i++) {
        Vector tmp = bioDataServer.getAllBioProcessPaths (allIDs [i]);
        for (int t=0; t < tmp.size (); t++)
          allPaths.addElement (tmp.elementAt (t));
      } // for i

      boolean matched = false;
      for (int v=0; v < allPaths.size (); v++) {
        Vector path = (Vector) allPaths.elementAt (v);
        for (int p=path.size()-1; p >= 0; p--) {
          Integer ID = (Integer) path.elementAt (p);
          int id = ID.intValue ();
          if (id == goID) {
            matched = true;
            // todo: break out of inner and outer loops from here
            }
          } // for p
        } // for v
      NodeRealizer nodeRealizer = graphView.getGraph2D().getRealizer(nodes [n]);
      nodeRealizer.setSelected (matched);
      } // for n
    } 
  catch (Exception ignoreForNow) {;}

  updateGraph ();

} // selectNodesSharingGoId
//------------------------------------------------------------------------------
protected void applyLayout (boolean animated)
{
  System.out.print ("starting layout...");  System.out.flush ();
  setInteractivity (false);
  layouter.doLayout (graphView.getGraph2D ());
  updateGraph ();

  setInteractivity (true);
  System.out.println (" done");

} // applyLayout
//------------------------------------------------------------------------------
protected LayoutAction createLayoutAction () { return new LayoutAction(); }
protected class LayoutAction extends AbstractAction   {
  LayoutAction () { super ("Refresh"); }
    
  public void actionPerformed (ActionEvent e) {
    applyLayout (false);
    }
}
//------------------------------------------------------------------------------
protected CircularLayoutAction createCircularLayoutAction () {
  return new CircularLayoutAction();
}
protected class CircularLayoutAction extends AbstractAction   {
  CircularLayoutAction () { super ("Circular"); }
    
  public void actionPerformed (ActionEvent e) {
    layouter = new CircularLayouter ();
    applyLayout (false);
    }
}
//------------------------------------------------------------------------------
protected HierarchicalLayoutAction createHierarchicalLayoutAction () {
  return new HierarchicalLayoutAction();
}
protected class HierarchicalLayoutAction extends AbstractAction   {
  HierarchicalLayoutAction () { super ("Hierarchical"); }
    
  public void actionPerformed (ActionEvent e) {
    HierarchicLayouter hl = new HierarchicLayouter ();
    hl.setMinimalLayerDistance (40);
    hl.setMinimalNodeDistance (20);
    layouter = hl;
    applyLayout (false);
    }
}
//------------------------------------------------------------------------------
protected OrganicLayoutAction createOrganicLayoutAction () {
  return new OrganicLayoutAction();
}
protected class OrganicLayoutAction extends AbstractAction   {
  OrganicLayoutAction () { super ("Organic"); }
    
  public void actionPerformed (ActionEvent e) {
    OrganicLayouter ol = new OrganicLayouter ();
    ol.setActivateDeterministicMode (true);
    ol.setPreferredEdgeLength(80);
    layouter = ol;
    applyLayout (false);
    }
}
//------------------------------------------------------------------------------
//protected RadialLayoutAction createRadialLayoutAction () {
//  return new RadialLayoutAction();
//}
//protected class RadialLayoutAction extends AbstractAction   {
//  RadialLayoutAction () { super ("Radial"); }
//    
//  public void actionPerformed (ActionEvent e) {
//    layouter = new RadialLayouter();
//    applyLayout (false);
//    }
//}
//------------------------------------------------------------------------------
protected RandomLayoutAction createRandomLayoutAction () {
  return new RandomLayoutAction();
}
protected class RandomLayoutAction extends AbstractAction   {
  RandomLayoutAction () { super ("Random"); }
    
  public void actionPerformed (ActionEvent e) {
    layouter = new RandomLayouter ();
    applyLayout (false);
    }
}


//-----------------------------------------------------------------------------
// class GroupWiseLayoutAction
//   groupwise layout wrapper
//   dramage : 2002.1.8
/***************************************
protected GroupWiseLayoutAction createGroupWiseLayoutAction () {
  return new GroupWiseLayoutAction();
}
protected class GroupWiseLayoutAction extends AbstractAction {
    GroupWiseLayoutAction () { super ("Groupwise"); }

    public void actionPerformed (ActionEvent e) {
        System.out.println("GroupWiseLayoutAction Beginning");

        // do layout
        layouter = new GroupWiseLayouter();
        applyLayout (false);

        graphView.fitContent();
        updateGraph ();

        System.out.println("GroupWiseLayoutAction Completed");
    }
}


*************************************/

//------------------------------------------------------------------------------
protected GoIDSelectAction createGoIDSelectAction () {
  return new GoIDSelectAction();
}
protected class GoIDSelectAction extends AbstractAction   {
  GoIDSelectAction () { super ("By GO ID"); }

  public void actionPerformed (ActionEvent e) {
    String answer = 
      (String) JOptionPane.showInputDialog (mainFrame, "Select genes with GO ID");
    if (answer != null && answer.length () > 0) try {
      int goID = Integer.parseInt (answer);
      selectNodesSharingGoID (goID);
      }
    catch (NumberFormatException nfe) {
      JOptionPane.showMessageDialog (mainFrame, "Not an integer: " + answer);
      }
    } // actionPerformed

}// GoIDSelectAction
//------------------------------------------------------------------------------
protected AlphabeticalSelectionAction createAlphabeticalSelectionAction () {
  return new AlphabeticalSelectionAction();
}
protected class AlphabeticalSelectionAction extends AbstractAction   {
  AlphabeticalSelectionAction () { super ("By Name"); }

  public void actionPerformed (ActionEvent e) {
    String answer = 
      (String) JOptionPane.showInputDialog (mainFrame, 
              "Select nodes whose name (or synonym) starts with");
    if (answer != null && answer.length () > 0)
      selectNodesStartingWith (answer.trim ());
    }
}
//------------------------------------------------------------------------------
protected DeselectAllAction createDeselectAllAction () {
  return new DeselectAllAction();
}
protected class DeselectAllAction extends AbstractAction   {
  DeselectAllAction () { super ("Deselect All"); }

  public void actionPerformed (ActionEvent e) {
    deselectAllNodes ();
    }
}
//------------------------------------------------------------------------------
protected DisplaySelectedInNewWindowAction
  createDisplaySelectedInNewWindowAction () {
  return new DisplaySelectedInNewWindowAction();
}
protected class DisplaySelectedInNewWindowAction extends AbstractAction   {
  DisplaySelectedInNewWindowAction () { super ("Display Selected Nodes in New Window"); }

  public void actionPerformed (ActionEvent e) {
    Graph2D g = graphView.getGraph2D ();
    NodeCursor nc = g.selectedNodes (); 
    EdgeCursor ec = g.selectedEdges (); 
    Graph2D newGraph = new Graph2D (g, nc);
    String title = "selection";
    if (titleForCurrentSelection != null) 
      title = titleForCurrentSelection;
    try {
      CytoscapeWindow newWindow =
          WindowFactory.create (windowListener, config, newGraph, expressionData, 
                                bioDataServer, nodeAttributes, edgeAttributes, 
                                "dataSourceName", expressionDataFilename, title);
      subwindows.add (newWindow);  
      }
    catch (Exception e00) {
      System.out.println ("exception when creating new window");
      e00.printStackTrace ();
      }

    } // actionPerformed

} // inner class DisplaySelectedInNewWindowAction
//------------------------------------------------------------------------------
protected ShowConditionAction createShowConditionAction (String conditionName)
{
  return new ShowConditionAction(conditionName);
}
protected class ShowConditionAction extends AbstractAction   {
  String conditionName;
  ShowConditionAction (String conditionName) { 
    super (conditionName);
    this.conditionName = conditionName;
    }

  public void actionPerformed (ActionEvent e) {
    System.out.println ("show " + conditionName);
    displayNodesWithExpressionValues (conditionName);
    }
}
//------------------------------------------------------------------------------
protected void loadGML (String filename)
{
  GMLIOHandler ioh = new GMLIOHandler ();
  ioh.read (graphView.getGraph2D (), filename);
  graph = graphView.getGraph2D ();
  displayGraph ();

} // loadGML
//------------------------------------------------------------------------------
protected void loadInteraction (String filename)
{
  InteractionsReader reader = new InteractionsReader (filename);
  reader.read ();
  graph = reader.getGraph ();
  edgeAttributes.add (reader.getEdgeAttributes ());
  displayGraph ();

} // loadInteraction
//------------------------------------------------------------------------------
protected ExitAction createExitAction () {
  return new ExitAction();
}
protected class ExitAction extends AbstractAction  {
  ExitAction () { super ("Exit"); }

  public void actionPerformed (ActionEvent e) {
    exit();
  }
}
//------------------------------------------------------------------------------
protected CloseWindowAction createCloseWindowAction () {
  return new CloseWindowAction();
}
protected class CloseWindowAction extends AbstractAction  {
  CloseWindowAction () { super ("Close"); }

  public void actionPerformed (ActionEvent e) {
    quit();
  }
}
//------------------------------------------------------------------------------
protected SaveAsGMLAction createSaveAsGMLAction () {
  return new SaveAsGMLAction();
}
protected class SaveAsGMLAction extends AbstractAction  
{
  SaveAsGMLAction () {super ("Save As GML..."); }
  public void actionPerformed (ActionEvent e) {
    File currentDirectory = new File (System.getProperty ("user.dir"));
    JFileChooser chooser = new JFileChooser (currentDirectory);
    if (chooser.showSaveDialog (CytoscapeWindow.this) == chooser.APPROVE_OPTION) {
      String name = chooser.getSelectedFile ().toString ();
      if (!name.endsWith (".gml")) name = name + ".gml";
      GMLIOHandler ioh = new GMLIOHandler ();
      ioh.write (graphView.getGraph2D (),name);
      } // if
    }

} // SaveAsAction
//------------------------------------------------------------------------------
protected LoadGMLFileAction createLoadGMLFileAction () {
  return new LoadGMLFileAction();
}
protected class LoadGMLFileAction extends AbstractAction {
  LoadGMLFileAction () { super ("GML..."); }
    
  public void actionPerformed (ActionEvent e)  {
   File currentDirectory = new File (System.getProperty ("user.dir"));
   JFileChooser chooser = new JFileChooser (currentDirectory);
   if (chooser.showOpenDialog (CytoscapeWindow.this) == chooser.APPROVE_OPTION) {
      String name = chooser.getSelectedFile ().toString ();
      geometryFilename = name;
      loadGML (name);
      } // if
    } // actionPerformed

} // inner class LoadAction
//------------------------------------------------------------------------------
protected LoadInteractionFileAction createLoadInteractionFileAction () {
  return new LoadInteractionFileAction();
}
protected class LoadInteractionFileAction extends AbstractAction {
  LoadInteractionFileAction() { super ("Interaction..."); }
    
  public void actionPerformed (ActionEvent e)  {
   File currentDirectory = new File (System.getProperty ("user.dir"));
   JFileChooser chooser = new JFileChooser (currentDirectory);
   if (chooser.showOpenDialog (CytoscapeWindow.this) == chooser.APPROVE_OPTION) {
      String name = chooser.getSelectedFile ().toString ();
      loadInteraction (name);
      } // if
    } // actionPerformed

} // inner class LoadAction
//------------------------------------------------------------------------------
protected LoadExpressionMatrixAction createLoadExpressionMatrixAction () {
  return new LoadExpressionMatrixAction();
}
protected class LoadExpressionMatrixAction extends AbstractAction {
  LoadExpressionMatrixAction () { super ("Expression Matrix File..."); }
    
  public void actionPerformed (ActionEvent e)  {
   File currentDirectory = new File (System.getProperty ("user.dir"));
   JFileChooser chooser = new JFileChooser (currentDirectory);
   if (chooser.showOpenDialog (CytoscapeWindow.this) == chooser.APPROVE_OPTION) {
      expressionDataFilename = chooser.getSelectedFile ().toString ();
      expressionData = new ExpressionData (expressionDataFilename);
      incorporateExpressionData ();
      } // if
    } // actionPerformed

} // inner class LoadExpressionMatrix
//------------------------------------------------------------------------------
protected DeleteSelectionAction createDeleteSelectionAction () {
  return new DeleteSelectionAction();
}
protected class DeleteSelectionAction extends AbstractAction {
  DeleteSelectionAction () { super ("Delete Selection"); }
  public void actionPerformed (ActionEvent e) {
    graphView.getGraph2D ().removeSelection ();
  updateGraph ();
    }
  }
//------------------------------------------------------------------------------
protected ZoomAction createZoomAction (double factor) {
  return new ZoomAction(factor);
}
protected class ZoomAction extends AbstractAction {
  double factor;
  ZoomAction (double factor) {
    super ("Zoom " +  (factor > 1.0 ? "In" : "Out"));
    this.factor = factor;
    }
    
  public void actionPerformed (ActionEvent e) {
    graphView.setZoom (graphView.getZoom ()*factor);
  updateGraph ();
    }
  }
//------------------------------------------------------------------------------
protected FitContentAction createFitContentAction () {
  return new FitContentAction();
}
protected class FitContentAction extends AbstractAction  {
   FitContentAction () { super ("Fit Content"); }
    public void actionPerformed (ActionEvent e) {
      graphView.fitContent ();
  updateGraph ();
      }
}
//------------------------------------------------------------------------------
protected ShowAllAction createShowAllAction () {
  return new ShowAllAction();
}
protected class ShowAllAction extends AbstractAction  {
   ShowAllAction () { super ("Show All"); }
    public void actionPerformed (ActionEvent e) {
      graphHider.unhideAll ();
      graphView.fitContent ();
      graphView.setZoom (graphView.getZoom ()*0.9);
  updateGraph ();
      }
}
//------------------------------------------------------------------------------
protected ZoomSelectedAction createZoomSelectedAction () {
  return new ZoomSelectedAction();
}
protected class ZoomSelectedAction extends AbstractAction  {
  ZoomSelectedAction ()  { super ("Zoom Selected"); }
  public void actionPerformed (ActionEvent e) {
    Graph2D g = graphView.getGraph2D ();
    NodeCursor nc = g.selectedNodes (); 
    if (nc.ok ()) { //selected nodes present? 
       Rectangle2D box = g.getRealizer (nc.node ()).getBoundingBox ();
       for (nc.next (); nc.ok (); nc.next ())
        g.getRealizer (nc.node ()).calcUnionRect (box);
        graphView.zoomToArea (box.getX(),box.getY(),box.getWidth(),box.getHeight());
        if (graphView.getZoom () > 2.0) graphView.setZoom (2.0);
        updateGraph ();
      }
    }
}
//------------------------------------------------------------------------------
protected CursorTesterAction createCursorTesterAction () {
  return new CursorTesterAction();
}
protected class CursorTesterAction extends AbstractAction  {
   boolean busy = false;
   CursorTesterAction () {
     super ("Cursor test"); 
     }
   public void actionPerformed (ActionEvent e) {
     if (busy)
       setInteractivity (true);
     else
       setInteractivity (false);
     busy = !busy;
     }

} // CursorTester
//------------------------------------------------------------------------------
protected AppearanceControllerLauncherAction
  createAppearanceControllerLauncherAction (GraphObjAttributes nodeAttributes, 
                                            GraphObjAttributes edgeAttributes)
{
  return
    new AppearanceControllerLauncherAction(nodeAttributes, edgeAttributes);
}
protected class AppearanceControllerLauncherAction extends AbstractAction  {
   GraphObjAttributes nodeAttributes;
   GraphObjAttributes edgeAttributes;
   AppearanceControllerLauncherAction (GraphObjAttributes nodeAttributes, 
                                 GraphObjAttributes edgeAttributes) {
     super ("Viz"); 
     this.nodeAttributes = nodeAttributes;
     this.edgeAttributes = edgeAttributes;
     } // ctor
   public void actionPerformed (ActionEvent e) {
     theVizChooser = 
        new VizChooser (CytoscapeWindow.this, mainFrame, nodeViz, nodeAttributes,
                        expressionData);
//                        edgeAttributes, expressionData);
     }

} // AppearanceControllerLauncher
//------------------------------------------------------------------------------
protected ActivePathControllerLauncherAction
  createActivePathControllerLauncherAction () {
  return new ActivePathControllerLauncherAction();
}
protected class ActivePathControllerLauncherAction extends AbstractAction  {
   ActivePathControllerLauncherAction () {
     super ("Active Paths"); 
     } // ctor
   public void actionPerformed (ActionEvent e) {
     JDialog dialog = new ConditionsVsPathwaysTable (mainFrame, conditionNames, 
                                                     activePaths, CytoscapeWindow.this);
     dialog.setLocationRelativeTo (mainFrame);
     dialog.pack ();
     dialog.setVisible (true);
     }

} // AppearanceControllerLauncher
//------------------------------------------------------------------------------
protected DescribeNodesAction createDescribeNodesAction () {
  return new DescribeNodesAction();
}
protected class DescribeNodesAction  extends AbstractAction  {

  DescribeNodesAction () { super ("Describe Nodes"); }

  public void actionPerformed (ActionEvent e) {
    System.out.println ("describe nodes...");
    } // actionPerformed

} // inner class DescribeNodes
//------------------------------------------------------------------------------
protected CalculateNetworkAction createCalculateNetworkAction () {
  return new CalculateNetworkAction();
}
protected class CalculateNetworkAction extends AbstractAction  {

  CalculateNetworkAction () { super ("Calculate Network"); }

  public void actionPerformed (ActionEvent e) {
    System.out.println ("calculate network...");
    } // actionPerformed


} // inner class CalculateNetwork
//------------------------------------------------------------------------------
protected void runActivePathsFinder (ActivePathFinderParameters params)
{
  runActivePathsFinder (params.getSignificanceThreshold (),
                        params.getInitialTemperature (),
                        params.getFinalTemperature (),
                        params.getTotalIterations (),
                        params.getNumberOfPaths (),
                        params.getDisplayInterval (),
                        params.getRandomSeed ());

} // runActivePathsFinder (alternate form)
//------------------------------------------------------------------------------
protected void runActivePathsFinder (double lambdaC, double initialTemperature, 
                                     double finalTemperature, int totalIterations, 
                                     int numberOfPaths, int displayInterval, 
                                     int randomSeed)
{
  setInteractivity (false);
  File graphFile = new File (geometryFilename);
  File expressionFile = new File (expressionDataFilename);

  finder = new ActivePathsFinderBridge (CytoscapeWindow.this, graph, expressionData);

  long start = System.currentTimeMillis ();
  updateGraph ();
  finder.setLambdaC (lambdaC);
  finder.setInitialTemperature (initialTemperature);
  finder.setFinalTemperature (finalTemperature);
  finder.setTotalIterations (totalIterations);
  finder.setDisplayInterval (displayInterval);
  finder.setNumberOfPaths (numberOfPaths);
  finder.setRandomSeed (randomSeed);
  finder.run ();
  long duration = System.currentTimeMillis () - start;
  int numberOfPathsFound = finder.getCount ();
  System.out.println ("-------------- back from finder: " +
                      numberOfPathsFound + " paths, " + duration + " msecs");
  activePaths = finder.getPaths ();
  setInteractivity (true);
  JDialog tableDialog = new ConditionsVsPathwaysTable (mainFrame, conditionNames,
                                                       activePaths, CytoscapeWindow.this);
  tableDialog.pack ();
  tableDialog.setLocationRelativeTo (mainFrame);
  tableDialog.setVisible (true);
  addActivePathToolbarButton ();
  setInteractivity (true);

} // runActivePathsFinder
//------------------------------------------------------------------------------
protected FindActivePathsAction createFindActivePathsAction () {
  return new FindActivePathsAction();
}
protected class FindActivePathsAction extends AbstractAction  
                            implements ActivePathsParametersPopupDialogListener {

  private double lambdaC, initialTemperature, finalTemperature;
  private int totalIterations, numberOfPaths, displayInterval, randomSeed;
  private boolean cancelActivePathsFinding;

  FindActivePathsAction () { super ("Find Active Paths"); }
    
  public void actionPerformed (ActionEvent e) {
    setInteractivity (false);
    JDialog paramsDialog = new ActivePathsParametersPopupDialog 
          (this, mainFrame, "Find Active Paths Parameters", expressionData);
    paramsDialog.pack ();
    paramsDialog.setLocationRelativeTo (mainFrame);
    paramsDialog.setVisible (true);
    runActivePathsFinder (lambdaC, initialTemperature, finalTemperature, totalIterations,
                          numberOfPaths, displayInterval, randomSeed);
    } // actionPerformed

  public void cancelActivePathsFinding () { // called when the popup dialog is dismissed
    cancelActivePathsFinding = true;
    }

  public void setActivePathsParameters (double lambdaC, 
                                        int totalIterations, 
                                        double initialTemperature,
                                        double finalTemperature, 
                                        int numberOfPaths,
                                        int displayInterval,
                                        int randomSeed) {

    this.lambdaC = lambdaC;
    this.totalIterations = totalIterations;
    this.initialTemperature = initialTemperature;
    this.finalTemperature = finalTemperature;
    this.numberOfPaths = numberOfPaths;
    this.displayInterval = displayInterval;
    this.randomSeed = randomSeed;

    System.out.println ("significance threshold: " + lambdaC);
    System.out.println ("       totalIterations: " + totalIterations);
    System.out.println ("    initialTemperature: " + initialTemperature);
    System.out.println ("      finalTemperature: " + finalTemperature);
    System.out.println ("       number of paths: " + numberOfPaths);
    System.out.println ("      display interval: " + displayInterval);
    System.out.println ("           random seed: " + randomSeed);
    } // setActivePathsParameters

}// FindActivePathsAction
//------------------------------------------------------------------------------
static protected boolean liveNode (String nodeName, String [] liveNodes)
      throws Exception
{
  // int nodeNumber = Integer.parseInt (nodeName);
  for (int i=0; i < liveNodes.length; i++) {
    if (nodeName.equalsIgnoreCase (liveNodes [i]))
      return true;
    }

  return false;

} // liveNode
//------------------------------------------------------------------------------
protected EditMode createDisabledEditingMode ()
{
  EditMode mode = new EditMode ();
  mode.allowNodeCreation (false);
  mode.allowEdgeCreation (false);
  mode.allowBendCreation (false);
  mode.showNodeTips (true);
  mode.showEdgeTips (true);
  return mode;

} // createDisabledEditingMode
//------------------------------------------------------------------------------
class DisabledEditMode extends EditMode {
  DisabledEditMode () { 
   super (); 
   allowNodeCreation (false);
   allowEdgeCreation (false);
   allowBendCreation (false);
   showNodeTips (true);
   showEdgeTips (true);
   }
  protected String getNodeTip (Node node) {
    String geneName = graphView.getGraph2D().getRealizer(node).getLabelText();
    String canonicalName = getCanonicalNodeName (geneName);
    if (canonicalName != null && canonicalName.length () > 0 && !canonicalName.equals (geneName))
      return geneName + " " + canonicalName;
    return geneName;
    } // getNodeTip

} // inncer class DisabledEditMode
//------------------------------------------------------------------------------
protected NodeAttributesPopupMode
  createNodeAttributesPopupMode () {
  return new NodeAttributesPopupMode();
}
protected class NodeAttributesPopupMode extends PopupMode {

  public JPopupMenu getNodePopup (Node v) {
    NodeRealizer r = graphView.getGraph2D().getRealizer(v);
    JDialog dialog = null;
    r.setSelected (false);
    String nodeName = r.getLabelText ();
    String [] nodeNames = new String [1];
    nodeNames [0] = nodeName;
    NodeAssessment assessment = nodeIsGoID (nodeName);
    if (assessment.isAGoID) {
      dialog = new GoIDAttributesPopupTable (mainFrame, nodeNames, bioDataServer, 
                                             nodeAttributes);
      }
    else {
      dialog = new NodeAttributesPopupTable (mainFrame, nodeNames, bioDataServer, 
                                             currentCondition, expressionData,
                                             nodeAttributes);
      }
    dialog.pack ();
    dialog.setLocationRelativeTo (mainFrame);
    dialog.setVisible (true);
    return null;
    }

  public JPopupMenu getPaperPopup (double x, double y) {
    return null;
    }
    
  public JPopupMenu getSelectionPopup (double x, double y) {
    Graph2D g = graphView.getGraph2D ();
    NodeCursor nc = g.selectedNodes (); 
    Vector nodeList = new Vector ();
    while (nc.ok ()) {
      Node node = nc.node ();
      NodeRealizer r = graphView.getGraph2D().getRealizer(node);
      String nodeName = r.getLabelText (); 
      nodeList.addElement (nodeName);
      nc.next ();
      }
    if (nodeList.size () > 0) {
      String [] nodeNames = new String [nodeList.size ()];
      for (int i=0; i < nodeList.size (); i++)
        nodeNames [i] = (String) nodeList.elementAt (i);
      JDialog dialog = null;
      NodeAssessment assessment = nodeIsGoID (nodeNames [0]);
      if (assessment.isAGoID) {
        dialog = new GoIDAttributesPopupTable (mainFrame, nodeNames, bioDataServer, 
                                               nodeAttributes);
        }
      else {
        dialog = new NodeAttributesPopupTable (mainFrame, nodeNames, bioDataServer, 
                                               currentCondition, expressionData,
                                               nodeAttributes);
        }
      dialog.pack ();
      dialog.setLocationRelativeTo (mainFrame);
      dialog.setVisible (true);
      } // if nodeList > 0     
    return null;
    }

} // inner class NodeAttributesPopupMode
//---------------------------------------------------------------------------------------
protected NodeAssessment createNodeAssessment () {
  return new NodeAssessment();
}
protected class NodeAssessment {
  boolean isAGoID;
  int id;
}
//---------------------------------------------------------------------------------------
protected NodeAssessment nodeIsGoID (String nodeName)
{
  NodeAssessment result = new NodeAssessment ();

  try {
    result.id = Integer.parseInt (nodeName);
    result.isAGoID = true;
    }
  catch (NumberFormatException nfe) {
    result.id = 0;
    result.isAGoID = false;
    }

  return result;

} // nodeIsGoID
//---------------------------------------------------------------------------------------
/**
 * Close this window.
 */
public void quit () {
  mainFrame.dispose();  
}
//---------------------------------------------------------------------------------------
/**
 * Close this window and all subwindows.
 */
public void exit () {
  if( subwindows != null ) {
    Enumeration subwindows_enum = subwindows.elements();
    while( subwindows_enum.hasMoreElements() ) {
      ( ( CytoscapeWindow )subwindows_enum.nextElement() ).exit();
    }
  }
  quit();
}
//---------------------------------------------------------------------------------------
} // CytoscapeWindow
