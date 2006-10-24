/*
 * FruchtermanTheEnd.java
 *
 * Created on August 14, 2006, 5:35 PM
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
import cytoscape.view.CyNetworkView;
import giny.model.Edge;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.NodeView;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.Random;
import java.util.*;
//import cytoscape.layout.AbstractLayout;
import cytoscape.data.annotation.*;
/**
 *
 * @author ogarcia
 */
public class FruchtermanTheEnd {
    Random rand = new Random(100);
    double kr;
    
    double ka;
    
    double nodeSize;
    
    int casesNumber;
    TreeSet [][] gridHashSet;
    
    double [][] vDisp;
    double [][] vDispGoNormal;
    double [][] vDispGoStrange;
    
    double k;
    double temp;
    double tempEnCours;
    int nodeEnCours;
    int nodeEnCours2;
    
    final int numNodesInTopology;
    final double[] nodeXPositions;
    final double[] nodeYPositions;
    final double[] normalGoNodeXPositions;
    final double[] normalGoNodeYPositions;
    final double[] strangeGoNodeXPositions;
    final double[] strangeGoNodeYPositions;
    
    Vector directedEdgeVector;
    Vector undirectedEdgeVector;
    final int[] directedEdgeSourceNodeIndices;
    final int[] directedEdgeTargetNodeIndices;
    
    
    double maxLayoutDimensionAgrandie;
    final double maxLayoutDimension;
    
    double maxDistanceRepulsionEffect;
    int iterations;
    double weightGo;
    double kFactorAnnotNode;
    double kFactorAnnotNodeR2;
    double vectorFactor;
    int goIndex;
    
    HashSet goNodesNormal=new HashSet();
    HashSet goNodesStrange=new HashSet();
    String [] goNodesNormalTranslation;
    String [] goNodesStrangeTranslation;
    ArrayList[] edgeNormalGoAnnotation;
    ArrayList[] edgeStrangeGoAnnotation;
    
    Annotation annotation;
    LayoutPanel panel;
    
