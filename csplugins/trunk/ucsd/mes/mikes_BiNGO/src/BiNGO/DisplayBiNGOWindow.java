package BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: Class which creates the new CyNetwork and CyNetworkView of the 
 * * overrepresented GO graph with accompanying visual style and attributes.     
 **/


import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.data.annotation.Ontology;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.data.CyAttributes;
import giny.model.Edge;
import giny.model.Node;
import giny.view.NodeView;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;





/**
 * *****************************************************************
 * DisplayBiNGOWindow.java
 * -----------------------
 * Steven Maere & Karel Heymans (c) March 2005
 * <p/>
 * Class which creates the new CyNetwork and CyNetworkView of the
 * overrepresented GO graph.
 * <p/>
 * ******************************************************************
 */


public class DisplayBiNGOWindow {

    /*--------------------------------------------------------------
    FIELDS.
    --------------------------------------------------------------*/

    /**
     * hasmap with key termID and value pvalue.
     */
    private HashMap testMap;
    /**
     * hasmap with key termID and value corrected pvalue.
     */
    private HashMap correctionMap;
    /**
     * hashmap with key termID and value x.
     */
    private HashMap mapSmallX;
    /**
     * hashmap with key termID and value n.
     */
    private HashMap mapSmallN;
    /**
     * hashmap with key termID and value X.
     */
    private HashMap mapBigX;
    /**
     * hashmap with key termID and value N.
     */
    private HashMap mapBigN;
    /**
     * String with significance level.
     */
    private String alpha;
    /**
     * String with cluster name.
     */
    private String clusterName;
    /**
     * string with number of categories in the graph.
     */
    private String categoriesString;
    /**
     * the ontology.
     */
    private Ontology ontology;
    /**
     * final defaultsize for the size of the nodes.
     */
    private final Double DEFAULT_SIZE = new Double(1);
    /**
     * final sizefactor for the size of the nodes.
     */
    private final int MAX_SIZE = 50;
    /**
     * final String for default name sif-file.
     */
    private final String SIFFILENAME = "BiNGO.sif";
    /**
     * constant string for the checking of numbers of categories, all categories.
     */
    private final String CATEGORY_ALL = BingoAlgorithm.CATEGORY;
    /**
     * constant string for the checking of numbers of categories, before correction.
     */
    private final String CATEGORY_BEFORE_CORRECTION = BingoAlgorithm.CATEGORY_BEFORE_CORRECTION;

    /**
     * constant string for the checking of numbers of categories, after correction.
     */
    private final String CATEGORY_CORRECTION = BingoAlgorithm.CATEGORY_CORRECTION;
    /**
     * scale for BigDecimal
     */
    private static final int SCALE_RESULT = 100;

    /*--------------------------------------------------------------
    CONSTRUCTORS.
    --------------------------------------------------------------*/

    /**
     * Constructor for an overrepresentation visualization without correction.
     *
     * @param testMap HashMap with key: termID and value: pvalue.
     * @param mapSmallX HashMap with key: termID and value: #genes in selection with GO termID.
     * @param mapSmallN HashMap with key: termID and value: #genes in reference with GO termID.
     * @param bigX int with value of X (# of selected genes).
     * @param bigN int with value of N (total # genes in reference).
     * @param alpha String with value for significance level.
     * @param ontology  the selected ontology.
     * @param SettingsSavePanel save or not and if save where.
     * @param categoriesString String with option what categories should be displayed.
     */
    /*public DisplayBiNGOWindow(HashMap testMap,
                                HashMap mapSmallX,
                                HashMap mapSmallN,
                                int bigX,
                                int bigN,
                                String alpha,
                                Ontology ontology,
                                String clusterName,
                                SettingsSavePanel dataPanel,
                                String categoriesString){

            this.testMap = testMap;
            this.correctionMap = null;
            this.mapSmallX = mapSmallX;
            this.mapSmallN = mapSmallN;
            this.bigX = new Integer(bigX);
            this.bigN = new Integer(bigN);
            this.alpha = alpha;
            this.ontology = ontology;
            this.clusterName = clusterName ;
            this.dataPanel = dataPanel;
            this.categoriesString = categoriesString;
        }
    */

