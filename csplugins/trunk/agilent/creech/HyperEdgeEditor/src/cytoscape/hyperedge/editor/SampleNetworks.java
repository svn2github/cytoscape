/* -*-Java-*-
********************************************************************************
*
* File:         SampleNetworks.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdgeEditor/src/cytoscape/hyperedge/editor/SampleNetworks.java,v 1.1 2007/07/04 01:19:09 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Sat Sep 17 20:13:12 2005
* Modified:     Thu Jun 28 16:59:18 2007 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Sun Nov 19 16:12:28 2006 (Michael L. Creech) creech@w235krbza760
*  Added a Glycolysis reaction sample network.
* Mon Nov 06 09:24:32 2006 (Michael L. Creech) creech@w235krbza760
*  Changed GraphPerspective-->CyNetwork, Node-->CyNode.
* Thu Nov 02 05:27:28 2006 (Michael L. Creech) creech@w235krbza760
*  Changed return value of createSampleNetworks().
* Sat Jul 29 14:01:49 2006 (Michael L. Creech) creech@w235krbza760
*  Changed MEDIATOR-->ACTIVATING_MEDIATOR.
* Sun Oct 02 08:33:04 2005 (Michael L. Creech) creech@Dill
*  Updated comments.
********************************************************************************
*/
package cytoscape.hyperedge.editor;

import cytoscape.hyperedge.EdgeTypeMap;
import cytoscape.hyperedge.EdgeTypeMap.EdgeRole;
import cytoscape.hyperedge.impl.HyperEdgeImpl;
import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.HyperEdgeFactory;


// import cytoscape.hyperedge.HyperEdgeManager;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.Semantics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class creates two sample HyperEdge networks
 * for display within Cytoscape.
 * @author Michael L. Creech
 * @version 1.0
 */
public class SampleNetworks {
    private static final String LEFT           = "biopax.left";
    private static final String RIGHT          = "biopax.right";
    private static final String INHIBITING_MOD = "biopax.inhibiting_modulation";
    private static final String ACTIVATING_MOD = "biopax.activating_modulation";
    private HyperEdgeFactory    _factory       = HyperEdgeFactory.INSTANCE;

    // private HyperEdgeManager _manager = _factory.getHyperEdgeManager();

    /**
     * Return a List of sample networks created that contain HyperEdges.
     * Each list element is a CyNetwork containing one or more HyperEdges.
     */
    public List<CyNetwork> createSampleNetworks() {
        List<CyNetwork> networks = new ArrayList<CyNetwork>(3);
        //        manager.reset (false);

        //        networks.add(sampleNet1);
        //        networks.add(sampleNet2);
        //        networks.addAll(createSimpleNetworks());
        networks.add(createKrebsCycle());
        networks.add(createGlycolysisReaction());

        return networks;
    }

    /**
     * Creates a sample Glycolysis reaction demonstrating HyperEdges
     * with shared edges and multiple connections to the same Node
     * (Ca2+).
     */

