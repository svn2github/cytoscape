package cytoscape.coreplugins.biopax.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.biopax.paxtools.controller.ConversionScore;
import org.biopax.paxtools.controller.Integrator;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level2.conversion;
import org.biopax.paxtools.model.level2.physicalEntityParticipant;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.coreplugins.biopax.PaxtoolsReader;
import cytoscape.coreplugins.biopax.mapping.MapNodeAttributes;
import cytoscape.coreplugins.biopax.style.BioPAXMergeVisualStyleUtil;
import cytoscape.coreplugins.biopax.util.BioPAXUtilRex;
import cytoscape.data.CyAttributes;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.util.CyNetworkNaming;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

class IntegrateBioPAXTask implements Task {
    private TaskMonitor taskMonitor;
    private Integrator integrator;
    private CyNetwork network1, network2;

    private boolean isColorize;
    private Color firstColor, secondColor, mergedColor;
    private List<ConversionScore> alternativeScores;

    public IntegrateBioPAXTask(Integrator integrator, CyNetwork network1, CyNetwork network2,
                               List<ConversionScore> alternativeScores,
                               boolean isColorize,
                               Color firstColor, Color secondColor, Color mergedColor) {
        this.integrator = integrator;
        this.network1 = network1;
        this.network2 = network2;

        this.isColorize = isColorize;
        this.firstColor= firstColor;
        this.secondColor = secondColor;
        this.mergedColor = mergedColor;
        this.alternativeScores = alternativeScores;
    }

    public void run() {
        taskMonitor.setStatus("Integrating BioPAX networks...");
        //List<ConversionScore> convScores = integrator.integrate(alternativeScores);
        List<ConversionScore> convScores = new ArrayList<ConversionScore>();

        taskMonitor.setStatus("Creating network from integration...");
        Model integratedModel = BioPAXUtilRex.getNetworkModel(network1);
        CyNetwork cyNetwork = Cytoscape.createNetwork(new PaxtoolsReader(integratedModel), true, null);
        cyNetwork.setTitle(CyNetworkNaming.getSuggestedNetworkTitle("(Integrated) " + cyNetwork.getTitle()));

        taskMonitor.setStatus("Recovering integrated models...");
        BioPAXUtilRex.resetNetworkModel(network1);
        BioPAXUtilRex.resetNetworkModel(network2);

        if(isColorize) {
            taskMonitor.setStatus("Colorizing nodes...");

            Set<String> integratedRDFs = new HashSet<String>();
            for(ConversionScore aScore: convScores) {
                             conversion conv = aScore.getConversion1();
                integratedRDFs.add(conv.getRDFId());

                for(physicalEntityParticipant pep: aScore.getMatchedPEPs())
                    integratedRDFs.add(pep.getPHYSICAL_ENTITY().getRDFId());
            }
            Set<String> n1_nodeIDs = new HashSet<String>(),
                        n2_nodeIDs = new HashSet<String>();

            for(int index: network1.getNodeIndicesArray())
                n1_nodeIDs.add( network1.getNode(index).getIdentifier() );
            for(int index: network2.getNodeIndicesArray())
                n2_nodeIDs.add( network2.getNode(index).getIdentifier() );

            CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

            for(int index: cyNetwork.getNodeIndicesArray()) {
                String nodeID = cyNetwork.getNode(index).getIdentifier();
                String rdfId = nodeAttributes.getStringAttribute(nodeID, MapNodeAttributes.BIOPAX_RDF_ID);

                if(rdfId == null)
                    rdfId = "";

                if(n1_nodeIDs.contains(nodeID) && !n2_nodeIDs.contains(nodeID) && !integratedRDFs.contains(rdfId))
                    nodeAttributes.setAttribute(nodeID, BioPAXUtilRex.BIOPAX_MERGE_SRC,
                                                    BioPAXMergeVisualStyleUtil.BIOPAX_MERGE_SRC_FIRST);
                else if(!n1_nodeIDs.contains(nodeID) && n2_nodeIDs.contains(nodeID) && !integratedRDFs.contains(rdfId))
                    nodeAttributes.setAttribute(nodeID, BioPAXUtilRex.BIOPAX_MERGE_SRC,
                                                    BioPAXMergeVisualStyleUtil.BIOPAX_MERGE_SRC_SECOND);
                else
                    nodeAttributes.setAttribute(nodeID, BioPAXUtilRex.BIOPAX_MERGE_SRC,
                                                    BioPAXMergeVisualStyleUtil.BIOPAX_MERGE_SRC_MERGE);
            }

            final VisualStyle bioPaxVisualStyle = BioPAXMergeVisualStyleUtil.getBioPAXMergeVisualStyle(firstColor,
                                                                                                       secondColor,
                                                                                                       mergedColor);
            final VisualMappingManager manager = Cytoscape.getVisualMappingManager();
            final CyNetworkView view = Cytoscape.getNetworkView(cyNetwork.getIdentifier());
            view.setVisualStyle(bioPaxVisualStyle.getName());
            manager.setVisualStyle(bioPaxVisualStyle);
            view.applyVizmapper(bioPaxVisualStyle);
        }

        taskMonitor.setPercentCompleted(100);
	    taskMonitor.setStatus("Integration successful.");
    }

    public void halt() {
        // No halt support
    }

    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    public String getTitle() {
        return "Integrating BioPAX networks";
    }

}