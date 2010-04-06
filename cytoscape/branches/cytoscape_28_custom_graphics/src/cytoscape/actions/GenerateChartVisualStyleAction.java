package cytoscape.actions;

import static cytoscape.visual.VisualPropertyType.EDGE_LABEL;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL;
import giny.model.Node;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMapDefinitionListener;
import cytoscape.util.CytoscapeAction;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.AbstractCalculator;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.customgraphic.CustomGraphicsPool;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.URLImageCustomGraphics;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

public class GenerateChartVisualStyleAction extends CytoscapeAction implements
		MultiHashMapDefinitionListener {

	private static final long serialVersionUID = -8744383285698760601L;
	private static final String DEF_VS_NAME = "Chart VS";

	/**
	 * Creates a new ListFromFileSelectionAction object.
	 */
	public GenerateChartVisualStyleAction() {
		super("Generate VS");
		setPreferredMenu("View");
		setEnabled(true);

		Cytoscape.getNodeAttributes().getMultiHashMapDefinition()
				.addDataDefinitionListener(this);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		buildFromFile();
	}

	private void buildFromFile() {
		final Object controllingAttr = JOptionPane.showInputDialog(Cytoscape
				.getDesktop(), "Controlling Attr", "Generate chart VS",
				JOptionPane.QUESTION_MESSAGE, null, nameArray, "ID");
		if (controllingAttr != null)
			generateVS(controllingAttr.toString());
	}

	@Override
	public void attributeDefined(String attributeName) {
		update();
	}

	@Override
	public void attributeUndefined(String attributeName) {
		update();
	}

	private void update() {
		final List<String> names = new ArrayList<String>();
		CyAttributes attr = Cytoscape.getNodeAttributes();
		nameArray = attr.getAttributeNames();
		Arrays.sort(nameArray);
		names.add("ID");

		for (String name : nameArray) {
			if (attr.getUserVisible(name)
					&& (attr.getType(name) != CyAttributes.TYPE_UNDEFINED)
					&& (attr.getType(name) != CyAttributes.TYPE_COMPLEX)) {
				names.add(name);
			}
		}
	}

	private void generateVS(String controllingAttrName) {
		final Color NODE_COLOR = Color.white;
		final Color EDGE_COLOR = Color.BLACK;

		final VisualStyle defStyle = new VisualStyle(DEF_VS_NAME);

		NodeAppearanceCalculator nac = defStyle.getNodeAppearanceCalculator();
		EdgeAppearanceCalculator eac = defStyle.getEdgeAppearanceCalculator();
		GlobalAppearanceCalculator gac = defStyle
				.getGlobalAppearanceCalculator();

		gac.setDefaultBackgroundColor(Color.LIGHT_GRAY);

		PassThroughMapping m = new PassThroughMapping(String.class, AbstractCalculator.ID);

		final Calculator calc = new BasicCalculator(DEF_VS_NAME + "-"
				+ "NodeLabelMapping", m, NODE_LABEL);
		PassThroughMapping me = new PassThroughMapping(String.class, "interaction");

		final Calculator calce = new BasicCalculator(DEF_VS_NAME + "-"
				+ "EdgeLabelMapping", me, EDGE_LABEL);
		nac.setCalculator(calc);

		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR,
				NODE_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE,
				NodeShape.RECT);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_OPACITY, 255);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LINE_WIDTH, 0);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SIZE, 30);

		eac.setCalculator(calce);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_COLOR,
				EDGE_COLOR);

		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_FONT_SIZE, 5);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_OPACITY, 255);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LINE_WIDTH, 4);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LABEL, "");

		// Set edge color based on datasource name
		DiscreteMapping nodeGraphics = new DiscreteMapping(Cytoscape.getVisualMappingManager().getCustomGraphicsPool()
				.getNullGraphics(), "ID", ObjectMapping.NODE_MAPPING);

		generateCharts(controllingAttrName, nodeGraphics);
		Calculator nodeGraphicsCalc = new BasicCalculator(DEF_VS_NAME + "-"
				+ "NodeCustomGraphicsMapping", nodeGraphics,
				VisualPropertyType.NODE_CUSTOM_GRAPHICS);

		
		nac.setCalculator(nodeGraphicsCalc);

		if (Cytoscape.getVisualMappingManager().getCalculatorCatalog()
				.getVisualStyle(defStyle.getName()) == null) {
			Cytoscape.getVisualMappingManager().getCalculatorCatalog()
					.addVisualStyle(defStyle);
		}

		Cytoscape.getVisualMappingManager().setVisualStyle(defStyle);
	}

	private void generateCharts(String ctr, final DiscreteMapping nodeGraphics) {
		final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		final CustomGraphicsPool pool = Cytoscape.getVisualMappingManager().getCustomGraphicsPool();
		for (Integer idx : Cytoscape.getRootGraph().getNodeIndicesArray()) {
			final Node node = Cytoscape.getRootGraph().getNode(idx);
//			Double targetAttrVal = nodeAttr.getDoubleAttribute(node
//					.getIdentifier(), ctr);
//			if (targetAttrVal != null) {
				try {
					final String dataString = createData();
					
					final String url = "http://chart.apis.google.com/chart?cht=ls&chs=300x300&chds=0,1,0,1&chxr=0,0,1&chma=5,5,5,5&" +
							"chd=t:" + dataString + "&chdl=wt|mutant&chco=FF0000,00FF00&chxt=y&chtt=" + node.getIdentifier();
					CyCustomGraphics<?> cg = new URLImageCustomGraphics(url);
					pool.addGraphics(cg.hashCode(), cg);
					nodeGraphics.putMapValue(node.getIdentifier(), cg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

//		}
	}
	
	private String createData() {
		StringBuilder builder = new StringBuilder();
		for(int j=0; j<2; j++) {
		for(int i=0; i<SIZE; i++) {
			Double rand = Math.abs(Math.random());
			builder.append(rand.toString());
			if(i != SIZE-1)
				builder.append(",");
		}
		if(j != 1)
			builder.append("|");
		}
		return builder.toString();
	}

	private final int SIZE = 5;
	private Random rnd = new Random();
	private String[] nameArray;
}