

import giny.view.NodeView;
import giny.model.*;

//import jboolexpr.*;
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
	ArrayList<String> attributeList = new ArrayList<String>();
	ArrayList<String> validAttributes = new ArrayList<String>();
	ArrayList<String> operations = new ArrayList<String>();
	ArrayList<ArrayList> masterList = new ArrayList<ArrayList>();
	//ArrayList<String> validCriteria = new ArrayList<String>();
	//ArrayList<String> orderedOperations = new ArrayList<String>();
	
	//ArrayList<String> letters = new ArrayList<String>();
	ArrayList<String> values = new ArrayList<String>();
	
	
	
	String currentOperator = "";
	String criteria;
	
	//CyAttributes atts = null;
	HashMap attributeTypeMap = new HashMap();
	HashMap nodeValueMap = null;
	Iterator t = null;
	int p = 0;
	
	CyAttributes nodeAttributes = null;
	CyAttributes edgeAttributes = null;
	
	
	public BooleanCalculator(){
		p=0;
		nodeAttributes = Cytoscape.getNodeAttributes();
		edgeAttributes = Cytoscape.getEdgeAttributes();
		createAttributeTypeHash();
		
	}

	//public String getAttribType(String attName){
		
	//}
	
	public boolean parse(String criteria){
		ArrayList<String> attributeChars = new ArrayList<String>();
		ArrayList<String> operationChars = new ArrayList<String>();
		boolean readingAttribute = false;
		boolean readingOperation = false;
		String attribute = "";
		String operation = "";
		System.out.println("CRITERIA:"+criteria);
		for(int i=0;i<criteria.length();i++){
			String parse = "" + criteria.charAt(i);
			if(parse.matches("\\[")){
				readingAttribute = true;
				readingOperation = false;
				operation = "";
				if(!(operationChars.size() == 0)){
					for(int j=0;j<operationChars.size();j++){
						operation = operation + operationChars.get(j);
					}
					operationChars.clear();
					operations.add(operation);
					validAttributes.add("");
				}
			}
			if(parse.matches("\\]") || i == criteria.length()-1){
				readingAttribute = false;
				readingOperation = true;
				attribute = "";
				for(int k=0;k<attributeChars.size();k++){
					attribute = attribute + attributeChars.get(k);
				}
				attributeChars.clear();
				if(isValidAttribute(attribute)){
					validAttributes.add(attribute);
					operations.add("");
				}
			}
			if(parse.matches(" ")){
				readingAttribute = true;
				readingOperation = false;
			}
			if(parse.matches(" ") && readingAttribute){
				readingAttribute = false;
				readingOperation = true;
			}
			if(readingAttribute){
				if(!parse.matches("\\]") && !parse.matches("\\[") && !parse.matches(" ")){
					attributeChars.add(parse);
				}
			}
			if(readingOperation){
				if(!parse.matches("\\]") && !parse.matches("\\[") && !parse.matches(" ")){
					operationChars.add(parse);
			
				}
		
			}
			
		}
		for(int b=1;b<validAttributes.size()+1;b++){
			if(b%2 != 0){
				System.out.println("b"+validAttributes.get(b-1));
			}
			if(b%2 == 0){
				System.out.println("a"+operations.get(b-1));
			}
		}
		
		//System.out.println(checkCriteria());
		return checkCriteria();
	}
	
	public boolean parse2(String criteria){
		ArrayList<String> valueChars = new ArrayList<String>();
		String value = "";
		int valueCount = 2;
		System.out.println("CRITERIA:"+criteria);
		criteria = criteria + "  ";
		//System.out.println(criteria.length());
		for(int i=0;i<criteria.length();i++){
			String parse = "" + criteria.charAt(i);
			//System.out.println("parse: "+ parse);
			if(!parse.matches(" ")){
				//System.out.println("parse: "+ parse);
				valueChars.add(parse);
				
			}
			if(parse.matches(" ") && valueChars.size() != 0){
				for(int k=0;k<valueChars.size();k++){
					value = value + valueChars.get(k);
					
				}
				//System.out.println("value: "+value);
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
		
		//System.out.println(checkCriteria());
		return checkCriteria();
	}
	
	
	
	
	public boolean checkCriteria(){
		boolean nFlag = false;
		boolean cFlag = true;
		int a = 0;
		int b = 0;
		int size = validAttributes.size();
		//System.out.println("size: "+size);
		if (size == 1){ return true; }
		for(int i=0; i<size; i++){
			//System.out.println(i);
			if(!validAttributes.get(i).equals("")){
				if(validAttributes.get(i).matches("^\\d")){
					if(!validAttributes.get(i).contains(".")){
						a = 3;
					}else{
						a = 2;
					}
				}else{
					a = (Integer)attributeTypeMap.get(validAttributes.get(i).substring(5,validAttributes.get(i).length()));
				}
			}
			if((i+2) < size && !validAttributes.get(i+2).equals("")){
				if(validAttributes.get(i+2).matches("^\\d")){
					if(!validAttributes.get(i).contains(".")){
						b = 3;
					}else{
						b = 2;
					}
				}else{
				
					b = (Integer)attributeTypeMap.get(validAttributes.get(i+2).substring(5,validAttributes.get(i+2).length()));
				}
			}
			if(cFlag && (a == 2 || a == 3) && (i+1) < size && operations.get(i+1).matches("[<>=]") && (b == 2 || b == 3)){
				nFlag = true;
				cFlag = false;
				i = i + 2;
				
			}else{
				if((a == 1) && operations.get(i+1).matches("[ANDORT]+") && (b == 1)){
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
		return true;
	}
	
	
	public boolean evaluate(){

		CyNetwork network = Cytoscape.getCurrentNetwork();
		List<Node> nodeList = network.nodesList();
		boolean isInteger = false;
		nodeValueMap = new HashMap();
		int criteriaSize = validAttributes.size();
		int type = 0;
		int numberValueCount = 0;
		for(int i=0;i<nodeList.size();i++){
			CyNode node = (CyNode)nodeList.get(i);
			Node gnode = nodeList.get(i);
			String nodeID = node.getIdentifier();


			numberValueCount = 0;


			for(int b=0;b<criteriaSize;b++){
				isInteger = false;
				String subbed = "";
				type = 0;
				String attribute = validAttributes.get(b);
				attribute = attribute.trim();

				if(!attribute.equals("")){

					if(attribute.length()>4){
						subbed = attribute.substring(5,attribute.length());
						type = (Integer)attributeTypeMap.get(subbed);
					}else{
						subbed = "number" + numberValueCount;
						numberValueCount++;
					}
					if(attribute.matches("^\\d")){
						isInteger = true;
						System.out.println("It worked: "+validAttributes.get(b));
						if(!validAttributes.get(b).contains(".")){
							//type = 3;
							nodeValueMap.put(subbed, attribute);
						}else{
							//type = 2;
							nodeValueMap.put(subbed, attribute);
						}
						
					}
					System.out.println("subbed: "+subbed+" type = " + type);

					if(!isInteger){
						if(type == 1){
							//boolean bvalue = getAttributeValue(attr,type);

							if(attribute.startsWith("node.")){
								boolean bvalue = nodeAttributes.getBooleanAttribute(nodeID, subbed);
								//String temp = "" + bvalue;
								nodeValueMap.put(subbed, bvalue);
							}else{ 
								if(attribute.startsWith("edge.")){
									boolean bvalue = edgeAttributes.getBooleanAttribute(nodeID, subbed);
									//String temp = "" + bvalue;
									nodeValueMap.put(subbed, bvalue);
								}
							}
						}
						if(type == 2){
							//double dvalue = atts.getDoubleAttribute(node.getIdentifier(), attr);
							if(attribute.startsWith("node.")){
								if(nodeAttributes.hasAttribute(nodeID, subbed)){
								double dvalue = nodeAttributes.getDoubleAttribute(nodeID, subbed);
								String temp = "" + dvalue;
								nodeValueMap.put(subbed, temp);
								}
							}else{ 
								if(attribute.startsWith("edge.")){
									double dvalue = edgeAttributes.getDoubleAttribute(nodeID, subbed);
									String temp = "" + dvalue;
									nodeValueMap.put(subbed, temp);
								}
							}
						}
						if(type == 3){
							//int ivalue = atts.getIntegerAttribute(node.getIdentifier(), attr);
							if(attribute.startsWith("node.")){
								int ivalue = nodeAttributes.getIntegerAttribute(nodeID, subbed);
								String temp = "" + ivalue;
								nodeValueMap.put(subbed, temp);
							}else{ 
								if(attribute.startsWith("edge.")){
									int ivalue = edgeAttributes.getIntegerAttribute(nodeID, subbed);
									String temp = "" + ivalue;
									nodeValueMap.put(subbed, temp);
								}
							}
						}
						if(type == 4){
							//String svalue = atts.getStringAttribute(node.getIdentifier(), attr);
							if(attribute.startsWith("node.")){
								String svalue = nodeAttributes.getStringAttribute(nodeID, subbed);
								nodeValueMap.put(subbed, svalue);
							}else{ 
								if(attribute.startsWith("edge.")){
									String svalue = edgeAttributes.getStringAttribute(nodeID, subbed);
									nodeValueMap.put(subbed, svalue);
								}
							}
						}
					}
				}
			}
			System.out.println(type);
			evaluateOnce(nodeValueMap, gnode, type, numberValueCount);
			//validAttributes.clear();
			//operations.clear();
			nodeValueMap.clear();
		}

		return true;
	}
		
	
	public void evaluateOnce(HashMap nodeValues, Node node, int type, int numberCount){
		ArrayList finalBooleans = new ArrayList();
		ArrayList<Boolean> comparisonBooleans = new ArrayList<Boolean>();
		CyNetwork network = Cytoscape.getCurrentNetwork();
		int k = 0;
		for(int j=0; j<masterList.size();j++){
			boolean nFlag = false;
			boolean cFlag = true;
			boolean didComparison = false;
			String logicalString = "";
			ArrayList<String> attributes = masterList.get(j);
			j++;
			ArrayList<String> operation = masterList.get(j);
			
			int size = attributes.size();
			//System.out.println("size: "+size);
			for(int i=0; i<size; i++){
				
				String shortName = "";
				String shortName2 = "";
				System.out.println("at "+attributes.get(i).toString());
				
				//System.out.println("operationsI" + operation.get(i+1));
				
				if(cFlag && (i+2) < size && operation.get(i+1).matches("[<>=]+")){
					boolean comparisonOutcome = false;
					//System.out.println(attributes.get(i));
					if(!(attributes.get(i).toString().length() < 5)){
						shortName = attributes.get(i).toString().substring(5, attributes.get(i).toString().length());
					}else{
						if(!attributes.get(i).matches("^\\d.*\\d$")){
							if(k < numberCount){
								shortName = "number" + k;
								k++;
							}
						}
					}
					if(!(attributes.get(i+2).toString().length() < 5)){
						shortName2 = attributes.get(i+2).toString().substring(5, attributes.get(i).toString().length());
					}else{
						if(!attributes.get(i+2).matches("^\\d.*\\d$")){
							if(k < numberCount){
								shortName2 = "number" + k;
								k++;
							}
						}
					}
					
					if(!(shortName.equals("") || shortName2.equals(""))){
						comparisonOutcome = doOperation(i, type, nodeValues, shortName, shortName2);
					}
					
					//didComparison = true;
					nFlag = true;
					cFlag = false;
					//comparisonBooleans.add(comparisonOutcome); 
					logicalString = logicalString + comparisonOutcome;
					i = i +2;
				}
				
				if(cFlag && (i+2) < size && operation.get(i+1).matches("[ANDORT]+")){
					logicalString = logicalString + attributes.get(i) + operation.get(i+1);
					nFlag = true;
					cFlag = false;
					i++;
					continue;
					
					
					/*if((i+4) < size && operations.get(i+3).matches("[<>=]+")){
						cFlag = true;
						logicalString = logicalString + "" + (Boolean)attributes.get(i) + operations.get(i+1);
					}else{
						if((i+4) < size && operations.get(i+3).matches("[ANDORT]+")){
						
							logicalString = logicalString + (Boolean)attributes.get(i) + operations.get(i+1) + (Boolean)attributes.get(i+2);
							i = i + 2;
							continue;
						}
							if(operations.get(i+1).matches("AND")){
							//if((Boolean)attributes.get(i) && (Boolean)attributes.get(i+2)){
							
							//}
							logicalString = logicalString + "&&" + (Boolean)attributes.get(i+2);
							i = i + 2;
								
							}
							if(operations.get(i+1).matches("OR")){
							//if((Boolean)attributes.get(i) || (Boolean)attributes.get(i+2)){
							
							//}
								logicalString = logicalString + "||" + (Boolean)attributes.get(i+2);
							}
							if(operations.get(i+1).matches("NOT")){
							//if((Boolean)attributes.get(i) && !(Boolean)attributes.get(i+2)){
							
							//}
								logicalString = logicalString + "!" + (Boolean)attributes.get(i+2);
							}
						
						}
						nFlag = true;
						cFlag = false;
				*/
				
				}
				
				
				if(nFlag && (i+2) == size){
					
					if(operations.get(i).matches("AND")){
						//if((Boolean)attributes.get(i) && (Boolean)attributes.get(i+2)){
						
						//}
						logicalString = logicalString + "&&" + nodeValues.get((String)attributes.get(i+1));
						i = i + 1;
							
						}
						if(operations.get(i).matches("OR")){
						//if((Boolean)attributes.get(i) || (Boolean)attributes.get(i+2)){
						
						//}
							logicalString = logicalString + "||" +nodeValues.get((String)attributes.get(i+1));
							i = i + 1;
						}
						if(operations.get(i).matches("NOT")){
						//if((Boolean)attributes.get(i) && !(Boolean)attributes.get(i+2)){
						
						//}
							logicalString = logicalString + "!" + nodeValues.get((String)attributes.get(i+1));
							i = i + 1;
						}
				}
				//if(nFlag && ())
				
				if(nFlag && (i+3) < size && operations.get(i+2).matches("[ANDORT]+")){
					logicalString = logicalString + operations.get(i) + attributes.get(i+1) + operations.get(i+2) + attributes.get(i+3);
				}
				
				
				
			}
			//System.out.println(node.getIdentifier() + logicalString );
			if(logicalString.equals("true")){
				network.setSelectedNodeState(node, true);
			}
			
		}
	}
	
	public boolean doOperation(int i,int type, HashMap nodeValues, String shortName, String shortName2){
		boolean comparisonOutcome = false;
		Object castType = "";
		
		
			if(operations.get(i+1).matches("<")){
				if(nodeValues.get(shortName) != null && nodeValues.get(shortName2) != null){
				if(Float.parseFloat((String)nodeValues.get(shortName)) < Float.parseFloat((String)nodeValues.get(shortName2))){
					comparisonOutcome = true;
				}else{
					comparisonOutcome = false;
				}
				}
			}
			if(operations.get(i+1).matches(">")){
				if(Double.parseDouble((String)nodeValues.get(shortName)) > Double.parseDouble((String)nodeValues.get(shortName2))){
					comparisonOutcome = true;
				}else{
					comparisonOutcome = false;
				}	
			}
			if(operations.get(i+1).matches("<=")){
				if(Double.parseDouble((String)nodeValues.get(shortName)) <= Double.parseDouble((String)nodeValues.get(shortName2))){
					comparisonOutcome = true;
				}else{
					comparisonOutcome = false;
				}
			}
			if(operations.get(i+1).matches(">=")){
				if(Double.parseDouble((String)nodeValues.get(shortName)) >= Double.parseDouble((String)nodeValues.get(shortName2))){
					comparisonOutcome = true;
				}else{
					comparisonOutcome = false;
				}
			}
			if(operations.get(i+1).matches("=")){
				if(Double.parseDouble((String)nodeValues.get(shortName)) == Double.parseDouble((String)nodeValues.get(shortName2))){
					comparisonOutcome = true;
				}else{
					comparisonOutcome = false;
				}
			}
			
			
			System.out.println("comparisonOutcome: "+comparisonOutcome);
			
		
		return comparisonOutcome;
	}

	
	
	public void createAttributeTypeHash(){
		CyAttributes atts = null;
		String[] names = getAllAttributes();
		
		
		
		for(int i=0;i<names.length;i++){
			if(names[i].startsWith("node.")){
				String nameKey = names[i].substring(5,names[i].length());
				//System.out.println(nameKey+"a");
				
				int type = nodeAttributes.getType(nameKey);
				
				attributeTypeMap.put(nameKey, type);
			}
			if(names[i].startsWith("egde.")){
				String nameKey = names[i].substring(5,names[i].length());
				//System.out.println(nameKey+"a");
				
				int type = edgeAttributes.getType(nameKey);
				
				attributeTypeMap.put(nameKey, type);
			}
				
			
		}
		
		
	
		for(int j=0;j<names.length;j++){
			String nameKey = names[j].substring(5,names[j].length());
			System.out.println(nameKey+" "+attributeTypeMap.get(nameKey));
		}
	}
	
	public void clearList(){
		validAttributes.clear();
		operations.clear();
	}
	/*public String doCalculation(String numericalCriteria) {
		String value = "";
		String type = "";
		if(!numericalCriteria.matches("[<>=]")){
			
		}
		
		for(int i=0; i<numericalCriteria.length(); i++){
			//if( 
			
			String parse = "" + numericalCriteria.charAt(i);
			String parse1 = "";
			
			if(parse.matches("[<>=]")){
				//if(!(i+1 == numericalCriteria.length())){ 
				parse1 = "" + numericalCriteria.charAt(i+1); //}
				value = "";
				for(int k=0;k<letters.size();k++){
					value = value + letters.get(k);
				}
				if( parse1.matches("=")){
					values.add(value);
					letters.clear();
					operations.add(parse+parse1);
				}else{
					letters.add(parse1);
					values.add(value);
					letters.clear();
					operations.add(parse);	
				}
			}else{
				letters.add(parse);
			}
			
		}
		value = "";
		for(int k=0;k<letters.size();k++){
			value = value + letters.get(k);
		}
		values.add(value);
		letters.clear();
		
		for(int j=0;j<values.size();j++){
			//System.out.println(values.get(j)+"hey");
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
					if(doCalculation(temp[i]).equals("valid")){
						validCriteria.add(temp[i]);
					}
					operations.add(currentOperator);
					System.out.println(p+" "+currentOperator+i+" "+temp[i]);
				}else{
					checkCriteria(temp[i]);
				}
			}
			System.out.println(criteria);
		}
		
		if(mor.matches()){
			currentOperator = "OR";
			String[] temp1 = criteria.split("\\]OR\\[");
			for(int i=0;i<temp1.length;i++){
				if(!temp1[i].contains("]AND[") && !temp1[i].contains("]NOT[")){
					if(doCalculation(temp1[i]).equals("valid")){
						validCriteria.add(temp1[i]);
					}
					operations.add(currentOperator);
					System.out.println(p+" "+currentOperator+i+" "+temp1[i]);
				}else{
					checkCriteria(temp1[i]);
				}
			}
		}
		if(mnot.matches()){
			currentOperator = "NOT";
			String[] temp2 = criteria.split("\\]NOT\\[");
			for(int i=0;i<temp2.length;i++){
				if(!temp2[i].contains("]OR[") && !temp2[i].contains("]AND[")){
					if(doCalculation(temp2[i]).equals("valid")){
						validCriteria.add(temp2[i]);
					}
					operations.add(currentOperator);
					System.out.println(p+" "+currentOperator+i+" "+temp2[i]);
				}else{
					checkCriteria(temp2[i]);
				}
			}
		}
		if(!mand.matches() && !mor.matches() && !mnot.matches()){
			doCalculation(criteria);
		}
		//validCriteria.add(criteria);
		//orderedOperations.add(currentOperator);
		
		
		
		//System.out.println(p+" "+criteria);
		//System.out.println(p+" "+currentOperator); 
		//Matcher m = p.matcher(criteria);
		 //System.out.println(m.matches());
		 return(true);
	}*/
	
	
	//public String parsedCriteria()
	
	/*public String[] parseCriteria(String criteria){
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
	}*/
	
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
		
	
        for(int j=0;j<values.size();j++){
			System.out.println(values.get(j)+"hey");
		}

		System.out.println("made it");
		
		
	}

	public String[] getAllAttributes() {
		// Create the list by combining node and edge attributes into a single
		// list

		getAttributesList(attributeList, Cytoscape.getNodeAttributes(), "node.");
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes(), "edge.");
		
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


