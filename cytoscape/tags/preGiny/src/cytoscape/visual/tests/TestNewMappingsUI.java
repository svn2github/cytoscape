// TestNewMappingsUI.java
//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.tests;
//--------------------------------------------------------------------------
import java.util.Properties;
import java.io.*;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.*;
import cytoscape.*;

import java.lang.Math;

import y.base.*;
import y.view.*;
import y.algo.*;

import cytoscape.data.*;
// added below imports
import cytoscape.visual.mappings.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.ui.*;
import cytoscape.visual.*;

/**  Contains methods and classes for exercising the calculators,
 *   mappings, and UI's in the cytoscape.visual package.
 */
public class TestNewMappingsUI {
    /** standard reference to the CytoscapeWindow. */
    protected CytoscapeWindow cytoscapeWindow;
    /** contains all information necessary to compute appearances. */
    protected CyNetwork network;
    public TestNewMappingsUI (CytoscapeWindow cytoscapeWindow)
    {
	this.cytoscapeWindow = cytoscapeWindow;
	this.network = cytoscapeWindow.getNetwork();
 	cytoscapeWindow.getOperationsMenu().addSeparator();
	/*
 	cytoscapeWindow.getOperationsMenu().add
	    (new RandomlyColorEdgesAction ());
 	cytoscapeWindow.getOperationsMenu().addSeparator();

 	cytoscapeWindow.getOperationsMenu().add
	    (new RandomlyColorNodesByGoLev4Action ());
 	cytoscapeWindow.getOperationsMenu().add
	    (new RandomlyColorNodeBordersByGoLev4Action ());
 	cytoscapeWindow.getOperationsMenu().add
	    (new RandomlySizeNodesByGoLev4Action ());
 	cytoscapeWindow.getOperationsMenu().add
	    (new SetNodeBorderThicknessByGoLev4Action ());
 	cytoscapeWindow.getOperationsMenu().add
	    (new SetNodeShapeByGoLev4Action ());
 	cytoscapeWindow.getOperationsMenu().add
	    (new SetNodeShapeByCanonicalNameAction ());
 	cytoscapeWindow.getOperationsMenu().add
	    (new SetNodeLabelByGoLev4Action ());
 	cytoscapeWindow.getOperationsMenu().addSeparator();
	*/

 	cytoscapeWindow.getOperationsMenu().add
	    (new VizPropsUIAction ());
 	cytoscapeWindow.getOperationsMenu().addSeparator();
    }

    /**
     *  This action tests the new VizPropsUI.
     */
    class VizPropsUIAction extends AbstractAction   {
	private VisualMappingManager vmm;
	private VizMapUI vmu;
	private boolean initialized = false;

	VizPropsUIAction () {
	    super ("Test: VizPropsUI");
	}
	public void actionPerformed (ActionEvent e) {
	    if (! initialized) {
		NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
		addNodeShapeMapping(nac);
		addNodeLineTypeMapping(nac);
		addNodeBorderColorMapping(nac);
		addNodeFillColorMapping(nac);
		addNodeHeightMapping(nac);
		addNodeWidthMapping(nac);
		addDiscreteNodeLabelMapping(nac);
		// node tool tip
		EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator();
		addEdgeColorMapping(eac);
		addEdgeLineTypeMapping(eac);
		addEdgeSourceArrowMapping(eac);
		addEdgeTargetArrowMapping(eac);
		addDiscreteEdgeLabelMapping(eac);
		// edge tool tip
                GlobalAppearanceCalculator gac = new GlobalAppearanceCalculator();
                VisualStyle vs = new VisualStyle("mappingsTest", nac, eac, gac);
                CalculatorCatalog cc = new CalculatorCatalog();
		this.vmm = new VisualMappingManager(cytoscapeWindow, cc, vs);
		cc.addNodeLabelCalculator(getPassThroughNLC());
		cc.addNodeColorCalculator(getContinuousNFCC());
		cc.addNodeColorCalculator(getMultipointContinuousNFCC());
		cc.addEdgeLabelCalculator(getPassThroughELC());
		cc.addNodeSizeCalculator(getMultipointContinuousNSC());
                //load in calculators from file
                Properties props = new Properties();
                try {
                    InputStream is = new FileInputStream("vizmap.props");
                    props.load(is);
                    is.close();
                } catch (Exception ioe) {
                    ioe.printStackTrace();
                }
                CalculatorIO.loadCalculators(props, cc);
		// register mappings
		cc.addMapping("Discrete Mapper", DiscreteMapping.class);
		cc.addMapping("Continuous Mapper", ContinuousMapping.class);
		cc.addMapping("Passthrough Mapper", PassThroughMapping.class);
		this.vmu = new VizMapUI(vmm);
		initialized = true;
	    }
	    else {
		this.vmu.refreshUI();
		this.vmu.show();
	    }
	}
    }


