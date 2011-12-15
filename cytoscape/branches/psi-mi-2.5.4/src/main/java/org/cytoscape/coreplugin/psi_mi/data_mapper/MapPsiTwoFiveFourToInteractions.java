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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.cytoscape.coreplugin.psi_mi.model.ExternalReference;
import org.cytoscape.coreplugin.psi_mi.model.Interaction;
import org.cytoscape.coreplugin.psi_mi.model.Interactor;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractionVocab;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractorVocab;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.Alias;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.Attribute;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.AttributeList;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.Bibref;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.Confidence;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.ConfidenceList;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.CvType;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.DbReference;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.Entry;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.EntrySet;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.ExperimentDescription;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.ExperimentDescriptionList;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.ExperimentList;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.ExperimentalRole;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.ExperimentalRoleList;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.InteractionList;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor.Organism;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.InteractorList;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.Names;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.OpenCvType;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.Participant;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.ParticipantList;
import org.cytoscape.coreplugin.psi_mi.schema.mi254.Xref;
import org.cytoscape.coreplugin.psi_mi.util.ListUtil;
import org.jdom.Text;

/**
* Maps PSI-MI Level 2.5 to Interaction Objects.
*
* @author Ethan Cerami
* @author Nisha Vinod
*/
public class MapPsiTwoFiveFourToInteractions implements Mapper {
	private Map<String, org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor> interactorMap;
	private Map<String, ExperimentDescription> experimentMap;
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
	public MapPsiTwoFiveFourToInteractions(String xml, List<Interaction> interactions) {
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
			interactorMap = new HashMap<String, org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor>();
			experimentMap = new HashMap<String, ExperimentDescription>();

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
			classes[0] = org.cytoscape.coreplugin.psi_mi.schema.mi254.EntrySet.class;
			classes[1] = org.cytoscape.coreplugin.psi_mi.schema.mi254.ObjectFactory.class;

			JAXBContext jc = JAXBContext.newInstance(classes);
			Unmarshaller u = jc.createUnmarshaller();

			JAXBElement<EntrySet> element = (JAXBElement<EntrySet>) u.unmarshal(reader);
			final EntrySet entrySet = (EntrySet) element.getValue();
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
		final ExperimentDescriptionList expList = entry.getExperimentList();
		extractExperimentList(expList);

		final InteractorList interactorList = entry.getInteractorList();
		extractInteractorList(interactorList);

		final InteractionList interactionList = entry.getInteractionList();
		extractInteractionList(interactionList);
	}

	/**
	 * Extracts Experiment List, and places into HashMap.
	 */
	private void extractExperimentList(ExperimentDescriptionList expList) {
		log("Extracting Experiment List: Start");

		if (expList != null) {
			int count = expList.getExperimentDescription().size();

			for (int i = 0; i < count; i++) {
				ExperimentDescription expType = expList.getExperimentDescription().get(i);
				String id = "" + expType.getId();
				experimentMap.put(id, expType);
			}
		}

		log("Extracting Experiment List: End");
	}