    /** Creates a new instance of FruchtermanTheEnd */
    
    
    public FruchtermanTheEnd(LayoutPanel pane,HashSet goNodesNorma,HashSet goNodesStrang,CyNetworkView graphView
            , String sWeightGo,String krep,String katt,String iter,String t) {
    
        panel=pane;
        goNodesNormal=goNodesNorma;
        goNodesStrange=goNodesStrang;
        
        int iterations1stPass;
        int iterations2ndPass;
        
            iterations1stPass = Integer.parseInt(panel.iterations1TF.getText());
            iterations2ndPass = Integer.parseInt(panel.iterations2TF.getText());
        
        try{
            kFactorAnnotNode=1.0/Double.parseDouble(panel.goStrength1.getText());
        }
        catch (Exception e){
            kFactorAnnotNode=1.0/4;
            panel.goStrength1.setText("0.25");
        }
        try{
            kFactorAnnotNodeR2=1.0/Double.parseDouble(panel.kFactorAnnotationNodeRound2.getText());
        }
        catch (Exception e){
            kFactorAnnotNodeR2=1.0;
            panel.kFactorAnnotationNodeRound2.setText("1");
        }
        try {
            vectorFactor=Double.parseDouble(panel.vectorFactorForStrangeThings.getText());
        }
        catch (Exception e){
            vectorFactor=100;
            panel.vectorFactorForStrangeThings.setText("100");
        }
        
        //double nodeHeight=graphView.getVizMapManager().getVisualStyle().getNodeAppearanceCalculator().getDefaultNodeHeight();
        //double nodeWidth=graphView.getVizMapManager().getVisualStyle().getNodeAppearanceCalculator().getDefaultNodeWidth();
        //nodeSize= Math.max(nodeHeight,nodeWidth);
        double nodeHeight=graphView.NODE_HEIGHT;
        double nodeWidth=graphView.NODE_WIDTH;
        nodeSize= Math.max(nodeHeight,nodeWidth);//ca marche pas!!!
        nodeSize=30;
        //System.out.println(nodeSize);
        
        if (goNodesStrange.size()<2)
            kFactorAnnotNode=kFactorAnnotNodeR2;
        
        
        
        //nodeSize=30;
        numNodesInTopology = graphView.getNodeViewCount();
        //final double maxLayoutDimension = /*Math.sqrt(kr*(double)numNodesInTopology);*/400.0d +
        //  Math.sqrt(((double) (numNodesInTopology * numNodesInTopology)) * 200.0d);
//final double maxLayoutDimension = 5000.0*numNodesInTopology 
       // ;
//final double maxLayoutDimension =100.0d +Math.sqrt((double) (numNodesInTopology) * 20000.0d);

        maxLayoutDimension =3.0*nodeSize +Math.sqrt((double) (numNodesInTopology))  * 5 * /**600*/nodeSize;
        maxLayoutDimensionAgrandie=maxLayoutDimension*2.0;
        
        
        // Definiition of nodeTranslation:
        // nodeindexTranslation[i] defines, for node at index i in our
        // GraphTopology object, the corresponding NodeView in Giny.  We just
        // need this to be able to call NodeView.setOffset() at the end - this
        // is what the legacy layout used to do.
        final NodeView[] nodeTranslation = new NodeView[numNodesInTopology];

        // Definiton of nodeIndexTranslation:
        // Both keys and values of this hashtable are java.lang.Integer objects.
        // There are exactly numNodesInTopology keys in this hashtable.
        // Key-to-value mappings define index-of-node-in-Giny to
        // index-of-node-in-GraphTopology mappings.  When I say
        // "index-of-node-in-Giny", I mean giny.model.Node.getRootGraphIndex().
        final Hashtable nodeIndexTranslation = new Hashtable();

        Iterator nodeIterator = graphView.getNodeViewsIterator();
        int nodeIndex = 0;
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        while (nodeIterator.hasNext())
        {
          NodeView currentNodeView = (NodeView) nodeIterator.next();
          nodeTranslation[nodeIndex] = currentNodeView;
          //////////////////////////System.out.println(nodeIndex);
          /*if (nodeIndexTranslation.put
              (new Integer(currentNodeView.getNode().getRootGraphIndex()),
               new Integer(nodeIndex++)) != null)
            throw new IllegalStateException("Giny farted and someone lit a match");*/
          minX = Math.min(minX, currentNodeView.getXPosition());
          maxX = Math.max(maxX, currentNodeView.getXPosition());
          minY = Math.min(minY, currentNodeView.getYPosition());
          maxY = Math.max(maxY, currentNodeView.getYPosition());
          
          nodeIndex++;
        }
         //if (nodeIndex != numNodesInTopology)
         // throw new IllegalStateException("something smells really bad here");
        
        
        
        
        for (int i = 1; i < nodeTranslation.length; i++) {
			int j = i ; 
			// get the first unsorted value ...
			//String insert_label = labels[i] ;
                        NodeView nodeInsert = nodeTranslation[i];
			String val = cytoscape.Cytoscape.getCurrentNetwork().getNode(nodeTranslation[i].getRootGraphIndex()).getIdentifier();	
			// ... and insert it among the sorted
                                
			while ((j > 0) && (val.compareTo(cytoscape.Cytoscape.getCurrentNetwork().getNode(nodeTranslation[j-1].getRootGraphIndex()).getIdentifier()) < 0)) {	
				
                                nodeTranslation[j] = nodeTranslation[j-1];
				j-- ;
			}
			// reinsert value
			nodeTranslation[j] = nodeInsert;
        }
        
        
        
        for (nodeIndex=0;nodeIndex<nodeTranslation.length;nodeIndex++){
            System.out.println(cytoscape.Cytoscape.getCurrentNetwork().getNode(nodeTranslation[nodeIndex].getRootGraphIndex()).getIdentifier());
            NodeView currentNodeView =nodeTranslation[nodeIndex];
            if (nodeIndexTranslation.put
              (new Integer(currentNodeView.getNode().getRootGraphIndex()),
               new Integer(nodeIndex)) != null)
            throw new IllegalStateException("Giny farted and someone lit a match");
        }
		
        
        //for (int i=0; i<nodeTranslation.length;i++){
            
        //}
        
        ///ICI on s'occupe des vecteurs d'edges et on fait difference entre edge diriges et les pas diriges
        directedEdgeVector = new Vector();
        undirectedEdgeVector = new Vector();
        Iterator edgeIterator = graphView.getEdgeViewsIterator();
        while (edgeIterator.hasNext())
        {
          Edge currentEdge = ((EdgeView) edgeIterator.next()).getEdge();
          int ginySourceNodeIndex = currentEdge.getSource().getRootGraphIndex();
          int ginyTargetNodeIndex = currentEdge.getTarget().getRootGraphIndex();
          int nativeSourceNodeIndex =
            ((Integer) nodeIndexTranslation.get
             (new Integer(ginySourceNodeIndex))).intValue();
          int nativeTargetNodeIndex =
            ((Integer) nodeIndexTranslation.get
             (new Integer(ginyTargetNodeIndex))).intValue();
          Vector chosenEdgeVector = undirectedEdgeVector;
          if (currentEdge.isDirected()) chosenEdgeVector = directedEdgeVector;
          chosenEdgeVector.add(new int[] { nativeSourceNodeIndex,
                                           nativeTargetNodeIndex });
        }
        directedEdgeSourceNodeIndices =
          new int[directedEdgeVector.size()];
        directedEdgeTargetNodeIndices =
          new int[directedEdgeVector.size()];
        for (int i = 0; i < directedEdgeVector.size(); i++) {
          int[] edge = (int[]) directedEdgeVector.get(i);
          directedEdgeSourceNodeIndices[i] = edge[0];
          directedEdgeTargetNodeIndices[i] = edge[1]; }
        final int[] undirectedEdgeSourceNodeIndices =
          new int[undirectedEdgeVector.size()];
        final int[] undirectedEdgeTargetNodeIndices =
          new int[undirectedEdgeVector.size()];
        for (int i = 0; i < undirectedEdgeVector.size(); i++) {
          int[] edge = (int[]) undirectedEdgeVector.get(i);
          undirectedEdgeSourceNodeIndices[i] = edge[0];
          undirectedEdgeTargetNodeIndices[i] = edge[1]; }
        
        
        
        nodeXPositions = new double[numNodesInTopology];
        nodeYPositions = new double[numNodesInTopology];
        
        
        
        final double xScaleFactor;
        if (((float) (maxX - minX)) == 0.0) xScaleFactor = 1.0d;
        else xScaleFactor = maxLayoutDimension / (maxX - minX);
        final double yScaleFactor;
        if (((float) (maxY - minY)) == 0.0) yScaleFactor = 1.0d;
        else yScaleFactor = maxLayoutDimension / (maxY - minY);/*
        for (int i = 0; i < numNodesInTopology; i++) {
           nodeXPositions[i] =
             Math.min(maxLayoutDimension,
                      Math.max(0.0d, (nodeTranslation[i].getXPosition() - minX) *
                               xScaleFactor));
           nodeYPositions[i] =
             Math.min(maxLayoutDimension,
                      Math.max(0.0d, (nodeTranslation[i].getYPosition() - minY) *
                               yScaleFactor)); }
        */
        
        //je replace tout avec coordonnees alant de -maxLayoutDimension jusqu'a +maxLayout Dimension
        for (int i = 0; i < numNodesInTopology; i++) {
           nodeXPositions[i] = Math.min(maxLayoutDimension-1.0,
                      Math.max(-maxLayoutDimension+1.0, (nodeTranslation[i].getXPosition()-   
                              /*(maxX - minX)/2*/ maxLayoutDimension)))      /*maxLayoutDimension) *xScaleFactor))*/;
             
           nodeYPositions[i] =Math.min(maxLayoutDimension-1.0,
                      Math.max(-maxLayoutDimension+1.0, (nodeTranslation[i].getYPosition()-
                                /*(maxY - minY)/2*/maxLayoutDimension)))         /*-maxLayoutDimension) *xScaleFactor))*/ ; 
        }
        
        
        
        ///////System.out.println(directedEdgeSourceNodeIndices.length+" "+undirectedEdgeSourceNodeIndices.length);
        
        
        
        
        
        try {
            this.weightGo=Double.parseDouble(sWeightGo);
        }
        catch(Exception e){
            weightGo=1;
        }
        try {
            temp=Double.parseDouble(t);
        }
        catch(Exception e){
            temp=10;
        }
        try {
            iterations=Integer.parseInt(iter);
        }
        catch(Exception e){
            iterations=500;
        }
        //double weightGo = Double.parseDouble(sWeightGo);
        
        try {
            ka = Double.parseDouble(panel.attractionForce.getText());
        }
        catch (Exception e){
            ka =10;
        }
        try {
            kr = Double.parseDouble(krep);
        }
        catch (Exception e){
            kr =(double)directedEdgeSourceNodeIndices.length / nodeTranslation.length;
        }
        
        
        
        
        
        /////////
        this.normalGoNodeXPositions=new double [this.goNodesNormal.size()];
        this.normalGoNodeYPositions=new double [this.goNodesNormal.size()];
        this.strangeGoNodeXPositions=new double [this.goNodesStrange.size()];
        this.strangeGoNodeYPositions=new double [this.goNodesStrange.size()];
        goNodesNormalTranslation=new String[this.goNodesNormal.size()];
        goNodesStrangeTranslation=new String[this.goNodesStrange.size()];
        Iterator it = this.goNodesNormal.iterator();
        int ii=0;
        while (it.hasNext()){
            goNodesNormalTranslation[ii]=(String)it.next();
            ii++;
        }
        it = this.goNodesStrange.iterator();
        ii=0;
        while (it.hasNext()){
            goNodesStrangeTranslation[ii]=(String)it.next();
            ii++;
        }
        
        
        this.edgeNormalGoAnnotation=this.buildEdgeGoAnnotation(nodeTranslation,this.goNodesNormalTranslation);
        this.edgeStrangeGoAnnotation=this.buildEdgeGoAnnotation(nodeTranslation,this.goNodesStrangeTranslation);
        
        
        if (((String)this.panel.randomize.getSelectedItem()).equals("Randomize")){
            randomize(this.normalGoNodeXPositions,normalGoNodeYPositions,maxLayoutDimension/2);
            randomize (this.strangeGoNodeXPositions,strangeGoNodeYPositions,maxLayoutDimension/2);
            randomize ( nodeXPositions,nodeYPositions,  maxLayoutDimension);
        }
        else {
            circularize (this.normalGoNodeXPositions,normalGoNodeYPositions,maxLayoutDimension/2);
            circularize (this.strangeGoNodeXPositions,strangeGoNodeYPositions,maxLayoutDimension/2);
            circularize ( nodeXPositions,nodeYPositions,  maxLayoutDimension);
            
        }
        //if (panel.circularize.isSelected()){
        //    circularize ( nodeXPositions,nodeYPositions,  maxLayoutDimension);
        //}
        /*else{
            for (int i =0;i<this.normalGoNodeXPositions.length;i++){
                if (panel.goNodesNormalPosition.containsKey(goNodesNormalTranslation[i])){
                    this.normalGoNodeXPositions[i]=((double[])panel.goNodesNormalPosition.get(goNodesNormalTranslation[i]))[0];
                    this.normalGoNodeYPositions[i]=((double[])panel.goNodesNormalPosition.get(goNodesNormalTranslation[i]))[1];
                }
            }
            for (int i =0;i<strangeGoNodeXPositions.length;i++){
                if (panel.goNodesStrangePosition.containsKey(goNodesStrangeTranslation[i])){
                    strangeGoNodeXPositions[i]=
                            ((double[])panel.goNodesStrangePosition.get(goNodesStrangeTranslation[i]))[0];
                    strangeGoNodeYPositions[i]=
                            ((double[])panel.goNodesStrangePosition.get(goNodesStrangeTranslation[i]))[1];
                }
            }
        }
        */
        
        
        
        
        //goIndex = ordonneEdgeGo(directedEdgeSourceNodeIndices,directedEdgeTargetNodeIndices,graphView,nodeTranslation);
        
        double area = maxLayoutDimension*maxLayoutDimension;
        
        k=Math.sqrt(area/numNodesInTopology);
        
        
        
        
        
        /////GRID VARIANT
        maxDistanceRepulsionEffect=3.0*k;
        casesNumber=(int)(Math.ceil((maxLayoutDimensionAgrandie/maxDistanceRepulsionEffect)+1) );
        gridHashSet = new TreeSet[casesNumber][casesNumber];
        for (int i=0;i<casesNumber;i++){
            for (int j=0;j<casesNumber;j++){
                gridHashSet[i][j]=new TreeSet();
            }
        }
        
        
        //double [] limitMinPerCase=new double[casesNumber];
        //double limitMin = -(maxLayoutDimension/2.0);
        int tempColumn;
        int tempRow;
        
        
        
        for (int i = 0;i<numNodesInTopology;i++){
            tempColumn = (int)(Math.ceil(((maxLayoutDimension)+nodeXPositions[i])/maxDistanceRepulsionEffect));
            tempRow = (int)(Math.ceil(((maxLayoutDimension)+nodeYPositions[i])/maxDistanceRepulsionEffect));
            gridHashSet[tempColumn][tempRow].add(new Integer(i));
        }
        ///////////////////////////////
        
        
        
        
        
        
        
        
        vDisp=new double [numNodesInTopology][2];
        vDispGoNormal = new double [goNodesNormalTranslation.length][2];
        vDispGoStrange = new double [goNodesStrangeTranslation.length][2];
        
        temp=maxLayoutDimension/temp;
        
        
        
       // double kAlt;
        
        int stop =1;
        if (goNodesStrangeTranslation.length>=2)
            iterations = iterations1stPass;
        
        else
            iterations = iterations2ndPass;  
        
        
        
   /////////////////MAIN LOOP
        boolean again =false;
        while (stop<=iterations){
            
            
            this.repulsionTurn(k,stop,nodeXPositions,nodeYPositions,vDisp);
            
            this.attractionTurn(k,directedEdgeSourceNodeIndices,directedEdgeTargetNodeIndices,
                    nodeXPositions,nodeYPositions,goIndex,vDisp);
            this.goAttractionTurn(k*this.kFactorAnnotNode,this.normalGoNodeXPositions,this.normalGoNodeYPositions,this.goNodesNormalTranslation,
                    this.edgeNormalGoAnnotation,nodeXPositions,nodeYPositions,vDisp,vDispGoNormal);
            this.goAttractionTurn(k*kFactorAnnotNode,this.strangeGoNodeXPositions,this.strangeGoNodeYPositions,this.goNodesStrangeTranslation,
                    this.edgeStrangeGoAnnotation,nodeXPositions,nodeYPositions,vDisp,vDispGoStrange);
            
            /////////////////////////////calculate temperature////////////////////////////////
            
            if (stop<iterations*3/4){
                tempEnCours=temp-4/3*(temp-1)*stop/(iterations);
            }
           
            else {
                tempEnCours=1;
                //tempEnCours=temp/iterations-(temp/iterations)/iterations;
            }
            //////////////////////////////////////////////////////////////////////////////////
            
            
            
            this.replacementTurn(nodeXPositions,nodeYPositions,tempEnCours,vDisp);
            this.goReplacementTurn(this.normalGoNodeXPositions,this.normalGoNodeYPositions,tempEnCours,vDispGoNormal);
            this.goReplacementTurn(this.strangeGoNodeXPositions,this.strangeGoNodeYPositions,tempEnCours,vDispGoStrange);
            
            //if (stop >iterations /5 && !again){
            //    again=true;
            //    stop=0;
            //}
            
            stop ++;
        }
        
        
        //////STRANGE THINGS
        stop =1;
        double[] finalXPositionsStrange = new double[goNodesStrangeTranslation.length];
        double[] finalYPositionsStrange = new double[goNodesStrangeTranslation.length];
        double goMinX=maxLayoutDimensionAgrandie;
        double goMaxX=-maxLayoutDimensionAgrandie;
        double goMinY=maxLayoutDimensionAgrandie;
        double goMaxY=-maxLayoutDimensionAgrandie;
        double[] startXPositionsStrange = new double[goNodesStrangeTranslation.length];
        double[] startYPositionsStrange = new double[goNodesStrangeTranslation.length];
        
        int goIndexMinX;
        int goIndexMinY;
        int goIndexMaxX;
        int goIndexMaxY;
        
        //System.out.println("on a au depart");
        
        if (this.goNodesStrangeTranslation.length>=2){
            
            iterations = iterations2ndPass;

            //////////////////////////////////////////////////////////////////////////////////////////////////
            System.out.println("on a au depart");
            double barycentreX=0;
            double barycentreY=0;
            for (int i=0;i<this.strangeGoNodeXPositions.length;i++){
                barycentreX+=strangeGoNodeXPositions[i];
                barycentreY+=strangeGoNodeYPositions[i];
                startXPositionsStrange[i]=strangeGoNodeXPositions[i];
                startYPositionsStrange[i]=strangeGoNodeYPositions[i];
                System.out.println(i+" "+startXPositionsStrange[i]+" "+startYPositionsStrange[i]);
            }
            
            
            
            
            barycentreX=barycentreX/strangeGoNodeXPositions.length;
            barycentreY=barycentreY/strangeGoNodeXPositions.length;
            
            
            
            for (int i=0;i<this.strangeGoNodeXPositions.length;i++){
                //finalXPositionsStrange[i]=strangeGoNodeXPositions[i] + 
                //        (this.vectorFactor-1)*(strangeGoNodeXPositions[i]-barycentreX);
                //finalYPositionsStrange[i]=strangeGoNodeYPositions[i] + 
                //        (this.vectorFactor-1)*(strangeGoNodeYPositions[i]-barycentreY);
                if (finalXPositionsStrange[i]<goMinX){
                    goMinX=startXPositionsStrange[i];
                    goIndexMinX=i;
                }
                if (finalXPositionsStrange[i]>goMaxX){
                    goMaxX=startXPositionsStrange[i];
                    goIndexMaxX=i;
                }
                if (finalYPositionsStrange[i]<goMinY){
                    goMinY=startYPositionsStrange[i];
                    goIndexMinY=i;
                }
                if (finalYPositionsStrange[i]>goMaxY){
                    goMaxY=startYPositionsStrange[i];
                    goIndexMaxY=i;
                }
            }
            boolean isX=false;
            double maxDiff;
            if (goMaxX-goMinX>goMaxY-goMinY){
                maxDiff =goMaxX-goMinX;
                isX=true;
            }
            else
                maxDiff=goMaxY-goMinY;
            
            double maxVectorFactor= 1.5 * maxLayoutDimension /maxDiff ;
            double vectorFactEffective = maxVectorFactor * this.vectorFactor / 100;
            System.out.println("maxLayoutDimension "+maxLayoutDimension+" maxDiff "+maxDiff +" maxVectorFactor "+maxVectorFactor);
            
            for (int i=0;i<this.strangeGoNodeXPositions.length;i++){
                finalXPositionsStrange[i]=strangeGoNodeXPositions[i] + 
                        (vectorFactEffective)*(strangeGoNodeXPositions[i]-barycentreX);
                finalYPositionsStrange[i]=strangeGoNodeYPositions[i] + 
                        (vectorFactEffective)*(strangeGoNodeYPositions[i]-barycentreY);
                if (finalXPositionsStrange[i]<goMinX){
                    goMinX=finalXPositionsStrange[i];
                    goIndexMinX=i;
                }
                if (finalXPositionsStrange[i]>goMaxX){
                    goMaxX=finalXPositionsStrange[i];
                    goIndexMaxX=i;
                }
                if (finalYPositionsStrange[i]<goMinY){
                    goMinY=finalYPositionsStrange[i];
                    goIndexMinY=i;
                }
                if (finalYPositionsStrange[i]>goMaxY){
                    goMaxY=finalYPositionsStrange[i];
                    goIndexMaxY=i;
                }
                
            
            }
            
            
            
            
            
            
            
            
            /*
            for (int i=0;i<this.strangeGoNodeXPositions.length;i++){
                finalXPositionsStrange[i]=strangeGoNodeXPositions[i] + 
                        (this.vectorFactor-1)*(strangeGoNodeXPositions[i]-barycentreX);
                finalYPositionsStrange[i]=strangeGoNodeYPositions[i] + 
                        (this.vectorFactor-1)*(strangeGoNodeYPositions[i]-barycentreY);
                if (finalXPositionsStrange[i]<goMinX){
                    goMinX=finalXPositionsStrange[i];
                    goIndexMinX=i;
                }
                if (finalXPositionsStrange[i]>goMaxX){
                    goMaxX=finalXPositionsStrange[i];
                    goIndexMaxX=i;
                }
                if (finalYPositionsStrange[i]<goMinY){
                    goMinY=finalYPositionsStrange[i];
                    goIndexMinY=i;
                }
                if (finalYPositionsStrange[i]>goMaxY){
                    goMaxY=finalYPositionsStrange[i];
                    goIndexMaxY=i;
                }
            }
            */
            if (goMinX < -3.0/4.0*maxLayoutDimension){
                double tropAGaucheDe =  -(goMinX + 3.0/4.0*maxLayoutDimension) ;
                if (goMaxX < 3.0/4.0*maxLayoutDimension - tropAGaucheDe){
                    for (int i=0;i<this.strangeGoNodeXPositions.length;i++){
                        finalXPositionsStrange[i]+=tropAGaucheDe;
                    }
                }
                else {
                    double tropLargeDe = (goMaxX-goMinX)/(maxLayoutDimension*3.0/2.0);
                    for (int i=0;i<strangeGoNodeXPositions.length;i++){
                        double difference = finalXPositionsStrange[i]-goMinX;
                        finalXPositionsStrange[i] = -3.0/4.0*maxLayoutDimension+difference/tropLargeDe;
                    }
                }
                
            }
            else if (goMaxX>3.0/4.0*maxLayoutDimension){
                double tropADroiteDe = goMaxX-3.0/4.0*maxLayoutDimension;
                if (goMinX > -3.0/4.0*maxLayoutDimension + tropADroiteDe){
                    for (int i=0;i<this.strangeGoNodeXPositions.length;i++){
                        finalXPositionsStrange[i] -= tropADroiteDe;
                    }
                }
                else {
                    double tropLargeDe = (goMaxX-goMinX)/(maxLayoutDimension*3.0/2.0);
                    for (int i=0;i<strangeGoNodeXPositions.length;i++){
                        double difference = goMaxX-finalXPositionsStrange[i];
                        finalXPositionsStrange[i]  = 3.0/4.0*maxLayoutDimension-difference/tropLargeDe;
                    }
                }
            }
            
            if (goMinY < -3.0/4.0*maxLayoutDimension){
                double tropAGaucheDe =  -(goMinY + 3.0/4.0*maxLayoutDimension) ;
                if (goMaxY < 3.0/4.0*maxLayoutDimension - tropAGaucheDe){
                    for (int i=0;i<this.strangeGoNodeYPositions.length;i++){
                        finalYPositionsStrange[i]+=tropAGaucheDe;
                    }
                }
                else {
                    double tropLargeDe = (goMaxY-goMinY)/(maxLayoutDimension*3.0/2.0);
                    for (int i=0;i<strangeGoNodeYPositions.length;i++){
                        double difference = finalYPositionsStrange[i]-goMinY;
                        finalYPositionsStrange[i] = -3.0/4.0*maxLayoutDimension+difference/tropLargeDe;
                    }
                }
                
            }
            else if (goMaxY>3.0/4.0*maxLayoutDimension){
                double tropADroiteDe = goMaxY-3.0/4.0*maxLayoutDimension;
                if (goMinY > -3.0/4.0*maxLayoutDimension + tropADroiteDe){
                    for (int i=0;i<this.strangeGoNodeYPositions.length;i++){
                        finalYPositionsStrange[i] -= tropADroiteDe;
                    }
                }
                else {
                    double tropLargeDe = (goMaxY-goMinY)/(maxLayoutDimension*3.0/2.0);
                    for (int i=0;i<strangeGoNodeYPositions.length;i++){
                        double difference = goMaxY-finalYPositionsStrange[i];
                        finalYPositionsStrange[i] = 3.0/4.0*maxLayoutDimension-difference/tropLargeDe;
                    }
                }
            }
            
            //////////////////////////////////////////////////////
            //System.out.println("on veut a la fin");
            //for (int i=0;i<this.strangeGoNodeXPositions.length;i++){
            //    System.out.println(i+" "+finalXPositionsStrange[i]+" "+finalYPositionsStrange[i]);
            //}
            if (this.panel.style.getSelectedItem().equals(panel.METHOD2)){
                double [] nodeXMouv=new double[nodeXPositions.length];
                double [] nodeYMouv=new double[nodeXPositions.length];
                int [] nodeNumberVect = new int[nodeXPositions.length];
                for (int i=0;i<nodeXPositions.length;i++){
                    nodeXMouv[i]=0;
                    nodeYMouv[i]=0;
                    nodeNumberVect[i] =0;    
                }
                
                for (int i =0;i<edgeStrangeGoAnnotation.length;i++){
                    double XMouv = finalXPositionsStrange[i]-strangeGoNodeXPositions[i];
                    double YMouv = finalYPositionsStrange[i]-strangeGoNodeYPositions[i];
                    for (int j=0;j<edgeStrangeGoAnnotation[i].size();j++){
                        //nodeXPositions[((Integer)edgeStrangeGoAnnotation[i].get(j)).intValue()]+=XMouv;
                        //nodeYPositions[((Integer)edgeStrangeGoAnnotation[i].get(j)).intValue()]+=YMouv;
                        nodeXMouv[((Integer)edgeStrangeGoAnnotation[i].get(j)).intValue()]+=XMouv;
                        nodeYMouv[((Integer)edgeStrangeGoAnnotation[i].get(j)).intValue()]+=YMouv;
                        nodeNumberVect[((Integer)edgeStrangeGoAnnotation[i].get(j)).intValue()]+=1;
                    }
                }
                for (int i=0;i<nodeXPositions.length;i++){
                    if (nodeNumberVect[i]!=0){
                        nodeXPositions[i]+=nodeXMouv[i]/nodeNumberVect[i];
                        nodeYPositions[i]+=nodeYMouv[i]/nodeNumberVect[i];
                    }
                }
            }
            
            
            
            
            
            
            if (this.panel.style.getSelectedItem().equals(panel.METHOD1) || this.panel.style.getSelectedItem().equals(panel.METHOD3)){
                stop =1;
                
                //temp = k;
                double moveTheo = ((temp-1.0)/2.0)*iterations*3.0/4.0;


                while (stop<=iterations){


                    this.repulsionTurn(k,stop,nodeXPositions,nodeYPositions,vDisp);

                    this.attractionTurn(k,directedEdgeSourceNodeIndices,directedEdgeTargetNodeIndices,
                            nodeXPositions,nodeYPositions,goIndex,vDisp);
                    this.goAttractionTurn(k*this.kFactorAnnotNodeR2,this.normalGoNodeXPositions,this.normalGoNodeYPositions,this.goNodesNormalTranslation,
                            this.edgeNormalGoAnnotation,nodeXPositions,nodeYPositions,vDisp,vDispGoNormal);
                    if (this.panel.style.getSelectedItem().equals(panel.METHOD3))
                        this.goAttractionTurn(k*kFactorAnnotNodeR2,this.strangeGoNodeXPositions,this.strangeGoNodeYPositions,this.goNodesStrangeTranslation,
                            this.edgeStrangeGoAnnotation,nodeXPositions,nodeYPositions,vDisp,vDispGoStrange);
                    else 
                        this.goAttractionTurn(k*kFactorAnnotNodeR2,finalXPositionsStrange,finalYPositionsStrange,this.goNodesStrangeTranslation,
                            this.edgeStrangeGoAnnotation,nodeXPositions,nodeYPositions,vDisp,vDispGoStrange);

                    /////////////////////////////calculate temperature////////////////////////////////

                    if (stop<iterations*3/4){
                        tempEnCours=temp-4/3*(temp-1)*stop/(iterations);
                    }

                    
                    else {
                        tempEnCours=1;
                        //tempEnCours=temp/iterations-(temp/iterations)/iterations;
                    }
                    //////////////////////////////////////////////////////////////////////////////////



                    this.replacementTurn(nodeXPositions,nodeYPositions,tempEnCours,vDisp);
                    this.goReplacementTurn(this.normalGoNodeXPositions,this.normalGoNodeYPositions,tempEnCours,vDispGoNormal);
                    
                    
                    if  (this.panel.style.getSelectedItem().equals(panel.METHOD3))
                    if (stop <= 3.0/4.0 *iterations)
                        for (int i =0;i<strangeGoNodeYPositions.length;i++){
                            strangeGoNodeXPositions[i]+= tempEnCours*(finalXPositionsStrange[i]-startXPositionsStrange[i])/moveTheo;
                            strangeGoNodeYPositions[i]+= tempEnCours*(finalYPositionsStrange[i]-startYPositionsStrange[i])/moveTheo;
                        }
                    


                    stop ++;
                }

                //////////////////////////////////////////////////////
                System.out.println(" et finalement on a ");
                for (int i=0;i<this.strangeGoNodeXPositions.length;i++){
                    System.out.println(i+" "+strangeGoNodeXPositions[i]+" "+strangeGoNodeYPositions[i]);
                }
            }
            
            
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
  ////////////////////////////////////////////////////////////////      
        
        for (int i = 0; i < nodeTranslation.length; i++) {
         // nodeTranslation[i].setOffset(nodeXPositions[i]+(maxLayoutDimensionAgrandie/2.0),  COMPRENDS PAS CETRUC !!!!!
         //                              nodeYPositions[i]+(maxLayoutDimensionAgrandie/2.0));
            nodeTranslation[i].setXPosition(nodeXPositions[i]+(maxLayoutDimension/2.0));
            nodeTranslation[i].setYPosition(nodeYPositions[i]+(maxLayoutDimension/2.0));
        //System.out.println(nodeXPositions[i]+" "+nodeYPositions[i]);
        }
    }
    
    
    
    
    
    private double fAtt(double z, double k){
        return ka*(z*z)/k;
    }
    private double fRep(double z,double k){
        //return kr*(k*k)/z;
        return kr*(k*k)/Math.max(0.001,z-nodeSize);
    }
    private double norme(double ux,double uy){
        double distance = Math.sqrt((ux*ux)+(uy*uy));
        //if (distance<0.000000001){
        //    distance=0.000000001;
            
        //}
            
        
        //System.out.println(distance);
        return distance;
    }
    
    private void circularize (double [] nodeXPositions,double [] nodeYPositions, double maxLayoutDimension){
        int nbNode=nodeXPositions.length;
        
        for (int i=0 ;i<nbNode;i++){
            double pas =((double)i)*360.0/(double)nbNode;
            nodeXPositions[i]= maxLayoutDimension/2.0*java.lang.Math.cos(pas);
            nodeYPositions[i]= maxLayoutDimension/2.0*java.lang.Math.sin(pas);
        
        }
    }
    
    private void randomize (double [] nodeXPositions,double [] nodeYPositions, double maxLayoutDimension){
        int nbNode=nodeXPositions.length;
        for (int i=0 ;i<nbNode;i++){
            nodeXPositions[i] = Math.random()* maxLayoutDimension - maxLayoutDimension/2.0;
            nodeYPositions[i]=Math.random()* maxLayoutDimension - maxLayoutDimension/2.0;
            
        }
        
    }
    
    private int ordonneEdgeGo (int[] edgeSource,int[] edgeTarget,CyNetworkView graphView,NodeView[] nodeTranslation){
        int goIndex=edgeSource.length-1;
        int temp;
        for (int i=goIndex;i>=0;i--){
            if (nodeTranslation[edgeSource[i]].getNode().getIdentifier().indexOf("Go_Id ")==0
            || nodeTranslation[edgeTarget[i]].getNode().getIdentifier().indexOf("Go_Id ")==0){
                if (i!=goIndex){
                    temp = edgeSource[i];
                    edgeSource[i]=edgeSource[goIndex];
                    edgeSource[goIndex]=temp;
                    temp = edgeTarget[i];
                    edgeTarget[i]=edgeTarget[goIndex];
                    edgeTarget[goIndex]=temp;
                    goIndex--;
                    
                }
            }
        }
        return goIndex+1;
    }
    
    private int ordonneNodeGo (int[] edgeSource,int[] edgeTarget,CyNetworkView graphView,NodeView[] nodeTranslation){
        int goIndex=edgeSource.length-1;
        int temp;
        for (int i=goIndex;i>=0;i--){
            if (nodeTranslation[edgeSource[i]].getNode().getIdentifier().indexOf("Go_Id ")==0
            || nodeTranslation[edgeTarget[i]].getNode().getIdentifier().indexOf("Go_Id ")==0){
                if (i!=goIndex){
                    temp = edgeSource[i];
                    edgeSource[i]=edgeSource[goIndex];
                    edgeSource[goIndex]=temp;
                    temp = edgeTarget[i];
                    edgeTarget[i]=edgeTarget[goIndex];
                    edgeTarget[goIndex]=temp;
                    goIndex--;
                }
            }
        }
        return goIndex+1;
    }
    
    private ArrayList[] buildEdgeGoAnnotation(NodeView[] nodeTranslation,String []goNodeTranslation){
        
        ArrayList[] retour = new ArrayList[goNodeTranslation.length];
        //ArrayList veGo = new ArrayList();
        //ArrayList veNode = new ArrayList();
        HashMap term_Annotation = panel.getTermAnnotation();
        Annotation annotation;
        String term;
        int termint;
        int[] classification;
        for (int j=0;j<goNodeTranslation.length;j++){
            term = goNodeTranslation[j];
            annotation = (Annotation)term_Annotation.get(term);
            termint = Integer.parseInt(term);
            retour[j]=new ArrayList();
            for (int i=0;i<nodeTranslation.length;i++){
                classification=annotation.getClassifications(((cytoscape.CyNode)(nodeTranslation[i].getNode())).getIdentifier());
                for (int k=0;k<classification.length;k++){
                    if (classification[k]==termint)
                        retour[j].add(new Integer(i));
                }
            }
        }
        return retour;
        
        
    }
    
    
    private void repulsionTurn(double kk, int stop,double [] nodeXPosition,double [] nodeYPosition, double[][] vDis){
        int nodeEnCours;
        int nodeEnCours2;
        double k=kk;
        double kAlt;
        double distance;
        double [] nodeXPositions=nodeXPosition;
        double [] nodeYPositions=nodeYPosition;
        double vDisp[][]=vDis;
        //double sqrtnumNodesInTranslation = Math.sqrt(numNodesInTopology);
        
        
        double [] delta=new double[2];
        
        for (int col=0;col<casesNumber;col++){
                for (int row=0;row<casesNumber;row++){
                    Iterator it= gridHashSet[col][row].iterator();
                    while (it.hasNext()){
                        nodeEnCours=((Integer)it.next()).intValue();
                        vDisp[nodeEnCours][0]=0;
                        vDisp[nodeEnCours][1]=0;
                        
                       
                        if (stop> 3/4*iterations){
                            if (nodeXPositions[nodeEnCours]<-maxLayoutDimension*3/4){
                                //distance = 6*k+.1*k*(nodeXPositions[nodeEnCours]+maxLayoutDimensionAgrandie/2.0)/maxLayoutDimensionAgrandie/4.0;
                                distance = k/2+6*k*(nodeXPositions[nodeEnCours]+maxLayoutDimension)/(maxLayoutDimension/4.0);
                                vDisp[nodeEnCours][0]+=fRep(distance,k);
                            }
                            if (nodeXPositions[nodeEnCours] >maxLayoutDimension*3/4){
                                //distance = 6*k+.1*k*(nodeXPositions[nodeEnCours]-maxLayoutDimensionAgrandie/2.0)/maxLayoutDimensionAgrandie/4.0;
                                distance = k/2-6*k*(nodeXPositions[nodeEnCours]-maxLayoutDimension)/(maxLayoutDimension/4.0);
                                vDisp[nodeEnCours][0]-=fRep(distance,k);
                            }

                            if (nodeYPositions[nodeEnCours]<-maxLayoutDimension*3/4 ){
                                //distance = 6*k+.1*k*(nodeYPositions[nodeEnCours]+maxLayoutDimensionAgrandie/2.0)/maxLayoutDimensionAgrandie/4.0;
                                distance = k/2+6*k*(nodeYPositions[nodeEnCours]+maxLayoutDimension)/(maxLayoutDimension/4.0);
                                vDisp[nodeEnCours][1]+=fRep(distance,k);
                            }
                            if (nodeYPositions[nodeEnCours] >maxLayoutDimension*3/4){
                                //distance = 6*k+.1*k*(nodeYPositions[nodeEnCours]-maxLayoutDimensionAgrandie/2.0)/maxLayoutDimensionAgrandie/4.0;
                                distance = k-6*k*(nodeYPositions[nodeEnCours]-maxLayoutDimension)/(maxLayoutDimension/4.0);
                                vDisp[nodeEnCours][1]-=fRep(distance,k);
                            }
                        }
                                                 
                        for (int i=-1;i<=1;i++){
                            if (!(col+i<0 || col+i>=casesNumber))
                            {    //continue;
                            for (int j=-1;j<=1;j++){
                                if (!(row+j<0 || row+j>=casesNumber))
                                {    //continue;
                                Iterator it2=gridHashSet[col+i][row+j].iterator();
                                while (it2.hasNext()){
                                    nodeEnCours2=((Integer)it2.next()).intValue();
                                    if (nodeEnCours!=nodeEnCours2){
                                                //System.out.println("calcul repulsion\nposition "+i+" "+nodeXPositions[i]+","+nodeYPositions[i]);
                                                //System.out.println("position "+j+" "+nodeXPositions[j]+","+nodeYPositions[j]);


                                        delta[0]=nodeXPositions[nodeEnCours]-nodeXPositions[nodeEnCours2];
                                        delta[1]=nodeYPositions[nodeEnCours]-nodeYPositions[nodeEnCours2];




                                        

                                        //System.out.println("diferrence ="+i+" "+delta[0]+","+delta[1]);

                                        distance=norme(delta[0],delta[1]);


                                        if (distance==0){
                                            if (rand.nextDouble()<0.5){
                                                nodeXPositions[nodeEnCours]+=1;
                                                nodeYPositions[nodeEnCours]+=1;
                                            }
                                            else {
                                                nodeXPositions[nodeEnCours]-=1;
                                                nodeYPositions[nodeEnCours]-=1;
                                            }
                                            if (nodeXPositions[nodeEnCours]>=maxLayoutDimension/2)
                                                nodeXPositions[nodeEnCours]-=2;
                                            if (nodeXPositions[nodeEnCours]<=-maxLayoutDimension/2)
                                                nodeXPositions[nodeEnCours]+=2;
                                            if (nodeYPositions[nodeEnCours]>=maxLayoutDimension/2)
                                                nodeYPositions[nodeEnCours]-=2;
                                            if (nodeYPositions[nodeEnCours]<=-maxLayoutDimension/2)
                                                nodeYPositions[nodeEnCours]+=2;


                                            delta[0]=nodeXPositions[nodeEnCours]-nodeXPositions[nodeEnCours2];
                                            delta[1]=nodeYPositions[nodeEnCours]-nodeYPositions[nodeEnCours2];
                                            distance=norme(delta[0],delta[1]);
                                            //System.out.println("diferrence ="+i+" "+delta[0]+","+delta[1]);
                                            //System.out.println("position "+j+" "+nodeXPositions[j]+","+nodeYPositions[j]);

                                        }

                                        if (distance<maxDistanceRepulsionEffect){
                                            if (stop>iterations*1  && stop <iterations*1/2 && distance <2*nodeSize){
                                                vDisp[nodeEnCours][0]+=(delta[0]/distance)*fRep(2*nodeSize,k);
                                                vDisp[nodeEnCours][1]+=(delta[1]/distance)*fRep(2*nodeSize,k); 
                                            }
                                            //else if (stop>iterations*1/3  && stop <iterations*3/4 && distance <2*nodeSize){
                                            //    vDisp[nodeEnCours][0]+=(delta[0]/distance)*fRep(2*nodeSize,k);
                                            //    vDisp[nodeEnCours][1]+=(delta[1]/distance)*fRep(2*nodeSize,k); 
                                            //}
                                            else {
                                                vDisp[nodeEnCours][0]+=(delta[0]/distance)*fRep(distance,k);
                                                vDisp[nodeEnCours][1]+=(delta[1]/distance)*fRep(distance,k);   
                                            }
                                        }
                                        
                                                
                                    }
                                }
                                }
                            }
                            }
                        }
                    }
                            
                    
                }
            }
    }
    private void repulsionTurn2(double kk,int stop,double [] nodeXPosition,double [] nodeYPosition, double[][] vDis){
        int nodeEnCours;
        int nodeEnCours2;
        double k=kk;
        double kAlt;
        double distance;
        double [] nodeXPositions=nodeXPosition;
        double [] nodeYPositions=nodeYPosition;
        double vDisp[][]=vDis;
        double [] delta=new double[2];
        
        
        for (int col=0;col<casesNumber;col++){
                for (int row=0;row<casesNumber;row++){
                    Iterator it= gridHashSet[col][row].iterator();
                    while (it.hasNext()){
                        nodeEnCours=((Integer)it.next()).intValue();
                        vDisp[nodeEnCours][0]=0;
                        vDisp[nodeEnCours][1]=0;
                        
                        if (nodeXPositions[nodeEnCours]<-maxLayoutDimensionAgrandie/4 ){
                            kAlt = (10*k)-((10*k)*(nodeXPositions[nodeEnCours]-maxLayoutDimensionAgrandie/2.0)
                            /(maxLayoutDimensionAgrandie/4.0));
                            vDisp[nodeEnCours][0]+=fRep(2*k,kAlt);
                        }
                        if (nodeXPositions[nodeEnCours] >maxLayoutDimensionAgrandie*3/4){
                            kAlt = (10*k)-((10*k)*(maxLayoutDimensionAgrandie/2.0-nodeXPositions[nodeEnCours])
                            /(maxLayoutDimensionAgrandie/4.0));
                            vDisp[nodeEnCours][0]-=fRep(2*k,kAlt);
                        }
                        if (nodeYPositions[nodeEnCours]<-maxLayoutDimensionAgrandie/4){
                            kAlt = (10*k)-((10*k)*(nodeYPositions[nodeEnCours]-maxLayoutDimensionAgrandie/2.0)
                            /(maxLayoutDimensionAgrandie/4.0));
                            vDisp[nodeEnCours][1]+=fRep(2*k,kAlt);
                        }
                        if (nodeYPositions[nodeEnCours] >maxLayoutDimensionAgrandie*3/4){
                            kAlt = (10*k)-((10*k)*(maxLayoutDimensionAgrandie/2.0-nodeYPositions[nodeEnCours])
                            /(maxLayoutDimensionAgrandie/4.0));
                            vDisp[nodeEnCours][1]-=fRep(2*k,kAlt);
                        }
                                                 
                        for (int i=-1;i<=1;i++){
                            if (!(col+i<0 || col+i>=casesNumber))
                            {    //continue;
                            for (int j=-1;j<=1;j++){
                                if (!(row+j<0 || row+j>=casesNumber))
                                {    //continue;
                                Iterator it2=gridHashSet[col+i][row+j].iterator();
                                while (it2.hasNext()){
                                    nodeEnCours2=((Integer)it2.next()).intValue();
                                    if (nodeEnCours!=nodeEnCours2){
                                                //System.out.println("calcul repulsion\nposition "+i+" "+nodeXPositions[i]+","+nodeYPositions[i]);
                                                //System.out.println("position "+j+" "+nodeXPositions[j]+","+nodeYPositions[j]);


                                        delta[0]=nodeXPositions[nodeEnCours]-nodeXPositions[nodeEnCours2];
                                        delta[1]=nodeYPositions[nodeEnCours]-nodeYPositions[nodeEnCours2];




                                        

                                        //System.out.println("diferrence ="+i+" "+delta[0]+","+delta[1]);

                                        distance=norme(delta[0],delta[1]);


                                        if (distance==0){
                                            if (rand.nextDouble()<0.5){
                                                nodeXPositions[nodeEnCours]+=1;
                                                nodeYPositions[nodeEnCours]+=1;
                                            }
                                            else {
                                                nodeXPositions[nodeEnCours]-=1;
                                                nodeYPositions[nodeEnCours]-=1;
                                            }
                                            if (nodeXPositions[nodeEnCours]>=maxLayoutDimension/2)
                                                nodeXPositions[nodeEnCours]-=2;
                                            if (nodeXPositions[nodeEnCours]<=-maxLayoutDimension/2)
                                                nodeXPositions[nodeEnCours]+=2;
                                            if (nodeYPositions[nodeEnCours]>=maxLayoutDimension/2)
                                                nodeYPositions[nodeEnCours]-=2;
                                            if (nodeYPositions[nodeEnCours]<=-maxLayoutDimension/2)
                                                nodeYPositions[nodeEnCours]+=2;


                                            delta[0]=nodeXPositions[nodeEnCours]-nodeXPositions[nodeEnCours2];
                                            delta[1]=nodeYPositions[nodeEnCours]-nodeYPositions[nodeEnCours2];
                                            distance=norme(delta[0],delta[1]);
                                            //System.out.println("diferrence ="+i+" "+delta[0]+","+delta[1]);
                                            //System.out.println("position "+j+" "+nodeXPositions[j]+","+nodeYPositions[j]);

                                        }

                                                
                                        if (distance<maxDistanceRepulsionEffect){
                                                

                                            if (stop>iterations*1  && stop <iterations*1/3 && distance <2*nodeSize){
                                                vDisp[nodeEnCours][0]+=(delta[0]/distance)*fRep(2*nodeSize,k);
                                                vDisp[nodeEnCours][1]+=(delta[1]/distance)*fRep(2*nodeSize,k); 
                                          }
                                            else if (stop>iterations*2/3  && stop <iterations*3/4 && distance <2*nodeSize){
                                                vDisp[nodeEnCours][0]+=(delta[0]/distance)*fRep(2*nodeSize,k);
                                                vDisp[nodeEnCours][1]+=(delta[1]/distance)*fRep(2*nodeSize,k); 
                                          }


                                            else {
                                                vDisp[nodeEnCours][0]+=(delta[0]/distance)*fRep(distance,k);
                                                vDisp[nodeEnCours][1]+=(delta[1]/distance)*fRep(distance,k);   



                                            }


                                        }

                                                //System.out.println("les deux composantes de la force de repulsion pour "+i+" ="+vDisp[i][0]+","+vDisp[i][1]);
                                                //System.out.println();
                                    }
                                }
                                }
                            }
                            }
                        }
                    }
                            
                    
                }
            }
    }
    private void attractionTurn(double kk , int[] directedEdgeSourceNodeIndice , int[] directedEdgeTargetNodeIndice,
        double[] nodeXPosition , double[] nodeYPosition,int goInde, double[][] vDis){
        int[] directedEdgeSourceNodeIndices=directedEdgeSourceNodeIndice;
        int[] directedEdgeTargetNodeIndices=directedEdgeTargetNodeIndice;
        double [] nodeXPositions=nodeXPosition;
        double [] nodeYPositions=nodeYPosition;
        int goIndex=goInde;
        double [][]vDisp = vDis;
        
        int source;
        int target;
        double distance;
        double trucEdgeX;
        double trucEdgeY;
        double k=kk;
        double [] delta=new double[2];
        
        
        for (int i=0;i<directedEdgeSourceNodeIndices.length;i++){
                source = directedEdgeSourceNodeIndices[i];
                target = directedEdgeTargetNodeIndices[i];
                        //System.out.println("calcul attraction source = "+source+" target "+target);
                
                delta[0]=nodeXPositions[source]-nodeXPositions[target];
                delta[1]=nodeYPositions[source]-nodeYPositions[target];
                
                        //System.out.println("difference ="+i+" "+delta[0]+","+delta[1]);
                
                distance = norme(delta[0],delta[1]);
                
                
                
                
                
                
                
                
                
                if (distance==0){
                            if (rand.nextDouble()<0.5){
                                nodeXPositions[source]+=1;
                                nodeYPositions[source]+=1;
                            }
                            else {
                                nodeXPositions[source]-=1;
                                nodeYPositions[source]-=1;
                            }
                            if (nodeXPositions[source]>=maxLayoutDimension/2)
                                nodeXPositions[source]-=2;
                            if (nodeXPositions[source]<=-maxLayoutDimension/2)
                                nodeXPositions[source]+=2;
                            if (nodeYPositions[source]>=maxLayoutDimension/2)
                                nodeYPositions[source]-=2;
                            if (nodeYPositions[source]<=-maxLayoutDimension/2)
                                nodeYPositions[source]+=2;
                            
                            
                            delta[0]=nodeXPositions[source]-nodeXPositions[target];
                            delta[1]=nodeYPositions[source]-nodeYPositions[target];
                            distance=norme(delta[0],delta[1]);
                                //System.out.println("difference ="+i+" "+delta[0]+","+delta[1]);
                                //System.out.println("position "+target+" "+nodeXPositions[target]+","+nodeYPositions[target]);
                            
                }
                
                
                
                
                
                        //System.out.println("distance "+distance);
                
                trucEdgeX=(delta[0]/distance)*fAtt(distance,k);
                trucEdgeY=(delta[1]/distance)*fAtt(distance,k);
                
                        //System.out.println("trucX "+trucEdgeX);
                        //System.out.println("trucY "+trucEdgeY);
                if (i<goIndex){
                    vDisp[source][0]-=trucEdgeX;
                    vDisp[source][1]-=trucEdgeY;

                                    //System.out.println("les deux composantes de la force d'attraction pour "+source+" = "+-trucEdgeX+","+-trucEdgeY);


                    vDisp[target][0]+=trucEdgeX;
                    vDisp[target][1]+=trucEdgeY;
                }
                else {
                    
                    
                    
                    vDisp[source][0]-=weightGo*trucEdgeX;
                    vDisp[source][1]-=weightGo*trucEdgeY;

                                    //System.out.println("les deux composantes de la force d'attraction pour "+source+" = "+-trucEdgeX+","+-trucEdgeY);


                    vDisp[target][0]+=weightGo*trucEdgeX;
                    vDisp[target][1]+=weightGo*trucEdgeY;
                }
                
                                //System.out.println("les deux composantes de la force d'attraction pour "+target+" = "+trucEdgeX+", "+trucEdgeY);
                                //System.out.println();
               
            }
    }
    
    private void goAttractionTurn(double kk ,double[] goNodeXPos,double[] goNodeYPos,String[] goNodeTransl,
             ArrayList[] edgeGoAnnot ,double[] nodeXPosition , double[] nodeYPosition, double[][] vDis,double[][] vDispG){
        
        String [] goNodeTranslation = goNodeTransl;
        ArrayList[] edgeGoAnnotation =  edgeGoAnnot;
        double[] goNodeXPositions = goNodeXPos;
        double[] goNodeYPositions = goNodeYPos;
        double [] nodeXPositions=nodeXPosition;
        double [] nodeYPositions=nodeYPosition;
        
        double [][]vDisp = vDis;
        double [][]vDispGo = vDispG;
        
        double distance;
        double trucEdgeX;
        double trucEdgeY;
        double k=kk;
        double [] delta=new double[2];
        
        double goXPosition;
        double goYPosition;
        int currentNodeAnnotated;
        ArrayList annotatedNodesList;
        
        
        for (int i=0;i<edgeGoAnnotation.length;i++){
            goXPosition=goNodeXPositions[i];
            goYPosition=goNodeYPositions[i];
            annotatedNodesList=edgeGoAnnotation[i];
            
            /////dans le cas ou on garde pas de repulsion pour les noeuds GO
            vDispGo[i][0]=0;
            vDispGo[i][1]=0;
            
            for (int j=0;j<annotatedNodesList.size();j++){
                
                currentNodeAnnotated=((Integer)annotatedNodesList.get(j)).intValue();
                delta[0]=goXPosition-nodeXPositions[currentNodeAnnotated];
                delta[1]=goYPosition-nodeYPositions[currentNodeAnnotated];

                    //System.out.println("difference ="+i+" "+delta[0]+","+delta[1]);

                distance = norme(delta[0],delta[1]);
                

                if (distance==0){
                        if (rand.nextDouble()<0.5){
                            goXPosition+=1;
                            goYPosition+=1;
                        }
                        else {
                            goXPosition-=1;
                            goYPosition-=1;
                        }
                        if (goXPosition>=maxLayoutDimension/2)
                            goXPosition-=2;
                        if (goXPosition<=-maxLayoutDimension/2)
                            goXPosition+=2;
                        if (goYPosition>=maxLayoutDimension/2)
                            goYPosition-=2;
                        if (goYPosition<=-maxLayoutDimension/2)
                            goYPosition+=2;


                        delta[0]=goXPosition-nodeXPositions[currentNodeAnnotated];
                        delta[1]=goYPosition-nodeYPositions[currentNodeAnnotated];
                        distance=norme(delta[0],delta[1]);
                            //System.out.println("difference ="+i+" "+delta[0]+","+delta[1]);
                            //System.out.println("position "+target+" "+nodeXPositions[target]+","+nodeYPositions[target]);

                }
                

                
                
                
                        //System.out.println("distance "+distance);
                
                trucEdgeX=(delta[0]/distance)*fAtt(distance,k);
                trucEdgeY=(delta[1]/distance)*fAtt(distance,k);
                
                        //System.out.println("trucX "+trucEdgeX);
                        //System.out.println("trucY "+trucEdgeY);
                if (i<goIndex){
                    vDispGo[i][0]-=trucEdgeX;
                    vDispGo[i][1]-=trucEdgeY;

                                    //System.out.println("les deux composantes de la force d'attraction pour "+source+" = "+-trucEdgeX+","+-trucEdgeY);


                    vDisp[currentNodeAnnotated][0]+=trucEdgeX;
                    vDisp[currentNodeAnnotated][1]+=trucEdgeY;
                }
                else {
                    
                    
                    
                    vDispGo[i][0]-=weightGo*trucEdgeX;
                    vDispGo[i][1]-=weightGo*trucEdgeY;

                                    //System.out.println("les deux composantes de la force d'attraction pour "+source+" = "+-trucEdgeX+","+-trucEdgeY);


                    vDisp[currentNodeAnnotated][0]+=weightGo*trucEdgeX;
                    vDisp[currentNodeAnnotated][1]+=weightGo*trucEdgeY;
                }
                
                                //System.out.println("les deux composantes de la force d'attraction pour "+target+" = "+trucEdgeX+", "+trucEdgeY);
                                //System.out.println();
            }   
        }
    }
    
    private void replacementTurn(double [] nodeXPosition,double [] nodeYPosition, double tempEnCour, double[][] vDis){
        int tempColumn;
        int tempRow;
        int tempColumnApres;
        int tempRowApres;
        double distance;
        double [] nodeXPositions=nodeXPosition;
        double [] nodeYPositions=nodeYPosition;
        double tempEnCours =tempEnCour;
        double [][] vDisp = vDis;
        
        for (int i=0;i<numNodesInTopology;i++){
                
////////////////////////////////////////////////////////////////////////////////   
                tempColumn = (int)(Math.ceil(((maxLayoutDimensionAgrandie/2.0)+nodeXPositions[i])/maxDistanceRepulsionEffect));
                tempRow = (int)(Math.ceil(((maxLayoutDimensionAgrandie/2.0)+nodeYPositions[i])/maxDistanceRepulsionEffect));
                //gridHashSet[tempColumn][tempRow].remove(new Integer(i));
                    
////////////////////////////////////////////////////////////////////////////////
                distance=norme(vDisp[i][0],vDisp[i][1]);
                if (distance ==0){
                    distance =0.001;
                }
                
                            //System.out.println("calcul du mouvement\nnorme pour le parametres de "+i+" ="+distance);
                
                            //System.out.println("position de "+i+" passe de "+nodeXPositions[i]+","+nodeYPositions[i]+" a ");
                
                

                
                
                nodeXPositions[i]+=(vDisp[i][0]/distance)*Math.min(distance,tempEnCours);
                nodeYPositions[i]+=(vDisp[i][1]/distance)*Math.min(distance,tempEnCours);
                
                            //System.out.println(nodeXPositions[i]+","+nodeYPositions[i]);
                


                ///////////pour eviter que ca sorte du maxLayoutDimension
                double change =Math.min(maxLayoutDimensionAgrandie/2,Math.max(-maxLayoutDimensionAgrandie/2,nodeXPositions[i]));
                if (change !=nodeXPositions[i]){
                    //if (nodeXPositions[i])
                    nodeXPositions[i]=change+k/10*rand.nextDouble();
                }
                //nodeXPositions[i]=Math.min(maxLayoutDimension/2,Math.max(-maxLayoutDimension/2,nodeXPositions[i]));
                
                 change =Math.min(maxLayoutDimensionAgrandie/2,Math.max(-maxLayoutDimensionAgrandie/2,nodeYPositions[i]));
                if (change !=nodeYPositions[i]){
                    nodeYPositions[i]=change+k/10*rand.nextDouble();
                }
                 
                 
                 
///////////////////// Si on doit changer de case dans la grille on le fait ////////////////////////////////
                tempColumnApres = (int)(Math.ceil(((maxLayoutDimensionAgrandie/2.0)+nodeXPositions[i])/maxDistanceRepulsionEffect));
                tempRowApres = (int)(Math.ceil(((maxLayoutDimensionAgrandie/2.0)+nodeYPositions[i])/maxDistanceRepulsionEffect));
                //gridHashSet[tempColumn][tempRow].add(new Integer(i));
                if (tempColumnApres != tempColumn || tempRowApres != tempRow){
                    gridHashSet[tempColumn][tempRow].remove(new Integer(i));
                    gridHashSet[tempColumnApres][tempRowApres].add(new Integer(i));
                }
/////////////////////////////////////////////////////////////////////////////
                 
                 
                 
                 
                 
                 
                //nodeYPositions[i]=Math.min(maxLayoutDimension/2,Math.max(-maxLayoutDimension/2,nodeYPositions[i]));
                            //System.out.println(nodeXPositions[i]+","+nodeYPositions[i]);
                    //System.out.println( nodeXPositions[i]);
                    //System.out.println();
                
                
            }
    }
    private void goReplacementTurn(double [] nodeXPosition,double [] nodeYPosition, double tempEnCour, double[][] vDis){
        
        double distance;
        double [] nodeXPositions=nodeXPosition;
        double [] nodeYPositions=nodeYPosition;
        double tempEnCours =tempEnCour;
        double [][] vDisp = vDis;
        
        for (int i=0;i<nodeXPositions.length;i++){
                
                distance=norme(vDisp[i][0],vDisp[i][1]);
                
                
                if (vDisp[i][0] !=0)
                    nodeXPositions[i]+=(vDisp[i][0]/distance)*Math.min(distance,tempEnCours);
                if (vDisp[i][1] !=0)
                    nodeYPositions[i]+=(vDisp[i][1]/distance)*Math.min(distance,tempEnCours);
                
                            //System.out.println(nodeXPositions[i]+","+nodeYPositions[i]);
                


                ///////////pour eviter que ca sorte du maxLayoutDimension
                double change =Math.min(maxLayoutDimensionAgrandie/2,Math.max(-maxLayoutDimensionAgrandie/2,nodeXPositions[i]));
                if (change !=nodeXPositions[i]){
                    //if (nodeXPositions[i])
                    nodeXPositions[i]=change+k/10*rand.nextDouble();
                }
                //nodeXPositions[i]=Math.min(maxLayoutDimension/2,Math.max(-maxLayoutDimension/2,nodeXPositions[i]));
                
                 change =Math.min(maxLayoutDimensionAgrandie/2,Math.max(-maxLayoutDimensionAgrandie/2,nodeYPositions[i]));
                if (change !=nodeYPositions[i]){
                    nodeYPositions[i]=change+k/10*rand.nextDouble();
                }
                
                
            }
    }
    
    
}