    /**
     * Constructor for an overrepresentation visualization with correction.
     *
     * @param testMap          HashMap with key: termID and value: pvalue.
     * @param correctionMap    HashMap with key: termID and value: corrected pvalue.
     * @param mapSmallX        HashMap with key: termID and value: #genes in selection with GO termID.
     * @param mapSmallN        HashMap with key: termID and value: #genes in reference with GO termID.
     * @param bigX             int with value of X (# of selected genes).
     * @param bigN             int with value of N (total # genes in reference).
     * @param alpha            String with value for significance level.
     * @param ontology         the selected ontology.
     * @param categoriesString String with option what categories should be displayed.
     */
    public DisplayBiNGOWindow(HashMap testMap,
                              HashMap correctionMap,
                              HashMap mapSmallX,
                              HashMap mapSmallN,
                              HashMap mapBigX,
                              HashMap mapBigN,
                              String alpha,
                              Ontology ontology,
                              String clusterName,
                              String categoriesString) {

        this.testMap = testMap;
        this.correctionMap = correctionMap;
        this.mapSmallX = mapSmallX;
        this.mapSmallN = mapSmallN;
        this.mapBigX = mapBigX;
        this.mapBigN = mapBigN;
        this.alpha = alpha;
        this.ontology = ontology;
        this.clusterName = clusterName;
        this.categoriesString = categoriesString;
    }

    /*--------------------------------------------------------------
    METHODS.
    --------------------------------------------------------------*/

    /**
     * Method that builds up the new CyNetwork and shows it to the user.
     */
    public void makeWindow() {
        CyNetwork network = buildNetwork();
        buildNodeAttributes(network);
        buildEdgeAttributes(network);
        CyNetworkView bingoCyNetworkView = Cytoscape.createNetworkView(network, clusterName);

        //from MCODE plugin, MCODEResultsDialog class
        //layout graph and fit it to window
        //randomize node positions before layout so that they don't all layout in a line
        //(so they don't fall into a local minimum for the SpringEmbedder)
        //If the SpringEmbedder implementation changes, this code may need to be removed
        NodeView nv;
        for (Iterator in = bingoCyNetworkView.getNodeViewsIterator(); in.hasNext();) {
            nv = (NodeView) in.next();
            nv.setXPosition(nv.getXPosition() * Math.random());
            //height is small for many default drawn graphs, thus +100
            nv.setYPosition((nv.getYPosition() + 100) * Math.random());
        }

        //apply spring embedded layout...
        SpringEmbeddedLayouter spring = new SpringEmbeddedLayouter(bingoCyNetworkView);
        //  Configure JTask
        JTaskConfig config = new JTaskConfig();

        //  Show Cancel/Close Buttons
        config.displayCancelButton(true);
        config.displayStatus(true);

        //  Execute Task via TaskManager
        //  This automatically pops-open a JTask Dialog Box.
        //  This method will block until the JTask Dialog Box is disposed.
        boolean success = TaskManager.executeTask(spring, config);


        Cytoscape.getDesktop().toFront();
        bingoCyNetworkView.fitContent();
        //create visual style with ID dependent on clusterName
        TheVisualStyle vs = new TheVisualStyle(clusterName, (new Double(alpha)).doubleValue());
        VisualStyle visualStyle = vs.createVisualStyle(network);
        CytoscapeDesktop cytoscapedesktop = Cytoscape.getDesktop();
        VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
        try{
            vmm.getCalculatorCatalog().addVisualStyle(visualStyle);
            //VisualStyle oldStyle = vmm.setVisualStyle(visualStyle.getName());
            bingoCyNetworkView.applyVizmapper(visualStyle);
            //bingoCyNetworkView.setVisualStyle(visualStyle.getName());
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(cytoscapedesktop, "A visual style already exists for the cluster name you specified." + "\n" + "The existing style will be overwritten.");
            vs.adaptVisualStyle(vmm.getCalculatorCatalog().getVisualStyle(visualStyle.getName()),network);
            //VisualStyle oldStyle = vmm.setVisualStyle(visualStyle.getName());
            bingoCyNetworkView.applyVizmapper(vmm.getCalculatorCatalog().getVisualStyle(visualStyle.getName()));
            //bingoCyNetworkView.setVisualStyle(visualStyle.getName());
        }

        // add color scale panel
        JFrame window = new JFrame(clusterName + " Color Scale");
        String alpha1 = SignificantFigures.sci_format(alpha, 3);
        String tmp = (new BigDecimal(alpha)).divide(new BigDecimal("100000"), SCALE_RESULT, BigDecimal.ROUND_HALF_UP).toString();
        String alpha2 = SignificantFigures.sci_format(tmp, 3);
        ColorPanel colPanel = new ColorPanel(alpha1, alpha2, new Color(255, 255, 0), new Color(255, 127, 0));
        window.getContentPane().add(colPanel);
        window.getContentPane().setBackground(Color.WHITE);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.pack();
        // for bottom right position of the color scale panel.
        window.setLocation(screenSize.width - window.getWidth() - 10, screenSize.height - window.getHeight() - 30);
        window.setVisible(true);
        window.setResizable(false);

    }


