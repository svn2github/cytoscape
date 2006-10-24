/*
 * DisplayPieChart.java
 *
 * Created on July 31, 2006, 4:10 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * The software and documentation provided hereunder is on an "as is" basis,
 * and the Pasteur Institut
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the
 * Pasteur Institut
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * the Pasteur Institut
 * has been advised of the possibility of such damage. See the
 * GNU General Public License for more details: 
 *                http://www.gnu.org/licenses/gpl.txt.
 *
 * Authors: Olivier Garcia
 */

package GOlorize;


import java.util.*;
import java.awt.event.*;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.Line;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.geom.Point2D;
import cytoscape.view.CyNetworkView;
import cytoscape.CyNetwork;
import cytoscape.*;
import cytoscape.data.Semantics;
import cytoscape.view.CyNodeView;
import cytoscape.visual.*;
import giny.view.EdgeView;
import giny.view.NodeView;
import cytoscape.data.annotation.Ontology;
import cytoscape.data.annotation.Annotation;
import giny.model.Node;
import giny.model.Edge;
import cytoscape.giny.PhoebeNetworkView;
import  phoebe.*;





import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.activities.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.handles.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolox.util.*;
import edu.umd.cs.piccolox.event.PNotificationCenter;
import edu.umd.cs.piccolox.event.PNotification;

import cytoscape.*;
import cytoscape.view.*;

import giny.model.*;


import giny.view.*;
import phoebe.util.*;
import  phoebe.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


import java.beans.*;


import java.util.*;

import javax.swing.event.*;

/**
 *
 * @author ogarcia
 */
public class DisplayPieChart2 implements ActionListener {
    //I have to clean usage of this :
    CyNetwork currentNetwork;
    CyNetworkView currentNetworkView;
    
    //i don't remember seems very useless
    Object[][] data;
    
    //to retrieve the selected GO term to display
    javax.swing.JTable jTable1;
    
    //quite useful to do this work (filling geneGo for example)
    Annotation annotation;
    Ontology ontology;
    //private HashSet goToDisplay;
    //private Options options;
    
    //network_Option maintiens an Option instance for each cyNetworkView in which the user has processed a GO vizu ().
    //Option contiens mainly a data structure geneGo but also useless infos
    private HashMap network_Option;
    
    //just to access the selected options in the gui
    private ResultAndStartPanel resultPanel;
    
    //relation go term / choosen color
    private HashMap goColor;
    
    //geneGo is the main data structure : keys are the nodes(protein nodes), and values are lists of go term choosen to be vizualized
    //geneGo is updated each time the user applies changes on the go term he wants to vizu.
    //there's a geneGo data structure for each cyNetworkView the user has processed a GO vizu (in the Option instance value of the network_Option HashMap).
    private HashMap geneGo;
    
