/*
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package org.cytoscape.coreplugin.psi_mi.data_mapper;

import org.cytoscape.coreplugin.psi_mi.model.ExternalReference;
import org.cytoscape.coreplugin.psi_mi.model.Interaction;
import org.cytoscape.coreplugin.psi_mi.model.Interactor;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractionVocab;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractorVocab;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.BibrefType;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType.Confidence;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.EntrySet;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.EntrySet.Entry;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.EntrySet.Entry.ExperimentList;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.EntrySet.Entry.InteractionList;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.EntrySet.Entry.InteractorList;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementType;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.OpenCvType;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType;
import org.cytoscape.coreplugin.psi_mi.util.ListUtil;

import org.jdom.Text;

import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


/**
 * Maps PSI-MI Level 2.5 to Interaction Objects.
 *
 * @author Ethan Cerami
 * @author Nisha Vinod
 */
public class MapPsiTwoFiveToInteractions implements Mapper {
	private Map<String, InteractorElementType> interactorMap;
	private Map<String, ExperimentType> experimentMap;
	private List<Interaction> interactions;
	private String xml;
	private static final boolean DEBUG = false;

	/**
	 * Constructor.
	 *
	 * @param xml
	 *            XML Document.
	 * @param interactions
	 *            ArrayList of Interaction objects.
	 */
	public MapPsiTwoFiveToInteractions(String xml, List<Interaction> interactions) {
		this.xml = xml;
		this.interactions = interactions;
	}

	/**
	 * Perform Mapping.
	 *
	 * @throws MapperException
	 *             Problem Performing mapping.
	 */
	public void doMapping() throws MapperException {
		parseXml(xml);
	}

	/**
	 * Parses the PSI XML Document.
	 */
	private void parseXml(String content) throws MapperException {
		try {
			interactorMap = new HashMap<String, InteractorElementType>();
			experimentMap = new HashMap<String, ExperimentType>();

			StringReader reader = new StringReader(content);

			// Note to self. The following method will not work
			// JAXBContext jc = JAXBContext.newInstance(
			// "org.cytoscape.coreplugin.psi_mi.schema.mi25");
			// Using the line above results in the following exception:
			// javax.xml.bind.JAXBException:
			// "org.cytoscape.coreplugin.psi_mi.schema.mi1"
			// doesnt contain ObjectFactory.class or jaxb.index

			// The alternative is to use the syntax below. I don't know why this
			// works,
			// but the tip is described online here:
			// http://forums.java.net/jive/thread.jspa?forumID=46&threadID=20124&messageID=174472
			Class<?>[] classes = new Class[2];
			classes[0] = org.cytoscape.coreplugin.psi_mi.schema.mi25.EntrySet.class;
			classes[1] = org.cytoscape.coreplugin.psi_mi.schema.mi25.ObjectFactory.class;

			JAXBContext jc = JAXBContext.newInstance(classes);
			Unmarshaller u = jc.createUnmarshaller();

			final EntrySet entrySet = (EntrySet) u.unmarshal(reader);
			final List<Entry> entryList = entrySet.getEntry();

			for (Entry entry : entryList)
				extractEntry(entry);
		} catch (JAXBException e) {
			throw new MapperException(e, "PSI-MI XML File is invalid:  " + e.getMessage());
		}
	}

	/**
	 * Extracts PSI Entry Root Element.
	 */
	private void extractEntry(Entry entry) throws MapperException {
		final ExperimentList expList = entry.getExperimentList();
		extractExperimentList(expList);

		final InteractorList interactorList = entry.getInteractorList();
		extractInteractorList(interactorList);

		final InteractionList interactionList = entry.getInteractionList();
		extractInteractionList(interactionList);
	}

	/**
	 * Extracts Experiment List, and places into HashMap.
	 */
	private void extractExperimentList(EntrySet.Entry.ExperimentList expList) {
		log("Extracting Experiment List: Start");

		if (expList != null) {
			int count = expList.getExperimentDescription().size();

			for (int i = 0; i < count; i++) {
				ExperimentType expType = expList.getExperimentDescription().get(i);
				String id = "" + expType.getId();
				experimentMap.put(id, expType);
			}
		}

		log("Extracting Experiment List: End");
	}

	/**
	 * Extracts PSI InteractorList, and places into HashMap.
	 */
	private void extractInteractorList(EntrySet.Entry.InteractorList interactorList) {
		log("Extracting Interactor List: Start");

		if (interactorList != null) {
			int count = interactorList.getInteractor().size();
			ListUtil.setInteractorCount(count);

			List<InteractorElementType> list = interactorList.getInteractor();

			String id;

			for (InteractorElementType cProtein : list) {
				id = "" + cProtein.getId();
				log("Extracting:  " + id + " --> " + cProtein);
				interactorMap.put(id, cProtein);
			}
		}

		log("Extracting Interactor List: End");
	}