    /**
     * Method that builds up the new network.
     *
     * @return CyNetwork the network that was built.
     */

    public CyNetwork buildNetwork() {
        HashSet set;
        if (testMap != null) {
            set = new HashSet(testMap.keySet());
        } else {
            set = new HashSet(mapSmallX.keySet());
        }
        Iterator iterator = set.iterator();
        // put the edges in a set of Strings
        HashSet sifSet = new HashSet();
        // some GO labels might have multiple termIds, which need to be canonicalized.
        //(for safety, has also been taken care of in BiNGOOntologyFlatFilereader)
        HashMap nameMap = new HashMap();

        CyNetwork network = Cytoscape.createNetwork(clusterName);
        
        while (iterator.hasNext()) {
            int termID = new Integer(iterator.next().toString()).intValue();
            // ifs for determining GO coverage of graphs.
            if (categoriesString.equals(CATEGORY_ALL) ||
                    (categoriesString.equals(CATEGORY_BEFORE_CORRECTION) && new BigDecimal(testMap.get(new Integer(termID)).toString()).compareTo(new BigDecimal(alpha)) < 0) ||
                    (categoriesString.equals(CATEGORY_CORRECTION) && new BigDecimal(correctionMap.get(termID + "").toString()).compareTo(new BigDecimal(alpha)) < 0))
            {

                int [] [] paths = ontology.getAllHierarchyPaths(termID);

                int previousNode;
                for (int i = 0; i < paths.length; i++) {
                    previousNode = paths[i][0];
                    // for singleton nodes
                    if ((paths.length == 1) && (paths[i].length == 1)) {
                        sifSet.add(previousNode + "\n");
                    }
                    //first substring added to any map value will be null
                    nameMap.put(ontology.getTerm(previousNode).getName(),
                            nameMap.get(ontology.getTerm(previousNode).getName()) +
                                    " " + previousNode);
                    for (int j = 1; j < paths[i].length; j++) {
                        //first substring added to any map value will be null
                        nameMap.put(ontology.getTerm(paths[i][j]).getName(),
                                nameMap.get(ontology.getTerm(paths[i][j]).getName()) + " " + paths[i][j]);
                        sifSet.add(previousNode + " pp " + paths[i][j] + "\n");
                        previousNode = paths[i][j];
                    }
                }
            }
        }

        // canonicalize nodes.
        HashMap termIdMap = makeTermIdMap(nameMap);

        // canonicalize edges and build network.

        Iterator sifIterator = sifSet.iterator();

        while (sifIterator.hasNext()) {
            String writeString = sifIterator.next().toString();
            StringTokenizer st = new StringTokenizer(writeString);
            String firstTermId = termIdMap.get(st.nextToken()).toString();
            if (st.hasMoreTokens()) {
                st.nextToken();
                String secondTermId = termIdMap.get(st.nextToken()).toString();
                Node node1 = (Node) Cytoscape.getCyNode(firstTermId, true);
                //add actionlistener for feedback to original network (the one you analyze)
                Node node2 = (Node) Cytoscape.getCyNode(secondTermId, true);
                //network build procedure does not allow duplicate edges
                Edge edge = (Edge) Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, "pp", true);
                network.addNode(node1);
                network.addNode(node2);
                network.addEdge(edge);
            } else {//singular nodes
                Node node1 = (Node) Cytoscape.getCyNode(firstTermId, true);
                network.addNode(node1);
            }
        }