    private GoBin goBin;
    private HashMap goTerm_Annotation = new HashMap();
    final String RESET="Reset";
    final String APPLY_CHANGE="Validate";
    String termToRemove;
    
    
    /** Creates a new instance of DisplayPieChart */
    public DisplayPieChart2(ResultAndStartPanel result,HashMap network_Optio) {
        this.resultPanel = result;
        this.jTable1 = result.getJTable();
        //this.data = result.data;
        
        
        
        //this.originalNetwork = result.originalNetwork;
        //this.originalNetworkView = result.originalNetworkView;
        //this.annotation = result.annotation;
        //this.ontology = result.ontology; 
        
        
        
        //this.goColor = result.goColor;
        //this.goColor = goBin.goColor;
        
        
        this.network_Option=network_Optio;
        this.goBin=result.getGoBin();
        this.goColor = goBin.getGoColor();
        
    }
    public void actionPerformed(ActionEvent ev){
        
        doIt(ev.getActionCommand(),true,Cytoscape.getCurrentNetworkView());
        //this.annotation=resultPanel.getAnnotation();
        //this.ontology=resultPanel.getOntology();
        
    }    
    
        
    public void doIt(String actionCommande,boolean general,CyNetworkView currentNetworkVie){   
        this.annotation=resultPanel.getAnnotation();
        //this.ontology=resultPanel.getOntology();
        Set currentGoSelected=new HashSet();
        
        
        
        
        goTerm_Annotation = goBin.getGoTerm_Annotation();
        
        
        
        
        if (!general)
            currentGoSelected = getSelectedGoSet(annotation, jTable1,resultPanel.getSelectColumn(),
                    resultPanel.getGoTermColumn());
        else {
            
            currentGoSelected = getSelectedGoSet(annotation, jTable1,resultPanel.getSelectColumn(),
                    resultPanel.getGoTermColumn());

            
            
            StartPanelPanel all = goBin.getStartPanel().tabAll;
            JTable jtAll = all.getJTable();
            for (int i=0;i<jtAll.getRowCount();i++){
                currentGoSelected.addAll(getSelectedGoSet(null,
                            all.getJTable(), all.getSelectColumn(),
                            resultPanel.getGoTermColumn()));
            }
            
            
            
            for (int i =1;i<=goBin.getResultPanelCount();i++){
                if (goBin.getResultPanelAt(i)!= resultPanel) 
                    currentGoSelected.addAll(getSelectedGoSet(goBin.getResultPanelAt(i).annotation,
                            goBin.getResultTableAt(i), resultPanel.getSelectColumn(),
                            resultPanel.getGoTermColumn()));
            }
        }
          
        
        
        if (termToRemove!=null){
            currentGoSelected.remove(termToRemove);
        }
        termToRemove=null;
        
        
        
        Set newGoSet = new HashSet();
        
        Set goToDelete = new HashSet();
        
        
        String st;
        
        currentNetworkView = currentNetworkVie;
        if (currentNetworkView==null){
            
        ///////////////////////////////////////////////////////////////////////////    
            
        }
        
        currentNetwork = currentNetworkView.getNetwork();
        CyNode geneNode;
        float pieNodeSize;
        
        
        if (!network_Option.containsKey(currentNetworkView)){
            if (actionCommande!=RESET){
        
                /*network_Option.put(currentNetworkView,new Options((String)resultPanel.genesLinked.getSelectedItem(),
                                                                                (String)resultPanel.goNodesView.getSelectedItem(),
                                                                                currentGoSelected,
                                                                                (String)resultPanel.genesLinkedView.getSelectedItem(),
                                                                                null)
                                                                                );*/
                network_Option.put(currentNetworkView,new PieChartInfo((String)goBin.getGenesLinked().getSelectedItem(),
                                                                                
                                                                                currentGoSelected,
                                                                                (String)goBin.getGenesLinkedView().getSelectedItem(),
                                                                                null)
                                                                                );
                //Object[] contextMen=((PGraphView)currentNetworkView).getContextMethods(PNodeView.class.toString(),true);
                newGoSet = currentGoSelected;

                geneGo =  (HashMap)((PieChartInfo)network_Option.get(currentNetworkView)).geneGo;
                Collection nodesCollection;

                if (goBin.getGenesLinked().getSelectedItem() == "All nodes in view")
                    nodesCollection = getAllNodesViewedCollection();
                else   
                    nodesCollection = currentNetworkView.getNetwork().getFlaggedNodes();


                addVisualizedGoAnnotations(newGoSet,nodesCollection);
            }
        }
        else {
            
            if (actionCommande==APPLY_CHANGE){
                Set previousGoSelected = ((PieChartInfo)network_Option.get(currentNetworkView)).goToDisplay;
                Iterator it = previousGoSelected.iterator();
                while (it.hasNext()){
                    st = (String)it.next();
                    if (!currentGoSelected.contains(st)){
                        goToDelete.add(st);
                    }
                }
                it = currentGoSelected.iterator();
                while (it.hasNext()){
                    st = (String)it.next();
                    if (!previousGoSelected.contains(st)){
                        newGoSet.add(st);
                    }
                }
                applyChanges( currentGoSelected,  goToDelete,  newGoSet);
                //System.out.println("apply change");
            }
            
            
            if (actionCommande==RESET){
                currentGoSelected.clear();
                System.out.println(currentGoSelected.size());
                goBin.getGenesLinked().setSelectedItem("All nodes in view");
                Set previousGoSelected = ((PieChartInfo)network_Option.get(currentNetworkView)).goToDisplay;
                Iterator it = previousGoSelected.iterator();
                while (it.hasNext()){
                    st = (String)it.next();
                    //if (!currentGoSelected.contains(st)){
                        goToDelete.add(st);
                    //}
                }
                
                applyChanges(currentGoSelected,goToDelete,newGoSet);
                
            }
        }
        pieNodeSize = 2f/3f;
        if (goBin.getGenesLinkedView().getSelectedItem()=="Default pie size")
            pieNodeSize = 1;
        if (goBin.getGenesLinkedView().getSelectedItem()=="Large pie size")
            pieNodeSize = 2;
        
            
        HashSet aVirer = new HashSet();
        Iterator it = geneGo.keySet().iterator();
        while (it.hasNext()){
            geneNode = (CyNode)it.next();
            
            createPieChild(geneNode,pieNodeSize);//attention createPieChild efface aussi
            if (((Set)(geneGo.get(geneNode))).isEmpty())
                aVirer.add(new Integer(geneNode.getRootGraphIndex()));
        }
        it = aVirer.iterator();
        while (it.hasNext()){
            geneGo.remove(currentNetworkView.getRootGraph().getNode(((Integer)it.next()).intValue()));
        }
        
       
        
        
        goBin.synchroSelections();
        annotation=null;
        ontology=null;
        this.goTerm_Annotation=null;
        //this.goTerm_Annotation=null;
    }
    
