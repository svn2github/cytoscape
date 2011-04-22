package org.cytoscape.task.internal.quickstart.subnetworkbuilder;

import java.util.Set;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class SearchRelatedGenesTask extends AbstractTask {

    @Tunable(description = "Gene Ontology")
    public String go;
    
    @Tunable(description = "Disease/Phynotype")
    public String phynotype;
    
    
    private final SubnetworkBuilderState state;
    
    SearchRelatedGenesTask(final SubnetworkBuilderState state) {
	this.state = state;
    }
    
    @Override
    public void run(TaskMonitor tm) throws Exception {
	final NCBISearchClient client = new NCBISearchClient();
	
	final Set<String> idSet = client.search(phynotype, go);
	state.setDiseaseGenes(idSet);
	state.setSearchTerms(phynotype + "," + go);
    }
    

}
