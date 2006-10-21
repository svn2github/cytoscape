/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id$
 */

package org.cytoscape.coreplugin.psi_mi.schema.mi1;

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
     * Name(s) of the interaction.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType _names;

    /**
     * Either refer to an already defined availability statement in
     * this entry or insert description.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionElementTypeChoice _interactionElementTypeChoice;

    /**
     * List of experiments in which this interaction has been
     * determined.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentList _experimentList;

    /**
     * A list of molecules participating in this interaction.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.ParticipantList _participantList;

    /**
     * External controlled vocabulary characterising the
     * interaction type, for example "aggregation".
     */
    private java.util.Vector _interactionTypeList;

    /**
     * Confidence in this interaction. Usually a statistical measure
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence _confidence;

    /**
     * Interaction database ID
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType _xref;

    /**
     * Semi-structured additional description of the data contained
     * in the entry.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType _attributeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public InteractionElementType() {
        super();
        _interactionTypeList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionElementType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addInteractionType
     * 
     * @param vInteractionType
     */
    public void addInteractionType(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType vInteractionType)
        throws java.lang.IndexOutOfBoundsException
    {
        _interactionTypeList.addElement(vInteractionType);
    } //-- void addInteractionType(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType)

    /**
     * Method addInteractionType
     * 
     * @param index
     * @param vInteractionType
     */
    public void addInteractionType(int index, org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType vInteractionType)
        throws java.lang.IndexOutOfBoundsException
    {
        _interactionTypeList.insertElementAt(vInteractionType, index);
    } //-- void addInteractionType(int, org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType)

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
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType getAttributeList()
    {
        return this._attributeList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType getAttributeList()

    /**
     * Method getConfidenceReturns the value of field 'confidence'.
     * The field 'confidence' has the following description:
     * Confidence in this interaction. Usually a statistical
     * measure.
     * 
     * @return the value of field 'confidence'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence getConfidence()
    {
        return this._confidence;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence getConfidence()

    /**
     * Method getExperimentListReturns the value of field
     * 'experimentList'. The field 'experimentList' has the
     * following description: List of experiments in which this
     * interaction has been determined.
     * 
     * @return the value of field 'experimentList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentList getExperimentList()
    {
        return this._experimentList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentList getExperimentList()

    /**
     * Method getInteractionElementTypeChoiceReturns the value of
     * field 'interactionElementTypeChoice'. The field
     * 'interactionElementTypeChoice' has the following
     * description: Either refer to an already defined availability
     * statement in this entry or insert description.
     * 
     * @return the value of field 'interactionElementTypeChoice'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionElementTypeChoice getInteractionElementTypeChoice()
    {
        return this._interactionElementTypeChoice;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionElementTypeChoice getInteractionElementTypeChoice()

    /**
     * Method getInteractionType
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType getInteractionType(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _interactionTypeList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType) _interactionTypeList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType getInteractionType(int)

    /**
     * Method getInteractionType
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType[] getInteractionType()
    {
        int size = _interactionTypeList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType) _interactionTypeList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType[] getInteractionType()

    /**
     * Method getInteractionTypeCount
     */
    public int getInteractionTypeCount()
    {
        return _interactionTypeList.size();
    } //-- int getInteractionTypeCount() 

    /**
     * Method getNamesReturns the value of field 'names'. The field
     * 'names' has the following description: Name(s) of the
     * interaction.
     * 
     * @return the value of field 'names'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType getNames()
    {
        return this._names;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType getNames()

    /**
     * Method getParticipantListReturns the value of field
     * 'participantList'. The field 'participantList' has the
     * following description: A list of molecules participating in
     * this interaction.
     * 
     * @return the value of field 'participantList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.ParticipantList getParticipantList()
    {
        return this._participantList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ParticipantList getParticipantList()

    /**
     * Method getXrefReturns the value of field 'xref'. The field
     * 'xref' has the following description: Interaction database
     * ID
     * 
     * @return the value of field 'xref'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType getXref()
    {
        return this._xref;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType getXref()

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
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType removeInteractionType(int index)
    {
        java.lang.Object obj = _interactionTypeList.elementAt(index);
        _interactionTypeList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType removeInteractionType(int)

    /**
     * Method setAttributeListSets the value of field
     * 'attributeList'. The field 'attributeList' has the following
     * description: Semi-structured additional description of the
     * data contained in the entry.
     * 
     * @param attributeList the value of field 'attributeList'.
     */
    public void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType attributeList)
    {
        this._attributeList = attributeList;
    } //-- void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType)

    /**
     * Method setConfidenceSets the value of field 'confidence'.
     * The field 'confidence' has the following description:
     * Confidence in this interaction. Usually a statistical
     * measure.
     * 
     * @param confidence the value of field 'confidence'.
     */
    public void setConfidence(org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence confidence)
    {
        this._confidence = confidence;
    } //-- void setConfidence(org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence)

    /**
     * Method setExperimentListSets the value of field
     * 'experimentList'. The field 'experimentList' has the
     * following description: List of experiments in which this
     * interaction has been determined.
     * 
     * @param experimentList the value of field 'experimentList'.
     */
    public void setExperimentList(org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentList experimentList)
    {
        this._experimentList = experimentList;
    } //-- void setExperimentList(org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentList)

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
    public void setInteractionElementTypeChoice(org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionElementTypeChoice interactionElementTypeChoice)
    {
        this._interactionElementTypeChoice = interactionElementTypeChoice;
    } //-- void setInteractionElementTypeChoice(org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionElementTypeChoice)

    /**
     * Method setInteractionType
     * 
     * @param index
     * @param vInteractionType
     */
    public void setInteractionType(int index, org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType vInteractionType)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _interactionTypeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _interactionTypeList.setElementAt(vInteractionType, index);
    } //-- void setInteractionType(int, org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType)

    /**
     * Method setInteractionType
     * 
     * @param interactionTypeArray
     */
    public void setInteractionType(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType[] interactionTypeArray)
    {
        //-- copy array
        _interactionTypeList.removeAllElements();
        for (int i = 0; i < interactionTypeArray.length; i++) {
            _interactionTypeList.addElement(interactionTypeArray[i]);
        }
    } //-- void setInteractionType(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType)

    /**
     * Method setNamesSets the value of field 'names'. The field
     * 'names' has the following description: Name(s) of the
     * interaction.
     * 
     * @param names the value of field 'names'.
     */
    public void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType names)
    {
        this._names = names;
    } //-- void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType)

    /**
     * Method setParticipantListSets the value of field
     * 'participantList'. The field 'participantList' has the
     * following description: A list of molecules participating in
     * this interaction.
     * 
     * @param participantList the value of field 'participantList'.
     */
    public void setParticipantList(org.cytoscape.coreplugin.psi_mi.schema.mi1.ParticipantList participantList)
    {
        this._participantList = participantList;
    } //-- void setParticipantList(org.cytoscape.coreplugin.psi_mi.schema.mi1.ParticipantList)

    /**
     * Method setXrefSets the value of field 'xref'. The field
     * 'xref' has the following description: Interaction database
     * ID
     * 
     * @param xref the value of field 'xref'.
     */
    public void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType xref)
    {
        this._xref = xref;
    } //-- void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType)

    /**
     * Method unmarshalInteractionElementType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionElementType unmarshalInteractionElementType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionElementType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionElementType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractionElementType unmarshalInteractionElementType(java.io.Reader)

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
