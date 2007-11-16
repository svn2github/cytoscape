package org.systemsbiology.cytoscape.visual;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cytoscape.*;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;


/**
 * @author skillcoy
 * Class to create a seed of node mappings specifically for movies (handleHashMap()) so something can be
 * shown and the user can customize later.
 */
public class SeedMappings
	{
	private static HashSet<String> MappedAttributes;
	private NodeAppearanceCalculator NAC;
	private int CountMapping = 0;
	
	public SeedMappings(NodeAppearanceCalculator nac)
		{
		if (MappedAttributes == null) MappedAttributes = new HashSet<String>();
		this.NAC = nac;
		}
	
	public void seedMappings(String Attribute, double UpperPoint, double LowerPoint)
		{
		if (MappedAttributes.contains(Attribute) || isNodeAttributeMapped(Attribute)) 
			{
			// this way if it's already present we will only have gone through the calculators to determine that once
			MappedAttributes.add(Attribute);
			//System.out.println(" *** " + Attribute + " is mapped to a calculator, skipping seed");

			return; 
			}
		else
			{
			System.out.println("*** " + Attribute + " NOT mapped, creating seed");
			MappedAttributes.add(Attribute);
			double MidPoint = (UpperPoint + LowerPoint)/2;
			
			switch(CountMapping)
				{
				case 0:
					NAC.setCalculator(
								new GenericNodeFillColorCalculator("GaggleNodeColor_"+Attribute, 
										this.getNodeColor(Attribute, UpperPoint, MidPoint, LowerPoint)
								) );
					break;
				case 1:
					NAC.setCalculator(
							new GenericNodeSizeCalculator("GaggleNodeSize_"+Attribute,
									this.getNodeSize(Attribute, UpperPoint, MidPoint, LowerPoint)
							) );
					break;
				case 2:
					NAC.setCalculator(
							new GenericNodeShapeCalculator("GaggleNodeShape_"+Attribute, 
									this.getNodeShape(Attribute, UpperPoint, MidPoint, LowerPoint)
						) );
					break;
				case 3:
					NAC.setCalculator(
							new GenericNodeLineTypeCalculator("GaggleNodeBorder_"+Attribute,
									this.getNodeBorderLineType(Attribute, UpperPoint, MidPoint, LowerPoint)
							) );
					break;
				case 4:
					NAC.setCalculator(
							new GenericNodeBorderColorCalculator("GaggleBorderColor_"+Attribute,
									this.getNodeBorderColor(Attribute, UpperPoint, MidPoint, LowerPoint)
							) );
					break;
				}
			CountMapping++;
			}
		}
	

	private boolean isNodeAttributeMapped(String AttributeName)
		{
		boolean HasAttribute = false;
		
		List<Calculator> NodeCalcs = this.NAC.getCalculators();
		Iterator<Calculator> nI = NodeCalcs.iterator();
		while(nI.hasNext())
			{
			Calculator Current = nI.next();
			java.util.Vector<ObjectMapping> AllMaps = Current.getMappings();
			Iterator<ObjectMapping> mI = AllMaps.iterator();
			while(mI.hasNext())
				{
				ObjectMapping Map = mI.next();
				String ControllingAttName = Map.getControllingAttributeName();
				if ( ControllingAttName != null &&
						 ControllingAttName.equalsIgnoreCase(AttributeName) ) HasAttribute = true;
				}
			}
		return HasAttribute;
		}

	
	private ContinuousMapping getNodeColor(String Attribute, double UpperPoint, double MidPoint, double LowerPoint)
		{
	  // colors used in nodes
	  final Color RED = new Color(255, 0, 51); 
	  final Color PINK = new Color(255, 153, 153);
	  final Color GREEN = new Color(0, 255, 0);
	  final Color LT_GREEN = new Color(153, 255, 153);
	  final Color WHITE = Color.WHITE;
		
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
	
	
	private ContinuousMapping getNodeShape(String Attribute, double UpperPoint, double MidPoint, double LowerPoint)
		{
	  // shapes used
	  final Byte CIRCLE = ShapeNodeRealizer.ROUND_RECT;
	  final Byte SQUARE = ShapeNodeRealizer.RECT;
	  final Byte TRIANGLE = ShapeNodeRealizer.TRIANGLE;
	  final Byte HEXAGON = ShapeNodeRealizer.HEXAGON;
	  final Byte TRAPEZOID = ShapeNodeRealizer.TRAPEZOID;

		ContinuousMapping Map = new ContinuousMapping( new Byte(CIRCLE), ObjectMapping.NODE_MAPPING);
		Map.setControllingAttributeName(Attribute, Cytoscape.getCurrentNetwork(), false);

		BoundaryRangeValues Lower = new BoundaryRangeValues();
  	Lower.equalValue = TRAPEZOID;
  	Lower.lesserValue = SQUARE;
  	Lower.greaterValue = CIRCLE;
  	Map.addPoint(LowerPoint, Lower);
  	
		BoundaryRangeValues Equal = new BoundaryRangeValues();
  	Equal.equalValue = CIRCLE;
  	Equal.greaterValue = TRIANGLE;
  	Equal.lesserValue = TRAPEZOID;
  	Map.addPoint(MidPoint, Equal);

		BoundaryRangeValues Upper = new BoundaryRangeValues();
		Upper.equalValue = TRIANGLE;
		Upper.greaterValue = HEXAGON;
		Upper.lesserValue = CIRCLE;
  	Map.addPoint(UpperPoint, Upper);
  	
		return Map;
		}
	
	
	private ContinuousMapping getNodeSize(String Attribute, double UpperPoint, double MidPoint, double LowerPoint)
		{
	  // sizes used
		final Double High = new Double(105);
		final Double MidHigh = new Double(70);
	  final Double Mid = new Double(35);
	  final Double MidLow = new Double(50);
	  final Double Low = new Double(15);

		ContinuousMapping Map = new ContinuousMapping( new Double(35), ObjectMapping.NODE_MAPPING);
		Map.setControllingAttributeName(Attribute, Cytoscape.getCurrentNetwork(), false);
		
		BoundaryRangeValues Lower = new BoundaryRangeValues();
  	Lower.equalValue = MidLow;
  	Lower.lesserValue = Low;
  	Lower.greaterValue = Mid;
  	Map.addPoint(LowerPoint, Lower);
  	
		BoundaryRangeValues Equal = new BoundaryRangeValues();
  	Equal.equalValue = Mid;
  	Equal.greaterValue = MidHigh;
  	Equal.lesserValue = MidLow;
  	Map.addPoint(MidPoint, Equal);

		BoundaryRangeValues Upper = new BoundaryRangeValues();
		Upper.equalValue = MidHigh;
		Upper.greaterValue = High;
		Upper.lesserValue = Mid;
  	Map.addPoint(UpperPoint, Upper);
  	
		return Map;
		}

	private ContinuousMapping getNodeBorderLineType(String Attribute, double UpperPoint, double MidPoint, double LowerPoint)
		{
	  // sizes used
		final LineType High = LineType.LINE_7;
		final LineType MidHigh = LineType.DASHED_5;
		final LineType Mid = LineType.LINE_4;
		final LineType MidLow = LineType.DASHED_3;
		final LineType Low = LineType.LINE_1;
		
		ContinuousMapping Map = new ContinuousMapping( LineType.LINE_4, ObjectMapping.NODE_MAPPING);
		Map.setControllingAttributeName(Attribute, Cytoscape.getCurrentNetwork(), false);
		
		BoundaryRangeValues Lower = new BoundaryRangeValues();
  	Lower.equalValue = MidLow;
  	Lower.lesserValue = Low;
  	Lower.greaterValue = Mid;
  	Map.addPoint(LowerPoint, Lower);
  	
		BoundaryRangeValues Equal = new BoundaryRangeValues();
  	Equal.equalValue = Mid;
  	Equal.greaterValue = MidHigh;
  	Equal.lesserValue = MidLow;
  	Map.addPoint(MidPoint, Equal);

		BoundaryRangeValues Upper = new BoundaryRangeValues();
		Upper.equalValue = MidHigh;
		Upper.greaterValue = High;
		Upper.lesserValue = Mid;
  	Map.addPoint(UpperPoint, Upper);
  	
		return Map;
		}

	private ContinuousMapping getNodeBorderColor(String Attribute, double UpperPoint, double MidPoint, double LowerPoint)
		{
	  // colors used in nodes
	  final Color BLUE = new Color(0, 0, 204); 
	  final Color PURPLE = new Color(204, 0, 204);
	  final Color YELLOW = new Color(255, 255, 0);
	  final Color ORANGE = new Color(255, 153, 0);
	  final Color BLACK = Color.BLACK;

		ContinuousMapping Map = new ContinuousMapping( BLACK, ObjectMapping.NODE_MAPPING);
		Map.setControllingAttributeName(Attribute, Cytoscape.getCurrentNetwork(), false);
		
		BoundaryRangeValues Lower = new BoundaryRangeValues();
  	Lower.equalValue = PURPLE;
  	Lower.lesserValue = BLUE;
  	Lower.greaterValue = BLACK;
  	Map.addPoint(LowerPoint, Lower);
  	
		BoundaryRangeValues Equal = new BoundaryRangeValues();
  	Equal.equalValue = BLACK;
  	Equal.greaterValue = ORANGE;
  	Equal.lesserValue = PURPLE;
  	Map.addPoint(MidPoint, Equal);

		BoundaryRangeValues Upper = new BoundaryRangeValues();
		Upper.equalValue = ORANGE;
		Upper.greaterValue = YELLOW;
		Upper.lesserValue = BLACK;
  	Map.addPoint(UpperPoint, Upper);
  	
		return Map;
		}
	

	
	}