    private void addNodeShapeMapping(NodeAppearanceCalculator nac) {
	DiscreteMapping m =
	    new DiscreteMapping(new Byte((byte)0), ObjectMapping.NODE_MAPPING);
	m.setControllingAttributeName("GO Molecular Function (level 4)", network, false);
	m.put("autophagy",new Byte(ShapeNodeRealizer.DIAMOND));
	m.put("budding",new Byte(ShapeNodeRealizer.HEXAGON));
	m.put("growth",new Byte(ShapeNodeRealizer.OCTAGON));
	m.put("metabolism",new Byte(ShapeNodeRealizer.PARALLELOGRAM));
	m.put("transport",new Byte(ShapeNodeRealizer.ROUND_RECT));
	m.put("biological_process unknown",
	      new Byte(ShapeNodeRealizer.RECT));
	GenericNodeShapeCalculator nsc =
	    new GenericNodeShapeCalculator("Example Node Shape Map",m);
	nac.setNodeShapeCalculator(nsc);
    }

    private void addCanonicalNodeShapeMapping(NodeAppearanceCalculator nac) {
	DiscreteMapping m =
	    new DiscreteMapping(new Byte((byte)0), ObjectMapping.NODE_MAPPING);
	m.setControllingAttributeName("canonicalName", network, false);
	m.put("A",new Byte(ShapeNodeRealizer.DIAMOND));
	m.put("B",new Byte(ShapeNodeRealizer.HEXAGON));
	m.put("C",new Byte(ShapeNodeRealizer.OCTAGON));
	m.put("D",new Byte(ShapeNodeRealizer.PARALLELOGRAM));
	m.put("E",new Byte(ShapeNodeRealizer.ROUND_RECT));
	m.put("F",new Byte(ShapeNodeRealizer.RECT));
	m.put("G",new Byte(ShapeNodeRealizer.RECT_3D));
	m.put("H",new Byte(ShapeNodeRealizer.ELLIPSE));
	m.put("I",new Byte(ShapeNodeRealizer.TRAPEZOID));
	m.put("J",new Byte(ShapeNodeRealizer.TRAPEZOID_2));
	m.put("K",new Byte(ShapeNodeRealizer.TRIANGLE));
	GenericNodeShapeCalculator nsc =
	    new GenericNodeShapeCalculator("Canonical Node Shape Example",m);
	nac.setNodeShapeCalculator(nsc);
    }

    private void addNodeLineTypeMapping(NodeAppearanceCalculator nac) {
	DiscreteMapping m =
	    new DiscreteMapping(LineType.DASHED_3, ObjectMapping.NODE_MAPPING);
	m.setControllingAttributeName("GO Molecular Function (level 4)", network, false);
	m.put("autophagy",LineType.DASHED_4);
	m.put("budding",LineType.DASHED_5);
	m.put("growth",LineType.LINE_3);
	m.put("metabolism",LineType.LINE_5);
	m.put("transport",LineType.LINE_6);
	m.put("biological_process unknown",LineType.LINE_7);
	GenericNodeLineTypeCalculator nltc =
	    new GenericNodeLineTypeCalculator("Example Node LineType Map",m);
	nac.setNodeLineTypeCalculator(nltc);
    }

