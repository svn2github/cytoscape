package src;

/*
 * Steve Federowicz 
 * Google Summer of Code
 * 
 * This class currently contains all of the code for calculating the outcome
 * of the user entered criteria. 
 */

import giny.model.*;





import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.*;
import java.util.HashMap;
import java.util.Stack;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

public class BooleanCalculator {
	ArrayList<String> attributeList = new ArrayList<String>();
	ArrayList<ArrayList> masterList = new ArrayList<ArrayList>();
	ArrayList<String> values = new ArrayList<String>();
	AttributeManager attManager = new AttributeManager();
	
	HashMap<String, Integer> attributeTypeMap = new HashMap<String, Integer>();
	HashMap<String, String> nodeValueMap = null;
	HashMap<String, Boolean> nodeColorMap = new HashMap<String, Boolean>();
	
	CyAttributes nodeAttributes = null;
	CyAttributes edgeAttributes = null;
	
	
	public BooleanCalculator(){
		
		nodeAttributes = Cytoscape.getNodeAttributes();
		edgeAttributes = Cytoscape.getEdgeAttributes();
		createAttributeTypeHash();
		
	}

	
	public ArrayList[] parse2(String criteria){
		ArrayList<String> valueChars = new ArrayList<String>();
		ArrayList<String> validAttributes = new ArrayList<String>();
		ArrayList<String> operations = new ArrayList<String>();
		String value = "";
		int valueCount = 2;
		
		criteria = criteria + "  ";
		
		for(int i=0;i<criteria.length();i++){
			String parse = "" + criteria.charAt(i);
			
			if(!parse.matches(" ")){
				valueChars.add(parse);
			}
			if(parse.matches(" ") && valueChars.size() != 0){
				for(int k=0;k<valueChars.size();k++){
					value = value + valueChars.get(k);
				}
				valueChars.clear();
				if(valueCount%2 == 0){
					validAttributes.add(value);
					operations.add("");
				}else{
					validAttributes.add("");
					operations.add(value);
					
				}
				valueCount++;
				value = "";
			}
			
		}	
		
		for(int b=2;b<validAttributes.size()+2;b++){
			if(b%2 == 0){
				//System.out.println("b"+validAttributes.get(b-2));
			}
			if(b%2 != 0){
				//System.out.println("a"+operations.get(b-2));
			}
		}
		
		boolean outcome = checkCriteria(validAttributes, operations);
		
		ArrayList<String>[] test = new ArrayList[2];
		if(outcome){
			test[0] = validAttributes;
			test[1] = operations;
			return test;
		}
		return null;
	}
	
	
	/*  Method which does something similiar to an LALR parser.  It parses from left to right and does
	 *  a one or two position look ahead.  
	 */ 
	 