	/**
	 * Extracts PSI Interaction List
	 */
	private void extractInteractionList(InteractionList interactionList) throws MapperException {
		log("Extracting Interaction List: Start");

		List<org.cytoscape.coreplugin.psi_mi.schema.mi25.EntrySet.Entry.InteractionList.Interaction> list = interactionList
		                                                                                                    .getInteraction();

		for (InteractionList.Interaction cInteraction : list) {
			Interaction interaction = new org.cytoscape.coreplugin.psi_mi.model.Interaction();

			InteractionElementType.ParticipantList pList = cInteraction.getParticipantList();
			int pCount = pList.getParticipant().size();
			final List<Interactor> interactorList = new ArrayList<Interactor>();
			Map interactorRoles = new HashMap();

			for (int j = 0; j < pCount; j++) {
				Interactor interactor = extractInteractorRefOrElement(pList, j);
				log("Getting interactor:  " + interactor);
				interactorList.add(interactor);

				ParticipantType participant = pList.getParticipant().get(j);
				ParticipantType.ExperimentalRoleList role = participant.getExperimentalRoleList();

				if (role != null) {
					for (ParticipantType.ExperimentalRoleList.ExperimentalRole expRole : role
					                                                                                                                                                                                                                      .getExperimentalRole()) {
						NamesType namesType = expRole.getNames();
						String roleName = namesType.getShortLabel();
						log("Storing role for:  " + interactor.getName() + " --> " + roleName);
						interactorRoles.put(interactor.getName(), roleName);
					}
				}
			}

			interaction.setInteractors(interactorList);
			interaction.setInteractionId(cInteraction.getId());

			List expDatalist = extractExperimentalData(cInteraction, interaction);

			// Add BAIT MAP / Names To all Interactions.
			for (int j = 0; j < expDatalist.size(); j++) {
				interaction = (org.cytoscape.coreplugin.psi_mi.model.Interaction) expDatalist.get(j);
				interaction.addAttribute(InteractionVocab.BAIT_MAP, interactorRoles);
				extractInteractionNamesXrefs(cInteraction, interaction);

				// Extract Confidence Values
				extractConfidence(cInteraction, interaction);
			}

			log("Adding num interactions:  " + expDatalist.size());
			interactions.addAll(expDatalist);
		}

		log("Extracting Interaction List: End");
	}

	private void extractConfidence(InteractionElementType cInteraction,
	                               org.cytoscape.coreplugin.psi_mi.model.Interaction interaction) {
		final ConfidenceListType conf = cInteraction.getConfidenceList();

		if (conf == null)
			return;

		List<Confidence> confList = conf.getConfidence();

		// Pick first one only (for now)
		if (confList != null) {
			Confidence c = confList.get(0);

			try {
				interaction.addAttribute(InteractionVocab.CONFIDENCE_VALUE,
				                         Double.valueOf(c.getValue()));
			} catch (NumberFormatException e) {
				return;
			}

			final OpenCvType unit = c.getUnit();

			if ((unit != null) && (unit.getNames() != null)) {
				if (c.getUnit().getNames().getShortLabel() != null)
					interaction.addAttribute(InteractionVocab.CONFIDENCE_UNIT_SHORT_NAME,
					                         c.getUnit().getNames().getShortLabel());

				if (c.getUnit().getNames().getFullName() != null)
					interaction.addAttribute(InteractionVocab.CONFIDENCE_UNIT_FULL_NAME,
					                         c.getUnit().getNames().getFullName());
			}
		}
	}

	/**
	 * Extracts Interaction Names.
	 */
	private void extractInteractionNamesXrefs(InteractionElementType cInteraction,
	                                          org.cytoscape.coreplugin.psi_mi.model.Interaction interaction) {
		NamesType names = cInteraction.getNames();

		if (names != null) {
			String shortLabel = names.getShortLabel();
			String fullName = names.getFullName();

			if (shortLabel != null) {
				interaction.addAttribute(InteractionVocab.INTERACTION_SHORT_NAME, shortLabel);
			}

			if (fullName != null) {
				interaction.addAttribute(InteractionVocab.INTERACTION_FULL_NAME, fullName);
			}
		}

		XrefType xref = cInteraction.getXref();
		ExternalReference[] refs = extractExternalRefs(xref);

		if ((refs != null) && (refs.length > 0)) {
			log("Got refs:  " + refs.length);
			interaction.setExternalRefs(refs);
		}
	}

