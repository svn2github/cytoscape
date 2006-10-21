package org.cytoscape.coreplugin.psi_mi.data_mapper;

//import org.cytoscape.coreplugin.psi_mi.schema.mi1.*;
import org.cytoscape.coreplugin.psi_mi.util.ListUtil;
import org.cytoscape.coreplugin.psi_mi.model.Interactor;
import org.cytoscape.coreplugin.psi_mi.model.AttributeBag;
import org.cytoscape.coreplugin.psi_mi.model.ExternalReference;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractorVocab;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractionVocab;

import java.util.*;

import org.cytoscape.coreplugin.psi_mi.schema.mi25.*;

/**
 * Converts Data Servics Object Model to the PSI-MI Format.
 * <p/>
 * Official version of PSI-MI is available at:
 * http://psidev.sourceforge.net/mi/xml/src/MIF.xsd
 *
 * @author Ethan Cerami
 */
public class MapInteractionsToPsiTwoFive implements Mapper {

    private static final String EXP_AFFINITY_PRECIPITATION =
            "Affinity Precipitation";

    private static final String EXP_AFFINITY_CHROMOTOGRAPHY =
            "Affinity Chromatography";

    private static final String EXP_TWO_HYBRID = "Two Hybrid";

    private static final String EXP_PURIFIED_COMPLEX = "Purified Complex";

    /**
     * Pub Med Database.
     */
    private static final String PUB_MED_DB = "pubmed";

    private EntrySet entrySet;

    /**
     * ArrayList of Protein-Protein Interactions.
     */
    private ArrayList interactions;

    /**
     * Constructor.
     *
     * @param interactions ArrayList of Interactions.
     */
    public MapInteractionsToPsiTwoFive(ArrayList interactions) {
        this.interactions = interactions;
    }

    /**
     * Perform Mapping.
     */
    public void doMapping() {
        // Create Entry Set and Entry
        /*entrySet = new EntrySet();
        entrySet.setLevel(2);
        entrySet.setVersion(5);

        Entry entry = new Entry();

        //  Get Interactor List
        InteractorList interactorList = getInteractorList();
        System.out.println("interactorList" + interactorList.getInteractorCount());
        //  Get Interaction List
        InteractionList interactionList = getInteractionList();
        System.out.println("interactionList" + interactionList.getInteractionCount());

        //  Add to Entry node
        entry.setInteractorList(interactorList);
        System.out.println("Set interactor list");
        entry.setInteractionList(interactionList);
        System.out.println("set interaction list");
        entrySet.addEntry(entry);
        System.out.println("Added entry");    */
        entrySet = ListUtil.getEntrySet();

        /*int entryCount = entrySet.getEntryCount();
            System.out.println("EntryCount1:" + ListUtil.getEntrySet().getEntryCount() );
            InteractionList intList;
            for (int i = 0; i < entryCount; i++) {
                Entry entry = entrySet.getEntry(i);
                System.out.println("Got entry");
                intList = extractEntry(entry);
                System.out.println("Got new intList");
                entry.setInteractionList(intList);
                System.out.println("Set new intList");
            }

        System.out.println("DoMapping: " + entrySet.getEntryCount());*/
    }

     /**
     * Extracts PSI Entry Root Element.
     */
    private InteractionList extractEntry(Entry entry)  {
        Map map =  ListUtil.getInteractionMap();
        System.out.println("ListUtil Map size: " + map.size());

        InteractionList interactionList = entry.getInteractionList();
        int count = interactionList.getInteractionCount();
        boolean inetractionFound = false;
        System.out.println("Count:" + count);
         ArrayList list = new ArrayList();
        for (int i = 0; i < count; i++)
         {
             System.out.println("I:" + i);
                InteractionElementType cInteraction = interactionList.getInteraction(i);
                 System.out.println("II");
                Iterator iter = map.keySet().iterator();
                while(iter.hasNext()) {
                    //System.out.println("3"+iter.next().getClass().getName());
                    int id = ((Integer)iter.next()).intValue();
                    System.out.println("4:, id" + id);
                    if(cInteraction.getId() == id)
                    {
                        inetractionFound = true;
                        break;
                    }
                    else
                    {
                        inetractionFound = false;
                    }
            }
            if(!(inetractionFound))
            {
                System.out.println("Removing: " + cInteraction.getId() + "Index: " + i );
                //interactionList.removeInteraction(i);
                list.add(new Integer(i));

            }

        }
        Iterator iter = list.iterator();
        System.out.println("List: " + list.size());
        while(iter.hasNext())
        {
            //InteractionElementType cInteraction =(InteractionElementType) iter.next();
             int index = ( (Integer) iter.next()).intValue();
            System.out.println("Removing: " +  index  + "Int count:" + interactionList.getInteractionCount());
            if(interactionList.getInteractionCount() == index)
            {
                index = index -1;
            }
            interactionList.removeInteraction(index);
            System.out.println("Removed ");

        }

       return interactionList;
    }