    //clear all pie nodes of all networkView
    public void resetAll(GoBin goBin){
        Iterator it=goBin.getNetwork_Options().keySet().iterator();
        while (it.hasNext()){
            this.doIt(RESET,true,(CyNetworkView)it.next());
        }
    }
    
    
    void applyChanges(Set currentGoSelected, Set goToDelete, Set newGoSet){    
                
                ((PieChartInfo)network_Option.get(currentNetworkView)).goToDisplay = currentGoSelected;
                geneGo = (HashMap)((PieChartInfo)network_Option.get(currentNetworkView)).geneGo;
                
                Collection nodesCollection;
                if (goBin.getGenesLinked().getSelectedItem() == "All nodes in view")
                    nodesCollection = getAllNodesViewedCollection();
                else   
                    nodesCollection = currentNetworkView.getNetwork().getFlaggedNodes();
                
                //addVisualizedGoAnnotations(newGoSet,nodesCollection);
                removeVisualizedGoAnnotations(goToDelete);
                addVisualizedGoAnnotations(newGoSet,nodesCollection);
                
    }
    
    private void addVisualizedGoAnnotations(Set newGo, Collection nodesCollection){
        String goTerm;
        
        
        CyNode geneNode;
        CyEdge goEdge;
        java.util.List edgeList = new ArrayList();
        CyEdge edge;
        NodeView geneView;
        int [] edgeToHide;
        Annotation annotation;
        
        Iterator it = newGo.iterator();
        
        while (it.hasNext()){
            goTerm = (String)it.next();
            annotation = (Annotation)goTerm_Annotation.get(goTerm);
            
            if (annotation==null){
                annotation=(Annotation)this.resultPanel.getAnnotation(goTerm);
            }
            if (annotation==null){
                annotation=(Annotation)this.goBin.getStartPanel().tabAll.getAnnotation(goTerm);
            }
            //create a new node GO  
            
            
            int goTermInt = Integer.parseInt(goTerm);
            
            Iterator it2 = nodesCollection.iterator();
            while (it2.hasNext()){
                geneNode = (CyNode)it2.next();
                
                if (!hasNodeView(geneNode))
                    continue;
                
                
                try{
                 
                    
                int[] goAnnot = annotation.getClassifications(geneNode.getIdentifier());
                
                if (goAnnot != null){
                    for (int i=0;i<goAnnot.length;i++){

                        if (goAnnot[i]==goTermInt){

                            addGoTermToGeneGo( geneNode, goTerm);
    
                        }
                    }
                }
                
                
                }
                catch (Exception e){
                    JOptionPane.showMessageDialog(goBin,"Sorry it is a bug\n the annotation of "+goTerm+" has been lost\n remove it and add it again");
                    System.out.println("!!!!!!!!!!!!!!!!!!!go_term "+goTerm+" na pas d'annot ");
                    return;
                    
                }
            }
        }
        
    }
    