    private void addNodeBorderColorMapping(NodeAppearanceCalculator nac) {
	DiscreteMapping m =
	    new DiscreteMapping(new Color(0,0,0), ObjectMapping.NODE_MAPPING);
	m.setControllingAttributeName("GO Molecular Function (level 4)", network, false);
	addRandomColor(m,"autophagy");
	addRandomColor(m,"budding");
	addRandomColor(m,"growth");
	addRandomColor(m,"metabolism");
	addRandomColor(m,"transport");
	addRandomColor(m,"biological_process unknown");
	GenericNodeColorCalculator ncc =
	    new GenericNodeColorCalculator("Example Node Border Color Map",m);
	nac.setNodeBorderColorCalculator(ncc);
    }

    private void addNodeFillColorMapping(NodeAppearanceCalculator nac) {
	DiscreteMapping m =
	    new DiscreteMapping(new Color(0,0,0), ObjectMapping.NODE_MAPPING);
	m.setControllingAttributeName("GO Molecular Function (level 4)", network, false);
	addRandomColor(m,"autophagy");
	addRandomColor(m,"budding");
	addRandomColor(m,"growth");
	addRandomColor(m,"metabolism");
	addRandomColor(m,"transport");
	addRandomColor(m,"biological_process unknown");
	GenericNodeColorCalculator ncc =
	    new GenericNodeColorCalculator("Example Node Fill Color Map",m);
	nac.setNodeFillColorCalculator(ncc);
    }

    private void addNodeHeightMapping(NodeAppearanceCalculator nac) {
	DiscreteMapping m =
	    new DiscreteMapping(new Double(0), ObjectMapping.NODE_MAPPING);
	m.setControllingAttributeName("GO Molecular Function (level 4)", network, false);
	addRandomDouble(m,"autophagy");
	addRandomDouble(m,"budding");
	addRandomDouble(m,"growth");
	addRandomDouble(m,"metabolism");
	addRandomDouble(m,"transport");
	addRandomDouble(m,"biological_process unknown");
	GenericNodeSizeCalculator nHc =
	    new GenericNodeSizeCalculator("Example Node Height Map",m);
	nac.setNodeHeightCalculator(nHc);
    }

    private void addNodeWidthMapping(NodeAppearanceCalculator nac) {
	DiscreteMapping m2 =
	    new DiscreteMapping(new Double(0), ObjectMapping.NODE_MAPPING);
	m2.setControllingAttributeName("GO Molecular Function (level 4)", network, false);
	addRandomDouble(m2,"autophagy");
	addRandomDouble(m2,"budding");
	addRandomDouble(m2,"growth");
	addRandomDouble(m2,"metabolism");
	addRandomDouble(m2,"transport");
	addRandomDouble(m2,"biological_process unknown");
	GenericNodeSizeCalculator nWc =
	    new GenericNodeSizeCalculator("Example Node Width Map",m2);
	nac.setNodeWidthCalculator(nWc);
    }

    private void addDiscreteNodeLabelMapping(NodeAppearanceCalculator nac) {
	DiscreteMapping m =
	    new DiscreteMapping(new String(""), ObjectMapping.NODE_MAPPING);
	m.setControllingAttributeName("GO Molecular Function (level 4)", network, false);
	m.put("autophagy","Autophage");
	m.put("budding","Bud");
	m.put("growth","Grow");
	m.put("metabolism","Metabolize");
	m.put("transport","Transporter");
	m.put("biological_process unknown","?????");
	GenericNodeLabelCalculator nlc =
	    new GenericNodeLabelCalculator("Example Node Label Map",m);
	nac.setNodeLabelCalculator(nlc);
    }