    /**
     * Gets PSI XML.
     *
     * @return Root PSI Element.
     */
    public EntrySet getPsiXml() {
        //System.out.println("Returning pSI xml");

        return entrySet;
    }

    /**
     * Gets Interactor List.
     *
     * @return Castor InteractorList.
     */
    private InteractorList getInteractorList() {
        HashMap proteinSet = getNonRedundantInteractors();
        InteractorList interactorList = new InteractorList();

        //  Iterate through all Interactors
        Iterator iterator = proteinSet.values().iterator();
        while (iterator.hasNext()) {
            //  Create new Interactor
            Interactor interactor = (Interactor) iterator.next();
            InteractorElementType castorInteractor
                    = new InteractorElementType();
            setNameId(interactor, castorInteractor);
            setOrganism(interactor, castorInteractor);
            setSequence(interactor, castorInteractor);
            NamesType names = new NamesType();
            names.setShortLabel(interactor.getName());
            String fullName = (String) interactor.getAttribute
                (InteractorVocab.FULL_NAME);
            CvType cvType = new CvType();
            cvType.setNames(names);//setInteractorType(interactor);
            castorInteractor.setInteractorType(cvType);
            /*if(cvType != null)
            {
              castorInteractor.setInteractorType(cvType);
            } */
            XrefType xref = createExternalRefs(interactor);
            if (xref != null) {
                castorInteractor.setXref(xref);
                cvType.setXref(xref);
            }

            //  Add to Interactor List
            interactorList.addInteractor(castorInteractor);
        }
        return interactorList;
    }

    /**
     * Sets Sequence Data.
     */
    private void setSequence(Interactor interactor, InteractorElementType
            castorInteractor) {
        String seqData = (String) interactor.getAttribute
                (InteractorVocab.SEQUENCE_DATA);
        if (seqData != null) {
            castorInteractor.setSequence(seqData);
        }
    }

    /**
     * Sets Interactor Name and ID.
     *
     * @param interactor       Data Services Interactor object.
     * @param castorInteractor Castor Protein Interactor Object.
     */
    private void setNameId(Interactor interactor, InteractorElementType
            castorInteractor) {
        NamesType names = new NamesType();
        names.setShortLabel(interactor.getName());
        String fullName = (String) interactor.getAttribute
                (InteractorVocab.FULL_NAME);
        if (fullName != null) {
            names.setFullName(fullName);
        }
        castorInteractor.setNames(names);
        castorInteractor.setId(new Integer(interactor.getName()).intValue());//Integer.parseInt(interactor.getName()));
    }

    /**
     * Sets Interactor Organism.
     *
     * @param interactor       Data Services Interactor Object.
     * @param castorInteractor Castor Protein Interactor Object.
     */
    private void setOrganism(Interactor interactor,
                             InteractorElementType castorInteractor) {
        Organism organism = new Organism();
        String taxonomyID = (String) interactor.getAttribute
                (InteractorVocab.ORGANISM_NCBI_TAXONOMY_ID);
        if (taxonomyID != null) {
            int taxID = Integer.parseInt(taxonomyID);
            organism.setNcbiTaxId(taxID);//taxonomyID);
        }

        NamesType orgNames = new NamesType();
        String commonName = (String) interactor.getAttribute
                (InteractorVocab.ORGANISM_COMMON_NAME);
        if (commonName != null) {
            orgNames.setShortLabel(commonName);
        }

        String speciesName = (String) interactor.getAttribute
                (InteractorVocab.ORGANISM_SPECIES_NAME);
        if (speciesName != null) {
            orgNames.setFullName(speciesName);
        }
        organism.setNames(orgNames);

        //  If organism is valid, add it;  otherwise, do not add it.
        if (organism.isValid()) {
            castorInteractor.setOrganism(organism);
        }
    }

