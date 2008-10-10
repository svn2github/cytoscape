package src;

import giny.model.Node;

import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class CriteriaCalculator {

	String criteria;
	String input;
	int charNr = 0;
	int charNumber = 0;
	HashMap<String, Integer> attributeTypeMap = new HashMap<String, Integer>();
	ArrayList<String> attributeList = new ArrayList<String>();
	ArrayList<Token> tokenList;
	
	AttributeManager attManager = null;
	
	CyAttributes nodeAttributes = null;
	CyAttributes edgeAttributes = null;
	
	public CriteriaCalculator(){
		
		attManager = new AttributeManager();
		attributeList = getAllAttributes();
		nodeAttributes = Cytoscape.getNodeAttributes();
		edgeAttributes = Cytoscape.getEdgeAttributes();
	}
	
	
	public void parse(String a) throws Exception{
		criteria = a;
		StringReader reader = new StringReader(criteria);
		StreamTokenizer st = new StreamTokenizer(reader);
		attributeList = getAllAttributes();
		tokenList = new ArrayList<Token>();
		int i;
		double tmp;
		String variable;
		System.out.println("SCANNER: "+ a);
		while((i = st.nextToken()) != StreamTokenizer.TT_EOF ){
			switch(i) {
			
            case StreamTokenizer.TT_NUMBER: // Found a number, push value to stack
                
            	//System.out.println(st.nval);
            	tokenList.add(new Token(Token.NUMBER, st.nval));
            	break;
            
            case '<':
               
               
            	//System.out.println("<");
            	if(st.nextToken() == '='){
            		//System.out.println("=");
            		tokenList.add(new Token(Token.LTEQ));
            		break;
            	}else{
            		st.pushBack();
            	}
            	tokenList.add(new Token(Token.LT));
            	break;
            case '>':
                // - operator: order matters.
                //tmp = pop( );
                //push(pop( ) - tmp);
            	//System.out.println(">");
            	if(st.nextToken() == '='){
            		//System.out.println("=");
            		tokenList.add(new Token(Token.GTEQ));
            		break;
            	}else{
            		st.pushBack();
            	}
            	tokenList.add(new Token(Token.GT));
                break;
            case '=':
                // Multiply is commutative
                //push(pop( ) * pop( ));
            	tokenList.add(new Token(Token.EQ));
                break;
           
            case StreamTokenizer.TT_WORD:
                // Found a variable, save its name. Not used here.
                variable = st.sval;
                if(variable.equals("AND")){
                	//System.out.println("AND");
                	tokenList.add(new Token(Token.AND));
                	break;
                }
                if(variable.equals("OR")){
                	//System.out.println("OR");
                	tokenList.add(new Token(Token.OR));
                	break;
                }
                if(variable.equals("NOT")){
                	//System.out.println("NOT");
                	tokenList.add(new Token(Token.NOT));
                	break;
                }
                if(variable.equals("true")){
                	//System.out.println("NOT");
                	tokenList.add(new Token(Token.TRUE));
                	break;
                }
                if(variable.equals("false")){
                	//System.out.println("NOT");
                	tokenList.add(new Token(Token.FALSE));
                	break;
                }
                if(attributeList.contains(variable)){
                	System.out.println("PARSE: "+variable);
                	tokenList.add(new Token(Token.ATTRIBUTE, variable));
                	break;
                }
                throw new Exception("Unrecognized attribute: "+variable);
               
            default:
                System.out.println("What's this? iType = " + i);
            }
        }
    
		
		
	}
	
	
	
	
	public ArrayList<String> getAllAttributes() {
		// Create the list by combining node and edge attributes into a single
		// list
		attributeList.clear();
		
		getAttributesList(attributeList, Cytoscape.getNodeAttributes(), "");
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes(), "");
		
		//String[] str = (String[])attributeList.toArray(new String[attributeList.size()]);
		
		
		return attributeList;

	}
	

	public void getAttributesList(ArrayList<String> attributeList, CyAttributes attributes, String prefix) {
		String[] names = attributes.getAttributeNames();
		ArrayList<String> internalAttributes = new ArrayList<String>();
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING
					 || attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER || attributes.getType(names[i]) == CyAttributes.TYPE_BOOLEAN) {
				
					attributeList.add(prefix + names[i]);
			}
		}
	}
	
	public void printTokens(){
		for(int i=0; i<tokenList.size(); i++){
			Token token = tokenList.get(i);
			switch (token.type)
			{
			case Token.AND:
				System.out.println("AND");
				break;
			case Token.OR:
				System.out.println("OR");
				break;
			case Token.NOT:
				System.out.println("NOT");
				break;
			case Token.GT:
				System.out.println(">");
				break;
			case Token.LT:
				System.out.println("<");
				break;
			case Token.GTEQ:
				System.out.println(">=");
				break;
			case Token.LTEQ:
				System.out.println("<=");
				break;
			case Token.ATTRIBUTE:
				System.out.println("Attname: "+token.attributeName);
				break;
			case Token.NUMBER:
				System.out.println(token.numberValue);
				break;
				//throw new Exception("Can't evaluate this expression as boolean");
		}
			System.out.println("tokens "+i);
		}
	}
	
	public void evaluateLeftToRight(String label){

		CyNetwork network = Cytoscape.getCurrentNetwork();
		List<Node> nodeList = network.nodesList();
		HashMap<String, String> nodeValueMap = new HashMap<String, String>();
		createAttributeTypeHash();
		
		int attributeType = 0;
		for(int j=0;j<nodeList.size();j++){

			CyNode node = (CyNode)nodeList.get(j);
			Node gnode = nodeList.get(j);
			String nodeID = node.getIdentifier();
			Stack<Boolean> finalValue = new Stack<Boolean>();
			
			int size = tokenList.size();
			for(int i=0; i<size; i++){

				
				
				String logicalString = "";
				
				
			
				int type0 = tokenList.get(i).type;
				int type1 = tokenList.get(i+1).type;
				int type2 = tokenList.get(i+2).type;
				//System.out.println(attributes.get(i)+ "evaluateOnce");
				if((i+2) < size && (type1 == Token.LT || type1 == Token.GT || 
						type1 == Token.LTEQ ||type1 == Token.GTEQ || type1 == Token.EQ)){

					boolean comparisonOutcome = false;

					//if(!(attributes.get(i).equals("") || attributes.get(i+2).equals(""))){

					comparisonOutcome = doNumericalOperation(i, tokenList.get(i), tokenList.get(i+2), node);
					//System.out.println("made it"+comparisonOutcome);
					//}

					logicalString = logicalString + comparisonOutcome;
					finalValue.push(comparisonOutcome);
					i = i +2;
				}else{

					if((i+2) < size && (type1 == Token.AND || type1 == Token.OR || type1 == Token.NOT)){
						//logicalString = logicalString + attributes.get(i) + operations.get(i+1);
						
						finalValue.push(tokenList.get(i).booleanValue);

						i++;
					}else{
						if((type0 == Token.AND || type0 == Token.OR || type0 == Token.NOT)){

							if(i+3 < size && (type2 == Token.AND || type2 == Token.OR || type2 == Token.NOT)){
								boolean temp = finalValue.pop();
								boolean temp2 = tokenList.get(i+1).booleanValue;
								boolean outcome = doBooleanOperation(tokenList.get(i), temp, temp2);
								finalValue.push(outcome);
								i++;
							}else{

								if(i+2 == size){
									//logicalString = logicalString + attributes.get(i+1);
									boolean temp = finalValue.pop();
									boolean outcome = doBooleanOperation(tokenList.get(i), temp, tokenList.get(i+1).booleanValue);
									finalValue.push(outcome);
									i++;
								}else{
									if(i+3 < size && (type2 == Token.LT || type2 == Token.GT || 
											type2 == Token.LTEQ ||type2 == Token.GTEQ || type2 == Token.EQ)){
										boolean temp = doNumericalOperation(i+1, tokenList.get(i+1), tokenList.get(i+3), node);
										boolean temp2 = finalValue.pop();
										boolean outcome = doBooleanOperation(tokenList.get(i), temp, temp2);
										finalValue.push(outcome);
										i = i + 3; 
									}
								}
							}
						}
					}
				}
			}
			//System.out.println(node.getIdentifier() + logicalString );
			//network.setSelectedNodeState(node, false);
			if(!finalValue.isEmpty()){
				boolean outcome = finalValue.pop();

				//System.out.println("true");
				//createAttribute()
				//System.out.println("label: "+label);

				attManager.setColorAttribute(label, node.getIdentifier(), outcome);
				//if(outcome){
					//network.setSelectedNodeState(node,true);

				//}
				//System.out.println("label: "+label);
				//System.out.println(outcome);

			}


		}
	}

		public boolean doBooleanOperation(Token token, boolean firstValue, boolean secondValue){
			if(token.type == Token.AND){
				if(firstValue && secondValue){
					return true;
				}else{
					return false;
				}
			}else{
				if(token.type == Token.OR){
					if(firstValue || secondValue){
						return true;
					}else{
						return false;
					}

					
				}else{
					if(token.type == Token.NOT){
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
	
		
		
		
		public boolean doNumericalOperation(int position, Token firstToken, Token secondToken,  Node node){
			boolean comparisonOutcome = false;


			//float value1 = 0;
			//float value2 = 0;
			double dvalue1 = 0;
			double dvalue2 = 0;
			
			
			CyNode cnode = (CyNode)node;
			String nodeID = cnode.getIdentifier();

			//System.out.println("second Node Value"+ nodeValues.get(secondValue));

			if(firstToken.type == Token.NUMBER){
				
					dvalue1 = firstToken.numberValue;

			
				//System.out.println("digit1: "+dvalue1);
			}else{
				System.out.println(firstToken.attributeName);
				if(firstToken.type == Token.ATTRIBUTE && attributeTypeMap.get(firstToken.attributeName) == 2){
						
						//value = getValue(node, firstValue);
						if(nodeAttributes.hasAttribute(nodeID, firstToken.attributeName)){
							dvalue1 = nodeAttributes.getDoubleAttribute(nodeID, firstToken.attributeName);
							//String stemp = temp + "";
							//nodeValueMap.put(attribute, stemp);
							//System.out.println("dvalue1: "+dvalue1);
						}else{
							if(edgeAttributes.hasAttribute(nodeID, firstToken.attributeName)){
								dvalue1 = edgeAttributes.getDoubleAttribute(nodeID, firstToken.attributeName);
								//String stemp = temp + "";
								//nodeValueMap.put(attribute, stemp);
								//System.out.println("dvalue1: "+dvalue1);
							}
						}

					}else{
						if(firstToken.type == Token.ATTRIBUTE && attributeTypeMap.get(firstToken.attributeName) == 3){
							
							if(nodeAttributes.hasAttribute(nodeID, firstToken.attributeName)){
								dvalue1 = nodeAttributes.getIntegerAttribute(nodeID, firstToken.attributeName);
								//String stemp = temp + "";
								//nodeValueMap.put(attribute, stemp);
								//System.out.println("ivalue1: "+ivalue1);
							}else{
								if(edgeAttributes.hasAttribute(nodeID, firstToken.attributeName)){
									dvalue1 = edgeAttributes.getIntegerAttribute(nodeID, firstToken.attributeName);
									//String stemp = temp + "";
									//nodeValueMap.put(attribute, stemp); 
								}
							}
						}else{
							if(firstToken.type == Token.ATTRIBUTE && attributeTypeMap.get(firstToken.attributeName) == 1){
								if(tokenList.get(position+1).type == Token.EQ){
									if(secondToken.type == Token.TRUE){
										//System.out.println("ayo");
										return true;
									}else{
										if(secondToken.type == Token.FALSE){
											return false;
										}
									}
								}
							}
						}
					}
				}
			



			if(secondToken.type == Token.NUMBER){
				
				dvalue1 = secondToken.numberValue;

		
			//System.out.println("digit1: "+dvalue1);
		}else{
			
			if(secondToken.type == Token.ATTRIBUTE && attributeTypeMap.get(secondToken.attributeName) == 2){
					
					//value = getValue(node, firstValue);
					if(nodeAttributes.hasAttribute(nodeID, secondToken.attributeName)){
						dvalue1 = nodeAttributes.getDoubleAttribute(nodeID, secondToken.attributeName);
						//String stemp = temp + "";
						//nodeValueMap.put(attribute, stemp);
						//System.out.println("dvalue1: "+dvalue1);
					}else{
						if(edgeAttributes.hasAttribute(nodeID, secondToken.attributeName)){
							dvalue1 = edgeAttributes.getDoubleAttribute(nodeID, secondToken.attributeName);
							//String stemp = temp + "";
							//nodeValueMap.put(attribute, stemp);
							//System.out.println("dvalue1: "+dvalue1);
						}
					}

				}else{
					if(secondToken.type == Token.ATTRIBUTE && attributeTypeMap.get(secondToken.attributeName) == 3){
						
						if(nodeAttributes.hasAttribute(nodeID, secondToken.attributeName)){
							dvalue1 = nodeAttributes.getIntegerAttribute(nodeID, secondToken.attributeName);
							//String stemp = temp + "";
							//nodeValueMap.put(attribute, stemp);
							//System.out.println("ivalue1: "+ivalue1);
						}else{
							if(edgeAttributes.hasAttribute(nodeID, secondToken.attributeName)){
								dvalue1 = edgeAttributes.getIntegerAttribute(nodeID, secondToken.attributeName);
								//String stemp = temp + "";
								//nodeValueMap.put(attribute, stemp); 
							}
						}
					}else{
						if(secondToken.type == Token.ATTRIBUTE && attributeTypeMap.get(secondToken.attributeName) == 1){
							if(tokenList.get(position+1).type == Token.EQ){
								if(firstToken.type == Token.TRUE){
									//System.out.println("ayo");
									return true;
								}else{
									if(firstToken.type == Token.FALSE){
										return false;
									}
								}
							}
						}
					}
				}
			}			
			
			//System.out.println("dd:"+dd+" di:"+di+" id:"+id+" ii"+ii);

				Token opToken = tokenList.get(position+1);

				if(opToken.type == Token.LT){
					//System.out.println("matched <");
					return (dvalue1 < dvalue2); //{
						//System.out.println("dd");
						//return true;

					//}else{
						//System.out.println("dd");
						//return false;

					

				}
				if(opToken.type == Token.GT){
						//System.out.println("matched >");
						//return true;
						return (dvalue1 > dvalue2);
						
				}
					
				if(opToken.type == Token.LTEQ){
					return (dvalue1 <= dvalue2);
					
				}	
				if(opToken.type == Token.GTEQ){
					return (dvalue1 >= dvalue2);
					
				}							
				if(opToken.type == Token.EQ){
					return (dvalue1 == dvalue2);
								
				}
						
					
			return false;	
			}
		


			

				//System.out.println("comparisonOutcome: "+comparisonOutcome);
			
			


		
		public void createAttributeTypeHash(){
			
			ArrayList<String> names = getAllAttributes();
			
			CyNetwork network = Cytoscape.getCurrentNetwork();
			List<Node> nodeList = network.nodesList();
			
			for(int i=0;i<names.size();i++){
				for(int j=0; j<nodeList.size(); j++){
					CyNode node = (CyNode)nodeList.get(j);
					if(nodeAttributes.hasAttribute(node.getIdentifier(), names.get(i))){
				
						int type = nodeAttributes.getType(names.get(i));
						System.out.println(type);
						attributeTypeMap.put(names.get(i), type);
						break;
					}else{
						if(edgeAttributes.hasAttribute(node.getIdentifier(), names.get(i))){
							int type = edgeAttributes.getType(names.get(i));
							attributeTypeMap.put(names.get(i), type);
							break;
						}
					}
				}
			}
		}
	
	
	
	}
	
	class Token {
		public int type;
		public static final int NONE = -2;
		public static final int END = -1;
		public static final int NUMBER = 0;
		public static final int ATTRIBUTE = 1;
		public static final int EQ = 2;
		public static final int GT = 3;
		public static final int LT = 4;
		public static final int GTEQ = 5;
		public static final int LTEQ = 6;
		public static final int AND = 7;
		public static final int OR = 8;
		public static final int NOT = 9;
		public static final int LPAREN = 10;
		public static final int RPAREN = 11;
		public static final int TRUE = 12;
		public static final int FALSE = 13;
		

		public double numberValue; // in case it is a number...
		public String attributeName; // in case it is a symbol
		public boolean booleanValue;
		
		public int getType(){
			return type;
		}
		
		Token (int _type) { type = _type; numberValue = 0; attributeName = ""; }
		Token (int _type, double _numberValue) { type = _type; numberValue = _numberValue; attributeName = ""; }
		Token (int _type, String _attributeName) { type = _type; numberValue = 0; attributeName = _attributeName; }

		
		
	}