    private GenericNodeColorCalculator getContinuousNFCC() {
	Interpolator fInt = new LinearNumberToColorInterpolator();
	ContinuousMapping m =
	    new ContinuousMapping(new Color(200,200,255),"gal4RG.sigsig",
				  fInt,ObjectMapping.NODE_MAPPING);
	return new GenericNodeColorCalculator("Test Continuous Color",m);
    }
    private GenericNodeColorCalculator getMultipointContinuousNFCC() {
	Interpolator fInt = new LinearNumberToColorInterpolator();
	ContinuousMapping m =
	    new ContinuousMapping(new Color(255,255,255),"gal4RG.sigsig",
				  fInt,ObjectMapping.NODE_MAPPING);
	BoundaryRangeValues brv;
	brv = new BoundaryRangeValues();
	brv.lesserValue = new Color(255,255,0);
	brv.equalValue = new Color(255,160,64);
	brv.greaterValue = new Color(255,160,32);
	m.put(new Double(0.001),brv);

	brv = new BoundaryRangeValues();
	brv.lesserValue = new Color(255,0,0);
	brv.equalValue = new Color(255,0,0);
	brv.greaterValue = new Color(255,0,0);
	m.put(new Double(0.02),brv);

	brv = new BoundaryRangeValues();
	brv.lesserValue = new Color(191,0,191);
	brv.equalValue = new Color(191,0,191);
	brv.greaterValue = new Color(0,0,191);
	m.put(new Double(0.4),brv);

	return new GenericNodeColorCalculator("Example Continuous Color",m);
    }

    private GenericNodeSizeCalculator getMultipointContinuousNSC() {
	Interpolator fInt = new LinearNumberToNumberInterpolator();
	ContinuousMapping m =
	    new ContinuousMapping(new Double(1.0),"gal4RG.sigsig",
				  fInt,ObjectMapping.NODE_MAPPING);
	BoundaryRangeValues brv;
	brv = new BoundaryRangeValues();
	brv.lesserValue = new Double(50);
	brv.equalValue = new Double(50);
	brv.greaterValue = new Double(50);
	m.put(new Double(0.001),brv);

	brv = new BoundaryRangeValues();
	brv.lesserValue = new Double(30);
	brv.equalValue = new Double(30);
	brv.greaterValue = new Double(30);
	m.put(new Double(0.02),brv);

	brv = new BoundaryRangeValues();
	brv.lesserValue = new Double(20);
	brv.equalValue = new Double(20);
	brv.greaterValue = new Double(20);
	m.put(new Double(0.4),brv);

	return new GenericNodeSizeCalculator("Example Continuous Size",m);
    }

    private void addPassThroughNodeLabelMapping(NodeAppearanceCalculator nac) {
	GenericNodeLabelCalculator nlc = getPassThroughNLC();
	nac.setNodeLabelCalculator(nlc);
    }
    private GenericNodeLabelCalculator getPassThroughNLC() {
	PassThroughMapping m =
	    new PassThroughMapping(new String(""),
				   "GO Molecular Function (level 4)");
	return new GenericNodeLabelCalculator("Example Label PassThru",m);
    }

    private void addEdgeColorMapping(EdgeAppearanceCalculator eac) {
	DiscreteMapping m =
	    new DiscreteMapping(new Color(0,0,0), ObjectMapping.EDGE_MAPPING);
	m.setControllingAttributeName("interaction", network, false);
	addRandomColor(m,"pp");
	addRandomColor(m,"pd");
	GenericEdgeColorCalculator ecc =
	    new GenericEdgeColorCalculator("Example Edge Color Map",m);
	eac.setEdgeColorCalculator(ecc);
    }

    private void addEdgeLineTypeMapping(EdgeAppearanceCalculator eac) {
	DiscreteMapping m =
	    new DiscreteMapping(LineType.DASHED_3, ObjectMapping.EDGE_MAPPING);
	m.setControllingAttributeName("interaction", network, false);
	m.put("pp",LineType.DASHED_4);
	m.put("pd",LineType.LINE_6);
	GenericEdgeLineTypeCalculator eltc =
	    new GenericEdgeLineTypeCalculator("Example Edge Line Type Map",m);
	eac.setEdgeLineTypeCalculator(eltc);
    }