    /**
     *
     */
    private CvType  setInteractorType(AttributeBag interactor)

    {
        System.out.println("setInteractorType");
        CvType cvType =(CvType)interactor.getCvType();
        if(cvType != null)
        {
            System.out.println("cvType" + cvType.getNames().getFullName());

        }
        else
        {
            System.out.println("null");
        }
        return cvType;
    }
    /**
     * Sets Interactor External References.
     * Filters out any redundant external references.
     */
    private XrefType createExternalRefs(AttributeBag bag) {
        HashSet set = new HashSet();
        ExternalReference refs [] = bag.getExternalRefs();
        XrefType xref = new XrefType();

        if (refs != null && refs.length > 0) {
            //  Add Primary Reference
            createPrimaryKey(refs[0], xref);

            //  All others become Secondary References
            if (refs.length > 1) {
                for (int i = 1; i < refs.length; i++) {
                    String key = this.generateXRefKey(refs[i]);
                    if (!set.contains(key)) {
                        createSecondaryKey(refs[i], xref);
                        set.add(key);
                    }
                }
            }
        }
        if (xref.getPrimaryRef() != null) {
            return xref;
        } else {
            return null;
        }
    }

    /**
     * Generates XRef Key.
     *
     * @param ref External Reference
     * @return Hash Key.
     */
    private String generateXRefKey(ExternalReference ref) {
        String key = ref.getDatabase() + "." + ref.getId();
        return key;
    }

    /**
     * Creates Primary Key.
     *
     * @param ref  External Reference.
     * @param xref Castor XRef.
     */
    private void createPrimaryKey(ExternalReference ref, XrefType xref) {
        DbReferenceType primaryRef = new DbReferenceType();
        primaryRef.setDb(ref.getDatabase());
        primaryRef.setId(ref.getId());
        xref.setPrimaryRef(primaryRef);
    }

    /**
     * Creates Secondary Key.
     *
     * @param ref  External Reference
     * @param xref Castro XRef.
     */
    private void createSecondaryKey(ExternalReference ref, XrefType xref) {
        DbReferenceType secondaryRef = new DbReferenceType();
        secondaryRef.setDb(ref.getDatabase());
        secondaryRef.setId(ref.getId());
        xref.addSecondaryRef(secondaryRef);
    }

    /**
     * Gets a complete list of NonRedundant Proteins.
     *
     * @return HashMap of NonRedundant Proteins.
     */
    private HashMap getNonRedundantInteractors() {
        HashMap interactorMap = new HashMap();
        for (int i = 0; i < interactions.size(); i++) {
            org.cytoscape.coreplugin.psi_mi.model.Interaction interaction = (org.cytoscape.coreplugin.psi_mi.model.Interaction) interactions.get(i);
            ArrayList interactors = interaction.getInteractors();
            for (int j = 0; j < interactors.size(); j++) {
                Interactor interactor = (Interactor) interactors.get(j);
                addToHashMap(interactor, interactorMap);
            }
        }
        return interactorMap;
    }

    /**
     * Conditionally adds Protein to HashMap.
     *
     * @param interactor    Interactor Object.
     * @param interactorMap HashMap of NonRedundant Interactors.
     */
    private void addToHashMap(Interactor interactor, HashMap interactorMap) {
        String orfName = interactor.getName();
        if (!interactorMap.containsKey(orfName)) {
            interactorMap.put(orfName, interactor);
        }
    }

