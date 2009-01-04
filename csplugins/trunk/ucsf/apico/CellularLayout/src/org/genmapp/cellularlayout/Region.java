package org.genmapp.cellularlayout;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

public class Region {

	// shape and parameters from template
	private String shape;
	private Color color;
	private double centerX;
	private double centerY;
	private double width;
	private double height;
	private double rotation;
	private String attValue;

	// additional parameters not from template
	private String attName;
	private List<String> nestedAttValues; // list of values represented by
											// attValue
	private CyNetworkView myView;
	private List<NodeView> nodeViews;
	private boolean visibleBorder;

	// identifies regions that maximally fill width or height
	private boolean fillWidth = false;
	private boolean fillHeight = false;

	public Region(String shape, String color, double centerX, double centerY,
			double width, double height, double rotation, String attValue) {

		this.shape = shape;
		this.color = Color.decode(color); // decode hexadecimal string into
											// Color
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = width;
		this.height = height;
		this.rotation = rotation;
		this.attValue = attValue;

		// nested terms based on Nathan's GO tree analysis
		if (this.attValue.equals("extracellular"))
			nestedAttValues = Arrays.asList("extracellular", "secreted");
		else if (this.attValue.equals("plasma membrane"))
			nestedAttValues = Arrays.asList("plasma membrane", "cell wall");
		else if (this.attValue.equals("cytoplasm"))
			nestedAttValues = Arrays.asList("cytoplasm", "intracellular");
		else if (this.attValue.equals("nucleus"))
			nestedAttValues = Arrays.asList("nucleus", "nucleolus",
					"nuclear membrane");
		else
			nestedAttValues = Arrays.asList(this.attValue);

		// additional parameters
		this.attName = "BasicCellularComponent"; // hard-coded, for now
		this.myView = Cytoscape.getCurrentNetworkView();

		populateNodeViews();
	}

	private void populateNodeViews() {
		CyAttributes attribs = Cytoscape.getNodeAttributes();
		Iterator it = Cytoscape.getCurrentNetwork().nodesIterator();
		List<NodeView> nodeViews = new ArrayList<NodeView>();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			nodeViews.add(Cytoscape.getCurrentNetworkView().getNodeView(node));
			String val = null;
			String terms[] = new String[1];
			// add support for parsing List type attributes
			if (attribs.getType(attName) == CyAttributes.TYPE_SIMPLE_LIST) {
				List valList = attribs.getListAttribute(node.getIdentifier(),
						attName);
				// iterate through all elements in the list
				if (valList != null && valList.size() > 0) {
					terms = new String[valList.size()];
					for (int i = 0; i < valList.size(); i++) {
						Object o = valList.get(i);
						terms[i] = o.toString();
					}
				}
				val = join(terms);
			} else {
				String valCheck = attribs.getStringAttribute(node
						.getIdentifier(), attName);
				if (valCheck != null && !valCheck.equals("")) {
					val = valCheck;
				}
			}

			// loop through elements in array below and match
			if ((!(val == null) && (!val.equals("null")) && (val.length() > 0))) {
				for (Object o : nestedAttValues) {
					if (val.indexOf(o.toString()) >= 0) {
						nodeViews.add(Cytoscape.getCurrentNetworkView()
								.getNodeView(node));
					}

				}
			} else if (nestedAttValues.get(0).equals("unassigned")) {
				nodeViews.add(Cytoscape.getCurrentNetworkView()
						.getNodeView(node));
			}

		}

	}

	/**
	 * generates comma-separated list as a single string
	 * 
	 * @param values
	 *            array
	 * @return string
	 */
	private static String join(String values[]) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			buf.append(values[i]);
			if (i < values.length - 1) {
				buf.append(", ");
			}
		}
		return buf.toString();
	}

	/**
	 * @return the nodeViews
	 */
	public List<NodeView> getNodeViews() {
		return nodeViews;
	}

	/**
	 * @param nodeViews
	 *            the nodeViews to set
	 */
	public void setNodeViews(List<NodeView> nodeViews) {
		this.nodeViews = nodeViews;
	}

	/**
	 * @return the visibleBorder
	 */
	public boolean isVisibleBorder() {
		return visibleBorder;
	}

	/**
	 * @param visibleBorder
	 *            the visibleBorder to set
	 */
	public void setVisibleBorder(boolean visibleBorder) {
		this.visibleBorder = visibleBorder;
	}

	/**
	 * @return the fillWidth
	 */
	public boolean isFillWidth() {
		return fillWidth;
	}

	/**
	 * @param fillWidth
	 *            the fillWidth to set
	 */
	public void setFillWidth(boolean fillWidth) {
		this.fillWidth = fillWidth;
	}

	/**
	 * @return the fillHeight
	 */
	public boolean isFillHeight() {
		return fillHeight;
	}

	/**
	 * @param fillHeight
	 *            the fillHeight to set
	 */
	public void setFillHeight(boolean fillHeight) {
		this.fillHeight = fillHeight;
	}

	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @return the attValue
	 */
	public String getAttValue() {
		return attValue;
	}

	/**
	 * @return the centerX
	 */
	public double getCenterX() {
		return centerX;
	}

	/**
	 * @return the centerY
	 */
	public double getCenterY() {
		return centerY;
	}

}