    private void addEdgeSourceArrowMapping(EdgeAppearanceCalculator eac) {
	DiscreteMapping m =
	    new DiscreteMapping(Arrow.DIAMOND, ObjectMapping.EDGE_MAPPING);
	m.setControllingAttributeName("interaction", network, false);
	m.put("pp",Arrow.WHITE_DIAMOND);
	m.put("pd",Arrow.WHITE_DELTA);
	GenericEdgeArrowCalculator earrowc =
	    new GenericEdgeArrowCalculator("Example Edge Source Arrow Map",m);
	eac.setEdgeSourceArrowCalculator(earrowc);
    }

    private void addEdgeTargetArrowMapping(EdgeAppearanceCalculator eac) {
	DiscreteMapping m =
	    new DiscreteMapping(Arrow.DIAMOND, ObjectMapping.EDGE_MAPPING);
	m.setControllingAttributeName("interaction", network, false);
	m.put("pp",Arrow.NONE);
	m.put("pd",Arrow.DELTA);
	GenericEdgeArrowCalculator earrowc =
	    new GenericEdgeArrowCalculator("Example Edge Target Arrow Map",m);
	eac.setEdgeTargetArrowCalculator(earrowc);
    }

    private void addDiscreteEdgeLabelMapping(EdgeAppearanceCalculator eac) {
	DiscreteMapping m =
	    new DiscreteMapping(new String(""), ObjectMapping.EDGE_MAPPING);
	m.setControllingAttributeName("interaction", network, false);
	m.put("pp","Protein-Protein");
	m.put("pd","Transcriptional");
	GenericEdgeLabelCalculator elc =
	    new GenericEdgeLabelCalculator("Example Edge Label Map",m);
	eac.setEdgeLabelCalculator(elc);
    }

    private void addPassThroughEdgeLabelMapping(EdgeAppearanceCalculator eac) {
	GenericEdgeLabelCalculator elc = getPassThroughELC();
	eac.setEdgeLabelCalculator(elc);
    }
    private GenericEdgeLabelCalculator getPassThroughELC() {
	PassThroughMapping m =
	    new PassThroughMapping(new String(""),"interaction");
	return new GenericEdgeLabelCalculator("Exaple Edge Label PassThru",m);
    }


    ///////////////////////////////////////////////////////////////////////

    // this is the graph update routine, much like
    // CytoscapeWindow.redrawGraph(), but without calling the old
    // vizmap updates.
    private void updateGraph() {
	    Graph2DView graphView = cytoscapeWindow.getGraphView();
	    System.out.println("Updating graph...");
	    graphView.updateView();
	    graphView.paintImmediately(0,0,graphView.getWidth(),
				       graphView.getHeight());
	    cytoscapeWindow.updateStatusText();
    }

    // this creates a random color and adds it to a map,
    // associated with a string.
    private void addRandomColor(DiscreteMapping m, String s) {
	m.put(s,new Color((int)Math.floor(Math.random()*255),
			  (int)Math.floor(Math.random()*255),
			  (int)Math.floor(Math.random()*255)));
    }

    // this creates a random Double and adds it to a map,
    // associated with a string.
    private void addRandomDouble(DiscreteMapping m, String s) {
	m.put(s,new Double(20+Math.random()*40));
    }

    ///////////////////////////////////////////////////////////////////////

    /** This action requires (in order to do anything) that you
     *  have already loaded the GO Molecular Function (level 4)
     *  annotations into the nodeAttributes.
     */
    class SetNodeShapeByGoLev4Action extends AbstractAction   {
	
	SetNodeShapeByGoLev4Action () {
	    super ("Test: Set Node Shape by GO Level 4");
	}
	