    public CyNetwork createGlycolysisReaction() {
        EdgeTypeMap etm = _factory.getEdgeTypeMap();
        etm.put(LEFT, EdgeRole.SOURCE);
        etm.put(RIGHT, EdgeRole.TARGET);
        etm.put(ACTIVATING_MOD, EdgeRole.SOURCE);
        etm.put(INHIBITING_MOD, EdgeRole.SOURCE);
        etm.put("biopax.catalysis", EdgeRole.SOURCE);

	// don't create the network view:
        CyNetwork glycolysis   = Cytoscape.createNetwork("Glycolysis Reaction", false);
        CyNode    pki_complex  = createBNode("complex131862",
                                             "PKI-Complex",
                                             "Complex");
        CyNode    pkii_complex = createBNode("complex131898",
                                             "PKII-Complex",
                                             "Complex");

        CyNode pyruvate_kinase_II_momomer = createBNode("protein131900",
                                                        "pyruvate kinase II momomer",
                                                        "Protein");
        CyNode pyruvate_kinase_I_momomer = createBNode("protein131864",
                                                       "pyruvate kinase I momomer",
                                                       "Protein");

        CyNode pyruvate                  = createBNode("smallMolecule131852",
                                                       "pyruvate",
                                                       "Small Molecule");
        CyNode ADP                       = createBNode("smallMolecule131523",
                                                       "ADP",
                                                       "Small Molecule");
        CyNode AMP                       = createBNode(
            "smallMolecule131570(cellular_component unknown)",
            "AMP(cellular_component unknown)",
            "Small Molecule");
        CyNode ATP1                      = createBNode("smallMolecule131517",
                                                       "ATP",
                                                       "Small Molecule");
        CyNode ATP2                      = createBNode(
            "smallMolecule131517(cellular_component unknown)",
            "ATP (cellular_component unknown)",
            "Small Molecule");
        CyNode Ca2plus                   = createBNode(
            "smallMolecule131881(cellular_component unknown)",
            "Ca2+(cellular_component unknown)",
            "Small Molecule");
        CyNode succinyl_CoA              = createBNode(
            "smallMolecule131886(cellular_component unknown)",
            "succinyl-CoA(cellular_component unknown)",
            "Small Molecule");
        CyNode fructose_1_6_bisphosphate = createBNode(
            "smallMolecule131530(cellular_component unknown)",
            "fructose-1,6-bisphosphate(cellular_component unknown)",
            "Small Molecule");
        CyNode phosphoenolpyruvate       = createBNode("smallMolecule131563",
                                                       "phosphoenolpyruvate",
                                                       "Small Molecule");
        glycolysis.addEdge(Cytoscape.getCyEdge(pyruvate_kinase_II_momomer,
                                               pkii_complex,
                                               Semantics.INTERACTION,
                                               "Contains",
                                               true));
        glycolysis.addEdge(Cytoscape.getCyEdge(pyruvate_kinase_I_momomer,
                                               pki_complex,
                                               Semantics.INTERACTION,
                                               "Contains",
                                               true));

        HyperEdge pyruvate_kinase_II = _factory.createHyperEdge(pkii_complex,
                                                                "biopax.catalysis",
                                                                AMP,
                                                                ACTIVATING_MOD,
                                                                glycolysis);
        pyruvate_kinase_II.setName("pyruvate kinase II");
        addNodeAttribute(pyruvate_kinase_II.getConnectorNode(),
                         "biopax.entity_type",
                         "Catalysis");

        //   Map<CyNode, String> argMap = new HashMap<CyNode, String>();
        CyNode[] nodes      = new CyNode[] {
                                  pki_complex, ATP2, fructose_1_6_bisphosphate,
                                  Ca2plus, Ca2plus, succinyl_CoA
                              };
        String[] edgeITypes = new String[] {
                                  "biopax.catalysis", INHIBITING_MOD,
                                  ACTIVATING_MOD, ACTIVATING_MOD, INHIBITING_MOD,
                                  INHIBITING_MOD
                              };

        //        argMap.put(pki_complex, "biopax.catalysis");
        //        argMap.put(ATP2, INHIBITING_MOD);
        //        argMap.put(fructose_1_6_bisphosphate, ACTIVATING_MOD);
        //        argMap.put(Ca2plus, ACTIVATING_MOD);
        //        argMap.put(Ca2plus, INHIBITING_MOD);
        //        argMap.put(succinyl_CoA, INHIBITING_MOD);
        HyperEdge pyruvate_kinase_I = _factory.createHyperEdge(nodes,
                                                               edgeITypes,
                                                               glycolysis);
        pyruvate_kinase_I.setName("pyruvate kinase I");
        addNodeAttribute(pyruvate_kinase_I.getConnectorNode(),
                         "biopax.entity_type",
                         "Catalysis");

        Map<CyNode, String> argMap = new HashMap<CyNode, String>();
        argMap.put(pyruvate, LEFT);
        argMap.put(ADP, RIGHT);
        argMap.put(ATP1, LEFT);
        argMap.put(phosphoenolpyruvate, RIGHT);

        HyperEdge reaction131850 = _factory.createHyperEdge(argMap, glycolysis);
        reaction131850.setName("phosphoenolpyruvate dephosphorylation");
        addNodeAttribute(reaction131850.getConnectorNode(),
                         "biopax.entity_type",
                         "Biochemical Reaction");
        // Now add shared edges to reaction131850:
        reaction131850.connectHyperEdges(pyruvate_kinase_I, "biopax.catalysis");
        reaction131850.connectHyperEdges(pyruvate_kinase_II, "biopax.catalysis");

        return glycolysis;
    }

