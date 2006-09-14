package cytoscape.bubbleRouter;

import giny.model.Node;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class LayoutRegion extends Component {

	private double x1;
	private double y1;
	private double w1;  
	private double h1;
	 
	private Paint paint;
	private List nodeViews;
	private Object attValue;
	

	private static int count = 0;
	private Color[] colors = new Color[]{Color.red, Color.green, Color.blue};
	
	/**
	 * @param value
	 * @param x1
	 * @param y1
	 * @param w1
	 * @param h1
	 */
	public LayoutRegion(Object value, double x1, double y1, double w1, double h1) {
		super();
		// TODO Auto-generated constructor stub
		attValue = value;
		this.x1 = x1;
		this.y1 = y1;
		this.h1 = h1;
		this.w1 = w1;
		
	nodeViews = new ArrayList();
	
	paint = colors[count];
	count = (count == 2)? 0 : count + 1;
	
	}
	
	/**
	 * Empty Constructor
	 *
	 */
	public LayoutRegion() {
		super();

		nodeViews = new ArrayList();
		
		paint = colors[count];
		count = (count == 2)? 0 : count + 1;

	}
	
	
	
	/**
	 * @return Returns the attValue.
	 */
	public Object getAttValue() {
		return attValue;
	}
	/**
	 * @param attValue The attValue to set.
	 */
	public void setAttValue(Object attValue) {
		this.attValue = attValue;
	}

	/**
	 * @return Returns the h1.
	 */
	public double getH1() {
		return h1;
	}

	/**
	 * @param h1 The h1 to set.
	 */
	public void setH1(double h1) {
		this.h1 = h1;
	}

	/**
	 * @return Returns the w1.
	 */
	public double getW1() {
		return w1;
	}

	/**
	 * @param w1 The w1 to set.
	 */
	public void setW1(double w1) {
		this.w1 = w1;
	}

	/**
	 * @return Returns the x1.
	 */
	public double getX1() {
		return x1;
	}

	/**
	 * @param x1 The x1 to set.
	 */
	public void setX1(double x1) {
		this.x1 = x1;
	}

	/**
	 * @return Returns the y1.
	 */
	public double getY1() {
		return y1;
	}

	/**
	 * @param y1 The y1 to set.
	 */
	public void setY1(double y1) {
		this.y1 = y1;
	}

	/**
	 * @return Returns the paint.
	 */
	public Paint getPaint() {
		return paint;
	}
	/**
	 * @param paint The paint to set.
	 */
	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	/**
	 * @return Returns the nodeViews.
	 */
	public List getNodeViews() {
		return nodeViews;
	}
	
	// AJK: 09/02/06 BEGIN
	//    select all nodeViews with specified attribute value for attribute
	public void populateNodeViews (String attributeName)
	{
		CyAttributes attribs = Cytoscape.getNodeAttributes();
		Iterator it = Cytoscape.getCurrentNetwork().nodesIterator();
		Collection selectedNodes = new ArrayList();
		while (it.hasNext())
		{
			Cytoscape.getCurrentNetwork().unselectAllNodes();
			Node node = (Node) it.next();
			String val = attribs.getStringAttribute(
					node.getIdentifier(), attributeName);
			if (val != null)
			{
				if (val.equalsIgnoreCase(this.attValue.toString()))
				{
					selectedNodes.add(node);
				}				
			}
			else if (attValue.equals("unassigned"))
			{
				selectedNodes.add(node);
			}

		}		
		Cytoscape.getCurrentNetwork().setSelectedNodeState(selectedNodes, true);
		System.out.println ("selected " + selectedNodes.size() + " nodes for layout.");
		
	    HierarchicalLayoutListener hierarchicalListener = new HierarchicalLayoutListener();
	    System.out.println ("running hierarchical layout algorithm.");
	    hierarchicalListener.actionPerformed(null);

	    NodeViewsTransformer.transform(Cytoscape.getCurrentNetworkView().getSelectedNodes(), 
	    		new Rectangle2D.Double(x1, y1, w1, h1));
	    
	    Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
	      
	}
	
	public void paint (Graphics g)
	{
		System.out.println ("Paintin region: " + this);
		g.setColor((Color) this.paint);
        g.fillRect((int) x1, (int) y1, (int) w1, (int) h1);
        g.setColor(Color.black);
        g.drawRect((int) x1, (int) y1, (int) w1, (int) h1);
	}
	
	// AJK: 09/02/06 END
	
	
}