	public void actionPerformed (ActionEvent e) {
	    System.out.println("Creating calculator...");
	    NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
	    addNodeShapeMapping(nac);

	    System.out.println("Gathering graph information...");
	    Graph2DView graphView = cytoscapeWindow.getGraphView();
	    Node [] nodes = graphView.getGraph2D().getNodeArray();
	    for (int i=0; i < nodes.length; i++) {
		Node node = nodes [i];
		NodeAppearance na = new NodeAppearance();
		nac.calculateNodeAppearance(na,node,network);
		NodeRealizer nr = graphView.getGraph2D().getRealizer(node);
		//nr.setFillColor(na.getFillColor());
		//nr.setLineColor(na.getBorderColor());
		//nr.setLineType(na.getBorderLineType());
		//nr.setHeight(na.getHeight());
		//nr.setWidth(na.getWidth());
		if (nr instanceof ShapeNodeRealizer) {
		    ShapeNodeRealizer snr = (ShapeNodeRealizer)nr;
		    snr.setShapeType(na.getShape());
		}
		//nr.setLabelText(na.getLabel());
	    }
	    updateGraph();

	}
	
    }



    /** This action requires (in order to do anything) that you
     *  have already loaded the GO Molecular Function (level 4)
     *  annotations into the nodeAttributes.
     */
    class SetNodeShapeByCanonicalNameAction extends AbstractAction   {
	
	SetNodeShapeByCanonicalNameAction () {
	    super ("Test: Set Node Shape by Canonical Name");
	}
	
	public void actionPerformed (ActionEvent e) {
	    System.out.println("Creating calculator...");
	    NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
	    addCanonicalNodeShapeMapping(nac);

	    System.out.println("Gathering graph information...");
	    Graph2DView graphView = cytoscapeWindow.getGraphView();
	    Node [] nodes = graphView.getGraph2D().getNodeArray();
	    for (int i=0; i < nodes.length; i++) {
		Node node = nodes [i];
		NodeAppearance na = new NodeAppearance();
		nac.calculateNodeAppearance(na,node,network);
		NodeRealizer nr = graphView.getGraph2D().getRealizer(node);
		//nr.setFillColor(na.getFillColor());
		//nr.setLineColor(na.getBorderColor());
		//nr.setLineType(na.getBorderLineType());
		//nr.setHeight(na.getHeight());
		//nr.setWidth(na.getWidth());
		if (nr instanceof ShapeNodeRealizer) {
		    ShapeNodeRealizer snr = (ShapeNodeRealizer)nr;
		    snr.setShapeType(na.getShape());
		}
		//nr.setLabelText(na.getLabel());
	    }
	    updateGraph();

	}
	
    }

    /** This action requires (in order to do anything) that you
     *  have already loaded the GO Molecular Function (level 4)
     *  annotations into the nodeAttributes.
     */
    class SetNodeBorderThicknessByGoLev4Action extends AbstractAction   {
	
	SetNodeBorderThicknessByGoLev4Action () {
	    super ("Test: Set Node Border Thickness by GO Level 4");
	}
	
	public void actionPerformed (ActionEvent e) {
	    System.out.println("Creating calculator...");
	    NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
	    addNodeLineTypeMapping(nac);

	    System.out.println("Gathering graph information...");
	    Graph2DView graphView = cytoscapeWindow.getGraphView();
	    Node [] nodes = graphView.getGraph2D().getNodeArray();
	    for (int i=0; i < nodes.length; i++) {
		Node node = nodes [i];
		NodeAppearance na = new NodeAppearance();
		nac.calculateNodeAppearance(na,node,network);
		NodeRealizer nr = graphView.getGraph2D().getRealizer(node);
		//nr.setFillColor(na.getFillColor());
		//nr.setLineColor(na.getBorderColor());
		nr.setLineType(na.getBorderLineType());
		//nr.setHeight(na.getHeight());
		//nr.setWidth(na.getWidth());
		//if (nr instanceof ShapeNodeRealizer) {
		//    ShapeNodeRealizer snr = (ShapeNodeRealizer)nr;
		//    snr.setShapeType(na.getShape());
		//}
		//nr.setLabelText(na.getLabel());
	    }
	    updateGraph();

	}
	
    }