    public CyNetwork createKrebsCycle() {
        // create the CyNodes used in the HyperEdges:
        CyNode CoA_SH_1        = createNode("P 738", "CoA-SH");
        CyNode CoA_SH_2        = createNode("S 579", "CoA-SH");
        CyNode CoA_SH_3        = createNode("node4", "CoA-SH");
        CyNode Acetyl_CoA      = createNode("S 486", "Acetyl-CoA");
        CyNode CitrateSynthase = createNode("M 499", "Citrate synthase");
        ;

        CyNode Citrate                           = createNode("P 499", "Citrate");
        CyNode Aconitase                         = createNode("M 383",
                                                              "Aconitase");
        CyNode Isocitrate                        = createNode("P 962",
                                                              "Isocitrate");
        CyNode Oxaloacetate                      = createNode("P 246",
                                                              "Oxaloacetate");
        CyNode Isocitrate_dehydrogenase          = createNode("M 556",
                                                              "Isocitrate dehydrogenase");
        CyNode NADPlus_1                         = createNode("S 556", "NAD+");
        CyNode NADPlus_2                         = createNode("node0 371 ",
                                                              "NAD+");
        CyNode NADPlus_3                         = createNode("S 682", "NAD+");
        CyNode NADH_1                            = createNode("P 556", "NADH");
        CyNode NADH_2                            = createNode("node1", "NADH");
        CyNode NADH_3                            = createNode("P 265", "NADH");
        CyNode CO2_1                             = createNode("node2 ", "CO2");
        CyNode CO2_2                             = createNode("P 579", "C02");
        CyNode alpha_Ketoglutarate               = createNode("P 846",
                                                              "alpha-Ketoglutarate");
        CyNode alpha_Ketoglutarate_dehydrogenase = createNode("M 579",
                                                              "alpha-Ketoglutarate dehydrogenase");
        CyNode Succinyl_CoA                      = createNode("P 667",
                                                              "Succinyl-CoA");
        CyNode GDP                               = createNode("node3", "GDP");
        CyNode GTP                               = createNode("P 413", "GTP");
        CyNode Pi                                = createNode("node0 538", "Pi");
        CyNode Succinate                         = createNode("p 861",
                                                              "Succinate");
        CyNode Succinyl_CoA_synthetase           = createNode("m 413",
                                                              "Succinyl-CoA synthetase");
        CyNode FAD                               = createNode("S 351", "FAD");
        CyNode FADH2                             = createNode("P 207", "FADH2");
        CyNode Fumarate                          = createNode("P 226",
                                                              "Fumarate");
        CyNode Succinate_dehydrogenase           = createNode("M 207",
                                                              "Succinate dehydrogenase");
        CyNode H2O                               = createNode("S517", "H2O");
        CyNode Fumerase                          = createNode("M 492",
                                                              "Fumerase");
        CyNode Malate                            = createNode("P 120", "Malate");
        CyNode Malate_dehydrongenase             = createNode("M 246",
                                                              "Malate dehydrongenase");

        // create the sample CyNeworks:
	// don't create the network view:
        CyNetwork KrebsCycle = Cytoscape.createNetwork("Krebs Cycle", false);

        // Now create the HyperEdges:
        // Show Map way to construct HyperEdges:
        Map<CyNode, String> argMap = new HashMap<CyNode, String>();
        argMap.put(Acetyl_CoA, EdgeTypeMap.SUBSTRATE);
        argMap.put(Oxaloacetate, EdgeTypeMap.SUBSTRATE);
        argMap.put(CitrateSynthase, EdgeTypeMap.ACTIVATING_MEDIATOR);
        argMap.put(Citrate, EdgeTypeMap.PRODUCT);
        argMap.put(CoA_SH_1, EdgeTypeMap.PRODUCT);

        _factory.createHyperEdge(argMap, KrebsCycle);
        _factory.createHyperEdge(Citrate,
                                 EdgeTypeMap.SUBSTRATE,
                                 Aconitase,
                                 EdgeTypeMap.ACTIVATING_MEDIATOR,
                                 Isocitrate,
                                 EdgeTypeMap.PRODUCT,
                                 KrebsCycle);
        argMap.clear();
        argMap.put(Isocitrate, EdgeTypeMap.SUBSTRATE);
        argMap.put(NADPlus_1, EdgeTypeMap.SUBSTRATE);
        argMap.put(Isocitrate_dehydrogenase, EdgeTypeMap.ACTIVATING_MEDIATOR);
        argMap.put(NADH_1, EdgeTypeMap.PRODUCT);
        argMap.put(CO2_1, EdgeTypeMap.PRODUCT);
        argMap.put(alpha_Ketoglutarate, EdgeTypeMap.PRODUCT);
        _factory.createHyperEdge(argMap, KrebsCycle);

        // Show Collection way to construct HyperEdges:
        List<CyNode> krebNodes  = new ArrayList<CyNode>();
        List<String> krebITypes = new ArrayList<String>();
        krebNodes.add(alpha_Ketoglutarate);
        krebITypes.add(EdgeTypeMap.SUBSTRATE);
        krebNodes.add(CoA_SH_2);
        krebITypes.add(EdgeTypeMap.SUBSTRATE);
        krebNodes.add(NADPlus_2);
        krebITypes.add(EdgeTypeMap.SUBSTRATE);
        krebNodes.add(alpha_Ketoglutarate_dehydrogenase);
        krebITypes.add(EdgeTypeMap.ACTIVATING_MEDIATOR);
        krebNodes.add(CO2_2);
        krebITypes.add(EdgeTypeMap.PRODUCT);
        krebNodes.add(NADH_2);
        krebITypes.add(EdgeTypeMap.PRODUCT);
        krebNodes.add(Succinyl_CoA);
        krebITypes.add(EdgeTypeMap.PRODUCT);
        _factory.createHyperEdge(krebNodes, krebITypes, KrebsCycle);

        argMap.clear();
        argMap.put(Succinyl_CoA, EdgeTypeMap.SUBSTRATE);
        argMap.put(GDP, EdgeTypeMap.SUBSTRATE);
        argMap.put(Pi, EdgeTypeMap.SUBSTRATE);
        argMap.put(Succinyl_CoA_synthetase, EdgeTypeMap.ACTIVATING_MEDIATOR);
        argMap.put(CoA_SH_3, EdgeTypeMap.PRODUCT);
        argMap.put(GTP, EdgeTypeMap.PRODUCT);
        argMap.put(Succinate, EdgeTypeMap.PRODUCT);
        _factory.createHyperEdge(argMap, KrebsCycle);

        argMap.clear();
        argMap.put(Succinate, EdgeTypeMap.SUBSTRATE);
        argMap.put(FAD, EdgeTypeMap.SUBSTRATE);
        argMap.put(Succinate_dehydrogenase, EdgeTypeMap.ACTIVATING_MEDIATOR);
        argMap.put(FADH2, EdgeTypeMap.PRODUCT);
        argMap.put(Fumarate, EdgeTypeMap.PRODUCT);
        _factory.createHyperEdge(argMap, KrebsCycle);

        argMap.clear();
        argMap.put(Fumarate, EdgeTypeMap.SUBSTRATE);
        argMap.put(H2O, EdgeTypeMap.SUBSTRATE);
        argMap.put(Fumerase, EdgeTypeMap.ACTIVATING_MEDIATOR);
        argMap.put(Malate, EdgeTypeMap.PRODUCT);
        _factory.createHyperEdge(argMap, KrebsCycle);

        argMap.clear();
        argMap.put(Malate, EdgeTypeMap.SUBSTRATE);
        argMap.put(NADPlus_3, EdgeTypeMap.SUBSTRATE);
        argMap.put(Malate_dehydrongenase, EdgeTypeMap.ACTIVATING_MEDIATOR);
        argMap.put(NADH_3, EdgeTypeMap.PRODUCT);
        argMap.put(Oxaloacetate, EdgeTypeMap.PRODUCT);
        _factory.createHyperEdge(argMap, KrebsCycle);

        return KrebsCycle;
    }

