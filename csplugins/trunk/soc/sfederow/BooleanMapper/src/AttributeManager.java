package src;

/*
 * Steve Federowicz 
 * Google Summer of Code
 * 
 * This class contains all of the code for getting, setting, and removing attributes.  
 */

import java.awt.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.CyNetwork;

import giny.model.*;

public class AttributeManager {
	private CyAttributes networkAttributes;
	private CyAttributes nodeAttributes;
	//private SortedSet<String> criteriaSetNames = null;
	private ArrayList<String> criteriaSetNames = new ArrayList<String>();
	
	public AttributeManager(){
		networkAttributes = Cytoscape.getNetworkAttributes();
		nodeAttributes = Cytoscape.getNodeAttributes();
		criteriaSetNames.add("");
		criteriaSetNames.add("New...");
		getAllAttributes();
	}

	/*
	 * The names attribute refers to a list of 'Criteria Set names'.  A 'Criteria Set Name'
	 * is a name which identifies a session that can be saved when creating criteria using 
	 * the criteria builder.
	 */
	public void addNamesAttribute(CyNetwork network, String setName){
		networkAttributes = Cytoscape.getNetworkAttributes();
		System.out.println("setname: "+setName);
		criteriaSetNames = (ArrayList<String>)networkAttributes.getListAttribute(network.getIdentifier(), "__criteria");
		
		if(!(criteriaSetNames.contains(setName))){ 
			criteriaSetNames.add(setName);
		}
		for(int i = 0; i<criteriaSetNames.size(); i++){
			System.out.println("AMITABHA: "+criteriaSetNames.get(i));
		}
		
		networkAttributes.setListAttribute(network.getIdentifier(), "__criteria", criteriaSetNames);
	}
	
	public void removeNamesAttribute(CyNetwork network, String setName){
		criteriaSetNames.remove(setName);
		List temp = (List)criteriaSetNames;
		networkAttributes.setListAttribute(network.getIdentifier(), "__criteria", temp);
	}
	public void setNamesAttribute(CyNetwork network, String[] setNames){
		//System.out.println("SET NAMES ATTRIBUTE!!!");
		networkAttributes = Cytoscape.getNetworkAttributes();
		//if(!networkAttributes.hasAttribute(Cytoscape.getCurrentNetwork().toString(), "Criteria")){
		List<String> temp = null;
		for(int i=0; i<setNames.length; i++){
			temp.add(setNames[i]);
		}
		networkAttributes.setListAttribute(network.getIdentifier(), "__criteria", temp);
		//}
		networkAttributes = Cytoscape.getNetworkAttributes();
	}
	
	
	/*
	 * The values attribute refers to a list of colon separated Strings.  The values separated
	 * by the colons are the criteria, label, and color respectively.  Any number of values, or colon
	 * separated strings can be associated with a setName or 'Criteria Set Name' attribute.
	 */
	public void setValuesAttribute(String setName,String mapTo, String[] criteriaLabelColor){
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(mapTo);
		for(int i=0; i<criteriaLabelColor.length; i++){
			temp.add(criteriaLabelColor[i]);
		}
		networkAttributes.setListAttribute(Cytoscape.getCurrentNetwork().getIdentifier(), setName, temp);
		networkAttributes = Cytoscape.getNetworkAttributes();
	}
	
	public void setColorAttribute(String label, String nodeID, Boolean outcome){
		nodeAttributes.setAttribute(nodeID, label, outcome);
		nodeAttributes = Cytoscape.getNodeAttributes();
	}
	
	//public int getCompositeAttribute(String nodeID, String compositeName){
		//nodeAttributes.getIntegerAttribute(nodeID, compositeName);
		
	//}
	
