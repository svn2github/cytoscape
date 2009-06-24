package org.cytoscape.phylotree.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;



public class PhylipTreeImpl implements Phylotree {
 
 private String treeStr;
 private LinkedList<PhylotreeNode> nodeList = null;

 
 // Constructors
 
 /*
 * Reads a PHYLIP string
 */ 
 
 public PhylipTreeImpl(String pTreeStr){
  this.treeStr = pTreeStr;
  nodeList = new LinkedList<PhylotreeNode>();
  parse();
 }
 
 /*
 * Reads a PHYLIP file
 */ 
 
 public PhylipTreeImpl(File pTreeFile){
 
  treeStr = getTreeTextFromFile(pTreeFile);
  parse();
 }

 private String getTreeTextFromFile(File pTreeFile){
  String retStr = null;
  
  try{
            
            BufferedReader reader = new BufferedReader(new FileReader(pTreeFile));
            retStr = reader.readLine();
         return retStr;
  }      
      catch(IOException l)
      {
       return null;
      }
      
      catch(NullPointerException l)
      {
      return null;
      }
      
    
  
  //open the TreeFile, read its content
  // retStr = read the text from file
  
 
 }
 
 private void parse(){
  //This is the core of the parser
  //Parse the treeStr, and populate the data items,
  
  
   Stack <String> stack = new Stack<String>(); 
   LinkedList<String> list = new LinkedList<String>();
   LinkedList<PhylipNode> childNodeList = new LinkedList<PhylipNode>();
   LinkedList<Double> branchLengthList = new LinkedList<Double>();
   
   LinkedList<PhylipEdge> allEdges = new LinkedList<PhylipEdge>();
   
   Iterator<String> iterator;
   Iterator<PhylipNode> childNodeListIterator;
   Iterator<Double> branchLengthListIterator;
   Double branchLength = 0.0;
   // Split the input string into a list
   String [] substrings = treeStr.split(":|,|;");
   for(int i =0; i<substrings.length;i++)
   {
    substrings[i] = substrings[i].trim();
   }

   // Parse the input into a list
   for(int i = 0; i<substrings.length; i++)
   {

    if (substrings[i].charAt(0) == '(')
    {
     list.add("(");
     for (int k = 1; k<substrings[i].length(); k++)
     {
      if(substrings[i].charAt(k) == '(')
      {
       list.add("("); 
      }
      else
      {
       String[] tempSub = substrings[i].split("\\(+");

       list.add(tempSub[1]);
       break;


      }
     }         
    }

    else if(substrings[i].charAt(0) != '(' && substrings[i].charAt(0) != ')')
    {
     String[] tempSub2 = substrings[i].split("\\)");
     list.add(tempSub2[0]);
    }
    if(substrings[i].charAt(substrings[i].length()-1)== ')')
    {
     list.add(")");
    }


   }

   
   // Parse the list into a node and edge lists using a stack
   
   iterator = list.iterator();
   int tempNodeIndex = 0;
   while(iterator.hasNext())
   {
    Object tempObj = iterator.next();
    String tempStr = (String) tempObj;
    
    if(!tempStr.equals(")"))
     {
     stack.push(tempStr);
     // Ignore
     }
    if(tempStr.equals(")"))
    {
     String stackTop = stack.pop();

     while(!stackTop.equals("("))
     {

      try
      {
       branchLength = Double.parseDouble(stackTop);
      }
      catch(NumberFormatException f)
      {
       // Add a node

       PhylipNode nodeA = new PhylipNode(stackTop);
       nodeList.add(nodeA);
       // Store each node label into a list

       childNodeList.add(nodeA);
       branchLengthList.add(branchLength);
      }

      stackTop = stack.pop();
     }
     if(stackTop.equals("("))
     {
      // Add a temporary parent node
      String tempNodeLabel = "tempNode"+tempNodeIndex;
      PhylipNode tempNode = new PhylipNode(tempNodeLabel);
     
      if(stack.isEmpty())
      {
        nodeList.add(tempNode);
        
      }
      
      tempNodeIndex++;

      // Add edges between the temporary parent and the children
      childNodeListIterator = childNodeList.iterator();
      branchLengthListIterator = branchLengthList.iterator();
      int tempEdgeIndex = 0;
      while(childNodeListIterator.hasNext())
      {
       PhylipNode childNode = childNodeListIterator.next();
       String tempEdgeLabel = "edge"+tempEdgeIndex;
       
       PhylipEdge edge = new PhylipEdge(tempNode, childNode);
       Double edgeLength = branchLengthListIterator.next();
       branchLengthListIterator.remove();
       edge.setEdgeLength(edgeLength);
        System.out.println(edge.getSourceNode().getName()+"<-->"+edge.getTargetNode().getName()+":"+edgeLength);
       tempEdgeIndex++;
       allEdges.add(edge);  // temporarily
       
      }
      childNodeList.clear();

      // Add the temporary Parent node back into the stack
      stack.push(tempNodeLabel);
      

     }
    }

   } 

 
  }
  
  
 
 
 // interface methods
 public LinkedList<PhylotreeNode> getNodeList(){
  return nodeList;
 }
 
 //Get the edges for given node
 public List<PhylotreeEdge> getEdges(PhylotreeNode pNode){
  Vector<PhylotreeEdge> retValue = new Vector<PhylotreeEdge>();

  // a node could have multiple edges
  //retValue.add(connectedNode1);
  //retValue.add(connectedNode2);
  //retValue.add(connectedNodeN);
  
  return retValue;
 }
 
 public List getEdgeAttribute(PhylotreeEdge pEdge){
  return null;
 }
 
 
 // inner classes
 class PhylipNode implements PhylotreeNode {
  private String nodeName = null;
  
  public PhylipNode(String pNodeName){
   this.nodeName = pNodeName;
  }
  
  public String getName(){
   return nodeName;
  }
 }
 
 
 class PhylipEdge implements PhylotreeEdge {
  private PhylipNode sourceNode = null;
  private PhylipNode targetNode = null;
  private double edgeLength = 0.0;
  
  public PhylipEdge(PhylipNode pSourceNode, PhylipNode pTargetNode){
   this.sourceNode = pSourceNode;
   this.targetNode = pTargetNode;
  }
  
  public void setEdgeLength(double length){
   edgeLength = length; 
  }
  
  public double getEdgeLength() {
   return edgeLength;
  }
  public PhylipNode getSourceNode() {
   return sourceNode;
  }
  
  public PhylipNode getTargetNode(){
   return targetNode;
  }
 }
}