    private CyNode createNode(String id, String label) {
        CyNode newNode = Cytoscape.getCyNode(id, true);
        addNodeAttribute(newNode, HyperEdgeImpl.LABEL_ATTRIBUTE_NAME, label);

        return newNode;
    }

    // create a Biopax Node:
    private CyNode createBNode(String id, String label, String biopax_type) {
        CyNode newNode = Cytoscape.getCyNode(id, true);
        addNodeAttribute(newNode, HyperEdgeImpl.LABEL_ATTRIBUTE_NAME, label);
        addNodeAttribute(newNode, "biopax.entity_type", biopax_type);

        return newNode;
    }

    private void addNodeAttribute(CyNode node, String attrName, String attrValue) {
        Cytoscape.getNodeAttributes()
                 .setAttribute(node.getIdentifier(), attrName, attrValue);
    }

    //    private List<CyNetwork> createSimpleNetworks() {
    //        // create the CyNodes used in the HyperEdges:
    //        CyNode S  = Cytoscape.getCyNode("S", true);
    //        CyNode M  = Cytoscape.getCyNode("M", true);
    //        CyNode P  = Cytoscape.getCyNode("P", true);
    //        CyNode A  = Cytoscape.getCyNode("A", true);
    //        CyNode B  = Cytoscape.getCyNode("B", true);
    //        CyNode C  = Cytoscape.getCyNode("C", true);
    //        CyNode D  = Cytoscape.getCyNode("D", true);
    //        CyNode S2 = Cytoscape.getCyNode("S2", true);
    //
    //        // create the sample CyNeworks:
    //        CyNetwork sampleNet1 = Cytoscape.createNetwork("sample-net1", false);
    //
    //        // Now create the HyperEdges:
    //        HyperEdge he1 = _factory.createHyperEdge(S,
    //                                                 EdgeTypeMap.SUBSTRATE,
    //                                                 M,
    //                                                 EdgeTypeMap.ACTIVATING_MEDIATOR,
    //                                                 P,
    //                                                 EdgeTypeMap.PRODUCT,
    //                                                 sampleNet1);
    //        he1.setName("he1");
    //
    //        // // Allow testing of attribute saving/load on Cytoscape objects:
    //        // Cytoscape.getNodeNetworkData ().setAttributeValue (S.getIdentifier (),
    //        //                                                    "attribute1",
    //        //                                                    new Integer(3));
    //        HyperEdge he2 = _factory.createHyperEdge(A,
    //                                                 EdgeTypeMap.SUBSTRATE,
    //                                                 B,
    //                                                 EdgeTypeMap.PRODUCT,
    //                                                 sampleNet1);
    //        he2.setName("he2");
    //
    //        // h34 is a homodimer--S-S-S2:
    //        HyperEdge he4 = _factory.createHyperEdge(S,
    //                                                 EdgeTypeMap.SUBSTRATE,
    //                                                 S,
    //                                                 EdgeTypeMap.SUBSTRATE,
    //                                                 S2,
    //                                                 EdgeTypeMap.PRODUCT,
    //                                                 sampleNet1);
    //        he4.setName("he4");
    //
    //        CyNetwork    sampleNet2 = Cytoscape.createNetwork("sample-net2", false);
    //        List<String> edgeITypes = new ArrayList<String>();
    //        edgeITypes.add(EdgeTypeMap.SUBSTRATE);
    //        edgeITypes.add(EdgeTypeMap.PRODUCT);
    //        edgeITypes.add(EdgeTypeMap.ACTIVATING_MEDIATOR);
    //        edgeITypes.add(EdgeTypeMap.ACTIVATING_MEDIATOR);
    //
    //        List<CyNode> nodes = new ArrayList<CyNode>();
    //        nodes.add(A);
    //        nodes.add(B);
    //        nodes.add(C);
    //        nodes.add(D);
    //
    //        HyperEdge he3 = _factory.createHyperEdge(nodes, edgeITypes, sampleNet2);
    //        he3.setName("he3");
    //
    //        // Now add the HyperEdges to the CyNetworks:
    //        he1.addToCyNetwork(sampleNet2);
    //
    //        List<CyNetwork> nets = new ArrayList<CyNetwork>(2);
    //        nets.add(sampleNet1);
    //        nets.add(sampleNet2);
    //
    //        return nets;
    //    }

}
