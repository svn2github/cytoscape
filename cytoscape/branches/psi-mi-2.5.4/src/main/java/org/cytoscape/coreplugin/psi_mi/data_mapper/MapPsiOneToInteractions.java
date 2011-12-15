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

import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.cytoscape.coreplugin.psi_mi.model.ExternalReference;
import org.cytoscape.coreplugin.psi_mi.model.Interaction;
import org.cytoscape.coreplugin.psi_mi.model.Interactor;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractionVocab;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractorVocab;
import org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType;
import org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType;
import org.cytoscape.coreplugin.psi_mi.schema.mi1.DbReferenceType;
import org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet;
import org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentType;
import org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionElementType;
import org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType;
import org.cytoscape.coreplugin.psi_mi.schema.mi1.ObjectFactory;
import org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType;
import org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType;
import org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType;
import org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType;
import org.cytoscape.coreplugin.psi_mi.util.ListUtil;
import org.jdom.Text;


/**
 * Maps PSI-MI Level 1 Document to Interaction Objects.
 *
 * @author Ethan Cerami
 * @author Nisha Vinod
 */
public class MapPsiOneToInteractions implements Mapper {
	private Map interactorMap;
	private Map experimentMap;
	private List interactions;
	private String xml;

	/**
	 * Constructor.
	 *
	 * @param xml          XML Document.
	 * @param interactions ArrayList of Interaction objects.
	 */
	public MapPsiOneToInteractions(String xml, List interactions) {
		this.xml = xml;
		this.interactions = interactions;
	}

	/**
	 * Perform Mapping.
	 *
	 * @throws MapperException Problem Performing mapping.
	 */
	public void doMapping() throws MapperException {
		parseXml(xml);
	}

	/**
	 * Parses the PSI XML Document.
	 */
	private void parseXml(String content) throws MapperException {
		try {
			interactorMap = new HashMap();
			experimentMap = new HashMap();

			StringReader reader = new StringReader(content);

			//  Note to self.  The following method will not work
			//  JAXBContext jc = JAXBContext.newInstance(
			//       "org.cytoscape.coreplugin.psi_mi.schema.mi1");
			//  Using the line above results in the following exception:
			//  javax.xml.bind.JAXBException: "org.cytoscape.coreplugin.psi_mi.schema.mi1"
			//  doesnt contain ObjectFactory.class or jaxb.index

			//  The alternative is to use the syntax below.  I don't know why this works,
			//  but the tip is described online here:
			//  http://forums.java.net/jive/thread.jspa?forumID=46&threadID=20124&messageID=174472
			Class[] classes = new Class[2];
			classes[0] = org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet.class;
			classes[1] = ObjectFactory.class;

			JAXBContext jc = JAXBContext.newInstance(classes);
			Unmarshaller u = jc.createUnmarshaller();
			org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet entrySet = (org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet) u
			                                                                                                                 .unmarshal(reader);
			ListUtil.setPsiOneEntrySet(entrySet);

			int entryCount = entrySet.getEntry().size();

			for (int i = 0; i < entryCount; i++) {
				EntrySet.Entry entry = entrySet.getEntry().get(i);
				extractEntry(entry);
			}
		} catch (JAXBException e) {
			throw new MapperException(e, "PSI-MI XML File is invalid:  " + e.getMessage());
		}
	}

	/**
	 * Extracts PSI Entry Root Element.
	 */
	private void extractEntry(EntrySet.Entry entry) throws MapperException {
		EntrySet.Entry.ExperimentList expList = entry.getExperimentList();
		extractExperimentList(expList);

		EntrySet.Entry.InteractorList interactorList = entry.getInteractorList();
		extractInteractorList(interactorList);

		EntrySet.Entry.InteractionList interactionList = entry.getInteractionList();
		extractInteractionList(interactionList);
	}

	/**
	 * Extracts Experiment List, and places into HashMap.
	 */
	private void extractExperimentList(EntrySet.Entry.ExperimentList expList) {
		if (expList != null) {
			int count = expList.getExperimentDescription().size();

			for (int i = 0; i < count; i++) {
				ExperimentType expType = expList.getExperimentDescription().get(i);
				String id = expType.getId();
				experimentMap.put(id, expType);
			}
		}
	}

