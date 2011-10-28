package org.idekerlab.PanGIAPlugin;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.idekerlab.PanGIAPlugin.data.DoubleVector;

import org.cytoscape.model.CyTable;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import java.util.Iterator;

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
		CyTable nodeAttr = view.getModel().getDefaultNodeTable(); //Cytoscape.getNodeAttributes();
		Map<String,Set<CyNode>> module_nodes = new HashMap<String,Set<CyNode>>();
		
		Iterator<CyNode> nodeIt = view.getModel().getNodeList().iterator();
		while (nodeIt.hasNext()){
			CyNode node = nodeIt.next();
		
			String nodeID = node.getCyRow().get("name", String.class);
			//String parent = nodeAttr.getAttribute(nodeID, VisualStyleObserver.PARENT_MODULE_ATTRIBUTE_NAME).toString();
			String parent = node.getCyRow().get(VisualStyleObserver.PARENT_MODULE_ATTRIBUTE_NAME, String.class);
						
			Set<CyNode> sset = module_nodes.get(parent);
			if (sset==null)
			{
				sset = new HashSet<CyNode>();
				//sset.add(view.getModel().getNode(ni));
				sset.add(node);

				module_nodes.put(parent, sset);
			}
			else
			{	
				//sset.add(view.getModel().getNode(ni));
				sset.add(node);
			}
		}
		
		
		//For each parent module
		CyLayoutAlgorithm fd = ServicesUtil.cyLayoutsServiceRef.getLayout("force-directed");
		fd.setSelectedOnly(true);
		for (Entry<String,Set<CyNode>> e : module_nodes.entrySet())
		{
			//Select all nodes with this attribute value
			//view.getModel().unselectAllNodes();
			Iterator<CyNode> it = view.getModel().getNodeList().iterator();
			while (it.hasNext()){
				it.next().getCyRow().set(CyNetwork.SELECTED, false);
			}
			
			//view.getModel().setSelectedNodeState(e.getValue(), true);
			Set<CyNode> nodeSet = e.getValue();
			Iterator<CyNode> nodeSetIt= nodeSet.iterator();
			while (nodeSetIt.hasNext()){
				nodeSetIt.next().getCyRow().set(CyNetwork.SELECTED, true);
			}
			
			//Perform force-directed layout of just the selected
			
			//fd.getSettings().updateValues();
			//fd.updateSettings();
			
			fd.setNetworkView(view);
			ServicesUtil.taskManagerServiceRef.execute(fd);
			
			view.updateView();
		}
		fd.setSelectedOnly(false);
		
		//view.getModel().unselectAllNodes();
		Iterator<CyNode> it = view.getModel().getNodeList().iterator();
		while (it.hasNext()){
			it.next().getCyRow().set(CyNetwork.SELECTED, false);
		}

		
		//Get the meanPosition and radius of each group
		List<String> moduleList = new ArrayList<String>(module_nodes.keySet());
		double[] centerX = new double[moduleList.size()];
		double[] centerY = new double[moduleList.size()];
		double[] radius = new double[moduleList.size()];
		
		for (int i=0;i<moduleList.size();i++)
		{
			Set<CyNode> nodes = module_nodes.get(moduleList.get(i));
			
			if (nodes.size()<=1)
			{
				radius[i] = 10;
				continue;
			}
			
			double minX = Double.MAX_VALUE;
			double maxX = Double.MIN_VALUE;
			double minY = Double.MAX_VALUE;
			double maxY = Double.MIN_VALUE;
			
			for (CyNode n : nodes)
			{
				View<CyNode> nv = view.getNodeView(n); 
				double x = nv.getVisualProperty(MinimalVisualLexicon.NODE_X_LOCATION);
				double y= nv.getVisualProperty(MinimalVisualLexicon.NODE_X_LOCATION);
				
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
		Map<String,View<CyNode>> module_overviewNode = new HashMap<String,View<CyNode>>(module_nodes.size(),1);
		
		@SuppressWarnings("rawtypes")
		//List overviewNodes = overview.getSelectedNodes();
		List<CyNode> overviewNodes =  CyTableUtil.getNodesInState(overview.getModel(), "selected", true);
		
		for (String mod : module_nodes.keySet())
			for (Object n : overviewNodes)
				if (((View<CyNode>)n).getModel().getCyRow().get("name", String.class).equals(mod))
				{
					module_overviewNode.put(mod, (View<CyNode>)n);
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
			View<CyNode> on = module_overviewNode.get(moduleList.get(i));
			double x = on.getVisualProperty(MinimalVisualLexicon.NODE_X_LOCATION);
			double y= on.getVisualProperty(MinimalVisualLexicon.NODE_X_LOCATION);
			
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
			for (CyNode n : module_nodes.get(moduleList.get(i)))
			{
				View<CyNode> on = view.getNodeView(n);

				double x = on.getVisualProperty(MinimalVisualLexicon.NODE_X_LOCATION);
				double y= on.getVisualProperty(MinimalVisualLexicon.NODE_X_LOCATION);

				x = x + shiftX[i];				
				y = y+ shiftY[i];
				
				on.setVisualProperty(MinimalVisualLexicon.NODE_X_LOCATION, x);
				on.setVisualProperty(MinimalVisualLexicon.NODE_Y_LOCATION, y);				
			}
		}
		
		view.fitContent();
		view.updateView();
	}
}
