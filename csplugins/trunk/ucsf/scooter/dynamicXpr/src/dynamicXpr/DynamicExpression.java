/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/

/**
 * A class that allows the user to see the changes in gene expression by
 * coloring nodes according to their expression values in different conditions.
 * It colors the nodes sequentially through each condition at a user set delay
 * between conditions.
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.net
 * @version %I%, %G%
 * @since 1.1
 */

// TODO: LEFT HERE Handle meta-nodes!!!!
package dynamicXpr;

import java.util.*;
import javax.swing.*;
import java.awt.Color;
import javax.swing.Timer;
import java.awt.event.*;
import java.io.*;

import cytoscape.*;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.*;
import cytoscape.visual.mappings.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.VisualPropertyType;
import cytoscape.data.*;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;

import dynamicXpr.dialogs.*;
import giny.model.*;

public class DynamicExpression extends AbstractAction {

	/**
	 * The node attribute name that is used to assign an expression value to
	 * each node.
	 */
	public static final String EXPRESSION_ATTR = "expression";
	
	/**
	 * The node attribute name that is used to assign significance values to
	 * each node
	 */
	public static final String SIGNIFICANCE_ATTR = "significance";

	/**
	 * The node attribute name that is used to assign an expression
	 * value/profile to each meta-node in the network.
	 */
	public static final String METANODE_EXPRESSION_ATTR = "meta_node_expression";

	/**
	 * The name of the node color calculator used to color nodes according to
	 * expression
	 */
	public static final String NODE_COLOR_CALC_NAME = "dynamicXpr";

	protected Timer timer;

	protected DisplayFramesAction action;

	protected DynamicExpressionDialog dialog;

	protected int pause;

	protected File currentDirectory;

	protected Calculator oldNodeColorCalculator;

	protected Calculator dynamicXprCalculator;

	protected HashMap oldFillColorAttr;

	protected String fillColorOverride;

	/**
	 * Constructor.
	 */
	public DynamicExpression() {
		super("Dynamic Expression...");
		this.currentDirectory = new File(System.getProperty("user.dir"));
		this.pause = -1;
		fillColorOverride = VisualPropertyType.NODE_FILL_COLOR.getBypassAttrName();
	}

	/**
	 * Constructor.
	 * 
	 * @param parent_menu
	 *            the JMenu to which this instance will be added if null, the
	 *            instance won't be added anywhere
	 */
	public DynamicExpression(JMenu parent_menu) {
		this();
		if (parent_menu != null) {
			parent_menu.add(this);
		}
	}// DynamicExpression

	/**
	 * Fills <code>oldFillColorAttr</code> member variable with
	 * <code>CyNode</code> keys and the values returned for the
	 * <code>NodeAppearanceCalculator.nodeFillColorBypass</code> node
	 * attribute.
	 */
	protected void saveFillColor() {
		CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
		if (this.oldFillColorAttr == null) {
			this.oldFillColorAttr = new HashMap();
		} else {
			this.oldFillColorAttr.clear();
		}

		Iterator it = cyNetwork.nodesIterator();
		while (it.hasNext()) {
			CyNode node = (CyNode) it.next();
			Object value = Cytoscape.getNodeAttributes().getStringAttribute(node.getIdentifier(), fillColorOverride);
			this.oldFillColorAttr.put(node, value);
		}// for i

	}// saveFillColor

	/**
	 * Get ready for playing the movie.
	 */
	protected void prepareBeforePlay() {
		saveFillColor();
		
		Cytoscape.getNodeAttributes().deleteAttribute(fillColorOverride);

		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		NodeAppearanceCalculator nac = 
			manager.getVisualStyle().getNodeAppearanceCalculator();
		
		// See if there is already a dynamic expression calculator in the catalog
		Calculator check = nac.getCalculator(VisualPropertyType.NODE_FILL_COLOR);
		
		if(check != null){
			this.dynamicXprCalculator = check;
		}else if (this.dynamicXprCalculator == null) {
			createCalculator();
		}
		
		// Make sure we are not getting our own node color calculator:
		Calculator nodeColorCalc = nac.getCalculator(VisualPropertyType.NODE_FILL_COLOR);
		if(this.dynamicXprCalculator != null && nodeColorCalc != this.dynamicXprCalculator){
			this.oldNodeColorCalculator = nodeColorCalc;
		}
		
		nac.setCalculator(this.dynamicXprCalculator);

		prepareMetaNodes();
	}// prepareBeforePlay