	/*
	 * This is perhaps one of the most important and confusing pieces of the entire program.  This method
	 * takes an array of user entered labels that each represent some user entered criteria.  It then progressively iterates
	 * through the value at each node for each label.  If the value is already true then 
	 */
	public void setCompositeAttribute(String[] labels) throws Exception {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		List<Node> nodesList = network.nodesList();
		String compositeName = labels[0];
		for(int k=1; k<labels.length; k++){
			if(labels[k].equals("")){ continue; }
			compositeName = compositeName + ":" + labels[k];
		}
		//System.out.println("AttManager: "+compositeName);
		for(int i=0; i<nodesList.size(); i++){
			Node node = nodesList.get(i);
			String nodeID = node.getIdentifier();
			//System.out.println("ggggg");
			for(int j=0; j<labels.length; j++){
				if(labels[j].equals("")){ continue; }
				if(!(nodeAttributes.hasAttribute(nodeID, labels[j]))){
					throw new Exception("ITERATION: "+j+"Node Attribute for node "+nodeID +" at " + labels[j] + " has not been calculated");
				}
				
				if(nodeAttributes.getBooleanAttribute(nodeID, labels[j])){
					
					if(nodeAttributes.hasAttribute(nodeID, compositeName)){ 
						//System.out.println("set attribute "+compositeName+" at node: "+nodeID +" to "+j);
						int b = nodeAttributes.getIntegerAttribute(nodeID, compositeName);
						if( b == -1){
							//System.out.println("set attribute "+compositeName+" at node: "+nodeID +" to "+j);
							nodeAttributes.setAttribute(nodeID, compositeName, j);
						}
					
					}else{
						//System.out.println("possibly overwrote");
						nodeAttributes.setAttribute(nodeID, compositeName, j);
					
					}
				}else{
					if(!(nodeAttributes.hasAttribute(nodeID, compositeName))){
					//System.out.println("set attribute "+compositeName+" at node: "+nodeID +" to -1");
					nodeAttributes.setAttribute(nodeID, compositeName, -1);
					}
				}
				//System.out.println("compositeName: "+compositeName);
				nodeAttributes = Cytoscape.getNodeAttributes();
			}
			
		}
		
	}
	
	public boolean isCompositeAttribute(String compositeName){
		getAllAttributes();
		if(attributeList.contains(compositeName)){
			return true;
		}else{
			return false;
		}
	}
	
	public void removeCompositeAttribute(String compositeName){
		nodeAttributes.deleteAttribute(compositeName);
	}
	
	public String[] getNamesAttribute(CyNetwork network){
		if(networkAttributes.hasAttribute(network.getIdentifier(), "__criteria")){
			String[] a = {""};
			ArrayList<String> temp = (ArrayList<String>)networkAttributes.getListAttribute(network.getIdentifier(), "__criteria");
			
			return temp.toArray(a);
		}else{
			
			return new String[] {"","New..."};
		}
	}
	
	public String[] getValuesAttribute(CyNetwork network, String setName){
		String[] a = {};
		ArrayList<String> temp = (ArrayList<String>)networkAttributes.getListAttribute(network.getIdentifier(), setName);
		if(temp != null){
			return temp.toArray(a);
		}else{
			return a;
		}
	}
	
	public boolean getColorAttribute(String nodeID, String label){
		if(nodeAttributes.hasAttribute(nodeID, label)){
			return nodeAttributes.getBooleanAttribute(nodeID, label);
		}
		return false;
	}
	
	
	public void removeColorAttribute(String label) throws Exception{
		if(!nodeAttributes.deleteAttribute(label)){
			throw new Exception("Could not delete Attribute");
		}
		
	}
	
	ArrayList<String> attributeList;
	public String[] getAllAttributes() {
		// Create the list by combining node and edge attributes into a single
		// list
		attributeList = new ArrayList<String>();
		getAttributesList(attributeList, Cytoscape.getNodeAttributes(), "");
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes(), "");
		
		String[] str = (String[])attributeList.toArray(new String[attributeList.size()]);
		attributeList.clear();
		return str;

	}

	
	public void getAttributesList(ArrayList<String> attributeList,
			CyAttributes attributes, String prefix) {
		String[] names = attributes.getAttributeNames();
		
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING
					 || attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER || attributes.getType(names[i]) == CyAttributes.TYPE_BOOLEAN) {
				
					/*for(int j = 0; j < names[i].length(); j++){
						String temp = names[i].charAt(j) + "";
						if(temp.matches(" ")){
							names[i] = names[i].substring(0,j) + "-" + names[i].substring(j+2, names[i].length());
						}
					}*/
				
				attributeList.add(names[i]);
			}
		}
		
	}
}