    /**
     * Gets Interaction List.
     *
     * @return Castor InteractionList.
     */
    private InteractionList getInteractionList() {
        InteractionList interactionList = new InteractionList();
        //  Iterate through all interactions
        for (int i = 0; i < interactions.size(); i++) {

            //  Create New Interaction
            org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction castorInteraction =
                    new org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction();
            org.cytoscape.coreplugin.psi_mi.model.Interaction interaction = (org.cytoscape.coreplugin.psi_mi.model.Interaction) interactions.get(i);

            //  Add Experiment List
            ExperimentList expList = getExperimentDescription(interaction, i);
            castorInteraction.setExperimentList(expList);

            //  Add Participants
            ParticipantList participantList = getParticipantList(interaction);
            castorInteraction.setParticipantList(participantList);

            //  Add to Interaction List
            interactionList.addInteraction(castorInteraction);

            //  Add Xrefs
            XrefType xref = createExternalRefs(interaction);
            if (xref != null) {
                castorInteraction.setXref(xref);
            }
            castorInteraction.setId(i);
        }
        return interactionList;
    }

    /**
     * Gets Experiment Description.
     *
     * @param interaction Interaction object.
     * @return Castor InteractionElementTypeChoice object.
     */
    private ExperimentList getExperimentDescription
            (org.cytoscape.coreplugin.psi_mi.model.Interaction interaction, int index) {
        //  Create New Experiment List
        ExperimentList expList = new ExperimentList();

        //  Create New Experiment Description
        ExperimentListItem expItem = new ExperimentListItem();
        ExperimentType expDescription = new ExperimentType();
        expItem.setExperimentDescription(expDescription);

        //  Set Experimental ID
        expDescription.setId(index);//"exp" + index);

        //  Set Bibliographic Reference
        BibrefType bibRef = null;

        Object pmid = interaction.getAttribute(InteractionVocab.PUB_MED_ID);
        if (pmid != null && pmid instanceof String) {
            bibRef = createBibRef(MapInteractionsToPsiTwoFive.PUB_MED_DB, (String) pmid);
            expDescription.setBibref(bibRef);
        }

        //  Set Interaction Detection
        CvType interactionDetection =
                getInteractionDetection(interaction);
        expDescription.setInteractionDetectionMethod(interactionDetection);

        //  Set Choice Element
        expList.addExperimentListItem(expItem);
        return expList;
    }

    /**
     * Creates a Bibliography Reference.
     *
     * @param database Database.
     * @param id       ID String.
     * @return Castor Bibref Object.
     */
    private BibrefType createBibRef(String database, String id) {
        XrefType xref = createXRef(database, id);
        BibrefType bibRef = new BibrefType();
        bibRef.setXref(xref);
        return bibRef;
    }

    /**
     * Creates a Primary Reference.
     *
     * @param database Database.
     * @param id       ID String.
     * @return Castor XRef object
     */
    private XrefType createXRef(String database, String id) {
        XrefType xref = new XrefType();
        DbReferenceType primaryRef = new DbReferenceType();
        primaryRef.setDb(database);
        primaryRef.setId(id);
        xref.setPrimaryRef(primaryRef);
        return xref;
    }