	public boolean checkCriteria(ArrayList<String> validAttributes, ArrayList<String> operations){
		boolean nFlag = false;
		boolean cFlag = true;
		int a = 0;  //correspond to the parsed variables type given by the cytoscape convention where
		int b = 0;  //1 is boolean, 2 is integer and 3 is floating point. 
		int size = validAttributes.size();
		
		nodeAttributes = Cytoscape.getNodeAttributes();
		edgeAttributes = Cytoscape.getEdgeAttributes();
		createAttributeTypeHash();
		
		//System.out.println("size: "+size);
		if (size == 1 && !validAttributes.get(0).equals("")){ return true; }
		for(int i=0; i<size; i++){
			//System.out.println(i);
			if(!validAttributes.get(i).equals("")){
				if(validAttributes.get(i).matches("^[0-9\\.\\-]+")){
					if(validAttributes.get(i).contains(".")){
						a = 2;
					}else{
						a = 3;
					}
				}else{
					if(validAttributes.get(i).equals("true") || validAttributes.get(i).equals("false")){
						a = 1;
					}else{
						a = (Integer)attributeTypeMap.get(validAttributes.get(i));
					}
				}
			}
			if((i+2) < size && !validAttributes.get(i+2).equals("")){
				
				if(validAttributes.get(i+2).matches("^[0-9\\.\\-]+")){
					
					if(validAttributes.get(i+2).contains(".")){
						b = 2;
						//Sytem.out.println("to TWO");
					}else{
						
						b = 3;
					}
				}else{
					if(validAttributes.get(i+2).equals("true") || validAttributes.get(i+2).equals("false")){
						b = 1;
					}else{
						b = (Integer)attributeTypeMap.get(validAttributes.get(i+2));
					}
				}
			}
			if(cFlag && (a == 2 || a == 3) && (i+1) < size && operations.get(i+1).matches("[<>=]+") && (b == 2 || b == 3)){
				nFlag = true;
				cFlag = false;
				i = i + 2;
				
			}else{
				if((a == 1) && (operations.get(i+1).matches("[ANDORT]+") || operations.get(i+1).equals("=")) && (b == 1)){
					nFlag = true;
					cFlag = false;
					i = i + 2;
				
				
				}else{
					if(nFlag && operations.get(i).matches("[ANDORT]+")){
						cFlag = true;
						nFlag = false;
					}else{
						return false;
			
					}
				}
			}
		}
		masterList.add(validAttributes);
		masterList.add(operations);
		//validAttributes.clear();
		//operations.clear();
		return true;
	}
	
	
	public boolean evaluate(String label, ArrayList<String> attributes, ArrayList<String> operations){
		
		CyNetwork network = Cytoscape.getCurrentNetwork();
		List<Node> nodeList = network.nodesList();
		
			int size = attributes.size();
			nodeValueMap = new HashMap<String, String>();

			boolean isInteger = false;
			int criteriaSize = attributes.size();
			int attributeType = 0;
			int numberValueCount = 0;
			for(int i=0;i<nodeList.size();i++){
				//System.out.println("heyNODE");
				CyNode node = (CyNode)nodeList.get(i);
				Node gnode = nodeList.get(i);
				String nodeID = node.getIdentifier();
				numberValueCount = 0;
				for(int b=0;b<criteriaSize;b++){
					isInteger = false;
					String subbed = "";
					attributeType = 0;
					String attribute = attributes.get(b);
					attribute = attribute.trim();

					if(!attribute.equals("")){

						//Only need to get booleans as we get numbers in doNumerical
						if(nodeAttributes.hasAttribute(nodeID, attribute)){
							Object temp = nodeAttributes.getAttribute(nodeID, attribute);
							String stemp = temp + "";
							nodeValueMap.put(attribute, stemp);
						}else{
							if(edgeAttributes.hasAttribute(nodeID, attribute));
							Object temp = edgeAttributes.getAttribute(nodeID, attribute);
							String stemp = temp + "";
							nodeValueMap.put(attribute, stemp); 
						}

					}

				}
				//System.out.println(type);
				evaluateOnce(nodeValueMap, attributes, operations, gnode, attributeType, numberValueCount, label);
				//validAttributes.clear();
				//operations.clear();
				nodeValueMap.clear();
			
		}
		Cytoscape.getCurrentNetworkView().updateView();
		return true;
	}

		
	
