package org.cytoscape.webservice.psicquic.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hupo.psi.mi.mif.AttributeList;
import org.hupo.psi.mi.mif.AvailabilityList;
import org.hupo.psi.mi.mif.CvType;
import org.hupo.psi.mi.mif.DbReference;
import org.hupo.psi.mi.mif.Entry;
import org.hupo.psi.mi.mif.ExperimentDescriptionList;
import org.hupo.psi.mi.mif.Interaction;
import org.hupo.psi.mi.mif.InteractionList;
import org.hupo.psi.mi.mif.Interactor;
import org.hupo.psi.mi.mif.InteractorList;
import org.hupo.psi.mi.mif.Names;
import org.hupo.psi.mi.mif.Participant;
import org.hupo.psi.mi.mif.ParticipantList;
import org.hupo.psi.mi.mif.Source;
import org.hupo.psi.mi.mif.Xref;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;



// Map PSI 2.5 entry to Cytoscape Objects
public class PSIEntryMapper {
	
	private Map<Integer, CyNode> nodeIDMap;
	private Map<Integer, CyEdge> edgeIDMap;
	
	public PSIEntryMapper() {
		nodeIDMap = new HashMap<Integer, CyNode>();
		edgeIDMap = new HashMap<Integer, CyEdge>();
	}
	
	public void map(Entry entry) {
		
		// Extract All
		AttributeList attrs = entry.getAttributeList();
		AvailabilityList availability = entry.getAvailabilityList();
		ExperimentDescriptionList expList = entry.getExperimentList();
		InteractionList interactions = entry.getInteractionList();
		InteractorList interactors = entry.getInteractorList();
		Source source = entry.getSource();
		
		mapInteractors(interactors);
		mapInteractions(interactions);
		Cytoscape.createNetwork(nodeIDMap.values(), edgeIDMap.values(), "test");
	}
	
	private void mapInteractors(InteractorList interactors) {
		final List<Interactor> iList = interactors.getInteractor();
		
		int id;
		Names names;
		Xref xref;
		String primaryID;
		DbReference pref;
		CyNode node;
		for(Interactor interactor: iList) {
			id = interactor.getId();
			xref = interactor.getXref();
			pref = xref.getPrimaryRef();
			primaryID = pref.getId();
			node = Cytoscape.getCyNode(primaryID, true);
			nodeIDMap.put(id, node);
			mapNodeAttributes(interactor, node);
		}
	}
	
	private void mapInteractions(InteractionList interactions) {
		final List<Interaction> itrList = interactions.getInteraction();
		CyNode source, target;
		CyEdge edge;
		// This is a primary xref of interaction type.
		String interactionType;
		
		for(Interaction it: itrList) {
			ParticipantList nodes = it.getParticipantList();
			List<Participant> parts = nodes.getParticipant();
			
			List<CvType> itrType = it.getInteractionType();
			interactionType = itrType.get(0).getXref().getPrimaryRef().getId();
			if(parts.size() == 2) {
				// Binary interaction
				source = nodeIDMap.get(parts.get(0).getInteractorRef());
				target = nodeIDMap.get(parts.get(1).getInteractorRef());
				edge = Cytoscape.getCyEdge(source, target, "interaction", interactionType, true);
			} else {
				
			}
			for(Participant p: parts) {
				 CyNode node = nodeIDMap.get(p.getInteractionRef());
			}
		}
	}
	
	private void mapNodeAttributes(Interactor interactor, CyNode node) {
		
		
		interactor.getInteractorType();
		interactor.getAttributeList();
		interactor.getOrganism();
		interactor.getSequence();
	}
	
	

}