	/**
	 * Assigns expression profiles to the meta-nodes in the current network.
	 */
	protected void prepareMetaNodes() {

		System.err.println("Preparing  meta-nodes for dynamic expression...");
		List<CyGroup> metaNodes = 
			CyGroupManager.getGroupList(CyGroupManager.getGroupViewer("metaNode"));
		
		if (metaNodes == null || metaNodes.size() == 0) {
			// No recorded meta-nodes for the current network
			System.err.println("... no metanodes present in network.");
			return;
		}

		assignAverageExpressionToMetaNodes(metaNodes);

		System.err.println("...done preparing meta-nodes for dynamic expression.");

	}// prepareMetaNodes

	/**
	 * Calculates the average expression for each condition of meta-node's
	 * members to create an average profile of expression for each meta-node. It
	 * stores the resulting Double [] of average expressions as a meta-node
	 * attribute called DynamicExpression.METANODE_EXPRESSION_ATTR.
	 * 
	 * @param meta_node_list a List of CyNodes that are meta-nodes
	 */
	protected void assignAverageExpressionToMetaNodes(List<CyGroup> meta_node_list) {
		
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		RootGraph rootGraph = currentNetwork.getRootGraph();
		ExpressionData expData = Cytoscape.getExpressionData();

		if (currentNetwork == null || expData == null) {
			return;
		}
		
		String[] conditions = expData.getConditionNames();

		// Get the children of each meta-node and average their expression ratio
		// for each condition
		Iterator<CyGroup> it = meta_node_list.iterator();
		while (it.hasNext()) {
			CyGroup metaGroup = it.next();
			CyNode metaNode = metaGroup.getGroupNode();
			List <CyNode> children = metaGroup.getNodes();
			if (children == null) {
				System.err.println("Meta-node [" + metaNode + "] has no children!");
				continue;
			}
			
			java.util.List averagedExpression = new ArrayList();
			Double zero = new Double(0);
			for(int i = 0; i < conditions.length; i++) averagedExpression.add(zero);
			
			int numChildrenWithRatios = 0;

			Iterator <CyNode> nodeIter = children.iterator();
			while (nodeIter.hasNext()) {
				CyNode node = nodeIter.next();
				String uid = node.getIdentifier();
				if (uid == null) {
					System.err
							.println("DynamicExpression.displayCondition, no unique identifier for node "
									+ node);
					continue;
				}
				Vector measurements = expData.getMeasurements(uid);
				if (measurements == null || measurements.size() == 0) {
					continue;
				}
				numChildrenWithRatios++;
				for (int c = 0; c < conditions.length; c++) {
					mRNAMeasurement measurement = 
						expData.getMeasurement(uid,conditions[c]);
					if (measurement == null) {
						continue;
					}
					double ratio = measurement.getRatio();
					
					Double oldValue = (Double)averagedExpression.get(c);	
					averagedExpression.set(c, new Double(oldValue.doubleValue() + ratio));
				}// for c
			}// for j
			
			if (numChildrenWithRatios > 1) {
				for (int k = 0; k < averagedExpression.size(); k++) {
					averagedExpression.set(k,new Double( ((Double)averagedExpression.get(k)).doubleValue()/ numChildrenWithRatios));
				}// for k
			}// if numChildrenWithRatios > 0
			
			// Set the profile in the meta-node's attributes
			Cytoscape.getNodeAttributes().setAttributeList(metaNode.getIdentifier(), DynamicExpression.METANODE_EXPRESSION_ATTR, averagedExpression);
		}// for i
	}// assignAverageExpressionToMetaNodes

	/**
	 * Called when a new <code>ExpressionData</code> object is loaded.
	 */
	public void adjustForNewExpressionData() {
		prepareBeforePlay();
	}// adjustForNewExpressionData