	/**
	 * Extracts PSI InteractorList, and places into HashMap.
	 */
	private void extractInteractorList(EntrySet.Entry.InteractorList interactorList) {
		if (interactorList != null) {
			int count = interactorList.getProteinInteractor().size();
			ListUtil.setInteractorCount(count);

			for (int i = 0; i < count; i++) {
				ProteinInteractorType cProtein = interactorList.getProteinInteractor().get(i);
				String id = cProtein.getId();
				interactorMap.put(id, cProtein);
			}
		}
	}

	/**
	 * Extracts PSI Interaction List
	 */
	private void extractInteractionList(EntrySet.Entry.InteractionList interactionList)
	    throws MapperException {
		int count = interactionList.getInteraction().size();

		for (int i = 0; i < count; i++) {
			Interaction interaction = new Interaction();
			InteractionElementType cInteraction = interactionList.getInteraction().get(i);
			interaction.setInteractionId(cInteraction.getInteractionType().size());

			InteractionElementType.ParticipantList pList = cInteraction.getParticipantList();
			int pCount = pList.getProteinParticipant().size();
			ArrayList interactorList = new ArrayList();
			HashMap interactorRoles = new HashMap();

			for (int j = 0; j < pCount; j++) {
				Interactor interactor = extractInteractorRefOrElement(pList, j);
				interactorList.add(interactor);

				ProteinParticipantType participant = pList.getProteinParticipant().get(j);
				String role = participant.getRole();

				if (role != null) {
					interactorRoles.put(interactor.getName(), role);
				}
			}

			interaction.setInteractors(interactorList);

			ArrayList list = extractExperimentalData(cInteraction, interaction);

			//  Add BAIT MAP / Names To all Interactions.
			for (int j = 0; j < list.size(); j++) {
				interaction = (Interaction) list.get(j);
				interaction.addAttribute(InteractionVocab.BAIT_MAP, interactorRoles);
				extractInteractionNamesXrefs(cInteraction, interaction);
			}

			interactions.addAll(list);
		}
	}

	/**
	 * Extracts Interaction Names.
	 */
	private void extractInteractionNamesXrefs(InteractionElementType cInteraction,
	                                          Interaction interaction) {
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
		ExternalReference[] refs = this.extractExternalRefs(xref);

		if ((refs != null) && (refs.length > 0)) {
			interaction.setExternalRefs(refs);
		}
	}

	/**
	 * Extracts Interactor From Reference or Element.
	 */
	private Interactor extractInteractorRefOrElement(InteractionElementType.ParticipantList pList,
	                                                 int j) throws MapperException {
		Interactor interactor = null;
		ProteinInteractorType cInteractor;
		ProteinParticipantType participant = pList.getProteinParticipant().get(j);
		RefType ref = participant.getProteinInteractorRef();

		if (ref != null) {
			String key = ref.getRef();
			cInteractor = (ProteinInteractorType) interactorMap.get(key);

			if (cInteractor == null) {
				throw new MapperException("No Interactor Found for " + "proteinInteractorRef:  "
				                          + key);
			}
		} else {
			cInteractor = participant.getProteinInteractor();
		}

		if (cInteractor != null) {
			interactor = createInteractor(cInteractor);
		}

		return interactor;
	}

