/*
 File: WordCloudVisualStyle.java

 Copyright 2010 - The Cytoscape Consortium (www.cytoscape.org)
 
 Code written by: Layla Oesper
 Authors: Layla Oesper, Ruth Isserlin, Daniele Merico
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.csplugins.semanticsummary;

import java.awt.Color;

import cytoscape.CyNetwork;
import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearance;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.Interpolator;
import cytoscape.visual.mappings.LinearNumberToNumberInterpolator;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

/**
 * This contains all of the visual style information for the associated network.
 * @author Layla Oesper
 * @version 1.0
 */

public class WordCloudVisualStyle 
{
	//VARIABLES
	private String networkName;
	private VisualStyle vs;
	private CloudParameters cloudParams;
	
	//CONSTRUCTORS
	
	/**
	 * Basic Constructor
	 * @param string - name of the visual style to create.
	 * @param  name - name of the network this visual style pertains to
	 */
	public WordCloudVisualStyle(String string, String name, CloudParameters params)
	{
		this.networkName = name;
		vs = new VisualStyle(string);
		cloudParams = params;
	}
	
	/**
	 * Creates visual style for this WordCloud network.
	 * @param network - network to apply this visual style
	 * @param name - name of the network, to be appended to attribute names
	 */
	public VisualStyle createVisualStyle(CyNetwork network, String name)
	{
		GlobalAppearanceCalculator globalAppCalc = new GlobalAppearanceCalculator();
		globalAppCalc.setDefaultBackgroundColor(new Color(205, 205, 235));
		
		vs.setGlobalAppearanceCalculator(globalAppCalc);
		
		createEdgeAppearance(network, name);
		createNodeAppearance(network, name);
		
		return vs;
	}
	
	/**
	 * Create edge appearances for this WordCloud network.  Specify edge thickness mapped
	 * to the inputted word co occurence probability.
	 * 
	 * @param network - network to apply this visual style
     * @param name - name to be appended to each of the attribute names
	 */
	private void createEdgeAppearance(CyNetwork network, String name)
	{
		EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator(vs.getDependency());
		
		//Set the default edge appearance
		EdgeAppearance edgeAppear = new EdgeAppearance();
		edgeAppear.set(VisualPropertyType.EDGE_COLOR, new Color(100, 200, 000));
		edgeAppCalc.setDefaultAppearance(edgeAppear);
		
		//Continuous Mapping - set edge line thickness based on the probability ratio
		ContinuousMapping continuousMapping_edgeWidth = new ContinuousMapping(1, ObjectMapping.EDGE_MAPPING);
		continuousMapping_edgeWidth.setControllingAttributeName(name + ":" + CreateCloudNetworkAction.CO_VAL, network, false);
		Interpolator numTonum2 = new LinearNumberToNumberInterpolator();
		continuousMapping_edgeWidth.setInterpolator(numTonum2);
		
		Double under_width = 0.5;
		Double min_width = 1.0;
		Double max_width = 8.0;
		Double over_width = 9.0;
		
		//Create boundary conditions
        BoundaryRangeValues bv4 = new BoundaryRangeValues(under_width, min_width, min_width);
        BoundaryRangeValues bv5 = new BoundaryRangeValues(max_width, max_width, over_width);
        continuousMapping_edgeWidth.addPoint(1.0, bv4);
        continuousMapping_edgeWidth.addPoint(40.0, bv5);
		
        Calculator edgeWidthCalculator = new BasicCalculator(name + "edgesize", continuousMapping_edgeWidth, VisualPropertyType.EDGE_LINE_WIDTH);
        edgeAppCalc.setCalculator(edgeWidthCalculator);

        vs.setEdgeAppearanceCalculator(edgeAppCalc);
	}
	
	/**
	 * Create node appearances for this WordCloud network.  Specify node size and label size
	 * based on the word probability values.
	 * 
	 * @param network - network to apply this visual style
     * @param name - name to be appended to each of the attribute names
	 */
	private void createNodeAppearance(CyNetwork network, String name)
	{
		NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator(vs.getDependency());
		
		//set the default appearance
		NodeAppearance nodeAppear = new NodeAppearance();
        nodeAppear.set(VisualPropertyType.NODE_FILL_COLOR, Color.GRAY);
        nodeAppear.set(VisualPropertyType.NODE_BORDER_COLOR, Color.GRAY);
        nodeAppear.set(VisualPropertyType.NODE_SHAPE, NodeShape.ELLIPSE);
        nodeAppear.set(VisualPropertyType.NODE_SIZE, new Double(35.0));
        nodeAppear.set(VisualPropertyType.NODE_LINE_WIDTH, new Double(4.0));
        nodeAppCalc.setDefaultAppearance(nodeAppear);
        
        //Continuous Mapping - set node size based on the probability value
        ContinuousMapping continuousMapping_size = new ContinuousMapping(35, ObjectMapping.NODE_MAPPING);
        continuousMapping_size.setControllingAttributeName(name + ":" + CreateCloudNetworkAction.WORD_VAL, network, false);
        Interpolator numTonum = new LinearNumberToNumberInterpolator();
        continuousMapping_size.setInterpolator(numTonum);
        
        Integer min = 20;
        Integer max = 65;
        
        //Boundary Conditions
        BoundaryRangeValues bv0 = new BoundaryRangeValues(min, min, min);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(max, max, max);
        
        continuousMapping_size.addPoint(cloudParams.getMinRatio(), bv0);
        continuousMapping_size.addPoint(cloudParams.getMaxRatio(), bv1);
        
        
        Calculator nodeSizeCalculator = new BasicCalculator(name + "size2size", continuousMapping_size, VisualPropertyType.NODE_SIZE);
        nodeAppCalc.setCalculator(nodeSizeCalculator);
        
        //Passthrough for node label
        PassThroughMapping pm = new PassThroughMapping(new String(), "canonicalName");
        Calculator nlc = new BasicCalculator(name + "nodeLabel", pm, VisualPropertyType.NODE_LABEL);
        nodeAppCalc.setCalculator(nlc);
        
        //Label size
        //Continuous Mapping - set node size based on the probability value
        ContinuousMapping continuousMapping_labelSize = new ContinuousMapping(35, ObjectMapping.NODE_MAPPING);
        continuousMapping_labelSize.setControllingAttributeName(name + ":" + CreateCloudNetworkAction.WORD_VAL, network, false);
        Interpolator numTonum2 = new LinearNumberToNumberInterpolator();
        continuousMapping_labelSize.setInterpolator(numTonum2);
        
        min = 12;
        max = 56;
        
        //Boundary Conditions
        bv0 = new BoundaryRangeValues(min, min, min);
        bv1 = new BoundaryRangeValues(max, max, max);
        
        continuousMapping_labelSize.addPoint(cloudParams.getMinRatio(), bv0);
        continuousMapping_labelSize.addPoint(cloudParams.getMaxRatio(), bv1);
        
        
        Calculator labelSizeCalculator = new BasicCalculator(name + "size2label", continuousMapping_labelSize, VisualPropertyType.NODE_FONT_SIZE);
        nodeAppCalc.setCalculator(labelSizeCalculator);
        
        vs.setNodeAppearanceCalculator(nodeAppCalc);
	}
	

}