	/**
	 * Extracts Interactor From Reference or Element.
	 */
	private Interactor extractInteractorRefOrElement(InteractionElementType.ParticipantList pList,
	                                                 int j) throws MapperException {
		Interactor interactor;
		org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType cInteractor = null;
		ParticipantType participant = pList.getParticipant().get(j);
		Integer ref = participant.getInteractorRef();

		if (ref != null) {
			cInteractor = (org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType) interactorMap
			                                                                              .get(""
			                                                                                   + ref);
		} else {
			cInteractor = participant.getInteractor();
		}

		interactor = createInteractor(cInteractor);

		return interactor;
	}

	/**
	 * Extracts Interactor Name
	 */
	private void extractInteractorName(InteractorElementType cProtein, Interactor interactor)
	    throws MapperException {
		NamesType names = cProtein.getNames();

		if (names != null) {
			String name = MapperUtil.extractName(cProtein, interactor.getExternalRefs());

			// Remove all surrounding and internal white space.
			Text jdomText = new Text(name);
			name = jdomText.getTextNormalize();

			interactor.setName(name);

			String fullName = names.getFullName();
			interactor.addAttribute(InteractorVocab.FULL_NAME, fullName);
		}
	}

	/**
	 * Extracts All Interactor External References.
	 */
	private ExternalReference[] extractExternalRefs(XrefType xref) {
		final List<ExternalReference> refList = new ArrayList<ExternalReference>();

		if (xref != null) {
			DbReferenceType primaryRef = xref.getPrimaryRef();
			createExternalReference(primaryRef.getDb(), primaryRef.getId(), refList);

			for (DbReferenceType secondaryRef : xref.getSecondaryRef()) {
				createExternalReference(secondaryRef.getDb(), secondaryRef.getId(), refList);
			}

			ExternalReference[] refs = new ExternalReference[refList.size()];
			refs = (ExternalReference[]) refList.toArray(refs);

			return refs;
		} else {
			return null;
		}
	}

	/**
	 * Creates ExternalReference.
	 */
	private void createExternalReference(String db, String id, List<ExternalReference> refList) {
		ExternalReference ref = new ExternalReference(db, id);
		refList.add(ref);
	}

	/**
	 * Extracts Experimental Data. <p/> Notes: In PSI-MI, each interaction
	 * element can have 1 or more experimentDescriptions. For each
	 * experimentDescription, we create a new DataServices Interaction object.
	 * <p/> In other words, a Data Services Interaction object contains data for
	 * one interaction, determined under exactly one experimental condition.
	 */
	private List<org.cytoscape.coreplugin.psi_mi.model.Interaction> extractExperimentalData(InteractionElementType cInteraction,
	                                                                                        org.cytoscape.coreplugin.psi_mi.model.Interaction interactionTemplate)
	    throws MapperException {
		InteractionElementType.ExperimentList expList = cInteraction.getExperimentList();
		List<org.cytoscape.coreplugin.psi_mi.model.Interaction> list = new ArrayList<org.cytoscape.coreplugin.psi_mi.model.Interaction>();

		if (expList != null) {
			int expCount = expList.getExperimentRefOrExperimentDescription().size();

			for (int i = 0; i < expCount; i++) {
				org.cytoscape.coreplugin.psi_mi.model.Interaction interaction = cloneInteractionTemplate(interactionTemplate);
				Object expItem = expList.getExperimentRefOrExperimentDescription().get(i);
				ExperimentType expType = extractExperimentReferenceOrElement(expItem);
				String id = getPubMedId(expType);

				if (id != null) {
					interaction.addAttribute(InteractionVocab.PUB_MED_ID, id);
				}

				extractInteractionDetection(expType, interaction);
				list.add(interaction);
			}
		} else {
			throw new MapperException("Could not determine experimental "
			                          + "data for one of the PSI-MI interactions");
		}

		return list;
	}

	/**
	 * Clones the InteractionTemplate. Only clones the Interactors contained
	 * within the interaction, and none of the Interaction attributes.
	 */
	private org.cytoscape.coreplugin.psi_mi.model.Interaction cloneInteractionTemplate(org.cytoscape.coreplugin.psi_mi.model.Interaction interactionTemplate) {
		org.cytoscape.coreplugin.psi_mi.model.Interaction interaction = new org.cytoscape.coreplugin.psi_mi.model.Interaction();
		List interactors = interactionTemplate.getInteractors();
		interaction.setInteractors(interactors);

		return interaction;
	}

	/**
	 * Extracts an Experiment Reference or Sub-Element.
	 */
	private ExperimentType extractExperimentReferenceOrElement(Object expItem) {
		if (expItem instanceof Integer) {
			String ref = "" + expItem;

			return (ExperimentType) experimentMap.get(ref);
		} else {
			return (ExperimentType) expItem;
		}
	}

