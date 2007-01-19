/**
 * 
 */
package org.systemsbiology.cytoscape.visual;

import java.awt.Color;

import cytoscape.*;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;


import cytoscape.visual.ShapeNodeRealizer;


/**
 * @author skillcoy
 * Class to create a seed of node mappings specifically for movies (handleHashMap()) so something can be
 * shown and the user can customize later.
 */
public class BasicMapping
	{
  // colors used in nodes
  private final Color RED = new Color(255, 0, 51); 
  private final Color PINK = new Color(255, 153, 153);
  private final Color GREEN = new Color(0, 255, 0);
  private final Color LT_GREEN = new Color(153, 255, 153);
  private final Color WHITE = Color.WHITE;

  // shapes used
  private final Byte CIRCLE = ShapeNodeRealizer.ROUND_RECT;
  private final Byte SQUARE = ShapeNodeRealizer.RECT;
  private final Byte TRIANGLE = ShapeNodeRealizer.TRIANGLE;
  private final Byte HEXAGON = ShapeNodeRealizer.HEXAGON;
  private final Byte TRAPEZOID = ShapeNodeRealizer.TRAPEZOID;
	
  // sizes used
  private final Double Mid = new Double(35);
  private final Double High = new Double(105);
  private final Double Low = new Double(15);
  
  // obviously these are arbitrary 
  private final double LowerPoint = -0.1;
  private final double MidPoint = 0.0;
  private final double UpperPoint = 0.1;
  
	public ObjectMapping getNewMap(String AttributeName)
		{
		return getNodeColor(AttributeName);
		}
	
	// TODO: implement outlined methods
	
	public ContinuousMapping getMap(String AttributeName, double UpperVal, double LowerVal)
		{
		return null;
		}
	public ContinuousMapping getMap(String AttributeName , int UpperVal, int LowerVal)
		{
		return null;
		}
	
	public DiscreteMapping getMap(String AttributeName, String[] Values)
		{
		return null;
		}
	
	private ContinuousMapping getNodeColor(String Attribute)
		{
		ContinuousMapping Map = new ContinuousMapping(WHITE, ObjectMapping.NODE_MAPPING);
		Map.setControllingAttributeName(Attribute, Cytoscape.getCurrentNetwork(), false);
		
		BoundaryRangeValues Lower = new BoundaryRangeValues();
  	Lower.equalValue = LT_GREEN;
  	Lower.lesserValue = GREEN;
  	Lower.greaterValue = WHITE;
  	Map.addPoint(LowerPoint, Lower);
  	
		BoundaryRangeValues Equal = new BoundaryRangeValues();
  	Equal.equalValue = WHITE;
  	Equal.greaterValue = PINK;
  	Equal.lesserValue = LT_GREEN;
  	Map.addPoint(MidPoint, Equal);

		BoundaryRangeValues Upper = new BoundaryRangeValues();
		Upper.equalValue = PINK;
		Upper.greaterValue = RED;
		Upper.lesserValue = WHITE;
  	Map.addPoint(UpperPoint, Upper);
  	
		return Map;
		}
	
	
	private ContinuousMapping getNodeShape(String Attribute)
		{
		ContinuousMapping Map = new ContinuousMapping( new Byte(CIRCLE), ObjectMapping.NODE_MAPPING);
		
		BoundaryRangeValues Lower = new BoundaryRangeValues();
  	Lower.equalValue = TRAPEZOID;
  	Lower.lesserValue = GREEN;
  	Lower.greaterValue = CIRCLE;
  	Map.addPoint(LowerPoint, Lower);
  	
		BoundaryRangeValues Equal = new BoundaryRangeValues();
  	Equal.equalValue = CIRCLE;
  	Equal.greaterValue = TRIANGLE;
  	Equal.lesserValue = SQUARE;
  	Map.addPoint(MidPoint, Equal);

		BoundaryRangeValues Upper = new BoundaryRangeValues();
		Upper.equalValue = TRIANGLE;
		Upper.greaterValue = HEXAGON;
		Upper.lesserValue = CIRCLE;
  	Map.addPoint(UpperPoint, Upper);
  	
		return Map;
		}
	
	
	private ContinuousMapping getNodeSize(String Attribute)
		{
		ContinuousMapping Map = new ContinuousMapping(WHITE, ObjectMapping.NODE_MAPPING);
		Map.setControllingAttributeName(Attribute, Cytoscape.getCurrentNetwork(), false);
		
		BoundaryRangeValues Lower = new BoundaryRangeValues();
  	Lower.equalValue = LT_GREEN;
  	Lower.lesserValue = GREEN;
  	Lower.greaterValue = WHITE;
  	Map.addPoint(LowerPoint, Lower);
  	
		BoundaryRangeValues Equal = new BoundaryRangeValues();
  	Equal.equalValue = WHITE;
  	Equal.greaterValue = PINK;
  	Equal.lesserValue = LT_GREEN;
  	Map.addPoint(MidPoint, Equal);

		BoundaryRangeValues Upper = new BoundaryRangeValues();
		Upper.equalValue = PINK;
		Upper.greaterValue = RED;
		Upper.lesserValue = WHITE;
  	Map.addPoint(UpperPoint, Upper);
  	
		return Map;
		}

	
	
	}