    /** This action requires (in order to do anything) that you
     *  have already loaded the GO Molecular Function (level 4)
     *  annotations into the nodeAttributes.
     */
    class SetNodeLabelByGoLev4Action extends AbstractAction   {
	
	SetNodeLabelByGoLev4Action () {
	    super ("Test: Set Node Label by GO Level 4");
	}
	
	public void actionPerformed (ActionEvent e) {
	    System.out.println("Creating calculator...");
	    NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
	    addDiscreteNodeLabelMapping(nac);

	    System.out.println("Gathering graph information...");
	    Graph2DView graphView = cytoscapeWindow.getGraphView();
	    Node [] nodes = graphView.getGraph2D().getNodeArray();
	    for (int i=0; i < nodes.length; i++) {
		Node node = nodes [i];
		NodeAppearance na = new NodeAppearance();
		nac.calculateNodeAppearance(na,node,network);
		NodeRealizer nr = graphView.getGraph2D().getRealizer(node);
		//nr.setFillColor(na.getFillColor());
		//nr.setLineColor(na.getBorderColor());
		//nr.setLineType(na.getBorderLineType());
		//nr.setHeight(na.getHeight());
		//nr.setWidth(na.getWidth());
		//if (nr instanceof ShapeNodeRealizer) {
		//    ShapeNodeRealizer snr = (ShapeNodeRealizer)nr;
		//    snr.setShapeType(na.getShape());
		//}
		nr.setLabelText(na.getLabel());
	    }
	    updateGraph();

	}
	
    }

    /** This action requires (in order to do anything) that you
     *  have already loaded the GO Molecular Function (level 4)
     *  annotations into the nodeAttributes.
     */
    class RandomlyColorNodeBordersByGoLev4Action extends AbstractAction   {
	
	RandomlyColorNodeBordersByGoLev4Action () {
	    super ("Test: Randomly Color Node Borders by GO Level 4");
	}
	
	public void actionPerformed (ActionEvent e) {
	    System.out.println("Creating calculator...");
	    NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
	    addNodeBorderColorMapping(nac);

	    System.out.println("Gathering graph information...");
	    Graph2DView graphView = cytoscapeWindow.getGraphView();
	    Node [] nodes = graphView.getGraph2D().getNodeArray();
	    for (int i=0; i < nodes.length; i++) {
		Node node = nodes [i];
		NodeAppearance na = new NodeAppearance();
		nac.calculateNodeAppearance(na,node,network);
		NodeRealizer nr = graphView.getGraph2D().getRealizer(node);
		//nr.setFillColor(na.getFillColor());
		nr.setLineColor(na.getBorderColor());
		//nr.setLineType(na.getBorderLineType());
		//nr.setHeight(na.getHeight());
		//nr.setWidth(na.getWidth());
		//if (nr instanceof ShapeNodeRealizer) {
		//    ShapeNodeRealizer snr = (ShapeNodeRealizer)nr;
		//    snr.setShapeType(na.getShape());
		//}
		//nr.setLabelText(na.getLabel());
	    }
	    updateGraph();

	}
	
    }

    /** This action requires (in order to do anything) that you
     *  have already loaded the GO Molecular Function (level 4)
     *  annotations into the nodeAttributes.
     */
    class RandomlyColorNodesByGoLev4Action extends AbstractAction   {
	
	RandomlyColorNodesByGoLev4Action () {
	    super ("Test: Randomly Color Nodes by GO Level 4");
	}
	