	public void evaluateOnce(HashMap nodeValues, ArrayList<String> attributes, ArrayList<String> operations, Node node, int attributeType, int numberCount, String label){
		
		CyNetwork network = Cytoscape.getCurrentNetwork();
	
		Stack<Boolean> finalValue = new Stack<Boolean>();
		boolean nFlag = false;
		boolean cFlag = false;
		String logicalString = "";
		int k = 0;
		int size = attributes.size();
		for(int i=0; i< size; i++){
			
			if((i+2) < size && operations.get(i+1).matches("[<>=]+")){
	
				boolean comparisonOutcome = false;
				comparisonOutcome = doNumericalOperation(i, attributes.get(i), attributes.get(i+2), nodeValues, operations, node);
				
				logicalString = logicalString + comparisonOutcome;
				finalValue.push(comparisonOutcome);
				i = i +2;
			}else{
				if((i+2) < size && operations.get(i+1).matches("[ANDORT]+")){
					logicalString = logicalString + attributes.get(i) + operations.get(i+1);
					finalValue.push((Boolean)nodeValues.get(attributes.get(i)));			
					i++;
				}else{
					if(operations.get(i).matches("[ANDORT]+")){
						if(i+3 < size && operations.get(i+2).matches("[ANDORT]+")){
							boolean temp = finalValue.pop();
							boolean temp2 = (Boolean)nodeValues.get(attributes.get(i+1));
							boolean outcome = doBooleanOperation(operations.get(i), temp, temp2);
							finalValue.push(outcome);
							i++;
						}else{
							if(i+2 == size){
								//logicalString = logicalString + attributes.get(i+1);
								boolean temp = finalValue.pop();
								boolean outcome = doBooleanOperation(operations.get(i), temp, (Boolean)nodeValues.get(attributes.get(i+1)));
								finalValue.push(outcome);
								i++;
							}else{
								if(i+3 < size && operations.get(i+2).matches("[<>=]+")){
									boolean temp = doNumericalOperation(i+1, attributes.get(i+1), attributes.get(i+3), nodeValues, operations, node);
									boolean temp2 = finalValue.pop();
									boolean outcome = doBooleanOperation(operations.get(i), temp, temp2);
									finalValue.push(outcome);
									i = i + 3; 
								}
							}
						}
					}
				}
			}
		}
		if(!finalValue.isEmpty()){
			boolean outcome = finalValue.pop();
			
			attManager.setColorAttribute(label, node.getIdentifier(), outcome);
			if(outcome){
				//network.setSelectedNodeState(node,true);
			}
		}
	}
	
	public boolean doBooleanOperation(String operation, boolean firstValue, boolean secondValue){
		if(operation.matches("AND")){
			if(firstValue && secondValue){
				return true;
			}else{
				return false;
			}
		}else{
			if(operation.matches("OR")){
				if(firstValue || secondValue){
					return true;
				}else{
					return false;
				}
			}else{
				if(operation.matches("NOT")){
					if(firstValue && !secondValue){
						return true;
					}else{
						return false;
					}
				}
			}
		}
		return false;
	}
	
	
	
