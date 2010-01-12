/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/**
 * Created on Jul 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.cytoscape;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;


import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import ding.view.EdgeContextMenuListener;
import ding.view.NodeContextMenuListener;

import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.DefaultSettings;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.Utilities;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;
import fr.pasteur.sysbio.rdfscape.query.AbstractQueryResultTable;
import fr.pasteur.sysbio.rdfscape.query.GraphQueryAnswerer;
import fr.pasteur.sysbio.rdfscape.query.RDQLQueryAnswerer;
import giny.model.Edge;
import giny.view.EdgeView;
import giny.view.NodeView;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PatternMenu implements NodeContextMenuListener, EdgeContextMenuListener {
	private static int uniqueID=0;
	public void addNodeContextMenuItems(NodeView node, JPopupMenu menu) {
		// TODO Auto-generated method stub
		System.out.println("Node Context Menu Activated");
		if(RDFScape.getCytoscapeDealer().isEditable()) {
			System.out.print("\tbuilding node-edit block : ");
			
			if(!RDFScape.getCytoscapeDealer().isVariable(node)) {
				JMenuItem makeNodeVar=makeNodeVar(node);
				menu.add(makeNodeVar);
			}
			else {
				if(RDFScape.getCommonMemory().cyNodeIsLiteral((CyNode)node.getNode())) {
					JMenuItem makeNodeFilter=makeNodeFilter(node);
					menu.add(makeNodeFilter);
				}
				JMenuItem unMakeNodeVar=PatternMenu.unMakeNodeVar(node);
				menu.add(unMakeNodeVar);
			}
			
		}
		JMenu extMenu=extendNetwork(node);
		menu.add(extMenu);
		System.out.println("Ok");
		
		
			
			
			
			
			
			
			/*
			myCurrentNetworkView.addContextMethod("class phoebe.PEdgeView",
					"fr.pasteur.sysbio.rdfscape.cytoscape.PatternMenu",
					"makeEdgeVar",
					new Object[] {myCurrentNetworkView},
					CytoscapeInit.getClassLoader());
			*/
			
			/*
			myCurrentNetworkView.addContextMethod("class phoebe.PEdgeView",
					"fr.pasteur.sysbio.rdfscape.cytoscape.PatternMenu",
					"unMakeEdgeVar",
					new Object[] {myCurrentNetworkView},
					CytoscapeInit.getClassLoader());
			*/
		
	}
	public void addEdgeContextMenuItems(EdgeView edge, JPopupMenu menu) {
		System.out.println("Edge Context Menu Activated");
		if(RDFScape.getCytoscapeDealer().isEditable()) {
			System.out.print("\tbuilding edge-edit block : ");
			
			if(!RDFScape.getCytoscapeDealer().isVariable(edge)) {
				JMenuItem makeEdgeVar=makeEdgeVar(edge);
				menu.add(makeEdgeVar);
			}
			else {
				JMenuItem unMakeEdgeVar=PatternMenu.unMakeEdgeVar(edge);
				menu.add(unMakeEdgeVar);
			}
			
		}
		System.out.println("Ok");
	}

	/**
	 * 
	 */
	
	
	/**
	 * NEW
	 * Make var: change the node to a variable
	 * @param node 
	 * @param args
	 * @param node
	 * @return
	 */
	public static JMenuItem makeNodeVar (NodeView node) {
		System.out.print(" make");
		JMenuItem changeNodeName=new JMenuItem("Make variable");
		CyNode cyNode=(CyNode)node.getNode();
		changeNodeName.addActionListener(new MyMakeNodeVarListener(cyNode));
		System.out.print("Var ");
		return changeNodeName;
	}
	static class  MyMakeNodeVarListener implements ActionListener {
		CyNode myCyNode=null;
		CyNetwork myCyNetwork=null;
		CyAttributes nodeAttributes=null;
		RDFScape myRDFScapeInstance=null;
		public MyMakeNodeVarListener(CyNode node) {
			myCyNode=node;
			myCyNetwork=RDFScape.getCytoscapeDealer().getCurrentNetwork();
			nodeAttributes=Cytoscape.getNodeAttributes();
		}
		
		public void actionPerformed(ActionEvent arg0) {
			uniqueID++;
			String newID="?x"+uniqueID;
			nodeAttributes.setAttribute(myCyNode.getIdentifier(),"VAR",newID);
			String tempLabel="?";
			String filter=nodeAttributes.getStringAttribute(myCyNode.getIdentifier(),"FILTER");
			if(filter!=null) tempLabel=tempLabel+"_/"+filter+"/";
			nodeAttributes.setAttribute(myCyNode.getIdentifier(),"LABEL",tempLabel);
			nodeAttributes.setAttribute(myCyNode.getIdentifier(),"COLOR","LIGHT_GRAY");
			Cytoscape.getCurrentNetworkView().redrawGraph(true,false);
			Cytoscape.getCurrentNetworkView().updateView();
		}
		
	}
	
	public static JMenuItem unMakeNodeVar ( NodeView node) {
		System.out.print("unmake");
		JMenuItem changeNodeName=new JMenuItem("Restore node");
		CyNode cyNode=(CyNode) node.getNode();
		changeNodeName.addActionListener(new MyUnMakeNodeVarListener(cyNode));
		System.out.print("Var ");
		return changeNodeName;
		
	}
	static class  MyUnMakeNodeVarListener implements ActionListener {
		CyNode myCyNode=null;
		CyNetwork myCyNetwork=null;
		CyAttributes nodeAttributes=null;
		CommonMemory myMemory;
		public MyUnMakeNodeVarListener(CyNode node) {
			myMemory=RDFScape.getCommonMemory();
			myCyNode=node;
			myCyNetwork=RDFScape.getCytoscapeDealer().getCurrentNetwork();
			nodeAttributes=Cytoscape.getNodeAttributes();
		}
		
		public void actionPerformed(ActionEvent arg0) {
			if(nodeAttributes.hasAttribute(myCyNode.getIdentifier(),"VAR"))
				nodeAttributes.deleteAttribute(myCyNode.getIdentifier(),"VAR");
			if(nodeAttributes.hasAttribute(myCyNode.getIdentifier(),"FILTER"))
				nodeAttributes.deleteAttribute(myCyNode.getIdentifier(),"FILTER");
			if(myMemory.cyNodeIsURI(myCyNode)) {
				nodeAttributes.setAttribute(myCyNode.getIdentifier(),"LABEL",myMemory.getLabelForURI(myMemory.getURIFromCyNode(myCyNode)));
				nodeAttributes.setAttribute(myCyNode.getIdentifier(),"COLOR",DefaultSettings.translateColor2String(myMemory.getNamespaceColor(myMemory.getNamespaceFromURI(myMemory.getURIFromCyNode(myCyNode)))));
			}
			if(myMemory.cyNodeIsBlank(myCyNode)) {
				nodeAttributes.setAttribute(myCyNode.getIdentifier(),"LABEL","B");
				nodeAttributes.setAttribute(myCyNode.getIdentifier(),"COLOR","LIGHT_GRAY");
			}
			if(myMemory.cyNodeIsLiteral(myCyNode)) {
				Object myLiteralNode=myMemory.getLiteralNodeForCyNode(myCyNode);
				nodeAttributes.setAttribute(myCyNode.getIdentifier(),"LABEL",myMemory.getLabelForLiteralNode(myLiteralNode));
				
				nodeAttributes.setAttribute(myCyNode.getIdentifier(),"COLOR",DefaultSettings.translateColor2String(myMemory.getNamespaceColor(myMemory.getNamespaceFromURI(myMemory.getDatatypeURIFromLiteralNode(myLiteralNode)))));
			}
			Cytoscape.getCurrentNetworkView().redrawGraph(true,false);
			Cytoscape.getCurrentNetworkView().updateView();
		}
		
	}
	
	
		
	
	public static JMenuItem makeNodeFilter ( NodeView node) {
		System.out.print("node");
		JMenuItem changeNodeName=new JMenuItem("Add filter");
		CyNode cyNode=(CyNode) node.getNode();
		
		changeNodeName.addActionListener(new MyMakeNodeFilterListener(cyNode));
		System.out.print("Filter ");
		return changeNodeName;
	}
	
	
	
	static class  MyMakeNodeFilterListener implements ActionListener {
		CyNode myCyNode=null;
		CyNetwork myCyNetwork=null;
		CommonMemory myMemory=null;
		CyAttributes nodeAttributes;
		public MyMakeNodeFilterListener(CyNode node) {
			myCyNode=node;
			myCyNetwork=RDFScape.getCytoscapeDealer().getCurrentNetwork();
			myMemory=RDFScape.getCommonMemory();
			
			nodeAttributes=Cytoscape.getNodeAttributes();
		}
		
		public void actionPerformed(ActionEvent arg0) {
			if(myMemory.cyNodeIsLiteral(myCyNode)) {
				String tempString="";
				if(nodeAttributes.hasAttribute(myCyNode.getIdentifier(),"FILTER"));
					tempString=nodeAttributes.getStringAttribute(myCyNode.getIdentifier(),"FILTER");
				String filter=JOptionPane.showInputDialog(RDFScape.getPanel(),"please enter your filter\n (/xxx/ format)",tempString);
				if(filter!=null) 
					nodeAttributes.setAttribute(myCyNode.getIdentifier(),"FILTER",filter);
					nodeAttributes.setAttribute(myCyNode.getIdentifier(),"LABEL","?_/"+filter+"/");
					Cytoscape.getCurrentNetworkView().redrawGraph(true,false);
					Cytoscape.getCurrentNetworkView().updateView();
			}
			else {
				JOptionPane.showMessageDialog(RDFScape.getPanel(),"This node does not support filters");
			}
		}
		
	}	
		
	public static JMenu extendNetwork (NodeView node) {
		/*
		RDFResourceWrapper subjectWrapped=null;
		RDFResourceWrapper propertyWrapped=null;
		RDFResourceWrapper objectWrapped=null;
		
		StmtIterator subjectOf= null;
		StmtIterator  objectOf= null;
		*/
		
		JMenu extendMenu=new JMenu("Extend");
		
		CyNetworkView myNetworkView=RDFScape.getCytoscapeDealer().getCurrentNetworkView();
		CommonMemory myMemory=RDFScape.getCommonMemory();
		KnowledgeWrapper myKnowledge=RDFScape.getKnowledgeEngine();
		
		CyNode cyNode=(CyNode) (node.getNode());
		CyAttributes nodeAttributes=Cytoscape.getNodeAttributes();
		//String nodeURI=null;
		//Resource myOntoNode=null;\
		if(nodeAttributes.hasAttribute(cyNode.getIdentifier(),"VAR")) {
			System.out.println("This is  a var!!!");
			//extendMenu.add(new JMenuItem("Variable"));
			extendMenu.setEnabled(false);
			return extendMenu;
			
		}
		
		RDQLQueryAnswerer myRDQLQueryEngine=null;
		GraphQueryAnswerer myGraphQueryAnswerer=null;
		try {
			myRDQLQueryEngine=(RDQLQueryAnswerer)myKnowledge;
		} catch (Exception e) {
			System.out.println("Only RDQL is supported now by extend functionalities");
			return extendMenu;
		}
		AbstractQueryResultTable incoming=null;
		AbstractQueryResultTable outgoing=null;
		
		
		if(!myMemory.cyNodeIsURI(cyNode) && !myMemory.cyNodeIsBlank(cyNode) && !myMemory.cyNodeIsLiteral(cyNode)) {
			System.out.println("This node doesn't come from me... maybe you want to run cytomapper...");
			extendMenu.setEnabled(false);
			extendMenu.add(new JMenuItem("Unknown to reasoner"));
			return extendMenu; // TODO this was added here as a bugfix, but perhaps teh control structure should be made cleaner
			
		}
		else {
			if(myMemory.cyNodeIsURI(cyNode)) {
				if(KnowledgeWrapper.hasRDQLSupport(myKnowledge)) {
				
					myRDQLQueryEngine=(RDQLQueryAnswerer)myKnowledge;
					System.out.println("Node is URI");
					String myNodeURI=myMemory.getURIFromCyNode(cyNode);
					String myInQuery="select ?x ?y\n WHERE (?x ?y <"+myNodeURI+">)";
					String myOutQuery="select ?x ?y\n WHERE (<"+myNodeURI+"> ?x ?y )";
			
					incoming=myRDQLQueryEngine.makeRDQLQuery(myInQuery);
					outgoing=myRDQLQueryEngine.makeRDQLQuery(myOutQuery);
			
				}
				else System.out.println("Could not find a reasoner I can ask how to move from a URI");
			}
			else if(myMemory.cyNodeIsBlank(cyNode)) {
				if(KnowledgeWrapper.hasGraphAccessSupport(myKnowledge)) {
					System.out.println("Node is blank");
					myGraphQueryAnswerer=(GraphQueryAnswerer)myKnowledge;
					Object tempNode=myMemory.getBNodeForCyNode(cyNode);
					if(tempNode!=null) {
						incoming=myGraphQueryAnswerer.getLeftOfNode(tempNode);
						outgoing=myGraphQueryAnswerer.getRightOfNode(tempNode);
					}
					else System.out.println("I don't remember anything about this blank node");
				
				
				}
				else System.out.println("Could not find a reasoner I can ask how to move from a blank node");
			}
			else if(myMemory.cyNodeIsLiteral(cyNode)) {
				if(KnowledgeWrapper.hasGraphAccessSupport(myKnowledge)) {
					System.out.println("Node is Literal");
					myGraphQueryAnswerer=(GraphQueryAnswerer)myKnowledge;
					Object tempNode=myMemory.getLiteralNodeForCyNode(cyNode);
					if(tempNode!=null) {
						incoming=myGraphQueryAnswerer.getLeftOfNode(tempNode);
						outgoing=myGraphQueryAnswerer.getRightOfNode(tempNode);
					}
					else System.out.println("I don't remember anything about this Literal");
				
				
				}
				else System.out.println("Could not find a reasoner I can ask how to move from literal");
			
			
			}
		}
		
		System.out.println("Making the menu");
		int totalItems=0;
		int incomingTotal=0;
		int outgoingTotal=0;
		if(incoming!=null) incomingTotal=incoming.getRowCount();
		if(outgoing!=null) outgoingTotal=outgoing.getRowCount();
		totalItems=incomingTotal+outgoingTotal;
		int totalDisplayable=40;
		int lastIncomingProcessed=0;
		int lastOutgoingProcessed=0;
		boolean toDisplay=false;
		
		boolean selectionInAnd=myMemory.isNamespaceConditionInAnd();
		System.out.println("Namespace selection mode: "+selectionInAnd);
		
		ArrayList rowsToSelect=new ArrayList();
		System.out.println("Incoming total "+incomingTotal);
		for(int i=0;i<incomingTotal;i++) {
			if(selectionInAnd) {
				if(incoming.isNamespaceSelected(i,0) && incoming.isNamespaceSelected(i,1)) 
					rowsToSelect.add(new Integer(i));
			}
			else {
				if(incoming.isNamespaceSelected(i,0) || incoming.isNamespaceSelected(i,1)) 
					rowsToSelect.add(new Integer(i));
			}
		}
		int[] indexes= new int[rowsToSelect.size()];
		for (int i = 0; i < indexes.length; i++) {
			indexes[i]=((Integer)rowsToSelect.get(i)).intValue();
		}
		System.out.println("Valid :"+indexes.length);
		incoming=incoming.getSubsetByRows(indexes);
			
		rowsToSelect=new ArrayList();
		System.out.println("Outgoing total "+outgoingTotal);
		for(int i=0;i<outgoingTotal;i++) {
			if(selectionInAnd) {
				if(outgoing.isNamespaceSelected(i,0) && outgoing.isNamespaceSelected(i,1)) 
					rowsToSelect.add(new Integer(i));
			}
			else {
				if(outgoing.isNamespaceSelected(i,0) || outgoing.isNamespaceSelected(i,1)) 
					rowsToSelect.add(new Integer(i));
			}
		}	
		indexes= new int[rowsToSelect.size()];
		for (int i = 0; i < indexes.length; i++) {
			indexes[i]=((Integer)rowsToSelect.get(i)).intValue();
		}
		System.out.println("Valid :"+indexes.length);
		outgoing=outgoing.getSubsetByRows(indexes);
			
		incomingTotal=incoming.getRowCount();
		outgoingTotal=outgoing.getRowCount();
		totalItems=incomingTotal+outgoingTotal;
		
		
		
		/*
		for (int i = 0; i < incomingTotal; i++) {
			totalDisplayable--;
			toDisplay=true;
			if(totalDisplayable==0) {
				JMenu mySubJMenuItem=ExtendNetworkItem(incoming,cyNode,true,lastIncomingProcessed,i+1,myMemory,myKnowledge);
				extendMenu.add(mySubJMenuItem);
				lastIncomingProcessed=i;
				totalDisplayable=40;
				toDisplay=false;
			}
		}
		if(toDisplay) {
			JMenu mySubJMenuItem=ExtendNetworkItem(incoming,cyNode,true,lastIncomingProcessed,incomingTotal,myMemory,myKnowledge);
			extendMenu.add(mySubJMenuItem);
		}
		*/
		if(incomingTotal>0) {
			JMenu myIncSubJMenuItem=ExtendNetworkItem(incoming,cyNode,true,myMemory,myKnowledge);
			extendMenu.add(myIncSubJMenuItem);
		}
		
		if(outgoingTotal>0) {
			JMenu myOutSubJMenuItem=ExtendNetworkItem(outgoing,cyNode,false,myMemory,myKnowledge);
			extendMenu.add(myOutSubJMenuItem);
		}
		/*
		totalDisplayable=40;
		for (int i = 0; i < outgoingTotal; i++) {
			totalDisplayable--;
			toDisplay=true;
			if(totalDisplayable==0) {
				JMenu mySubJMenuItem=ExtendNetworkItem(outgoing,cyNode,false,lastOutgoingProcessed,i+1,myMemory,myKnowledge);
				extendMenu.add(mySubJMenuItem);
				lastOutgoingProcessed=i;
				totalDisplayable=40;
				toDisplay=false;
			}
		}
		if(toDisplay) {
			JMenu mySubJMenuItem=ExtendNetworkItem(outgoing,cyNode,false,lastOutgoingProcessed,outgoingTotal,myMemory,myKnowledge);
			extendMenu.add(mySubJMenuItem);
		}
		*/
		
		return extendMenu;

	}
	/**
	 * TODO this is going to be obsoleted
	 * @param table
	 * @param node
	 * @param isIncoming
	 * @param start
	 * @param end
	 * @param myMemory
	 * @param myKnowledge
	 * @return
	 */
	public static JMenu ExtendNetworkItem(AbstractQueryResultTable table, CyNode node,boolean isIncoming, int start, int end,CommonMemory myMemory,KnowledgeWrapper myKnowledge) {
		String incString="->me ";
		if(isIncoming!=true) incString="me->"; 
		JMenu extendMenu=new JMenu(incString+" ("+start+","+end+")");
		if(isIncoming) {
			for (int i = start; i < end; i++) {
				System.out.println("->"+i);
				if((myMemory.isNamespaceConditionInAnd() && table.isNamespaceSelected(i,0) && table.isNamespaceSelected(i,1))
					|| (!myMemory.isNamespaceConditionInAnd() &&( table.isNamespaceSelected(i,0) || table.isNamespaceSelected(i,1)))) {
					System.out.println("Adding");
					String tempMenuString="<html>" +
					"<font color="+ Utilities.getNameSpaceColorHexString(DefaultSettings.translateColor2String(table.getColor(i,0)))+
					">"+table.getLabel(i,0) +"</font>&nbsp;&nbsp;"+
					"<font color="+ Utilities.getNameSpaceColorHexString(DefaultSettings.translateColor2String(table.getColor(i,1)))+
					">"+table.getLabel(i,1) +"</font>&nbsp;&nbsp;<b>Me</b></html>";	
					System.out.println(tempMenuString);
					MyExtAction myAction=new MyExtAction(tempMenuString,node, table,true,i,myMemory);
					JMenuItem mySubJMenuItem=new JMenuItem(myAction);
					extendMenu.add(mySubJMenuItem); 
				
				}
			}
		}		
		if(!isIncoming) {
			for (int i = start; i < end; i++) {
				System.out.println(i+"<-");
				String tempMenuString="<html><b>Me</b>&nbsp;&nbsp;" +
				"<font color="+Utilities.getNameSpaceColorHexString(DefaultSettings.translateColor2String(table.getColor(i,0)))+
				">"+table.getLabel(i,0)+"</font>&nbsp;&nbsp;"+
				"<font color="+Utilities.getNameSpaceColorHexString(DefaultSettings.translateColor2String(table.getColor(i,1)))+
				">"+table.getLabel(i,1)+"</font></html>";	
				//System.out.println(tempMenuString);
				MyExtAction myAction=new MyExtAction(tempMenuString,node, table, false,i,myMemory);
				JMenuItem mySubJMenuItem=new JMenuItem(myAction);
				extendMenu.add(mySubJMenuItem); 
					
			}
		}
		
		return extendMenu;
	}
	
	
	
	
	
	public static JMenu ExtendNetworkItem(AbstractQueryResultTable table, CyNode node,boolean isIncoming,CommonMemory myMemory,KnowledgeWrapper myKnowledge) {
		String mainMenuString="";
		if(isIncoming==true) mainMenuString="<html><b>Me</b> <i>(object)</i></html>";
		if(isIncoming!=true) mainMenuString="<html><b>Me</b> <i>(subject)</i></html>)"; 
		JMenu extendMenu=new JMenu(mainMenuString);
		if(isIncoming) {
			Hashtable<String,JMenu> propMenuList=new Hashtable<String,JMenu>();
		
			System.out.println("--> Node As Object");
			for (int i = 0; i < table.getRowCount(); i++) {
				System.out.println("(o) result #"+i);
				//if((myMemory.isNamespaceConditionInAnd() && table.isNamespaceSelected(i,0) && table.isNamespaceSelected(i,1))
				//	|| (!myMemory.isNamespaceConditionInAnd() &&( table.isNamespaceSelected(i,0) || table.isNamespaceSelected(i,1)))) {
				//		System.out.println("Matching conditions");
					String shortProp=table.getLabel(i,1);
					String longProp="<font color="+ Utilities.getNameSpaceColorHexString(DefaultSettings.translateColor2String(table.getColor(i,1)))+
					">"+shortProp +"</font>";
					JMenu myPropMenu=propMenuList.get(shortProp);
					if(myPropMenu==null) {
						myPropMenu=new JMenu("<html>"+longProp+"</html>"); 
						propMenuList.put(shortProp,myPropMenu);
					}
					String tempMenuString="<html>" +
					"<font color="+ Utilities.getNameSpaceColorHexString(DefaultSettings.translateColor2String(table.getColor(i,0)))+
					">"+table.getLabel(i,0) +"</font>&nbsp;&nbsp;"+
					"<font color="+ Utilities.getNameSpaceColorHexString(DefaultSettings.translateColor2String(table.getColor(i,1)))+
					">"+table.getLabel(i,1) +"</font>&nbsp;&nbsp;<b>Me</b></html>";	
					System.out.println(tempMenuString);
					MyExtAction myAction=new MyExtAction(tempMenuString,node, table,true,i,myMemory);
					JMenuItem mySubJMenuItem=new JMenuItem(myAction);
					myPropMenu.add(mySubJMenuItem); 
				
				//}
			}
			//PostProcessing 1
			
			//PostProcessing 2
			for (Iterator<JMenu> iterator = propMenuList.values().iterator(); iterator.hasNext();) {
			JMenu tempMenu=iterator.next();
			if(tempMenu.getItemCount()>50) {
				String tempText=tempMenu.getText();
				tempText=tempText.substring(0, tempText.indexOf("</html>"))+"&nbsp;&nbsp;<font color=red><b>Warning: "+tempMenu.getItemCount()+" items!!!</b></font></html>";
				tempMenu.setText(tempText);
			}
			extendMenu.add (tempMenu);
			
				
		}
		
		}		
		if(!isIncoming) {
			Hashtable<String,JMenu> propMenuList=new Hashtable<String,JMenu>();
			System.out.println("--> Node As Subject");
			for (int i = 0; i <table.getRowCount() ; i++) {
				System.out.println("(s) result # "+i);
				//if((myMemory.isNamespaceConditionInAnd() && table.isNamespaceSelected(i,0) && table.isNamespaceSelected(i,1))
				//		|| (!myMemory.isNamespaceConditionInAnd() &&( table.isNamespaceSelected(i,0) || table.isNamespaceSelected(i,1)))) {
				//		System.out.println("Matching conditions"); 
							
							String shortProp=table.getLabel(i,0);
							String longProp="<font color="+ Utilities.getNameSpaceColorHexString(DefaultSettings.translateColor2String(table.getColor(i,0)))+
							">"+shortProp +"</font>";
							JMenu myPropMenu=propMenuList.get(shortProp);
							if(myPropMenu==null) {
								myPropMenu=new JMenu("<html>"+longProp+"</html>"); 
								propMenuList.put(shortProp,myPropMenu);
							}
						
							
							String tempMenuString="<html><b>Me</b>&nbsp;&nbsp;" +
							"<font color="+Utilities.getNameSpaceColorHexString(DefaultSettings.translateColor2String(table.getColor(i,0)))+
							">"+table.getLabel(i,0)+"</font>&nbsp;&nbsp;"+
							"<font color="+Utilities.getNameSpaceColorHexString(DefaultSettings.translateColor2String(table.getColor(i,1)))+
							">"+table.getLabel(i,1)+"</font></html>";	
							//System.out.println(tempMenuString);
							MyExtAction myAction=new MyExtAction(tempMenuString,node, table, false,i,myMemory);
							JMenuItem mySubJMenuItem=new JMenuItem(myAction);
							myPropMenu.add(mySubJMenuItem); 
			//	}	
			}
			//PostProcessing 1
				
			//PostProcessing 2
			for (Iterator<JMenu> iterator = propMenuList.values().iterator(); iterator.hasNext();) {
				JMenu tempMenu=iterator.next();
				if(tempMenu.getItemCount()>50) {
					String tempText=tempMenu.getText();
					tempText=tempText.substring(0, tempText.indexOf("</html>"))+"&nbsp;&nbsp;<font color=red><b>"+tempMenu.getItemCount()+" items!!!</b></font></html>";
					tempMenu.setText(tempText);
				}
				extendMenu.add (tempMenu);
				
					
			}
		}
		
		return extendMenu;
	}
	private static int max(int i, int j) {
		if(i>=j) return i;
		else return j;
	}

	private static int min(int rowCount, int incomingTop) {
		if(rowCount<=incomingTop) return rowCount;
		else return incomingTop;
	}

	public static JMenuItem makeEdgeVar (EdgeView edge) {
		JMenuItem changeNodeName=new JMenuItem("Make variable");
		CyNetworkView cytoscapeView=RDFScape.getCytoscapeDealer().getCurrentNetworkView();
		CyNetwork cytoscapeNetwork=cytoscapeView.getNetwork();
		CyEdge cyEdge=(CyEdge) edge.getEdge();
		changeNodeName.addActionListener(new MyMakeEdgeVarListener(cyEdge));
		return changeNodeName;
	}
	static class  MyMakeEdgeVarListener implements ActionListener {
		Edge myEdge=null;
		CyNetwork myCyNetwork=null;
		CyAttributes myEdgeAttributes=null;
		public MyMakeEdgeVarListener(CyEdge edge) {
			myEdge=edge;
			myCyNetwork=RDFScape.getCytoscapeDealer().getCurrentNetwork();
			myEdgeAttributes=Cytoscape.getEdgeAttributes();
			
		}
		
		public void actionPerformed(ActionEvent arg0) {
			uniqueID++;
			String newID="?x"+uniqueID;
			myEdgeAttributes.setAttribute(myEdge.getIdentifier(),"VAR",newID);
			String tempLabel="?";
			String filter=myEdgeAttributes.getStringAttribute(myEdge.getIdentifier(),"FILTER");
			if(filter!=null) tempLabel=tempLabel+"_/"+filter+"/";
			myEdgeAttributes.setAttribute(myEdge.getIdentifier(),"LABEL",tempLabel);
			myEdgeAttributes.setAttribute(myEdge.getIdentifier(),"COLOR","LIGHT_GRAY");
			Cytoscape.getCurrentNetworkView().redrawGraph(true,false);
			Cytoscape.getCurrentNetworkView().updateView();
//			maybe we should issue some update here...
		}
		
	}	
		
	/*
	public static JMenuItem makeEdgeFilter (Object[] args , PNode node) {
		JMenuItem changeNodeName=new JMenuItem("Add filter");
		CyNetworkView cytoscapeView=(CyNetworkView)args[0];
		CyNetwork cytoscapeNetwork=cytoscapeView.getNetwork();
		RDFScape myRDFScapeInstance=(RDFScape)args[1];
		PEdgeView myEdge=(PEdgeView)node;
		changeNodeName.addActionListener(new MyMakeEdgeFilterListener(myEdge,cytoscapeNetwork,myRDFScapeInstance));
		return changeNodeName;
	}
	
	static class  MyMakeEdgeFilterListener implements ActionListener {
		PEdgeView myEdge=null;
		CyNetwork myCyNetwork=null;
		RDFScape myRDFScapeInstance=null;
		public MyMakeEdgeFilterListener(PEdgeView edge,CyNetwork cyNetwork,RDFScape rdfscape) {
			myEdge=edge;
			myCyNetwork=cyNetwork;
			myRDFScapeInstance=rdfscape;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			String nodeURI=(String) myCyNetwork.getEdgeAttributeValue(myEdge.getEdge(),"URI");
			String filter=JOptionPane.showInputDialog(myRDFScapeInstance.getPanel(),"please enter your filter\n (/xxx/ format)","");
			//myCyNode.setIdentifier(nodeURI);
			myCyNetwork.setEdgeAttributeValue(myEdge.getEdge(),"FILTER",filter);
//			meybe we should issue some update here...
		}
		
	}	
	*/
	public static JMenuItem unMakeEdgeVar (EdgeView edge) {
		JMenuItem changeNodeName=new JMenuItem("Restore edge");
		CyNetworkView cytoscapeView=RDFScape.getCytoscapeDealer().getCurrentNetworkView();
		CommonMemory commonMemory=RDFScape.getCommonMemory();
		CyNetwork cytoscapeNetwork=cytoscapeView.getNetwork();
		CyEdge cyEdge=(CyEdge) edge.getEdge();
		CyAttributes edgeAttributes=Cytoscape.getEdgeAttributes();
		changeNodeName.addActionListener(new MyUnMakeEdgeVarListener(cyEdge));
		
		return changeNodeName;
		
	}
	static class  MyUnMakeEdgeVarListener implements ActionListener {
		Edge myEdge=null;
		CyNetwork myCyNetwork=null;
		CyAttributes myEdgeAttributes=null;
		CommonMemory myMemory=null;
		public MyUnMakeEdgeVarListener(CyEdge edge) {
			myEdge=edge;
			myCyNetwork=RDFScape.getCytoscapeDealer().getCurrentNetwork();
			myEdgeAttributes=Cytoscape.getEdgeAttributes();
			myMemory=RDFScape.getCommonMemory();
		}
		
		public void actionPerformed(ActionEvent arg0) {
			if(myEdgeAttributes.hasAttribute(myEdge.getIdentifier(),"VAR"))
				myEdgeAttributes.deleteAttribute(myEdge.getIdentifier(),"VAR");
			if(myEdgeAttributes.hasAttribute(myEdge.getIdentifier(),"FILTER"))
				myEdgeAttributes.deleteAttribute(myEdge.getIdentifier(),"FILTER");
			
			myEdgeAttributes.setAttribute(myEdge.getIdentifier(),"LABEL",myMemory.getLabelForURI(myMemory.getURIFromCyEdge((CyEdge)myEdge)));
			myEdgeAttributes.setAttribute(myEdge.getIdentifier(),"COLOR",DefaultSettings.translateColor2String(myMemory.getNamespaceColor(myMemory.getNamespaceFromURI(myMemory.getURIFromCyEdge((CyEdge)myEdge)))));
			
			Cytoscape.getCurrentNetworkView().redrawGraph(true,false);
			Cytoscape.getCurrentNetworkView().updateView();
		}
		
	}

	
	
	
	/**
	 * Note: this is repeated in CytoscapeDealer. It should be designed better..
	 * @return
	 */
	/*
	public static  boolean hasRDQLSupport(KnowledgeWrapper knowledge) {
		try {
			RDQLQueryAnswerer test=(RDQLQueryAnswerer)knowledge;
			} catch (Exception e) {
				return false;
			}
			return true;
		
	}
		
	public static  boolean hasGraphAccessSupport(KnowledgeWrapper knowledge) {
		try {
			GraphQueryAnswerer test=(GraphQueryAnswerer)knowledge;
			} catch (Exception e) {
				return false;
			}
			return true;
		
	}*/
	
}