    //private void removeNode()
    
    
    
    
    private void removeVisualizedGoAnnotations(Set goToHide){
        String goTerm;
        CyNode geneNode;
        Iterator it = goToHide.iterator();
        int[] nodeAloneArray = new int[1];
        while (it.hasNext()){
            goTerm = (String)it.next();
            goTerm_Annotation.remove(goTerm);
            removeGoTermFromGeneGo(goTerm);
            
                 
        }
        
        
    }
    
    
    void removeGoTermFromGeneGo(String goTerm){
        CyNode geneNode;
        Iterator it = (geneGo.keySet()).iterator();
        while (it.hasNext()){
           geneNode = (CyNode)it.next();

           HashSet hs=((HashSet)geneGo.get(geneNode));
           hs.remove(goTerm);
           
        }   
                 
    }
    
    Collection getAllNodesViewedCollection() {
            Collection genesCollectionTemp = Cytoscape.getCyNodesList();
            Collection nodesCollection = new ArrayList();
            Iterator it2 = genesCollectionTemp.iterator();
            while (it2.hasNext()){
                CyNode geneNode = (CyNode)it2.next();

                NodeView geneView = currentNetworkView.getNodeView(geneNode);
                //System.out.println();
                if (geneView!=null){
                    nodesCollection.add(geneNode);
                }
            }
            return nodesCollection;
    }
    
     
     
     
     
     //if a list of hidden nodeview exists somewhere in Cytoscape API, this part of the code should be modified :
     //without this, edges between node "GO" and hidden nodes are created.
     //because of this code, even if "all nodes in view" option is selected,
     //hidden nodes are not changed and no edges are created
     boolean hasNodeView (CyNode geneNode){
                if ( (currentNetworkView.getNodeView(geneNode))!= null)
                    if (((PNodeView)(currentNetworkView.getNodeView(geneNode))).getParent()==null)//the parent is the NodeLayer if the node is not hidden
                        return false;
                return true;
     }   
     
                        
     void addGoTermToGeneGo(CyNode geneNode, String goTerm){
            if (geneGo.get(geneNode) == null){
                HashSet hs = new HashSet(); /// the hashSet contains all the goTerms we want to vizualise for a node
                hs.add(goTerm);
                geneGo.put(geneNode,(hs));
            }
            else {
                HashSet hs=((HashSet)geneGo.get(geneNode));
                if (!(hs.contains(goTerm))){//en fait c'est inutile puisque les hashSet ne rejoute pas un truc deja present !!!
                    hs.add(goTerm);
                }
            }
    }
  
    private HashSet getSelectedGoSet (Annotation annotation, JTable jTable1,int colonneSelect,int colonneTerm){
        //return a hashmap key = GO terms selected, //n'est utile aue si on s'interesse qu'au genes refiles a bingo
        HashSet goSet = new HashSet();
        ArrayList genesList ;
        
        for (int i=0;i<jTable1.getRowCount();i++){
            if (((Boolean)jTable1.getValueAt(i,colonneSelect)).booleanValue()){
                String term = (String)jTable1.getValueAt(i ,colonneTerm);
                if (!existAndIsUnselected(term)){
                    goSet.add((String)jTable1.getValueAt(i ,colonneTerm));
                    if (!goTerm_Annotation.containsKey(term))
                        goTerm_Annotation.put(term,(Annotation)this.resultPanel.
                                getAnnotation(term));
                }
            }
        }
        return goSet;
    }
    /*
    private HashSet getSelectedGoSet (StartPanelPanel result, JTable jTable1,int colonneSelect,int colonneTerm){
        //return a hashmap key = GO terms selected, //n'est utile aue si on s'interesse qu'au genes refiles a bingo
        HashSet goSet = new HashSet();
        ArrayList genesList ;
        
        for (int i=0;i<jTable1.getRowCount();i++){
            if (((Boolean)jTable1.getValueAt(i,colonneSelect)).booleanValue()){
                String term = (String)jTable1.getValueAt(i ,colonneTerm);
                if (!existAndIsUnselected(term)){
                    goSet.add((String)jTable1.getValueAt(i ,colonneTerm));
                    if (!goTerm_Annotation.containsKey(term))
                        goTerm_Annotation.put(term,result.getAnnotation(term));
                }
            }
        }
        return goSet;
    }*/
    
    
    
    
    /*
    private boolean existAndIsUnselected(String goTerm){
        boolean b=false;
        for (int i=0;i<this.resultPanel.getJTable().getRowCount();i++){
            if (((String)this.resultPanel.getJTable().getValueAt(i ,resultPanel.getGoTermColumn())) .equals(goTerm))
                if (!((Boolean)this.resultPanel.getJTable().getValueAt(i ,resultPanel.getSelectColumn())).booleanValue())
                    b=true;
        }
        return b;
    }*/
    