	public boolean doNumericalOperation(int position, String firstValue, String secondValue, HashMap nodeValues, ArrayList<String> operations, Node node){
		boolean comparisonOutcome = false;

		double dvalue1 = 0;
		double dvalue2 = 0;
		int ivalue1 = 0;
		int ivalue2 = 0;
		
		CyNode cnode = (CyNode)node;
		String nodeID = cnode.getIdentifier();

		if(firstValue.matches("^[0-9\\.\\-]+")){
			if(firstValue.matches("^[0-9]+")){
				ivalue1 = Integer.parseInt(firstValue);
				
				dvalue1 = ivalue1;
			}else{
				
				dvalue1 = Double.parseDouble(firstValue);		
			}
			//System.out.println("digit1: "+dvalue1);
		}else{
			if(firstValue.equals("true") || firstValue.equals("false")){

			}else{
				if(attributeTypeMap.get(firstValue) == 2){
					
					//value = getValue(node, firstValue);
					if(nodeAttributes.hasAttribute(nodeID, firstValue)){
						dvalue1 = nodeAttributes.getDoubleAttribute(nodeID, firstValue);

					}else{
						if(edgeAttributes.hasAttribute(nodeID, firstValue)){
							dvalue1 = edgeAttributes.getDoubleAttribute(nodeID, firstValue);
						}
					}
				}else{
					if(attributeTypeMap.get(firstValue) == 3){
						
						if(nodeAttributes.hasAttribute(nodeID, firstValue)){
							dvalue1 = nodeAttributes.getIntegerAttribute(nodeID, firstValue);

						}else{
							if(edgeAttributes.hasAttribute(nodeID, firstValue)){
								dvalue1 = edgeAttributes.getIntegerAttribute(nodeID, firstValue);
								//String stemp = temp + "";
								//nodeValueMap.put(attribute, stemp); 
							}
						}
					}else{
						if(attributeTypeMap.get(firstValue) == 1){
							if(operations.get(position+1).equals("=")){
								if(secondValue.equals("true") && nodeValues.get(secondValue).equals("true")){
									
									return true;
								}else{
									if(secondValue.equals("false") && nodeValues.get(secondValue).equals("false")){
										return false;
									}
								}
							}
						}
					}
				}
			}
		}

		if(secondValue.matches("^[0-9\\.\\-]+")){
			if(secondValue.matches("^[0-9]+")){
				ivalue2 = Integer.parseInt(secondValue);
				dvalue2 = ivalue2;
			}else{
				dvalue2 = Double.parseDouble(secondValue);

			}
		}else{
			if(secondValue.equals("true") || secondValue.equals("false")){

			}else{
				if(attributeTypeMap.get(secondValue) == 2){
					
					//value = getValue(node, firstValue);
					if(nodeAttributes.hasAttribute(nodeID, secondValue)){
						dvalue2 = nodeAttributes.getDoubleAttribute(nodeID, secondValue);
						
					}else{
						if(edgeAttributes.hasAttribute(nodeID, secondValue)){
							dvalue2 = edgeAttributes.getDoubleAttribute(nodeID, secondValue);
							
						}
					}

				}else{
					if(attributeTypeMap.get(secondValue) == 3){
						System.out.println("SHOULD HAVE DEGREE");
						if(nodeAttributes.hasAttribute(nodeID, secondValue)){
							dvalue2 = nodeAttributes.getIntegerAttribute(nodeID, secondValue);
						
						}else{
							if(edgeAttributes.hasAttribute(nodeID, secondValue)){
								dvalue2 = edgeAttributes.getIntegerAttribute(nodeID, secondValue);
							
							}
						}
					}else{
						if(attributeTypeMap.get(secondValue) == 1){
							if(operations.get(position+1).equals("=")){
								if(firstValue.equals("true")){
									return true;
								}else{
									if(firstValue.equals("false")){
										return false;
									}
								}
							}
						}
					}
				}	
			}
		}
		
		if(operations.get(position+1).matches("<")){
			return (dvalue1 < dvalue2); 
		}
		if(operations.get(position+1).matches(">")){
			return (dvalue1 > dvalue2);	
		}	
		if(operations.get(position+1).matches("<=")){
			return (dvalue1 <= dvalue2);
		}	
		if(operations.get(position+1).matches(">=")){
			return (dvalue1 >= dvalue2);		
		}		
		if(operations.get(position+1).matches("=")){
			return (dvalue1 == dvalue2);			
		}	
		return false;	
	}


	public void createAttributeTypeHash(){
		CyAttributes atts = null;
		String[] names = getAllAttributes();
		
		CyNetwork network = Cytoscape.getCurrentNetwork();
		List<Node> nodeList = network.nodesList();
		
		for(int i=0;i<names.length;i++){
			for(int j=0; j<nodeList.size(); j++){
				CyNode node = (CyNode)nodeList.get(j);
				if(nodeAttributes.hasAttribute(node.getIdentifier(), names[i])){
			
					int type = nodeAttributes.getType(names[i]);
					attributeTypeMap.put(names[i], type);
					break;
				}else{
					if(edgeAttributes.hasAttribute(node.getIdentifier(), names[i])){
						int type = edgeAttributes.getType(names[i]);
						attributeTypeMap.put(names[i], type);
						break;
					}
				}
			}
		}
	}

	public String[] getAllAttributes() {
		// Create the list by combining node and edge attributes into a single
		// list

		getAttributesList(attributeList, Cytoscape.getNodeAttributes(), "");
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes(), "");
		
		String[] str = (String[]) attributeList.toArray(new String[attributeList.size()]);
		attributeList.clear();
		return str;

	}

	
	
	public void getAttributesList(ArrayList attributeList,
			CyAttributes attributes, String prefix) {
		String[] names = attributes.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			//attributeList.add(prefix + names[i]);
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING
					|| attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER  || attributes.getType(names[i]) == CyAttributes.TYPE_BOOLEAN) {
				attributeList.add(prefix + names[i]);
			}
		}
	}

	public String getAttributeType(byte type){
		return "";
		
	}

	public boolean isValidAttribute(String attName){
		String[] attributes = getAllAttributes();
		for(int i=0;i<attributes.length;i++){
			if(attName.equals(attributes[i])){
				return true;
			}
		}
		return false;
	}
	
}


