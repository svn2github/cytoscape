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

import java.util.Vector;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * A molecular interaction.
 * 
 * @version $Revision$ $Date$
 */
public class InteractionElementType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Interaction identifier assigned by the IMEx consortium. Will
     * be unique for an interaction determined in one publication.
     * Details defined in the IMEx documents.
     */
    private java.lang.String _imexId;

    /**
     * All major objects have a numerical id which is unique to
     * that object within a PSI MI file. The object may be
     * repeated, though, e.g. in the denormalised representation.
     */
    private int _id;

    /**
     * keeps track of state for field: _id
     */
    private boolean _has_id;

    /**
     * Name(s) of the interaction.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType _names;

    /**
     * Interaction database ID
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType _xref;

    /**
     * Either refer to an already defined availability statement in
     * this entry or insert description.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementTypeChoice _interactionElementTypeChoice;

    /**
     * List of experiments in which this interaction has been
     * determined.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList _experimentList;

    /**
     * A list of molecules participating in this interaction. An
     * interaction has one (intramolecular), two (binary), or more
     * (n-ary, complexes) participants.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantList _participantList;

    /**
     * Describes inferred interactions, usually combining data from
     * more than one experiment. Examples: 1: Show the topology of
     * binary interactions within a complex. 2: Interaction
     * inferred from multiple experiments which on their own would
     * not support the interaction. Example: A-B in experiment 1,
     * B-C- in experiment 2, A-C is the inferred interaction.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteractionList _inferredInteractionList;

    /**
     * External controlled vocabulary characterising the
     * interaction type, for example "physical interaction".
     */
    private java.util.Vector _interactionTypeList;

    /**
     * If true, this element describes an interaction in a species
     * of interest, e.g. human, but has actually been investigated
     * in another organism, e.g. mouse. The transfer will usually
     * be based on a homology statement made by the data producer.
     * If this optional element is missing, it is assumed to be set
     * to false.
     */
    private boolean _modelled;

    /**
     * keeps track of state for field: _modelled
     */
    private boolean _has_modelled;

    /**
     * If true, this interaction is an intramolecular interaction,
     * e.g. an autophosphorylation. If missing, this element is
     * assumed to be false.
     */
    private boolean _intraMolecular = false;

    /**
     * keeps track of state for field: _intraMolecular
     */
    private boolean _has_intraMolecular;

    /**
     * If true, this interaction has been shown NOT to occur under
     * the described experimental conditions. Default false. If
     * this optional element is missing, it is assumed to be set to
     * false.
     */
    private boolean _negative = false;

    /**
     * keeps track of state for field: _negative
     */
    private boolean _has_negative;

    /**
     * Confidence in this interaction. Usually a statistical measure
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

    public InteractionElementType() {
        super();
        _interactionTypeList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addInteractionType
     * 
     * @param vInteractionType
     */
    public void addInteractionType(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType vInteractionType)
        throws java.lang.IndexOutOfBoundsException
    {
        _interactionTypeList.addElement(vInteractionType);
    } //-- void addInteractionType(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType)

    /**
     * Method addInteractionType
     * 
     * @param index
     * @param vInteractionType
     */
    public void addInteractionType(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType vInteractionType)
        throws java.lang.IndexOutOfBoundsException
    {
        _interactionTypeList.insertElementAt(vInteractionType, index);
    } //-- void addInteractionType(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType)

    /**
     * Method deleteIntraMolecular
     */
    public void deleteIntraMolecular()
    {
        this._has_intraMolecular= false;
    } //-- void deleteIntraMolecular() 

    /**
     * Method deleteModelled
     */
    public void deleteModelled()
    {
        this._has_modelled= false;
    } //-- void deleteModelled() 

    /**
     * Method deleteNegative
     */
    public void deleteNegative()
    {
        this._has_negative= false;
    } //-- void deleteNegative() 

    /**
     * Method enumerateInteractionType
     */
    public java.util.Enumeration enumerateInteractionType()
    {
        return _interactionTypeList.elements();
    } //-- java.util.Enumeration enumerateInteractionType() 

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
     * Method getConfidenceListReturns the value of field
     * 'confidenceList'. The field 'confidenceList' has the
     * following description: Confidence in this interaction.
     * Usually a statistical measure.
     * 
     * @return the value of field 'confidenceList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType getConfidenceList()
    {
        return this._confidenceList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType getConfidenceList()

    /**
     * Method getExperimentListReturns the value of field
     * 'experimentList'. The field 'experimentList' has the
     * following description: List of experiments in which this
     * interaction has been determined.
     * 
     * @return the value of field 'experimentList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList getExperimentList()
    {
        return this._experimentList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList getExperimentList()

    /**
     * Method getIdReturns the value of field 'id'. The field 'id'
     * has the following description: All major objects have a
     * numerical id which is unique to that object within a PSI MI
     * file. The object may be repeated, though, e.g. in the
     * denormalised representation.
     * 
     * @return the value of field 'id'.
     */
    public int getId()
    {
        return this._id;
    } //-- int getId() 

    /**
     * Method getImexIdReturns the value of field 'imexId'. The
     * field 'imexId' has the following description: Interaction
     * identifier assigned by the IMEx consortium. Will be unique
     * for an interaction determined in one publication. Details
     * defined in the IMEx documents.
     * 
     * @return the value of field 'imexId'.
     */
    public java.lang.String getImexId()
    {
        return this._imexId;
    } //-- java.lang.String getImexId() 

    /**
     * Method getInferredInteractionListReturns the value of field
     * 'inferredInteractionList'. The field
     * 'inferredInteractionList' has the following description:
     * Describes inferred interactions, usually combining data from
     * more than one experiment. Examples: 1: Show the topology of
     * binary interactions within a complex. 2: Interaction
     * inferred from multiple experiments which on their own would
     * not support the interaction. Example: A-B in experiment 1,
     * B-C- in experiment 2, A-C is the inferred interaction.
     * 
     * @return the value of field 'inferredInteractionList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteractionList getInferredInteractionList()
    {
        return this._inferredInteractionList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteractionList getInferredInteractionList()

    /**
     * Method getInteractionElementTypeChoiceReturns the value of
     * field 'interactionElementTypeChoice'. The field
     * 'interactionElementTypeChoice' has the following
     * description: Either refer to an already defined availability
     * statement in this entry or insert description.
     * 
     * @return the value of field 'interactionElementTypeChoice'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementTypeChoice getInteractionElementTypeChoice()
    {
        return this._interactionElementTypeChoice;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementTypeChoice getInteractionElementTypeChoice()

    /**
     * Method getInteractionType
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getInteractionType(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _interactionTypeList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType) _interactionTypeList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getInteractionType(int)

    /**
     * Method getInteractionType
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType[] getInteractionType()
    {
        int size = _interactionTypeList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType) _interactionTypeList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType[] getInteractionType()

    /**
     * Method getInteractionTypeCount
     */
    public int getInteractionTypeCount()
    {
        return _interactionTypeList.size();
    } //-- int getInteractionTypeCount() 

    /**
     * Method getIntraMolecularReturns the value of field
     * 'intraMolecular'. The field 'intraMolecular' has the
     * following description: If true, this interaction is an
     * intramolecular interaction, e.g. an autophosphorylation. If
     * missing, this element is assumed to be false.
     * 
     * @return the value of field 'intraMolecular'.
     */
    public boolean getIntraMolecular()
    {
        return this._intraMolecular;
    } //-- boolean getIntraMolecular() 

    /**
     * Method getModelledReturns the value of field 'modelled'. The
     * field 'modelled' has the following description: If true,
     * this element describes an interaction in a species of
     * interest, e.g. human, but has actually been investigated in
     * another organism, e.g. mouse. The transfer will usually be
     * based on a homology statement made by the data producer. If
     * this optional element is missing, it is assumed to be set to
     * false.
     * 
     * @return the value of field 'modelled'.
     */
    public boolean getModelled()
    {
        return this._modelled;
    } //-- boolean getModelled() 

    /**
     * Method getNamesReturns the value of field 'names'. The field
     * 'names' has the following description: Name(s) of the
     * interaction.
     * 
     * @return the value of field 'names'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType getNames()
    {
        return this._names;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType getNames()

    /**
     * Method getNegativeReturns the value of field 'negative'. The
     * field 'negative' has the following description: If true,
     * this interaction has been shown NOT to occur under the
     * described experimental conditions. Default false. If this
     * optional element is missing, it is assumed to be set to
     * false.
     * 
     * @return the value of field 'negative'.
     */
    public boolean getNegative()
    {
        return this._negative;
    } //-- boolean getNegative() 

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
     * Method getParticipantListReturns the value of field
     * 'participantList'. The field 'participantList' has the
     * following description: A list of molecules participating in
     * this interaction. An interaction has one (intramolecular),
     * two (binary), or more (n-ary, complexes) participants.
     * 
     * @return the value of field 'participantList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantList getParticipantList()
    {
        return this._participantList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantList getParticipantList()

    /**
     * Method getXrefReturns the value of field 'xref'. The field
     * 'xref' has the following description: Interaction database
     * ID
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
     * Method hasIntraMolecular
     */
    public boolean hasIntraMolecular()
    {
        return this._has_intraMolecular;
    } //-- boolean hasIntraMolecular() 

    /**
     * Method hasModelled
     */
    public boolean hasModelled()
    {
        return this._has_modelled;
    } //-- boolean hasModelled() 

    /**
     * Method hasNegative
     */
    public boolean hasNegative()
    {
        return this._has_negative;
    } //-- boolean hasNegative() 

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
     * Method removeAllInteractionType
     */
    public void removeAllInteractionType()
    {
        _interactionTypeList.removeAllElements();
    } //-- void removeAllInteractionType() 

    /**
     * Method removeInteractionType
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType removeInteractionType(int index)
    {
        java.lang.Object obj = _interactionTypeList.elementAt(index);
        _interactionTypeList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType removeInteractionType(int)

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
     * Method setConfidenceListSets the value of field
     * 'confidenceList'. The field 'confidenceList' has the
     * following description: Confidence in this interaction.
     * Usually a statistical measure.
     * 
     * @param confidenceList the value of field 'confidenceList'.
     */
    public void setConfidenceList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType confidenceList)
    {
        this._confidenceList = confidenceList;
    } //-- void setConfidenceList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType)

    /**
     * Method setExperimentListSets the value of field
     * 'experimentList'. The field 'experimentList' has the
     * following description: List of experiments in which this
     * interaction has been determined.
     * 
     * @param experimentList the value of field 'experimentList'.
     */
    public void setExperimentList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList experimentList)
    {
        this._experimentList = experimentList;
    } //-- void setExperimentList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList)

    /**
     * Method setIdSets the value of field 'id'. The field 'id' has
     * the following description: All major objects have a
     * numerical id which is unique to that object within a PSI MI
     * file. The object may be repeated, though, e.g. in the
     * denormalised representation.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(int id)
    {
        this._id = id;
        this._has_id = true;
    } //-- void setId(int) 

    /**
     * Method setImexIdSets the value of field 'imexId'. The field
     * 'imexId' has the following description: Interaction
     * identifier assigned by the IMEx consortium. Will be unique
     * for an interaction determined in one publication. Details
     * defined in the IMEx documents.
     * 
     * @param imexId the value of field 'imexId'.
     */
    public void setImexId(java.lang.String imexId)
    {
        this._imexId = imexId;
    } //-- void setImexId(java.lang.String) 

    /**
     * Method setInferredInteractionListSets the value of field
     * 'inferredInteractionList'. The field
     * 'inferredInteractionList' has the following description:
     * Describes inferred interactions, usually combining data from
     * more than one experiment. Examples: 1: Show the topology of
     * binary interactions within a complex. 2: Interaction
     * inferred from multiple experiments which on their own would
     * not support the interaction. Example: A-B in experiment 1,
     * B-C- in experiment 2, A-C is the inferred interaction.
     * 
     * @param inferredInteractionList the value of field
     * 'inferredInteractionList'.
     */
    public void setInferredInteractionList(org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteractionList inferredInteractionList)
    {
        this._inferredInteractionList = inferredInteractionList;
    } //-- void setInferredInteractionList(org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteractionList)

    /**
     * Method setInteractionElementTypeChoiceSets the value of
     * field 'interactionElementTypeChoice'. The field
     * 'interactionElementTypeChoice' has the following
     * description: Either refer to an already defined availability
     * statement in this entry or insert description.
     * 
     * @param interactionElementTypeChoice the value of field
     * 'interactionElementTypeChoice'.
     */
    public void setInteractionElementTypeChoice(org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementTypeChoice interactionElementTypeChoice)
    {
        this._interactionElementTypeChoice = interactionElementTypeChoice;
    } //-- void setInteractionElementTypeChoice(org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementTypeChoice)

    /**
     * Method setInteractionType
     * 
     * @param index
     * @param vInteractionType
     */
    public void setInteractionType(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType vInteractionType)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _interactionTypeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _interactionTypeList.setElementAt(vInteractionType, index);
    } //-- void setInteractionType(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType)

    /**
     * Method setInteractionType
     * 
     * @param interactionTypeArray
     */
    public void setInteractionType(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType[] interactionTypeArray)
    {
        //-- copy array
        _interactionTypeList.removeAllElements();
        for (int i = 0; i < interactionTypeArray.length; i++) {
            _interactionTypeList.addElement(interactionTypeArray[i]);
        }
    } //-- void setInteractionType(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType)

    /**
     * Method setIntraMolecularSets the value of field
     * 'intraMolecular'. The field 'intraMolecular' has the
     * following description: If true, this interaction is an
     * intramolecular interaction, e.g. an autophosphorylation. If
     * missing, this element is assumed to be false.
     * 
     * @param intraMolecular the value of field 'intraMolecular'.
     */
    public void setIntraMolecular(boolean intraMolecular)
    {
        this._intraMolecular = intraMolecular;
        this._has_intraMolecular = true;
    } //-- void setIntraMolecular(boolean) 

    /**
     * Method setModelledSets the value of field 'modelled'. The
     * field 'modelled' has the following description: If true,
     * this element describes an interaction in a species of
     * interest, e.g. human, but has actually been investigated in
     * another organism, e.g. mouse. The transfer will usually be
     * based on a homology statement made by the data producer. If
     * this optional element is missing, it is assumed to be set to
     * false.
     * 
     * @param modelled the value of field 'modelled'.
     */
    public void setModelled(boolean modelled)
    {
        this._modelled = modelled;
        this._has_modelled = true;
    } //-- void setModelled(boolean) 

    /**
     * Method setNamesSets the value of field 'names'. The field
     * 'names' has the following description: Name(s) of the
     * interaction.
     * 
     * @param names the value of field 'names'.
     */
    public void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType names)
    {
        this._names = names;
    } //-- void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType)

    /**
     * Method setNegativeSets the value of field 'negative'. The
     * field 'negative' has the following description: If true,
     * this interaction has been shown NOT to occur under the
     * described experimental conditions. Default false. If this
     * optional element is missing, it is assumed to be set to
     * false.
     * 
     * @param negative the value of field 'negative'.
     */
    public void setNegative(boolean negative)
    {
        this._negative = negative;
        this._has_negative = true;
    } //-- void setNegative(boolean) 

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
     * Method setParticipantListSets the value of field
     * 'participantList'. The field 'participantList' has the
     * following description: A list of molecules participating in
     * this interaction. An interaction has one (intramolecular),
     * two (binary), or more (n-ary, complexes) participants.
     * 
     * @param participantList the value of field 'participantList'.
     */
    public void setParticipantList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantList participantList)
    {
        this._participantList = participantList;
    } //-- void setParticipantList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParticipantList)

    /**
     * Method setXrefSets the value of field 'xref'. The field
     * 'xref' has the following description: Interaction database
     * ID
     * 
     * @param xref the value of field 'xref'.
     */
    public void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType xref)
    {
        this._xref = xref;
    } //-- void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType)

    /**
     * Method unmarshalInteractionElementType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementType unmarshalInteractionElementType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionElementType unmarshalInteractionElementType(java.io.Reader)

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
