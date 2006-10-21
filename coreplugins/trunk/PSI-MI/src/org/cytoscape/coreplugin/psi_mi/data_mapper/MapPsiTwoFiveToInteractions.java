/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Nisha Vinod
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.cytoscape.coreplugin.psi_mi.data_mapper;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.jdom.Text;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.*;
import org.cytoscape.coreplugin.psi_mi.util.ListUtil;
import org.cytoscape.coreplugin.psi_mi.model.ExternalReference;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractorVocab;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractionVocab;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Maps PSI-MI Level 2.5 to Interaction Objects.
 *
 * @author Ethan Cerami
 * @author Nisha Vinod
 */
public class MapPsiTwoFiveToInteractions implements Mapper {
    private HashMap interactorMap;
    private HashMap experimentMap;
    private ArrayList interactions;
    private String xml;


    /**
     * Constructor.
     *
     * @param xml          XML Document.
     * @param interactions ArrayList of Interaction objects.
     */
    public MapPsiTwoFiveToInteractions(String xml, ArrayList interactions) {
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
            EntrySet entrySet = EntrySet.unmarshalEntrySet(reader);
            
            int entryCount = entrySet.getEntryCount();

            for (int i = 0; i < entryCount; i++) {
                Entry entry = entrySet.getEntry(i);
                extractEntry(entry);
            }
        } catch (ValidationException e) {
            throw new MapperException (e, "PSI-MI XML File is invalid:  "
                    + e.getMessage());
        } catch (MarshalException e) {
            throw new MapperException (e, "PSI-MI XML File is invalid:  "
                    + e.getMessage());
        }
    }

    /**
     * Extracts PSI Entry Root Element.
     */
    private void extractEntry(Entry entry) throws MapperException {
        ExperimentList1 expList = entry.getExperimentList1();
        extractExperimentList(expList);
        InteractorList interactorList = entry.getInteractorList();

        extractInteractorList(interactorList);
        InteractionList interactionList = entry.getInteractionList();
        extractInteractionList(interactionList);
    }

    /**
     * Extracts Experiment List, and places into HashMap.
     */
    private void extractExperimentList(ExperimentList1 expList) {
        if (expList != null) {
            int count = expList.getExperimentDescriptionCount();

            for (int i = 0; i < count; i++) {
                ExperimentType expType = expList.getExperimentDescription(i);
                String id = "" + expType.getId();
                experimentMap.put(id, expType);
            }
        }
    }

    /**
     * Extracts PSI InteractorList, and places into HashMap.
     */
    private void extractInteractorList(InteractorList interactorList) {
        if (interactorList != null) {
            int count = interactorList.getInteractorCount();
            ListUtil.setInteractorCount(count);
            for (int i = 0; i < count; i++) {
                InteractorElementType cProtein =
                       interactorList.getInteractor(i);

                String id = "" + cProtein.getId();
                interactorMap.put(id, cProtein);
            }
        }
    }

    /**
     * Extracts PSI Interaction List
     */
    private void extractInteractionList(InteractionList interactionList)
            throws MapperException {
        int count = interactionList.getInteractionCount();

        for (int i = 0; i < count; i++) {
            org.cytoscape.coreplugin.psi_mi.model.Interaction interaction =
                    new org.cytoscape.coreplugin.psi_mi.model.Interaction();
            InteractionElementType cInteraction =
                    interactionList.getInteraction(i);

            ParticipantList pList = cInteraction.getParticipantList();
            int pCount = pList.getParticipantCount();
            ArrayList interactorList = new ArrayList();
            HashMap interactorRoles = new HashMap();
            for (int j = 0; j < pCount; j++) {
                org.cytoscape.coreplugin.psi_mi.model.Interactor interactor =
                        extractInteractorRefOrElement(pList, j);
                interactorList.add(interactor);
                ParticipantType participant = pList.getParticipant(j);
                       // getProteinParticipant(j);
                ExperimentalRoleList role = participant.getExperimentalRoleList();

                if (role != null) {
                    ExperimentalRole[] roles = role.getExperimentalRole();
                    NamesType namesType = null;
                    for (int k = 0; k < roles.length; k++) {
                        namesType = roles[k].getNames();
                    }
                    String roleName = namesType.getShortLabel();
                    interactorRoles.put(interactor.getName(), roleName);
                }
            }
            interaction.setInteractors(interactorList);
            interaction.setInteractionId(cInteraction.getId());
            ArrayList list = extractExperimentalData
                    (cInteraction, interaction);

            //  Add BAIT MAP / Names To all Interactions.
            for (int j = 0; j < list.size(); j++) {
                interaction = (org.cytoscape.coreplugin.psi_mi.model.Interaction) list.get(j);
                interaction.addAttribute(InteractionVocab.BAIT_MAP,
                        interactorRoles);
                extractInteractionNamesXrefs(cInteraction, interaction);
            }
            interactions.addAll(list);
        }
    }

    /**
     * Extracts Interaction Names.
     */
    private void extractInteractionNamesXrefs
            (InteractionElementType cInteraction,
                    org.cytoscape.coreplugin.psi_mi.model.Interaction interaction) {
        NamesType names = cInteraction.getNames();
        if (names != null) {
            String shortLabel = names.getShortLabel();
            String fullName = names.getFullName();
            if (shortLabel != null) {
                interaction.addAttribute
                        (InteractionVocab.INTERACTION_SHORT_NAME,
                                shortLabel);
            }
            if (fullName != null) {
                interaction.addAttribute
                        (InteractionVocab.INTERACTION_FULL_NAME,
                                fullName);
            }
        }
        XrefType xref = cInteraction.getXref();
        ExternalReference refs[] = this.extractExternalRefs(xref);
        if (refs != null && refs.length > 0) {
            interaction.setExternalRefs(refs);
        }
    }

    /**
     * Extracts Interactor From Reference or Element.
     */
    private org.cytoscape.coreplugin.psi_mi.model.Interactor
            extractInteractorRefOrElement(ParticipantList pList,
            int j) throws MapperException {
        org.cytoscape.coreplugin.psi_mi.model.Interactor interactor = null;
        org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType cInteractor = null;
        ParticipantType participant = pList.getParticipant(j);
        //participant.getXref()
        ParticipantTypeChoice type =
                participant.getParticipantTypeChoice();
        //RefType ref = type.getInteractorRef();
        String ref = type.getInteractorRef() + "";
        if (ref != null) {
            String key = ref;//ref.getContent()+"";
            cInteractor = (InteractorElementType) interactorMap.get(key);
            if (cInteractor == null) {
                throw new MapperException("No Interactor Found for "
                        + "proteinInteractorRef:  " + key);
            }
        } else {
            //cInteractor = type.getInteractor();
        }
        if (cInteractor != null) {
            interactor = createInteractor(cInteractor);
        }
        return interactor;
    }


    /**
     * Extracts Interactor Name
     */
    private void extractInteractorName(InteractorElementType cProtein,
            org.cytoscape.coreplugin.psi_mi.model.Interactor interactor) throws MapperException {
        NamesType names = cProtein.getNames();
        if (names != null) {
            String name = MapperUtil.extractName(cProtein,
                    interactor.getExternalRefs());

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
            createExternalReference(primaryRef.getDb(), primaryRef.getId(),
                    refList);
            int count = xref.getSecondaryRefCount();
            for (int i = 0; i < count; i++) {
                DbReferenceType secondaryRef = xref.getSecondaryRef(i);
                createExternalReference(secondaryRef.getDb(),
                        secondaryRef.getId(), refList);
            }
            ExternalReference refs [] =
                    new ExternalReference[refList.size()];
            refs = (ExternalReference[]) refList.toArray(refs);
            return refs;
        } else {
            return null;
        }
    }

    /**
     * Creates ExternalReference.
     */
    private void createExternalReference(String db, String id,
            ArrayList refList) {
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
    private ArrayList extractExperimentalData(InteractionElementType
            cInteraction, org.cytoscape.coreplugin.psi_mi.model.Interaction interactionTemplate)
            throws MapperException {
        ExperimentList expList = cInteraction.getExperimentList();
        ArrayList list = new ArrayList();
        if (expList != null) {
            int expCount = expList.getExperimentListItemCount();
            for (int i = 0; i < expCount; i++) {
                org.cytoscape.coreplugin.psi_mi.model.Interaction interaction
                        = cloneInteractionTemplate
                        (interactionTemplate);
                ExperimentListItem expItem = expList.getExperimentListItem(i);
                ExperimentType expType =
                        extractExperimentReferenceOrElement(expItem);

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
    private org.cytoscape.coreplugin.psi_mi.model.Interaction cloneInteractionTemplate
            (org.cytoscape.coreplugin.psi_mi.model.Interaction interactionTemplate) {
        org.cytoscape.coreplugin.psi_mi.model.Interaction interaction =
                new org.cytoscape.coreplugin.psi_mi.model.Interaction();
        ArrayList interactors = interactionTemplate.getInteractors();
        interaction.setInteractors(interactors);
        return interaction;
    }

    /**
     * Extracts an Experiment Reference or Sub-Element.
     */
    private ExperimentType extractExperimentReferenceOrElement
            (ExperimentListItem expItem) {
        ExperimentType expType = null;
        String ref = "" + expItem.getExperimentRef();
        if (ref != null) {
            String key = ref;//"" + ref.getContent();
            expType = (ExperimentType) experimentMap.get(key);
        } else {
            expType = expItem.getExperimentDescription();
        }
        return expType;
    }

    /**
     * Gets Interaction Detection.
     */
    private void extractInteractionDetection(ExperimentType expDesc,
            org.cytoscape.coreplugin.psi_mi.model.Interaction interaction) {
        String expSystem = null;
        if (expDesc != null) {
            CvType detection = expDesc.getInteractionDetectionMethod();
            NamesType names = detection.getNames();
            expSystem = names.getShortLabel();
            if (expSystem != null) {
                interaction.addAttribute
                        (InteractionVocab.EXPERIMENTAL_SYSTEM_NAME, expSystem);
            }
            XrefType xref = detection.getXref();
            if (xref != null) {
                DbReferenceType primaryRef = xref.getPrimaryRef();
                if (primaryRef != null) {
                    interaction.addAttribute
                            (InteractionVocab.EXPERIMENTAL_SYSTEM_XREF_DB,
                                    primaryRef.getDb());
                    interaction.addAttribute
                            (InteractionVocab.EXPERIMENTAL_SYSTEM_XREF_ID,
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
    private org.cytoscape.coreplugin.psi_mi.model.Interactor createInteractor
            (InteractorElementType cProtein)
            throws MapperException {
        org.cytoscape.coreplugin.psi_mi.model.Interactor interactor =
                new org.cytoscape.coreplugin.psi_mi.model.Interactor();
        extractOrganismInfo(cProtein, interactor);
        extractSequenceData(cProtein, interactor);
        ExternalReference refs[] = extractExternalRefs(cProtein.getXref());
        if (refs != null && refs.length > 0) {
            interactor.setExternalRefs(refs);
        }

        //  Set Local Id.
        String localId = "" + cProtein.getId();
        interactor.addAttribute(InteractorVocab.LOCAL_ID,
                localId);

        //  Set Interactor Name Last, as it may be dependent on
        //  external references.
        extractInteractorName(cProtein, interactor);
        extractCvType(cProtein, interactor);
        return interactor;
    }
    /**
     *
     */
    private void extractCvType(InteractorElementType cProtein,
            org.cytoscape.coreplugin.psi_mi.model.Interactor interactor)
    {
        //System.out.println("extractCvType:" );
       CvType cvType = cProtein.getInteractorType();
        //System.out.println("cvType:" + cvType.getNames().getFullName());
       if(cvType != null)
       {
           interactor.setCvType(cvType);
       }
    }

    /**
     * Extracts Sequence Data.
     */
    private void extractSequenceData(InteractorElementType cProtein,
            org.cytoscape.coreplugin.psi_mi.model.Interactor interactor) {
        String sequence = cProtein.getSequence();
        if (sequence != null) {
            interactor.addAttribute(InteractorVocab.SEQUENCE_DATA, sequence);
        }
    }

    /**
     * Extracts Organism Information.
     */
    private void extractOrganismInfo(InteractorElementType  cProtein,
            org.cytoscape.coreplugin.psi_mi.model.Interactor interactor) {
        Organism organism = cProtein.getOrganism();
        if (organism != null) {
            NamesType names = organism.getNames();
            String commonName = names.getShortLabel();
            String fullName = names.getFullName();
            int ncbiTaxID = organism.getNcbiTaxId();
            //String ncbiTaxId = organism.getNcbiTaxId();
            interactor.addAttribute(InteractorVocab.ORGANISM_COMMON_NAME,
                    commonName);
            interactor.addAttribute(InteractorVocab.ORGANISM_SPECIES_NAME,
                    fullName);
            interactor.addAttribute(InteractorVocab.ORGANISM_NCBI_TAXONOMY_ID,
                    Integer.toString(ncbiTaxID));
        }
    }
}