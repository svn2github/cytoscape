package csplugins.layout.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.LayoutTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.CyTableManager;

public class GroupAttributesLayoutTask extends LayoutTask {

	//@Tunable(description="Horizontal spacing between two partitions in a row")
	public double spacingx;
	//@Tunable(description="Vertical spacing between the largest partitions of two rows")
	public double spacingy;
	//@Tunable(description="Maximum width of a row")
	public double maxwidth;
	//@Tunable(description="Minimum width of a partition")
	public double minrad;
	//@Tunable(description="Scale of the radius of the partition")
	public double radmult;
	//@Tunable(description="The attribute to use for the layout")
	public String attributeName;
	//@Tunable(description="The namespace of the attribute to use for the layout")
	public String attributeNamespace;
	
	private TaskMonitor taskMonitor;	
	private CyNetwork network;
	private CyTableManager tableMgr;
	
	public GroupAttributesLayoutTask(final CyNetworkView networkView, final String name,
			  final boolean selectedOnly, final Set<View<CyNode>> staticNodes,
			  final double spacingx,final double spacingy,final double maxwidth,final double minrad,
			  final double radmult,final String attributeName,final String attributeNamespace, CyTableManager tableMgr)

	{
		super(networkView, name, selectedOnly, staticNodes);
		this.spacingx = spacingx;
		this.spacingy =spacingy;
		this.maxwidth =maxwidth;
		this.minrad = minrad;
		this.radmult =radmult;
		this.attributeName = attributeName;
		this.attributeNamespace =attributeNamespace;
		this.tableMgr = tableMgr;
	}


	
	final protected void doLayout(final TaskMonitor taskMonitor, final CyNetwork network) {
		this.taskMonitor = taskMonitor;
		this.network = network;
		
		construct(); 
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Set<Class<?>> supportsNodeAttributes() {
    	Set<Class<?>> ret = new HashSet<Class<?>>();

   		ret.add(Integer.class);
		ret.add(Double.class);
		ret.add(String.class);
		ret.add(Boolean.class);

    	return ret;
	}

	/**
	 * Sets the attribute to use for the weights
	 *
	 * @param value the name of the attribute
	 */
	public void setLayoutAttribute(String value) {
		if (value == null) {
			attributeName = null;
		} else {
			attributeName = value;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<String> getInitialAttributeList() {
		return null;
	}

	/*
	  Pseudo-procedure:
	  1. Call makeDiscrete(). This will create a map for each value of the
	     node attribute to the list of nodes with that attribute value.
	     Each of these lists will become a partition in the graph.
	     makeDiscrete() will also add nodes to the invalidNodes list
	     that do not have a value associated with the attribute.
	  2. Call sort(). This will return a list of partitions that is
	     sorted based on the value of the attribute. Add the invalid
	     nodes to the end of the sorted list. All the invalid nodes
	     will be grouped together in the last partition of the layout.
	  3. Begin plotting each partition.
	     a. Call encircle(). This will plot the partition in a circle.
	     b. Store the diameter of the last circle plotted.
	     c. Update maxheight. This stores the height of the largest circle
	        in a row.
	     d. Update offsetx. If we've reached the end of the row,
	        reset offsetx and maxheight; update offsety so that
	    it will store the y-axis location of the next row.
	*/
	/**
	 *  DOCUMENT ME!
	 */
	public void construct() {
		
		if (this.attributeName == null){
			System.out.println("\nWarning: GroupAttributesLayoutTask:construct(): attributeName is not defined!\n");
			return;
		}
		
		taskMonitor.setStatusMessage("Initializing");
		//initialize(); // Calls initialize_local

		CyTable dataTable = tableMgr.getTableMap(CyNode.class, network).get(CyNetwork.DEFAULT_ATTRS);
		
		//Class<?> klass = dataTable.getColumnTypeMap().get(attributeName);
		Class klass = dataTable.getColumn(attributeName).getType();
		
		if (Comparable.class.isAssignableFrom(klass)){
			//Class<Comparable<?>>kasted = (Class<Comparable<?>>) klass;
			Class<Comparable>kasted = (Class<Comparable>) klass;
			doConstruct(kasted);
		} else {
			/* FIXME Error! */
		}
	}
	/** Needed to allow usage of parametric types */
	private <T extends Comparable<T>> void doConstruct(Class<T> klass){
		Map<T, List<CyNode>> partitionMap = new TreeMap<T, List<CyNode>>();
		List<CyNode> invalidNodes = new ArrayList<CyNode>();
		makeDiscrete(partitionMap, invalidNodes, klass);

		List<List<CyNode>> partitionList = sort(partitionMap);
		partitionList.add(invalidNodes);

		double offsetx = 0.0;
		double offsety = 0.0;
		double maxheight = 0.0;

		for (List<CyNode> partition : partitionList) {
			if (cancelled)
				return;

			double radius = encircle(partition, offsetx, offsety);

			double diameter = 2.0 * radius;

			if (diameter > maxheight)
				maxheight = diameter;

			offsetx += diameter;

			if (offsetx > maxwidth) {
				offsety += (maxheight + spacingy);
				offsetx = 0.0;
				maxheight = 0.0;
			} else
				offsetx += spacingx;
		}
	}
	
	private <T extends Comparable<T>> void makeDiscrete(Map<T, List<CyNode>> map, List<CyNode> invalidNodes, Class<T> klass) {
		if (map == null)
			return;
		
		for (CyNode node:network.getNodeList()){
			T key = node.getCyRow(attributeNamespace).get(attributeName, klass);

			if (key == null) {
				if (invalidNodes != null)
					invalidNodes.add(node);
			} else {
				if (!map.containsKey(key))
					map.put(key, new ArrayList<CyNode>());

				map.get(key).add(node);
			}
		}
	}

	private <T extends Comparable<T>> List<List<CyNode>> sort(final Map<T, List<CyNode>> map) {
		if (map == null)
			return null;

		List<T> keys = new ArrayList<T>(map.keySet());
		Collections.sort(keys);

		Comparator<CyNode> comparator = new Comparator<CyNode>() {
			public int compare(CyNode node1, CyNode node2) {
				// FIXME: this code was originally comparing node1.getIdentifier() to node2.getIdentifier()
				// I'm not sure that comparing the indices of the nodes gets the same effect
				// on the other hand, nodes don't have a human-readable uid in 3.0
				Integer a = Integer.valueOf(node1.getIndex());
				Integer b = Integer.valueOf(node2.getIndex());

				return a.compareTo(b);
			}
		};

		List<List<CyNode>> sortedlist = new ArrayList<List<CyNode>>(map.keySet().size());

		for (T key : keys) {
			List<CyNode> partition = map.get(key);
			Collections.sort(partition, comparator);
			sortedlist.add(partition);
		}

		return sortedlist;
	}

	private double encircle(List<CyNode> partition, double offsetx, double offsety) {
		if (partition == null)
			return 0.0;

		if (partition.size() == 1) {
			CyNode node = partition.get(0);
			networkView.getNodeView(node).setVisualProperty(MinimalVisualLexicon.NODE_X_LOCATION, offsetx);
			networkView.getNodeView(node).setVisualProperty(MinimalVisualLexicon.NODE_Y_LOCATION, offsety);

			return 0.0;
		}

		double radius = radmult * Math.sqrt(partition.size());

		if (radius < minrad)
			radius = minrad;

		double phidelta = (2.0 * Math.PI) / partition.size();
		double phi = 0.0;

		for (CyNode node : partition) {
			double x = offsetx + radius + (radius * Math.cos(phi));
			double y = offsety + radius + (radius * Math.sin(phi));
			networkView.getNodeView(node).setVisualProperty(MinimalVisualLexicon.NODE_X_LOCATION, x);
			networkView.getNodeView(node).setVisualProperty(MinimalVisualLexicon.NODE_Y_LOCATION, y);
			phi += phidelta;
		}

		return radius;
	}

	
	
	
}