    /**
     * Gets Interaction Detection element.
     * It is possible that an interaction is missing important attributes,
     * such as Experimental System Name, XRef DB, and XRef ID.  All of these
     * attributes are required by PSI.  Rather than throwing an exception
     * here, the data_mapper manually specifies "Not Specified" for all missing
     * attributes.
     *
     * @param interaction Interaction.
     * @return InteractionDetection Object.
     */
    private CvType getInteractionDetection
            (org.cytoscape.coreplugin.psi_mi.model.Interaction interaction) {
        CvType interactionDetection = new CvType();
        String idStr = null;
        try {
            idStr = (String) interaction.getAttribute
                (InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
        } catch (ClassCastException e) {
            idStr = null;
        }

        if (idStr == null) {
            idStr = "Not Specified";
        }

        String idRef = null;
        try {
            idRef = (String) interaction.getAttribute
                    (InteractionVocab.EXPERIMENTAL_SYSTEM_XREF_ID);
        } catch (ClassCastException e) {
            idRef = null;
        }

        //  If there is no ID Ref, find a best match.
        if (idRef == null) {
            if (idStr.equals(MapInteractionsToPsiTwoFive.EXP_AFFINITY_PRECIPITATION)
                    || idStr.equals(MapInteractionsToPsiTwoFive.EXP_AFFINITY_CHROMOTOGRAPHY)) {
                idStr = "affinity chromatography technologies";
                idRef = "MI:0004";
            } else if (idStr.equals(MapInteractionsToPsiTwoFive.EXP_TWO_HYBRID)) {
                idStr = "classical two hybrid";
                idRef = "MI:0018";
            } else if (idStr.equals(MapInteractionsToPsiTwoFive.EXP_PURIFIED_COMPLEX)) {
                idStr = "copurification";
                idRef = "MI:0025";
            } else {
                idRef = "Not Specified";
            }
        }
        NamesType names = createName(idStr, null);
        interactionDetection.setNames(names);

        String dbRef = null;
        try {
            dbRef = (String) interaction.getAttribute
                (org.cytoscape.coreplugin.psi_mi.model.vocab.InteractionVocab.EXPERIMENTAL_SYSTEM_XREF_DB);
        } catch (ClassCastException e) {
            dbRef = null;
        }
        if (dbRef == null) {
            dbRef = "PSI-MI";
        }

        XrefType xref = createXRef(dbRef, idRef);
        interactionDetection.setXref(xref);
        return interactionDetection;
    }

    /**
     * Creates a new Names Object.
     *
     * @param shortLabel Short Name Label.
     * @param fullName   Full Name/Description.
     * @return Castor Names Object.
     */
    private NamesType createName(String shortLabel, String fullName) {
        NamesType names = new NamesType();
        names.setShortLabel(shortLabel);
        if (fullName != null) {
            names.setFullName(fullName);
        }
        return names;
    }

    /**
     * Gets the Interaction Participant List.
     *
     * @param interaction Interaction object.
     * @return Castor Participant List.
     */
    private ParticipantList getParticipantList(org.cytoscape.coreplugin.psi_mi.model.Interaction interaction) {
        ParticipantList participantList = new ParticipantList();

        ArrayList interactors = interaction.getInteractors();

        for (int i = 0; i < interactors.size(); i++) {
            Interactor interactor = (Interactor) interactors.get(i);
            String name = interactor.getName();
            //System.out.println("Name:" + name);
            ParticipantType participant = new ParticipantType();//
            // createParticipant(interactor);//name);
            ParticipantTypeChoice choice =
                new ParticipantTypeChoice();

            //int ref = new RefType();
            //ref.setContent(new Integer(interactor.getName()).intValue());//Integer.parseInt(id));
            //choice.setInteractorRef(ref);
            InteractorElementType intElement= new InteractorElementType() ;
            intElement.setId(new Integer(interactor.getName()).intValue());
            NamesType names = new NamesType();
            names.setShortLabel(interactor.getName());
            String fullName = (String) interactor.getAttribute
                (InteractorVocab.FULL_NAME);
            CvType cvType = new CvType();
            cvType.setNames(names);//setInteractorType(interactor);
            intElement.setInteractorType(cvType);
            intElement.setNames(names);

            XrefType xref = createExternalRefs(interactor);
            if (xref != null) {
                intElement.setXref(xref);
                cvType.setXref(xref);
            }
            choice.setInteractor(intElement);
            participant.setParticipantTypeChoice(choice);
            participant.setId(i);
                //System.out.println("participant1:" + participant1.getId());
            participantList.addParticipant(participant);
        }
        return participantList;
    }

    /**
     * Create New Protein Participant.
     *
     * @param interactor Protein ID.
     * @return Castor Protein Participant Object.
     */
    private ParticipantType createParticipant(Interactor interactor) {
        ParticipantType participant = new ParticipantType();
        ParticipantTypeChoice choice =
                new ParticipantTypeChoice();

        //RefType ref = new RefType();
        //ref.setContent(new Integer(interactor.getName()).intValue());//Integer.parseInt(id));
        choice.setInteractorRef(new Integer(interactor.getName()).intValue());//ref);
        participant.setParticipantTypeChoice(choice);
        //System.out.println("Participnt id:" + participant.getParticipantTypeChoice().getInteractionRef().getContent()) ;
        return participant;
    }
}