	/**
	 * Extracts Interactor Name
	 */
	private void extractInteractorName(ProteinInteractorType cProtein, Interactor interactor)
	    throws MapperException {
		NamesType names = cProtein.getNames();

		if (names != null) {
			String name = MapperUtil.extractName(cProtein, interactor.getExternalRefs());

			//  Remove all surrounding and internal white space.
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
		ArrayList refList = new ArrayList();

		if (xref != null) {
			DbReferenceType primaryRef = xref.getPrimaryRef();
            if (primaryRef != null) {
                createExternalReference(primaryRef.getDb(), primaryRef.getId(), refList);
            }

            int count = xref.getSecondaryRef().size();

			for (int i = 0; i < count; i++) {
				DbReferenceType secondaryRef = xref.getSecondaryRef().get(i);
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
	private void createExternalReference(String db, String id, ArrayList refList) {
		ExternalReference ref = new ExternalReference(db, id);
		refList.add(ref);
	}

	/**
	 * Extracts Experimental Data.
	 * <p/>
	 * Notes:  In PSI-MI, each interaction element can have 1 or more
	 * experimentDescriptions.  For each experimentDescription, we
	 * create a new DataServices Interaction object.
	 * <p/>
	 * In other words, a Data Services Interaction object contains
	 * data for one interaction, determined under exactly one experimental
	 * condition.
	 */
	private ArrayList extractExperimentalData(InteractionElementType cInteraction,
	                                          Interaction interactionTemplate)
	    throws MapperException {
		InteractionElementType.ExperimentList expList = cInteraction.getExperimentList();
		ArrayList list = new ArrayList();

		if (expList != null) {
			int expCount = expList.getExperimentRefOrExperimentDescription().size();

			for (int i = 0; i < expCount; i++) {
				Interaction interaction = cloneInteractionTemplate(interactionTemplate);
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
	 * Clones the InteractionTemplate.  Only clones the Interactors
	 * contained within the interaction, and none of the Interaction
	 * attributes.
	 */
	private Interaction cloneInteractionTemplate(Interaction interactionTemplate) {
		Interaction interaction = new Interaction();
		List interactors = interactionTemplate.getInteractors();
		interaction.setInteractors(interactors);

		return interaction;
	}

	/**
	 * Extracts an Experiment Reference or Sub-Element.
	 */
	private ExperimentType extractExperimentReferenceOrElement(Object expItem) {
		if (expItem instanceof RefType) {
			RefType refType = (RefType) expItem;

			return (ExperimentType) experimentMap.get(refType.getRef());
		} else {
			return (ExperimentType) expItem;
		}
	}

	/**
	 * Gets Interaction Detection.
	 */
	private void extractInteractionDetection(ExperimentType expDesc, Interaction interaction) {
		String expSystem = null;

		if (expDesc != null) {
			CvType detection = expDesc.getInteractionDetection();
            if (detection != null) {
                NamesType names = detection.getNames();
                if (names != null) {
                    expSystem = names.getShortLabel();

                    if (expSystem != null) {
                        interaction.addAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_NAME, expSystem);
                    }
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
	private Interactor createInteractor(ProteinInteractorType cProtein) throws MapperException {
		org.cytoscape.coreplugin.psi_mi.model.Interactor interactor = new org.cytoscape.coreplugin.psi_mi.model.Interactor();
		extractOrganismInfo(cProtein, interactor);
		extractSequenceData(cProtein, interactor);

		ExternalReference[] refs = extractExternalRefs(cProtein.getXref());

		if ((refs != null) && (refs.length > 0)) {
			interactor.setExternalRefs(refs);
		}

		//  Set Local Id.
		String localId = cProtein.getId();
		interactor.addAttribute(InteractorVocab.LOCAL_ID, localId);

		//  Set Interactor Name Last, as it may be dependent on
		//  external references.
		extractInteractorName(cProtein, interactor);

		return interactor;
	}

	/**
	 * Extracts Sequence Data.
	 */
	private void extractSequenceData(ProteinInteractorType cProtein, Interactor interactor) {
		String sequence = cProtein.getSequence();

		if (sequence != null) {
			interactor.addAttribute(InteractorVocab.SEQUENCE_DATA, sequence);
		}
	}

	/**
	 * Extracts Organism Information.
	 */
	private void extractOrganismInfo(ProteinInteractorType cProtein, Interactor interactor) {
		ProteinInteractorType.Organism organism = cProtein.getOrganism();

		if (organism != null) {
			NamesType names = organism.getNames();
			String commonName = names.getShortLabel();
			String fullName = names.getFullName();
			BigInteger ncbiTaxID = organism.getNcbiTaxId();
            if (commonName != null && commonName.length() > 0) {
                interactor.addAttribute(InteractorVocab.ORGANISM_COMMON_NAME, commonName);
            }
            if (fullName != null && fullName.length() > 0) {
                interactor.addAttribute(InteractorVocab.ORGANISM_SPECIES_NAME, fullName);
            }
            if (ncbiTaxID != null) {
                interactor.addAttribute(InteractorVocab.ORGANISM_NCBI_TAXONOMY_ID,
                        ncbiTaxID.toString());
            }
        }
	}
}