	/**
	 * Extracts PSI InteractorList, and places into HashMap.
	 */
	private void extractInteractorList(InteractorList interactorList) {
		log("Extracting Interactor List: Start");

		if (interactorList != null) {
			int count = interactorList.getInteractor().size();
			ListUtil.setInteractorCount(count);

			List<org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor> list = interactorList.getInteractor();

			String id;

			for (org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor cProtein : list) {
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

		List<org.cytoscape.coreplugin.psi_mi.schema.mi254.Interaction> list = interactionList.getInteraction();

		for (org.cytoscape.coreplugin.psi_mi.schema.mi254.Interaction cInteraction : list) {
			Interaction interaction = new org.cytoscape.coreplugin.psi_mi.model.Interaction();

			ParticipantList pList = cInteraction.getParticipantList();
			final List<Interactor> interactorList = new ArrayList<Interactor>();
			Map interactorRoles = new HashMap();

			for (Participant participant : pList.getParticipant()) {
				Interactor interactor = extractInteractorRefOrElement(participant);
				if (interactor == null) {
					continue;
				}
				log("Getting interactor:  " + interactor);
				interactorList.add(interactor);

				ExperimentalRoleList role = participant.getExperimentalRoleList();

				if (role != null) {
					for (ExperimentalRole expRole : role.getExperimentalRole()) {
						Names Names = expRole.getNames();
						String roleName = Names.getShortLabel();
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

	private void extractConfidence(org.cytoscape.coreplugin.psi_mi.schema.mi254.Interaction cInteraction,
	                               org.cytoscape.coreplugin.psi_mi.model.Interaction interaction) {
		final ConfidenceList conf = cInteraction.getConfidenceList();

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
	private void extractInteractionNamesXrefs(org.cytoscape.coreplugin.psi_mi.schema.mi254.Interaction cInteraction,
	                                          org.cytoscape.coreplugin.psi_mi.model.Interaction interaction) {
		Names names = cInteraction.getNames();

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

		Xref xref = cInteraction.getXref();
		ExternalReference[] refs = extractExternalRefs(xref);

		if ((refs != null) && (refs.length > 0)) {
			log("Got refs:  " + refs.length);
			interaction.setExternalRefs(refs);
		}
	}

	/**
	 * Extracts Interactor From Reference or Element.
	 */
	private Interactor extractInteractorRefOrElement(Participant participant) throws MapperException {
		Interactor interactor;
		org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor cInteractor = null;
		Integer ref = participant.getInteractorRef();

		if (ref != null) {
			cInteractor = (org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor) interactorMap
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
	private void extractInteractorName(org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor cProtein, Interactor interactor)
	    throws MapperException {
		Names names = cProtein.getNames();

		if (names != null) {
			String name = extractName(cProtein, interactor.getExternalRefs());

			// Remove all surrounding and internal white space.
			Text jdomText = new Text(name);
			name = jdomText.getTextNormalize();

			interactor.setName(name);

			String fullName = names.getFullName();
			interactor.addAttribute(InteractorVocab.FULL_NAME, fullName);
		}
	}

	private String extractName(org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor interactor, ExternalReference[] refs) throws MapperException {
		String shortLabel = null;
		String fullName = null;
		Names names = interactor.getNames();

		if (names != null) {
			shortLabel = names.getShortLabel();
			fullName = names.getFullName();
		}

		if ((shortLabel != null) && (shortLabel.trim().length() > 0)) {
			return shortLabel;
		} else {
			if (refs != null) {
				for (int i = 0; i < refs.length; i++) {
					String dbName = refs[i].getDatabase();

					if (dbName.equals("SWP") || dbName.equals("SWISS-PROT")
					    || dbName.equalsIgnoreCase("SwissProt")
					    || dbName.equalsIgnoreCase("UniProt")) {
						return refs[i].getId();
					}
				}
			}
		}

		if ((fullName != null) && (fullName.trim().length() > 0)) {
			return fullName;
		} else if ((("" + interactor.getId()) != null)
		           && (("" + interactor.getId()).trim().length() > 0)) {
			return "" + interactor.getId();
		} else {
			throw new MapperException("Unable to determine name" + "for interactor:  "
			                          + interactor.getId());
		}
	}

	/**
	 * Extracts All Interactor External References.
	 */
	private ExternalReference[] extractExternalRefs(Xref xref) {
		final List<ExternalReference> refList = new ArrayList<ExternalReference>();

		if (xref != null) {
			DbReference primaryRef = xref.getPrimaryRef();
			createExternalReference(primaryRef.getDb(), primaryRef.getId(), refList);

			for (DbReference secondaryRef : xref.getSecondaryRef()) {
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
	private List<org.cytoscape.coreplugin.psi_mi.model.Interaction> extractExperimentalData(org.cytoscape.coreplugin.psi_mi.schema.mi254.Interaction cInteraction,
	                                                                                        org.cytoscape.coreplugin.psi_mi.model.Interaction interactionTemplate)
	    throws MapperException {
		ExperimentList expList = cInteraction.getExperimentList();
		List<org.cytoscape.coreplugin.psi_mi.model.Interaction> list = new ArrayList<org.cytoscape.coreplugin.psi_mi.model.Interaction>();

		if (expList != null) {
			int expCount = expList.getExperimentRefOrExperimentDescription().size();

			for (int i = 0; i < expCount; i++) {
				org.cytoscape.coreplugin.psi_mi.model.Interaction interaction = cloneInteractionTemplate(interactionTemplate);
				Object expItem = expList.getExperimentRefOrExperimentDescription().get(i);
				ExperimentDescription expType = extractExperimentReferenceOrElement(expItem);
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
	private ExperimentDescription extractExperimentReferenceOrElement(Object expItem) {
		if (expItem instanceof Integer) {
			String ref = "" + expItem;

			return (ExperimentDescription) experimentMap.get(ref);
		} else {
			return (ExperimentDescription) expItem;
		}
	}

	/**
	 * Gets Interaction Detection.
	 */
	private void extractInteractionDetection(ExperimentDescription expDesc,
	                                         org.cytoscape.coreplugin.psi_mi.model.Interaction interaction) {
		String expSystem;

		if (expDesc != null) {
			CvType detection = expDesc.getInteractionDetectionMethod();
			// In some files, this field is empty.
			if(detection == null) 
				return;
			
			Names names = detection.getNames();
			expSystem = names.getShortLabel();

			if (expSystem != null) {
				interaction.addAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_NAME, expSystem);
			}

			Xref xref = detection.getXref();

			if (xref != null) {
				DbReference primaryRef = xref.getPrimaryRef();

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
	private String getPubMedId(ExperimentDescription expDesc) {
		String id = null;

		if (expDesc != null) {
			Bibref bibRef = expDesc.getBibref();

			if (bibRef != null) {
				Xref xRef = bibRef.getXref();

				if (xRef != null) {
					DbReference primaryRef = xRef.getPrimaryRef();

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
	private Interactor createInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor cProtein) throws MapperException {
		Interactor interactor = new Interactor();
		if (cProtein == null) {
			return null;
		}
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
	
	private void extractInteractorType(org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor cProtein, Interactor interactor) {
		final CvType type = cProtein.getInteractorType();
		
		if(type == null) return;
	}

	private void extractIndividualXrefs(org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor cProtein, Interactor interactor) {
		final Xref xrefs = cProtein.getXref();

		if (xrefs != null) {
			final DbReference pRef = xrefs.getPrimaryRef();

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

			final List<DbReference> sRef = xrefs.getSecondaryRef();

			if (sRef != null) {
				for (DbReference ref : sRef) {
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

	private void extractCvType(org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor cProtein, Interactor interactor) {
		final CvType cvType = cProtein.getInteractorType();

		if (cvType != null)
			interactor.setCvType(convert(cvType));
	}

	private org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType convert(CvType cvType) {
		if (cvType == null) {
			return null;
		}
		org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType result = new org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType();
		result.setNames(convert(cvType.getNames()));
		result.setXref(convert(cvType.getXref()));
		return result;
	}

	private XrefType convert(Xref xref) {
		if (xref == null) {
			return null;
		}
		XrefType result = new XrefType();
		result.setPrimaryRef(convert(xref.getPrimaryRef()));
		List<DbReferenceType> references = result.getSecondaryRef();
		for (DbReference reference : xref.getSecondaryRef()) {
			references.add(convert(reference));
		}
		return result;
	}

	private DbReferenceType convert(DbReference reference) {
		if (reference == null) {
			return null;
		}
		DbReferenceType result = new DbReferenceType();
		result.setAttributeList(convert(reference.getAttributeList()));
		result.setDb(reference.getDb());
		result.setDbAc(reference.getDbAc());
		result.setId(reference.getId());
		result.setRefType(reference.getRefType());
		result.setRefTypeAc(reference.getRefTypeAc());
		result.setSecondary(reference.getSecondary());
		result.setVersion(reference.getVersion());
		return result;
	}

	private AttributeListType convert(AttributeList attributeList) {
		if (attributeList == null) {
			return null;
		}
		AttributeListType result = new AttributeListType();
		List<AttributeListType.Attribute> attributes = result.getAttribute();
		for (Attribute attribute : attributeList.getAttribute()) {
			attributes.add(convert(attribute));
		}
		return result;
	}

	private AttributeListType.Attribute convert(Attribute attribute) {
		if (attribute == null) {
			return null;
		}
		AttributeListType.Attribute result = new AttributeListType.Attribute();
		return result;
	}

	private NamesType convert(Names names) {
		if (names == null) {
			return null;
		}
		NamesType result = new NamesType();
		result.setFullName(names.getFullName());
		result.setShortLabel(names.getShortLabel());
		List<NamesType.Alias> aliases = result.getAlias();
		for (Alias alias : names.getAlias()) {
			aliases.add(convert(alias));
		}
		return result;
	}

	private NamesType.Alias convert(Alias alias) {
		if (alias == null) {
			return null;
		}
		NamesType.Alias result = new NamesType.Alias();
		result.setType(alias.getType());
		result.setTypeAc(alias.getTypeAc());
		result.setValue(alias.getValue());
		return result;
	}

	/**
	 * Extracts Sequence Data.
	 */
	private void extractSequenceData(org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor cProtein, Interactor interactor) {
		String sequence = cProtein.getSequence();

		if (sequence != null) {
			interactor.addAttribute(InteractorVocab.SEQUENCE_DATA, sequence);
		}
	}

	/**
	 * Extracts Organism Information.
	 */
	private void extractOrganismInfo(org.cytoscape.coreplugin.psi_mi.schema.mi254.Interactor cProtein, Interactor interactor) {
		Organism organism = cProtein.getOrganism();

		if (organism != null) {
			Names names = organism.getNames();
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
