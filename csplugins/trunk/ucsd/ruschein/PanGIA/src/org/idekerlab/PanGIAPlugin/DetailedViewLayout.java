package org.idekerlab.PanGIAPlugin;

import giny.model.Node;
import giny.view.NodeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.idekerlab.PanGIAPlugin.data.DoubleVector;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;

public class DetailedViewLayout
{
	private static double BUFFER_RATIO = 1.5;
	
	public static void layout(CyNetworkView view, CyNetworkView overview)
	{
		/*
		CyLayoutAlgorithm alg = CyLayouts.getLayout("attributes-layout");
		
		alg.setLayoutAttribute(VisualStyleObserver.PARENT_MODULE_ATTRIBUTE_NAME);
		alg.getSettings().updateValues();
		alg.updateSettings();
		view.applyLayout(alg);
		
		view.redrawGraph(true, true);
		*/
		//Get values of Parent Module attribute
		CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		Map<String,Set<Node>> module_nodes = new HashMap<String,Set<Node>>();
		for (int ni : view.getNetwork().getNodeIndicesArray())
		{
			String nodeID = view.getNetwork().getNode(ni).getIdentifier();
			String parent = nodeAttr.getAttribute(nodeID, VisualStyleObserver.PARENT_MODULE_ATTRIBUTE_NAME).toString();
			
			Set<Node> sset = module_nodes.get(parent);
			if (sset==null)
			{
				sset = new HashSet<Node>();
				sset.add(view.getNetwork().getNode(ni));
				module_nodes.put(parent, sset);
			}else sset.add(view.getNetwork().getNode(ni));
		}
		
		//For each parent module
		CyLayoutAlgorithm fd = CyLayouts.getLayout("force-directed");
		fd.setSelectedOnly(true);
		for (Entry<String,Set<Node>> e : module_nodes.entrySet())
		{
			//Select all nodes with this attribute value
			view.getNetwork().unselectAllNodes();
			view.getNetwork().setSelectedNodeState(e.getValue(), true);
			
			//Perform force-directed layout of just the selected
			
			fd.getSettings().updateValues();
			fd.updateSettings();
			view.applyLayout(fd);
			
			view.redrawGraph(true, true);
		}
		fd.setSelectedOnly(false);
		view.getNetwork().unselectAllNodes();
		
		//Get the meanPosition and radius of each group
		List<String> moduleList = new ArrayList<String>(module_nodes.keySet());
		double[] centerX = new double[moduleList.size()];
		double[] centerY = new double[moduleList.size()];
		double[] radius = new double[moduleList.size()];
		
		for (int i=0;i<moduleList.size();i++)
		{
			Set<Node> nodes = module_nodes.get(moduleList.get(i));
			
			if (nodes.size()<=1)
			{
				radius[i] = 10;
				continue;
			}
			
			double minX = Double.MAX_VALUE;
			double maxX = Double.MIN_VALUE;
			double minY = Double.MAX_VALUE;
			double maxY = Double.MIN_VALUE;
			
			for (Node n : nodes)
			{
				NodeView nv = view.getNodeView(n); 
				double x = nv.getXPosition();
				double y = nv.getYPosition();
				
				if (x<minX) minX = x;
				else if (x>maxX) maxX = x;
				
				if (y<minY) minY = y;
				else if (y>maxY) maxY = y;
			}
			
			centerX[i] = (maxX+minX)/2.0;
			centerY[i] = (maxY+minY)/2.0;
			
			double diffX = maxX-centerX[i]; 
			double diffY = maxY-centerY[i];
			radius[i] = Math.sqrt(diffX*diffX+diffY*diffY);
			
			if (Double.isNaN(radius[i])) radius[i] = 10;
		}
		
		
		//Get the map from module to overview node
		Map<String,NodeView> module_overviewNode = new HashMap<String,NodeView>(module_nodes.size(),1);
		
		@SuppressWarnings("rawtypes")
		List overviewNodes = overview.getSelectedNodes();
		for (String mod : module_nodes.keySet())
			for (Object n : overviewNodes)
				if (((NodeView)n).getNode().getIdentifier().equals(mod))
				{
					module_overviewNode.put(mod, (NodeView)n);
					break;
				}
		
		
		//Get the reference positions. Normalize to current scale
		double minRadius = DoubleVector.min(radius);
		double maxRadius = DoubleVector.max(radius);
		
		double scale = BUFFER_RATIO*(minRadius+maxRadius);
		minRadius = Math.max(minRadius, scale/100);
		scale = BUFFER_RATIO*(minRadius+maxRadius);
		
		for (int i=0;i<radius.length;i++)
			if (radius[i]<minRadius) radius[i] = minRadius;
		
		double[] newCenterX = new double[moduleList.size()];
		double[] newCenterY = new double[moduleList.size()];
				
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		
		for (int i=0;i<radius.length;i++)
		{
			NodeView on = module_overviewNode.get(moduleList.get(i));
			double x = on.getXPosition();
			double y = on.getYPosition();
			
			newCenterX[i] = x;
			newCenterY[i] = y;
			
			if (x<minX) minX = x;
			else if (x>maxX) maxX = x;
			
			if (y<minY) minY = y;
			else if (y>maxY) maxY = y;
		}
		
		if (Math.abs(maxX-minX) < 1e-3) DoubleVector.fill(newCenterX,0);
		else newCenterX = DoubleVector.times(newCenterX, scale / (maxX-minX));
		
		if (Math.abs(maxY-minY) < 1e-3) DoubleVector.fill(newCenterY,0);
		else newCenterY = DoubleVector.times(newCenterY, scale / (maxY-minY));
		
		newCenterX = DoubleVector.meanCenter(newCenterX);
		newCenterY = DoubleVector.meanCenter(newCenterY);
		
		double maxOverlapRatio = Double.MIN_VALUE;
		OverlapRatio:
		while (maxOverlapRatio==Double.MIN_VALUE)
			for (int i=0;i<radius.length;i++)
				for (int j=i+1;j<radius.length;j++)
				{
					double diffX = Math.abs(newCenterX[i]-newCenterX[j]);
					double diffY = Math.abs(newCenterY[i]-newCenterY[j]);
					
					double overlapRatio = BUFFER_RATIO*(radius[i]+radius[j])/Math.sqrt(diffX*diffX+diffY*diffY);
					
					if (diffX<1e-3 || diffY<1e-3)
					{
						//System.out.println(i+", "+j+",  "+diffX+", "+diffY);
						
						if (diffX<1e-3)
						{
							newCenterX[i]-=(radius[i]/2+scale/100);
							newCenterX[j]+=(radius[j]/2+scale/100);
						}
						
						if (diffY<1e-3)
						{
							newCenterY[i]-=(radius[i]/2+scale/100);
							newCenterY[j]+=(radius[j]/2+scale/100);
						}
						
						maxOverlapRatio = Double.MIN_VALUE;
						continue OverlapRatio;
					}
					
					if (overlapRatio>maxOverlapRatio) maxOverlapRatio = overlapRatio;
				}
		
		if (Double.isInfinite(maxOverlapRatio)) maxOverlapRatio = Double.MAX_VALUE/1000;
		
		//System.out.println("Max overlap ratio: "+maxOverlapRatio);
				
		newCenterX = DoubleVector.times(newCenterX, maxOverlapRatio);
		newCenterY = DoubleVector.times(newCenterY, maxOverlapRatio);
		
		//Shift nodes to the new centers
		double[] shiftX = DoubleVector.subtract(newCenterX,centerX);
		double[] shiftY = DoubleVector.subtract(newCenterY,centerY);
		
		for (int i=0;i<radius.length;i++)
		{
			for (Node n : module_nodes.get(moduleList.get(i)))
			{
				NodeView on = view.getNodeView(n);
				on.setXPosition(on.getXPosition()+shiftX[i]);
				on.setYPosition(on.getYPosition()+shiftY[i]);
			}
		}
		
		view.fitContent();
		view.updateView();
	}
}