	/**
	 * Gets Interaction Detection.
	 */
	private void extractInteractionDetection(ExperimentType expDesc,
	                                         org.cytoscape.coreplugin.psi_mi.model.Interaction interaction) {
		String expSystem;

		if (expDesc != null) {
			CvType detection = expDesc.getInteractionDetectionMethod();
			NamesType names = detection.getNames();
			expSystem = names.getShortLabel();

			if (expSystem != null) {
				interaction.addAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_NAME, expSystem);
			}

			XrefType xref = detection.getXref();

			if (xref != null) {
				DbReferenceType primaryRef = xref.getPrimaryRef();

				if (primaryRef != null) {
					interaction.addAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_XREF_DB,
					                         primaryRef.getDb());
					interaction.addAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_XREF_ID,
					                         primaryRef.getId());
				}
			}
		}
	}

	/**
	 * Extracts Pub Med ID.
	 */
	private String getPubMedId(ExperimentType expDesc) {
		String id = null;

		if (expDesc != null) {
			BibrefType bibRef = expDesc.getBibref();

			if (bibRef != null) {
				XrefType xRef = bibRef.getXref();

				if (xRef != null) {
					DbReferenceType primaryRef = xRef.getPrimaryRef();

					if (primaryRef != null) {
						id = primaryRef.getId();
					}
				}
			}
		}

		return id;
	}

	/**
	 * Extracts Single PSI Interactor.
	 */
	private Interactor createInteractor(InteractorElementType cProtein) throws MapperException {
		Interactor interactor = new Interactor();
		extractOrganismInfo(cProtein, interactor);
		extractSequenceData(cProtein, interactor);
		
		// Create ID sets
		extractIndividualXrefs(cProtein, interactor);
		
		ExternalReference[] refs = extractExternalRefs(cProtein.getXref());

		if ((refs != null) && (refs.length > 0)) {
			interactor.setExternalRefs(refs);
		}

		// Set Local Id.
		String localId = "" + cProtein.getId();
		interactor.addAttribute(InteractorVocab.LOCAL_ID, localId);

		// Set Interactor Name Last, as it may be dependent on
		// external references.
		extractInteractorName(cProtein, interactor);
		extractCvType(cProtein, interactor);

		return interactor;
	}
	
	private void extractInteractorType(InteractorElementType cProtein, Interactor interactor) {
		final CvType type = cProtein.getInteractorType();
		
		if(type == null) return;
	}

	private void extractIndividualXrefs(InteractorElementType cProtein, Interactor interactor) {
		final XrefType xrefs = cProtein.getXref();

		if (xrefs != null) {
			final DbReferenceType pRef = xrefs.getPrimaryRef();

			List<String> newList = null;

			if (pRef != null) {
				Object value = interactor.getAttribute(pRef.getDb());

				if (value == null) {
					newList = new ArrayList<String>();
				} else if ((value != null) && value instanceof List) {
					newList = (List<String>) value;
				}

				newList.add(pRef.getId());
				interactor.addAttribute(pRef.getDb(), newList);
			}

			final List<DbReferenceType> sRef = xrefs.getSecondaryRef();

			if (sRef != null) {
				for (DbReferenceType ref : sRef) {
					Object value = interactor.getAttribute(ref.getDb());

					if (value == null) {
						newList = new ArrayList<String>();
					} else if ((value != null) && value instanceof List) {
						newList = (List<String>) value;
					}

					newList.add(ref.getId());
					interactor.addAttribute(ref.getDb(), newList);
				}
			}
		}
	}

	private void extractCvType(InteractorElementType cProtein, Interactor interactor) {
		final CvType cvType = cProtein.getInteractorType();

		if (cvType != null)
			interactor.setCvType(cvType);
	}

	/**
	 * Extracts Sequence Data.
	 */
	private void extractSequenceData(InteractorElementType cProtein, Interactor interactor) {
		String sequence = cProtein.getSequence();

		if (sequence != null) {
			interactor.addAttribute(InteractorVocab.SEQUENCE_DATA, sequence);
		}
	}

	/**
	 * Extracts Organism Information.
	 */
	private void extractOrganismInfo(InteractorElementType cProtein, Interactor interactor) {
		InteractorElementType.Organism organism = cProtein.getOrganism();

		if (organism != null) {
			NamesType names = organism.getNames();
			String commonName = names.getShortLabel();
			String fullName = names.getFullName();
			int ncbiTaxID = organism.getNcbiTaxId();
			interactor.addAttribute(InteractorVocab.ORGANISM_COMMON_NAME, commonName);
			interactor.addAttribute(InteractorVocab.ORGANISM_SPECIES_NAME, fullName);
			interactor.addAttribute(InteractorVocab.ORGANISM_NCBI_TAXONOMY_ID,
			                        Integer.toString(ncbiTaxID));
		}
	}

	private void log(String msg) {
		if (DEBUG) {
			System.out.println(msg);
		}
	}
}