	public void actionPerformed (ActionEvent e) {
	    System.out.println("Creating calculator...");
	    NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
	    addNodeFillColorMapping(nac);

	    System.out.println("Gathering graph information...");
	    Graph2DView graphView = cytoscapeWindow.getGraphView();
	    Node [] nodes = graphView.getGraph2D().getNodeArray();
	    for (int i=0; i < nodes.length; i++) {
		Node node = nodes [i];
		NodeAppearance na = new NodeAppearance();
		nac.calculateNodeAppearance(na,node,network);
		NodeRealizer nr = graphView.getGraph2D().getRealizer(node);
		nr.setFillColor(na.getFillColor());
		//nr.setLineColor(na.getBorderColor());
		//nr.setLineType(na.getBorderLineType());
		//nr.setHeight(na.getHeight());
		//nr.setWidth(na.getWidth());
		//if (nr instanceof ShapeNodeRealizer) {
		//    ShapeNodeRealizer snr = (ShapeNodeRealizer)nr;
		//    snr.setShapeType(na.getShape());
		//}
		//nr.setLabelText(na.getLabel());
	    }
	    updateGraph();

	}
	
    }


    /** This action requires (in order to do anything) that you
     *  have already loaded the GO Molecular Function (level 4)
     *  annotations into the nodeAttributes.
     */
    class RandomlySizeNodesByGoLev4Action extends AbstractAction   {
	
	RandomlySizeNodesByGoLev4Action () {
	    super ("Test: Randomly Size Nodes by GO Level 4");
	}
	
	public void actionPerformed (ActionEvent e) {
	    System.out.println("Creating calculator...");
	    NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
	    addNodeHeightMapping(nac);
	    addNodeWidthMapping(nac);

	    System.out.println("Gathering graph information...");
	    Graph2DView graphView = cytoscapeWindow.getGraphView();
	    Node [] nodes = graphView.getGraph2D().getNodeArray();
	    for (int i=0; i < nodes.length; i++) {
		Node node = nodes [i];
		NodeAppearance na = new NodeAppearance();
		nac.calculateNodeAppearance(na,node,network);
		NodeRealizer nr = graphView.getGraph2D().getRealizer(node);
		//nr.setFillColor(na.getFillColor());
		//nr.setLineColor(na.getBorderColor());
		//nr.setLineType(na.getBorderLineType());
		nr.setHeight(na.getHeight());
		nr.setWidth(na.getWidth());
		//if (nr instanceof ShapeNodeRealizer) {
		//    ShapeNodeRealizer snr = (ShapeNodeRealizer)nr;
		//    snr.setShapeType(na.getShape());
		//}
		//nr.setLabelText(na.getLabel());
	    }
	    updateGraph();

	}
	
    }


    /** This action randomly picks colors for interaction types
     *  "pp" and "pd", then colors the edges accordingly.  It
     *  assumes that edges have a property "interaction" and
     *  that some of the interactions have values "pp" and "pd".
     */
    class RandomlyColorEdgesAction extends AbstractAction   {
	
	RandomlyColorEdgesAction () {
	    super ("Test: Randomly Color Edges");
	}
	
	public void actionPerformed (ActionEvent e) {
	    System.out.println("Creating calculator...");
	    EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator();
	    addEdgeColorMapping(eac);

	    System.out.println("Gathering graph information...");
	    Graph2DView graphView = cytoscapeWindow.getGraphView();
	    EdgeCursor cursor = graphView.getGraph2D().edges();
	    cursor.toFirst ();
	    System.out.println("Applying calculator to graph...");
	    for (int i=0; i < cursor.size (); i++) {
		Edge edge = cursor.edge ();
		EdgeAppearance ea = new EdgeAppearance();
		eac.calculateEdgeAppearance(ea,edge,network);
		EdgeRealizer er = graphView.getGraph2D().getRealizer(edge);
		er.setLineColor(ea.getColor());
		//er.setLineType(ea.getLineType());
		//er.setSourceArrow(ea.getSourceArrow());
		//er.setTargetArrow(ea.getTargetArrow());
		//er.setLabelText(ea.getLabel());
		cursor.cyclicNext();
	    }
	    updateGraph();
	}
	
    }


}