	/**
	 * Returns true if the node attributes has an attribute called
	 * <code>DynamicExpression.EXPRESSION_ATTR</code>.
	 */
	public boolean expressionAttributeExists() {
		String[] attNames = Cytoscape.getNodeAttributes().getAttributeNames();
		for (int i = 0; i < attNames.length; i++) {
			if (attNames[i].equals(EXPRESSION_ATTR)) {
				return true;
			}
		}
		return false;
	}// expressionAttributesExists

	/**
	 * @return the node color calculator used for coloring nodes according to
	 *         their <code>EXPRESSION_ATTR</code> values.
	 */
	public Calculator getDynamicXprColorCalculator() {
		return this.dynamicXprCalculator;
	}// getDynamicXprColorCalculator

	/**
	 * Creates the <code>NodeColorCalculator</code> that will be used to
	 * calculate the fill color of nodes.
	 */
	// TODO: This hard-codes the boundary values for the calculator. Maybe make
	// a more inteligent calculator for this
	// that looks at the input ratios.
	protected Calculator createCalculator() {

		ContinuousMapping contMapping = new ContinuousMapping(new Color(204,
				204, 204), ObjectMapping.NODE_MAPPING);
		contMapping.setControllingAttributeName(EXPRESSION_ATTR, Cytoscape
				.getCurrentNetwork(), true);
		BoundaryRangeValues brVals;
		brVals = new BoundaryRangeValues();
		// blue
		brVals.lesserValue = new Color(0, 0, 255);
		brVals.equalValue = new Color(0, 0, 255);
		brVals.greaterValue = new Color(0, 0, 255);
		contMapping.addPoint(-1, brVals);

		// white
		brVals = new BoundaryRangeValues();
		brVals.lesserValue = new Color(255, 255, 255);
		brVals.equalValue = new Color(255, 255, 255);
		brVals.greaterValue = new Color(255, 255, 255);
		contMapping.addPoint(0.0, brVals);

		// red
		brVals = new BoundaryRangeValues();
		brVals.lesserValue = new Color(255, 0, 0);
		brVals.equalValue = new Color(255, 0, 0);
		brVals.greaterValue = new Color(255, 0, 0);
		contMapping.addPoint(1.0, brVals);

		this.dynamicXprCalculator = new BasicCalculator("Node Color Calc",
				                                            contMapping,
                                                    VisualPropertyType.NODE_FILL_COLOR);
		return this.dynamicXprCalculator;
	}// createCalculator

	/**
	 * Pops up the <code>DynamicExpressionDialog</code>.
	 */
	public void actionPerformed(ActionEvent event) {
		if (dialog == null) {
			dialog = new DynamicExpressionDialog(this, "Dynamic Expression");
		}
		prepareBeforePlay();
		dialog.pack();
		dialog.setLocationRelativeTo(Cytoscape.getDesktop());
		// dialog.setResizable(false);
		dialog.setVisible(true);
	}// actionPerformed

	/**
	 * Restores the old node color calculator.
	 */
	public void restoreOldNodeColorCalculator() {
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		NodeAppearanceCalculator nac = manager.getVisualStyle()
				.getNodeAppearanceCalculator();
		nac.setCalculator(this.oldNodeColorCalculator);
		if (this.oldFillColorAttr != null) {
			Set keyset = this.oldFillColorAttr.keySet();
			CyNode[] nodes = (CyNode[]) keyset
					.toArray(new CyNode[keyset.size()]);
			CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
			for (int i = 0; i < nodes.length; i++) {
				Object value = this.oldFillColorAttr.get(nodes[i]);
				nodeAtts.setAttribute(nodes[i].getIdentifier(), fillColorOverride, (String)value);
			}// for i
		}
		Cytoscape.getCurrentNetworkView().redrawGraph(false, true); // don't do
																	// layout,
																	// do apply
																	// vizmaps
	}// restoreOldNodeColorCalculator

	/**
	 * Dynamically displays the gene expression data contained in CyWindow See
	 * loadExpressionData() in CyWindow
	 * 
	 * @param delay
	 *            how long (in milliseconds) should it take for frames to change
	 *            (each frame being one condition)
	 */
	public void play(int delay) {
		prepareBeforePlay();
		ExpressionData expressionData = Cytoscape.getExpressionData();
		if (expressionData == null) {
			return;
		}
		if (this.action == null) {
			this.action = new DisplayFramesAction(expressionData
					.getConditionNames());
		}
		if (this.timer != null) {
			this.timer.stop();
		}
		this.timer = new Timer(delay, this.action);
		this.timer.setInitialDelay(0);
		this.timer.start();
		this.pause = 0;
	}// display