        return network;

    }


    /**
     * method that makes a termid map with as key a termid and as value
     * the termid which it is equal to.
     * e.g.
     * 8125 = 8125
     * 654 = 8125
     * 6546 = 8125
     * this allows to map every termid that is the same to a unique termid.
     *
     * @param nameMap key: termid, value: string with termids
     * @return HashMap key: termid, value: termid
     */

    public HashMap makeTermIdMap(HashMap nameMap) {

        HashSet set = new HashSet(nameMap.keySet());
        Iterator iterator = set.iterator();
        HashMap resultMap = new HashMap();

        while (iterator.hasNext()) {
            // first substring null deleted at beginning of every string
            String valueIDs = nameMap.get(iterator.next().toString()).toString().substring(5);
            StringTokenizer st = new StringTokenizer(valueIDs);
            String termID = st.nextToken();
            resultMap.put(termID, termID);
            while (st.hasMoreTokens()) {
                resultMap.put(st.nextToken(), termID);
            }
        }
        return resultMap;
    }


    /**
     * Method that creates the node attributes (size, color, ...).
     */
    public void buildNodeAttributes(CyNetwork network) {

        Iterator i = network.nodesIterator();
        while (i.hasNext()) {

            Node node = (Node) i.next();
            String termID = node.getIdentifier();
            String shape;
            String description;
            String pValue;
            String adj_pValue;
            String smallX;
            String smallN;
            String bigX;
            String bigN;
            //String color;
            Double color;
            Double size;
            shape = "ellipse";

            try {
                description = ontology.getTerm(Integer.parseInt(termID)).getName();
            }
            catch (Exception e) {
                description = "?";
            }

            try {
                if (testMap != null) {
                    pValue = SignificantFigures.sci_format(testMap.get(new Integer(termID)).toString(), 5);
                } else {
                    pValue = "N/A";
                }
            }
            catch (Exception e) {
                pValue = "N/A";
            }
            try {
                if (correctionMap != null) {
                    adj_pValue = SignificantFigures.sci_format(correctionMap.get(termID).toString(), 5);
                } else {
                    adj_pValue = "N/A";
                }
            }
            catch (Exception e) {
                adj_pValue = "N/A";
            }
            try {
                smallX = mapSmallX.get(new Integer(termID)).toString();
            }
            catch (Exception e) {
                smallX = "N/A";
            }
            try {
                smallN = mapSmallN.get(new Integer(termID)).toString();
            }
            catch (Exception e) {
                smallN = "N/A";
            }
             try {
                bigX = mapBigX.get(new Integer(termID)).toString();
            }
            catch (Exception e) {
                bigX = "N/A";
            }
            try {
                bigN = mapBigN.get(new Integer(termID)).toString();
            }
            catch (Exception e) {
                bigN = "N/A";
            }
            try {
                if (testMap == null) {
                    color = new Double(0);
                } else if (correctionMap == null) {
                    double a = -(Math.log((new BigDecimal(testMap.get(new Integer(termID)).toString())).doubleValue()) / Math.log(10));
                    color = new Double(a);
                } else {
                    double a = -(Math.log((new BigDecimal(correctionMap.get(termID).toString())).doubleValue()) / Math.log(10));
                    color = new Double(a);
                }
            }
            catch (Exception e) {
                color = new Double(0);
            }
            try {
                double numberOfGenes = new Integer(mapSmallX.get(new Integer(termID)).toString()).doubleValue();
                numberOfGenes = Math.sqrt(numberOfGenes) * 2;
                size = new Double(numberOfGenes);
            }
            catch (Exception e) {
                size = DEFAULT_SIZE;
            }
            
            CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
            nodeAttrs.setAttribute(node.getIdentifier(), "pValue_" + clusterName, pValue);
            nodeAttrs.setAttribute(node.getIdentifier(), "adjustedPValue_" + clusterName, adj_pValue);
            nodeAttrs.setAttribute(node.getIdentifier(), "x_"+ clusterName, smallX);
            nodeAttrs.setAttribute(node.getIdentifier(), "X_"+ clusterName, bigX);
            nodeAttrs.setAttribute(node.getIdentifier(), "n_"+ clusterName, smallN);
            nodeAttrs.setAttribute(node.getIdentifier(), "N_"+ clusterName, bigN);
            nodeAttrs.setAttribute(node.getIdentifier(), "description_"+ clusterName, description);	
            nodeAttrs.setAttribute(node.getIdentifier(), "nodeFillColor_"+ clusterName, color);
            nodeAttrs.setAttribute(node.getIdentifier(), "nodeSize_"+ clusterName, size );
            nodeAttrs.setAttribute(node.getIdentifier(), "nodeType_"+ clusterName, shape );	
            
        }
    }


    /**
     * Method that creates the edge attributes (actually one attribute determines all edge properties, i.e. color, target arrow, line width...).
     */
    public void buildEdgeAttributes(CyNetwork network) {
        Iterator i = network.edgesIterator();
        CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
        while (i.hasNext()) {
            Edge edge = (Edge) i.next();
            edgeAttrs.setAttribute(edge.getIdentifier(), "edgeType_"+ clusterName, "black");
	}
    }


}