    private boolean existAndIsUnselected(String goTerm){
        boolean b=false;
        for (int i=0;i<this.resultPanel.getJTable().getRowCount();i++){
            if (((String)this.resultPanel.getJTable().getValueAt(i ,resultPanel.getGoTermColumn())) .equals(goTerm))
                if (!((Boolean)this.resultPanel.getJTable().getValueAt(i ,resultPanel.getSelectColumn())).booleanValue())
                    b=true;
        }
        return b;
    }
    
    
    //a mettre dans la classe ColoredChildrenNode ?!
    void createPieChild(CyNode geneNode,float sizePieNode){
        PNodeView nv = (PNodeView)currentNetworkView.getNodeView(geneNode);
        PNode cCN = null;

        //fonction void removePieChild(cyNode geneNode)
        Iterator it2 = nv.getChildrenIterator();
        while (it2.hasNext()){
            cCN = (PNode)it2.next();
            if (! (cCN instanceof ColoredChildrenNode))
                cCN = null;
            else 
                break;
        }
        if (cCN != null) {
            nv.removeChild(cCN);

        }
        //vraie fonction void createPieChild(CyNode geneNode,float sizePieNode)
        if (goBin.getGenesLinkedView().getSelectedItem() != "No coloring"){
            if (((Set)geneGo.get(geneNode)).size() !=0){   

               cCN =new ColoredChildrenNode(currentNetworkView,geneNode,sizePieNode);
               cCN.setPickable(false);
               //nv.addChild(cCN);


            }
            //else
                //geneGo.remove(geneNode);

        }
    }
    
    class ColoredChildrenNode extends PNode {
        NodeView parentView;
        CyNode parent;
        CyNetworkView view;
        //SelectionCross selectionCross;
        double x;
        double y;
        float size;
        
        public ColoredChildrenNode (CyNetworkView view,CyNode parent,float size){
            this.parentView = view.getNodeView(parent);
            this.parent = parent;
            this.view = view;
            this.size = size;
            
            drawChild();
            
            this.setVisible(true);
            this.setPickable(false);
            ((PNode)parentView.getLabel()).moveInFrontOf(this);
            
            
        }
        
        public void drawChild (){
            HashSet goSet = (HashSet)geneGo.get(parent);//if the ColoredChidr... class is extracted of ZDispayGoNodes3, 
                                                        //we have to recover the geneGo corresponding to the current view
            
            this.x = ((PNode)(parentView)).getBounds().getWidth();
            this.y = ((PNode)(parentView)).getBounds().getHeight();
            
            Iterator it =goSet.iterator();
            int nbGo = goSet.size();
            double angleDebut = 0.0;
            double angle = 360.0/nbGo;
            while (it.hasNext()){
                String goTerm = (String)it.next();
                PPath child = new PPath();
                addChild(child);
                ((PNode)parentView).addChild(this);//bizarre mais obligatoire
                //child.setPathTo(new Arc2D.Double(0,0, this.getParent().getBounds().getWidth()*3/4 , this.getParent().getBounds().getHeight()*3/4 ,angleDebut,angle , Arc2D.PIE));
                if (size==2)
                    child.setPathTo(new Arc2D.Double(this.getParent().getBounds().getX()-this.getParent().getBounds().getWidth()/2,this.getParent().getBounds().getY()-this.getParent().getBounds().getHeight()/2, this.getParent().getBounds().getWidth()*size , this.getParent().getBounds().getHeight()*size ,angleDebut,angle , Arc2D.PIE));
                
                else
                    child.setPathTo(new Arc2D.Double(this.getParent().getBounds().getX(),this.getParent().getBounds().getY(), this.getParent().getBounds().getWidth()*size , this.getParent().getBounds().getHeight()*size ,angleDebut,angle , Arc2D.PIE));
                
                
                child.setStroke(null);
                child.setPaint((Color)goColor.get(goTerm));
                child.setPickable(false);
                
                angleDebut += angle;
            
            }
            
            
    
            
        }

        
    }
    
    
}