	/**
	 * Sets the delay in milliseconds for the next frame to appear
	 */
	public void setTimerDelay(int delay) {
		if (this.timer == null) {
			return;
		}
		this.timer.setDelay(delay);
	}// setTimerDelay

	/**
	 * Displays the given condition in the current CyNetwork
	 * 
	 * @param conditionName
	 *            the name of the condition to display
	 * @param conditionIndex
	 *            the index of the condition to display (used for meta-nodes)
	 */
	public static void displayCondition (String conditionName, int conditionIndex) {
		
		CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
		ExpressionData expressionData = Cytoscape.getExpressionData();
		
		if(expressionData == null || cyNetwork == null)
			return;
		
		CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
		
		Iterator it = cyNetwork.nodesIterator();
		
		while (it.hasNext()) {
			CyNode node = (CyNode) it.next();
			String uid = node.getIdentifier();
			if (uid == null) {
				System.err
						.println("DynamicExpression.displayCondition, no canonical name for node "
								+ node);
				continue;
			}
			mRNAMeasurement measurement = 
				expressionData.getMeasurement(uid,conditionName);
			double ratio;
			double significance;
			boolean hasSig = expressionData.hasSignificanceValues();
			
			if (measurement == null) {
				java.util.List metaNodeProfile = nodeAtts.getAttributeList(node.getIdentifier(),DynamicExpression.METANODE_EXPRESSION_ATTR); 
				if (metaNodeProfile == null) {
					continue;
				}
				Double Ratio = (Double)metaNodeProfile.get(conditionIndex);
				if(Ratio == null) {
					continue;
				}
				ratio = Ratio.doubleValue();
			} else {
				ratio = measurement.getRatio();	
			}
			nodeAtts.setAttribute(node.getIdentifier(), EXPRESSION_ATTR, new Double(ratio));
			
			if(hasSig && measurement != null){
				significance = measurement.getSignificance();
				nodeAtts.setAttribute(node.getIdentifier(), SIGNIFICANCE_ATTR, new Double(significance));
			}
				
		}// while it
		
		VisualMappingManager vmManager = 
			Cytoscape.getVisualMappingManager();
		vmManager.applyNodeAppearances();
		// vmManager.applyNodeFillColor(); // just apply color calculator
		// instead of all node apps!
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		if(view != null)
			view.redrawGraph(false, false);//needed???
	}// displayCondition

	/**
	 * Stops the current animation
	 */
	public void stop() {
		if(this.timer != null)
			this.timer.stop();
		if(this.action != null)
			this.action.reset();
		this.pause = -1;
	}// stop

	/**
	 * Pauses the current animation
	 */
	public void pause() {
		if (this.pause == 1) {
			// we were in pause, so user wants to continue play
			if(this.timer != null)
				this.timer.start();
			this.pause = 0;
		} else if (this.pause == 0) {
			// we were not in pause, so user wants to pause play
			if(this.timer != null)
				this.timer.stop();
			this.pause = 1;
		}
	}// pause

	/**
	 * Returns whether or not the display is currently paused, or if the
	 * animation is not playing. 0 : not paused and playing 1 : paused -1 : not
	 * playing
	 */
	public int isPaused() {
		return this.pause;
	}// isPaused

	// ------------------- internal classes --------------------//
	class DisplayFramesAction implements ActionListener {

		String[] conditions;

		int currCond;

		DisplayFramesAction (String[] conditions) {
			this.conditions = conditions;
			this.currCond = 0;
		}// DisplayFramesAction

		public void actionPerformed (ActionEvent evt) {

			if (currCond < conditions.length) {
				dialog.updateConditionsSlider(currCond, conditions[currCond]);
				DynamicExpression.displayCondition(conditions[currCond],
						currCond);
				currCond++;
			} else {
				DynamicExpression.this.stop();
				dialog.conditionsSliderEnabled(true);
			}
		}// actionPerformed

		public void reset() {
			currCond = 0;
		}
	}// DisplayFramesAction

}// DynamicExpression
