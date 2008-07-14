

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
	ArrayList validCriteria = new ArrayList();
	ArrayList orderedOperations = new ArrayList();
	String currentOperator = "";
	HashMap map = null;
	Iterator t = null;
	int p = 0;
	
	
	public BooleanCalculator(){
		p=0;
		
	}

	public String doCalculation(String numericalCriteria) {
		for(int i=0; i<numericalCriteria.length(); i++){
			//if( 
			ArrayList operations = new ArrayList();
			ArrayList letters = new ArrayList();
			ArrayList values = new ArrayList();
			String parse = "" + numericalCriteria.charAt(i);
			if( parse.matches("[<>=]")){
				String parse1 = "" + numericalCriteria.charAt(i+1);
				if( parse1.matches("=")){
					values.add(letters);
					operations.add(parse+parse1);
				}else{
					values.add(letters);
					operations.add(parse);	
				}
			}else{
				letters.add(parse);
			}
			
				
		}
		return "valid";
	}
	
	
	
	public boolean checkCriteria(String criteria){
		
		
		
		
		
		p++;
		//Pattern p = Pattern.compile("\\[(.*)\\]([<>=]+)\\[(.*)\\]"); 
		//System.out.println("hey");
		Pattern and = Pattern.compile(".*\\]AND\\[.*");
		Pattern or = Pattern.compile(".*\\]OR\\[.*");
		Pattern not = Pattern.compile(".*\\]NOT\\[.*");
		Matcher mand = and.matcher(criteria);
		Matcher mor = or.matcher(criteria);
		Matcher mnot = not.matcher(criteria);
		if(mand.matches()){
			currentOperator = "AND";
			String[] temp = criteria.split("\\]AND\\[");
			for(int i=0;i<temp.length;i++){
				
				if(!temp[i].contains("]OR[") && !temp[i].contains("]NOT[")){
					validCriteria.add(temp[i]);
					orderedOperations.add(currentOperator);
					System.out.println(p+" "+currentOperator+i+" "+temp[i]);
				}else{
					//checkCriteria(temp[i]);
				}
			}
			System.out.println(criteria);
		}
		
		if(mor.matches()){
			currentOperator = "OR";
			String[] temp1 = criteria.split("\\]OR\\[");
			for(int i=0;i<temp1.length;i++){
				if(!temp1[i].contains("]AND[") && !temp1[i].contains("]NOT[")){
					validCriteria.add(temp1[i]);
					orderedOperations.add(currentOperator);
					System.out.println(p+" "+currentOperator+i+" "+temp1[i]);
				}else{
					//checkCriteria(temp1[i]);
				}
			}
		}
		if(mnot.matches()){
			currentOperator = "NOT";
			String[] temp2 = criteria.split("\\]NOT\\[");
			for(int i=0;i<temp2.length;i++){
				if(!temp2[i].contains("]OR[") && !temp2[i].contains("]AND[")){
					validCriteria.add(temp2[i]);
					orderedOperations.add(currentOperator);
					System.out.println(p+" "+currentOperator+i+" "+temp2[i]);
				}else{
					//checkCriteria(temp2[i]);
				}
			}
		}
		//validCriteria.add(criteria);
		//orderedOperations.add(currentOperator);
		
		
		
		//System.out.println(p+" "+criteria);
		//System.out.println(p+" "+currentOperator); 
		//Matcher m = p.matcher(criteria);
		 //System.out.println(m.matches());
		 return(true);
	}
	
	
	
	public String[] parseCriteria(String criteria){
		//System.out.println(criteria); 
		Pattern p = Pattern.compile("\\[(.*)\\]([<>=]+)\\[(.*)\\]");
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
		
		//for(t = list.iterator(); t.hasNext(); t.next()){
		for(int p=0; p<network.getNodeCount();p++){
			CyNode node = Cytoscape.getCyNode(list.get(p).toString());
			System.out.println(list.get(p).toString() +" "+ node.getIdentifier());
			
		}
		/*for(int j=3;j<input.length+3;j++){
			int a = 0;
			
			String type = getAttributeType((attributes.getType(input[j-3]));
			if(j%2 == 0){}
			else{
				for(;t.hasNext();t.next()){
					CyNode node = Cytoscape.getCyNode(list.get(a).toString());
					if(attributes.getType(input[j-3]) == 1){
						
					}
					if(attributes.getType(input[j-3]) == 2){}
					if(attributes.getType(input[j-3]) == 3){}
					if(attributes.getType(input[j-3]) == 4){}
					//System.out.println(list.get(j));
					
					
					
					//System.out.println(attributes.getType(input[j-3]));
					//System.out.println(node.getIdentifier());
					Object d = attributes.getAttribute(node.getIdentifier(),input[j-3]);
					System.out.println(d+"hey"+a);
					a++;
				}
			}
		}*/
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

	public String getAttributeType(byte type){
		return "";
		
	}
	
	
}


