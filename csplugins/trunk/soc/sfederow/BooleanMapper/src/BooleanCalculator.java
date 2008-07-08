

import giny.view.NodeView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.*;
import java.util.HashMap;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

public class BooleanCalculator {
	String criteria;
	ArrayList attributeList = new ArrayList();
	HashMap map = null;
	Pattern p;
	
	public BooleanCalculator(){
		p = Pattern.compile("\\[(\\w*)\\]([<>=]+)\\[(\\w*)\\]");
		
	}

	
	public boolean checkCriteria(String criteria){
		 Matcher m = p.matcher(criteria);
		 return(m.matches());
	}
	
	
	
	public String[] parseCriteria(String criteria){
		//System.out.println(criteria); 
		//Pattern p = Pattern.compile("(\\w)([<>=])(\\w)");
		String[] ret = {""};
		 String[] out = p.split(criteria);
		 Matcher m = p.matcher(criteria);
		 boolean b = m.matches();
		 //System.out.println(b);
		 if(b){
			 int count = m.groupCount();
		 
			 ret = new String[count];
			 //System.out.println(count);
			 for(int i=1;i<=count;i++){
				 ret[i-1] = m.group(i);
				 //System.out.println(m.group(i));
			 }
		 }
		 
		
		 return ret;
	}
	
	public void evaluateCriteria(String[] input){
		//String[] attributes = getAllAttributes();
		
		
		
		
		CyNetwork network = Cytoscape.getCurrentNetwork();
        
        CyNetworkView view = Cytoscape.getCurrentNetworkView();
        
        if (network == null || view == null) {return;}
        
        CyAttributes attributes = Cytoscape.getNodeAttributes();
		
        
        
		String[] attNames = attributes.getAttributeNames();
		//String attDesc = attributes.getAttributeDescription(attNames[1]);
		//System.out.println(attDesc);
        
		//for(int k=0;k<attNames.length;k++){
		//	System.out.println(attNames[k]);
		//}
		
		
		
		List list = Cytoscape.getCyNodesList();
		
		
		
		List tList = network.nodesList();
		
		Iterator t = list.iterator();
		for(int j=0;j<input.length;j++){
			if(j%2 == 0){}
			else{
				for(;t.hasNext();t.next()){
		
					//System.out.println(list.get(j));
					CyNode node = Cytoscape.getCyNode(list.get(j).toString());
			
					//System.out.println(node.getIdentifier());
					Object d = attributes.getAttribute(node.getIdentifier(),input[j]);
					System.out.println(d);
				}
			}
		}
		Object[] names = list.toArray();
        for(int k=0; k<names.length; k++){
        	//System.out.println(names[k]);
        }
		
		//System.out.println(attributes.toString());
        /*for(int j=0; j<input.length; j++){
        	for(int i=0; i<attNames.length; i++){
        	
        		if(input[j].equals(attNames[i])){
        			
        		}
        	}	
        }*/
        
        view.redrawGraph(false, true);
		
		

		System.out.println("made it");
	}

	public String[] getAllAttributes() {
		// Create the list by combining node and edge attributes into a single
		// list

		getAttributesList(attributeList, Cytoscape.getNodeAttributes(), "");
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes(), "");
		String[] str = (String[]) attributeList.toArray(new String[attributeList.size()]);
		return str;

	}

	
	
	public void getAttributesList(ArrayList attributeList,
			CyAttributes attributes, String prefix) {
		String[] names = attributes.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING
					|| attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER) {
				attributeList.add(prefix + names[i]);
			}
		}
	}

}


