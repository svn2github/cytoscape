/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id$
 */

package org.cytoscape.coreplugin.psi_mi.schema.mi25;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * A molecule participating in an interaction.
 * 
 * @version $Revision$ $Date$
 */
public class ParticipantType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id
     */
    private int _id;

    /**
     * keeps track of state for field: _id
     */
    private boolean _has_id;

    /**
     * This contains the name(s) for the participant given by the
     * authors of a publication.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType _names;

    /**
     * Contains the xref(s) for the participant given by the
     * authors of a publication.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType _xref;

    /**
     * Description of the Interactor. Refers to an already defined
     * interactor in this entry, fully describes an interactor, or
     * references another interaction defined in this entry, to
     * allow the hierarchical building up of complexes from
     * subunits. 
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantTypeChoice _participantTypeChoice;

    /**
     * The method(s) by which this participant has been determined.
     * If this element is present, its value supersedes
     * experimentDescription/ participantIdentificationMethod.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethodList _participantIdentificationMethodList;

    /**
     * The role of the participant in the interaction. This
     * describes the biological role, e.g. enzyme or enzyme target.
     * The experimental role of the participant, e.g. 'bait', is
     * shown in experimentalForm. This element is controlled by the
     * PSI-MI controlled vocabulary "biologicalRole", root term id
     * MI:0500.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType _biologicalRole;

    /**
     * The role(s) of the participant in the interaction, e.g. bait.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRoleList _experimentalRoleList;

    /**
     * Terms describing the experimental sample preparation. 
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparationList _experimentalPreparationList;

    /**
     * Describes molecules which have been used in specific
     * experiments if these molecules are different from the one
     * listed as interactors. Example: The author of a paper makes
     * a statement about human proteins, but has really worked with
     * mouse proteins. In this case the human protein would be the
     * main interactor, while the experimentalForm would be the
     * mouse protein listed in this element. Optionally this can
     * refer to the experiment(s) in which this form has been used. 
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorList _experimentalInteractorList;

    /**
     * Sequence features relevant for the interaction, for example
     * binding domains, and experimental modifications, e.g.
     * protein tags. 
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureList _featureList;

    /**
     * The host organism(s) in which the protein has been
     * expressed. If not given, it is assumed to be the native
     * species of the protein.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList _hostOrganismList;

    /**
     * Confidence in participant detection.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType _confidenceList;

    /**
     * Lists parameters which are relevant for the Interaction,
     * e.g. kinetics.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterList _parameterList;

    /**
     * Semi-structured additional description of the data contained
     * in the entry.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType _attributeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ParticipantType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getAttributeListReturns the value of field
     * 'attributeList'. The field 'attributeList' has the following
     * description: Semi-structured additional description of the
     * data contained in the entry.
     * 
     * @return the value of field 'attributeList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType getAttributeList()
    {
        return this._attributeList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType getAttributeList()

    /**
     * Method getBiologicalRoleReturns the value of field
     * 'biologicalRole'. The field 'biologicalRole' has the
     * following description: The role of the participant in the
     * interaction. This describes the biological role, e.g. enzyme
     * or enzyme target. The experimental role of the participant,
     * e.g. 'bait', is shown in experimentalForm. This element is
     * controlled by the PSI-MI controlled vocabulary
     * "biologicalRole", root term id MI:0500.
     * 
     * @return the value of field 'biologicalRole'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getBiologicalRole()
    {
        return this._biologicalRole;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getBiologicalRole()

    /**
     * Method getConfidenceListReturns the value of field
     * 'confidenceList'. The field 'confidenceList' has the
     * following description: Confidence in participant detection.
     * 
     * @return the value of field 'confidenceList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType getConfidenceList()
    {
        return this._confidenceList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType getConfidenceList()

    /**
     * Method getExperimentalInteractorListReturns the value of
     * field 'experimentalInteractorList'. The field
     * 'experimentalInteractorList' has the following description:
     * Describes molecules which have been used in specific
     * experiments if these molecules are different from the one
     * listed as interactors. Example: The author of a paper makes
     * a statement about human proteins, but has really worked with
     * mouse proteins. In this case the human protein would be the
     * main interactor, while the experimentalForm would be the
     * mouse protein listed in this element. Optionally this can
     * refer to the experiment(s) in which this form has been used.
     * 
     * 
     * @return the value of field 'experimentalInteractorList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorList getExperimentalInteractorList()
    {
        return this._experimentalInteractorList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorList getExperimentalInteractorList()

    /**
     * Method getExperimentalPreparationListReturns the value of
     * field 'experimentalPreparationList'. The field
     * 'experimentalPreparationList' has the following description:
     * Terms describing the experimental sample preparation. 
     * 
     * @return the value of field 'experimentalPreparationList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparationList getExperimentalPreparationList()
    {
        return this._experimentalPreparationList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparationList getExperimentalPreparationList()

    /**
     * Method getExperimentalRoleListReturns the value of field
     * 'experimentalRoleList'. The field 'experimentalRoleList' has
     * the following description: The role(s) of the participant in
     * the interaction, e.g. bait. 
     * 
     * @return the value of field 'experimentalRoleList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRoleList getExperimentalRoleList()
    {
        return this._experimentalRoleList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRoleList getExperimentalRoleList()

    /**
     * Method getFeatureListReturns the value of field
     * 'featureList'. The field 'featureList' has the following
     * description: Sequence features relevant for the interaction,
     * for example binding domains, and experimental modifications,
     * e.g. protein tags. 
     * 
     * @return the value of field 'featureList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureList getFeatureList()
    {
        return this._featureList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureList getFeatureList()

    /**
     * Method getHostOrganismListReturns the value of field
     * 'hostOrganismList'. The field 'hostOrganismList' has the
     * following description: The host organism(s) in which the
     * protein has been expressed. If not given, it is assumed to
     * be the native species of the protein.
     * 
     * @return the value of field 'hostOrganismList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList getHostOrganismList()
    {
        return this._hostOrganismList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList getHostOrganismList()

    /**
     * Method getIdReturns the value of field 'id'.
     * 
     * @return the value of field 'id'.
     */
    public int getId()
    {
        return this._id;
    } //-- int getId() 

    /**
     * Method getNamesReturns the value of field 'names'. The field
     * 'names' has the following description: This contains the
     * name(s) for the participant given by the authors of a
     * publication.
     * 
     * @return the value of field 'names'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType getNames()
    {
        return this._names;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType getNames()

    /**
     * Method getParameterListReturns the value of field
     * 'parameterList'. The field 'parameterList' has the following
     * description: Lists parameters which are relevant for the
     * Interaction, e.g. kinetics.
     * 
     * @return the value of field 'parameterList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterList getParameterList()
    {
        return this._parameterList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterList getParameterList()

    /**
     * Method getParticipantIdentificationMethodListReturns the
     * value of field 'participantIdentificationMethodList'. The
     * field 'participantIdentificationMethodList' has the
     * following description: The method(s) by which this
     * participant has been determined. If this element is present,
     * its value supersedes experimentDescription/
     * participantIdentificationMethod.
     * 
     * @return the value of field
     * 'participantIdentificationMethodList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethodList getParticipantIdentificationMethodList()
    {
        return this._participantIdentificationMethodList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethodList getParticipantIdentificationMethodList()

    /**
     * Method getParticipantTypeChoiceReturns the value of field
     * 'participantTypeChoice'. The field 'participantTypeChoice'
     * has the following description: Description of the
     * Interactor. Refers to an already defined interactor in this
     * entry, fully describes an interactor, or references another
     * interaction defined in this entry, to allow the hierarchical
     * building up of complexes from subunits. 
     * 
     * @return the value of field 'participantTypeChoice'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantTypeChoice getParticipantTypeChoice()
    {
        return this._participantTypeChoice;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantTypeChoice getParticipantTypeChoice()

    /**
     * Method getXrefReturns the value of field 'xref'. The field
     * 'xref' has the following description: Contains the xref(s)
     * for the participant given by the authors of a publication.
     * 
     * @return the value of field 'xref'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType getXref()
    {
        return this._xref;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType getXref()

    /**
     * Method hasId
     */
    public boolean hasId()
    {
        return this._has_id;
    } //-- boolean hasId() 

    /**
     * Method isValid
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {

        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {

        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method setAttributeListSets the value of field
     * 'attributeList'. The field 'attributeList' has the following
     * description: Semi-structured additional description of the
     * data contained in the entry.
     * 
     * @param attributeList the value of field 'attributeList'.
     */
    public void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType attributeList)
    {
        this._attributeList = attributeList;
    } //-- void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType)

    /**
     * Method setBiologicalRoleSets the value of field
     * 'biologicalRole'. The field 'biologicalRole' has the
     * following description: The role of the participant in the
     * interaction. This describes the biological role, e.g. enzyme
     * or enzyme target. The experimental role of the participant,
     * e.g. 'bait', is shown in experimentalForm. This element is
     * controlled by the PSI-MI controlled vocabulary
     * "biologicalRole", root term id MI:0500.
     * 
     * @param biologicalRole the value of field 'biologicalRole'.
     */
    public void setBiologicalRole(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType biologicalRole)
    {
        this._biologicalRole = biologicalRole;
    } //-- void setBiologicalRole(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType)

    /**
     * Method setConfidenceListSets the value of field
     * 'confidenceList'. The field 'confidenceList' has the
     * following description: Confidence in participant detection.
     * 
     * @param confidenceList the value of field 'confidenceList'.
     */
    public void setConfidenceList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType confidenceList)
    {
        this._confidenceList = confidenceList;
    } //-- void setConfidenceList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType)

    /**
     * Method setExperimentalInteractorListSets the value of field
     * 'experimentalInteractorList'. The field
     * 'experimentalInteractorList' has the following description:
     * Describes molecules which have been used in specific
     * experiments if these molecules are different from the one
     * listed as interactors. Example: The author of a paper makes
     * a statement about human proteins, but has really worked with
     * mouse proteins. In this case the human protein would be the
     * main interactor, while the experimentalForm would be the
     * mouse protein listed in this element. Optionally this can
     * refer to the experiment(s) in which this form has been used.
     * 
     * 
     * @param experimentalInteractorList the value of field
     * 'experimentalInteractorList'.
     */
    public void setExperimentalInteractorList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorList experimentalInteractorList)
    {
        this._experimentalInteractorList = experimentalInteractorList;
    } //-- void setExperimentalInteractorList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorList)

    /**
     * Method setExperimentalPreparationListSets the value of field
     * 'experimentalPreparationList'. The field
     * 'experimentalPreparationList' has the following description:
     * Terms describing the experimental sample preparation. 
     * 
     * @param experimentalPreparationList the value of field
     * 'experimentalPreparationList'.
     */
    public void setExperimentalPreparationList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparationList experimentalPreparationList)
    {
        this._experimentalPreparationList = experimentalPreparationList;
    } //-- void setExperimentalPreparationList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparationList)

    /**
     * Method setExperimentalRoleListSets the value of field
     * 'experimentalRoleList'. The field 'experimentalRoleList' has
     * the following description: The role(s) of the participant in
     * the interaction, e.g. bait. 
     * 
     * @param experimentalRoleList the value of field
     * 'experimentalRoleList'.
     */
    public void setExperimentalRoleList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRoleList experimentalRoleList)
    {
        this._experimentalRoleList = experimentalRoleList;
    } //-- void setExperimentalRoleList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalRoleList)

    /**
     * Method setFeatureListSets the value of field 'featureList'.
     * The field 'featureList' has the following description:
     * Sequence features relevant for the interaction, for example
     * binding domains, and experimental modifications, e.g.
     * protein tags. 
     * 
     * @param featureList the value of field 'featureList'.
     */
    public void setFeatureList(org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureList featureList)
    {
        this._featureList = featureList;
    } //-- void setFeatureList(org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureList)

    /**
     * Method setHostOrganismListSets the value of field
     * 'hostOrganismList'. The field 'hostOrganismList' has the
     * following description: The host organism(s) in which the
     * protein has been expressed. If not given, it is assumed to
     * be the native species of the protein.
     * 
     * @param hostOrganismList the value of field 'hostOrganismList'
     */
    public void setHostOrganismList(org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList hostOrganismList)
    {
        this._hostOrganismList = hostOrganismList;
    } //-- void setHostOrganismList(org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList)

    /**
     * Method setIdSets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(int id)
    {
        this._id = id;
        this._has_id = true;
    } //-- void setId(int) 

    /**
     * Method setNamesSets the value of field 'names'. The field
     * 'names' has the following description: This contains the
     * name(s) for the participant given by the authors of a
     * publication.
     * 
     * @param names the value of field 'names'.
     */
    public void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType names)
    {
        this._names = names;
    } //-- void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType)

    /**
     * Method setParameterListSets the value of field
     * 'parameterList'. The field 'parameterList' has the following
     * description: Lists parameters which are relevant for the
     * Interaction, e.g. kinetics.
     * 
     * @param parameterList the value of field 'parameterList'.
     */
    public void setParameterList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterList parameterList)
    {
        this._parameterList = parameterList;
    } //-- void setParameterList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterList)

    /**
     * Method setParticipantIdentificationMethodListSets the value
     * of field 'participantIdentificationMethodList'. The field
     * 'participantIdentificationMethodList' has the following
     * description: The method(s) by which this participant has
     * been determined. If this element is present, its value
     * supersedes experimentDescription/
     * participantIdentificationMethod.
     * 
     * @param participantIdentificationMethodList the value of
     * field 'participantIdentificationMethodList'.
     */
    public void setParticipantIdentificationMethodList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethodList participantIdentificationMethodList)
    {
        this._participantIdentificationMethodList = participantIdentificationMethodList;
    } //-- void setParticipantIdentificationMethodList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantIdentificationMethodList)

    /**
     * Method setParticipantTypeChoiceSets the value of field
     * 'participantTypeChoice'. The field 'participantTypeChoice'
     * has the following description: Description of the
     * Interactor. Refers to an already defined interactor in this
     * entry, fully describes an interactor, or references another
     * interaction defined in this entry, to allow the hierarchical
     * building up of complexes from subunits. 
     * 
     * @param participantTypeChoice the value of field
     * 'participantTypeChoice'.
     */
    public void setParticipantTypeChoice(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantTypeChoice participantTypeChoice)
    {
        this._participantTypeChoice = participantTypeChoice;
    } //-- void setParticipantTypeChoice(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantTypeChoice)

    /**
     * Method setXrefSets the value of field 'xref'. The field
     * 'xref' has the following description: Contains the xref(s)
     * for the participant given by the authors of a publication.
     * 
     * @param xref the value of field 'xref'.
     */
    public void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType xref)
    {
        this._xref = xref;
    } //-- void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType)

    /**
     * Method unmarshalParticipantType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType unmarshalParticipantType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantType unmarshalParticipantType(java.io.Reader)

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
