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

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Describes one set of experimental parameters.
 * 
 * @version $Revision$ $Date$
 */
public class ExperimentType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id
     */
    private java.lang.String _id;

    /**
     * Field _names
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType _names;

    /**
     * Publication describing the experiment.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType _bibref;

    /**
     * Refers to external database description of the experiment.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType _xref;

    /**
     * The host organism in which the experiment has been performed.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.HostOrganism _hostOrganism;

    /**
     * Experimental method to determine the interaction. External
     * controlled vocabulary.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType _interactionDetection;

    /**
     * Experimental method to determine the interactors involved in
     * the interaction. External controlled vocabulary.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType _participantDetection;

    /**
     * Experimental method to determine the features of
     * interactors. External controlled vocabulary.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType _featureDetection;

    /**
     * Confidence in this experiment. Usually a statistical measure.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence _confidence;

    /**
     * Semi-structured additional description of the experiment.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType _attributeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ExperimentType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getAttributeListReturns the value of field
     * 'attributeList'. The field 'attributeList' has the following
     * description: Semi-structured additional description of the
     * experiment.
     * 
     * @return the value of field 'attributeList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType getAttributeList()
    {
        return this._attributeList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType getAttributeList()

    /**
     * Method getBibrefReturns the value of field 'bibref'. The
     * field 'bibref' has the following description: Publication
     * describing the experiment.
     * 
     * @return the value of field 'bibref'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType getBibref()
    {
        return this._bibref;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType getBibref()

    /**
     * Method getConfidenceReturns the value of field 'confidence'.
     * The field 'confidence' has the following description:
     * Confidence in this experiment. Usually a statistical
     * measure.
     * 
     * @return the value of field 'confidence'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence getConfidence()
    {
        return this._confidence;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence getConfidence()

    /**
     * Method getFeatureDetectionReturns the value of field
     * 'featureDetection'. The field 'featureDetection' has the
     * following description: Experimental method to determine the
     * features of interactors. External controlled vocabulary.
     * 
     * @return the value of field 'featureDetection'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType getFeatureDetection()
    {
        return this._featureDetection;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType getFeatureDetection()

    /**
     * Method getHostOrganismReturns the value of field
     * 'hostOrganism'. The field 'hostOrganism' has the following
     * description: The host organism in which the experiment has
     * been performed.
     * 
     * @return the value of field 'hostOrganism'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.HostOrganism getHostOrganism()
    {
        return this._hostOrganism;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.HostOrganism getHostOrganism()

    /**
     * Method getIdReturns the value of field 'id'.
     * 
     * @return the value of field 'id'.
     */
    public java.lang.String getId()
    {
        return this._id;
    } //-- java.lang.String getId() 

    /**
     * Method getInteractionDetectionReturns the value of field
     * 'interactionDetection'. The field 'interactionDetection' has
     * the following description: Experimental method to determine
     * the interaction. External controlled vocabulary.
     * 
     * @return the value of field 'interactionDetection'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType getInteractionDetection()
    {
        return this._interactionDetection;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType getInteractionDetection()

    /**
     * Method getNamesReturns the value of field 'names'.
     * 
     * @return the value of field 'names'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType getNames()
    {
        return this._names;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType getNames()

    /**
     * Method getParticipantDetectionReturns the value of field
     * 'participantDetection'. The field 'participantDetection' has
     * the following description: Experimental method to determine
     * the interactors involved in the interaction. External
     * controlled vocabulary.
     * 
     * @return the value of field 'participantDetection'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType getParticipantDetection()
    {
        return this._participantDetection;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType getParticipantDetection()

    /**
     * Method getXrefReturns the value of field 'xref'. The field
     * 'xref' has the following description: Refers to external
     * database description of the experiment.
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
     * Method setAttributeListSets the value of field
     * 'attributeList'. The field 'attributeList' has the following
     * description: Semi-structured additional description of the
     * experiment.
     * 
     * @param attributeList the value of field 'attributeList'.
     */
    public void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType attributeList)
    {
        this._attributeList = attributeList;
    } //-- void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType)

    /**
     * Method setBibrefSets the value of field 'bibref'. The field
     * 'bibref' has the following description: Publication
     * describing the experiment.
     * 
     * @param bibref the value of field 'bibref'.
     */
    public void setBibref(org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType bibref)
    {
        this._bibref = bibref;
    } //-- void setBibref(org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType)

    /**
     * Method setConfidenceSets the value of field 'confidence'.
     * The field 'confidence' has the following description:
     * Confidence in this experiment. Usually a statistical
     * measure.
     * 
     * @param confidence the value of field 'confidence'.
     */
    public void setConfidence(org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence confidence)
    {
        this._confidence = confidence;
    } //-- void setConfidence(org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence)

    /**
     * Method setFeatureDetectionSets the value of field
     * 'featureDetection'. The field 'featureDetection' has the
     * following description: Experimental method to determine the
     * features of interactors. External controlled vocabulary.
     * 
     * @param featureDetection the value of field 'featureDetection'
     */
    public void setFeatureDetection(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType featureDetection)
    {
        this._featureDetection = featureDetection;
    } //-- void setFeatureDetection(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType)

    /**
     * Method setHostOrganismSets the value of field
     * 'hostOrganism'. The field 'hostOrganism' has the following
     * description: The host organism in which the experiment has
     * been performed.
     * 
     * @param hostOrganism the value of field 'hostOrganism'.
     */
    public void setHostOrganism(org.cytoscape.coreplugin.psi_mi.schema.mi1.HostOrganism hostOrganism)
    {
        this._hostOrganism = hostOrganism;
    } //-- void setHostOrganism(org.cytoscape.coreplugin.psi_mi.schema.mi1.HostOrganism)

    /**
     * Method setIdSets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(java.lang.String id)
    {
        this._id = id;
    } //-- void setId(java.lang.String) 

    /**
     * Method setInteractionDetectionSets the value of field
     * 'interactionDetection'. The field 'interactionDetection' has
     * the following description: Experimental method to determine
     * the interaction. External controlled vocabulary.
     * 
     * @param interactionDetection the value of field
     * 'interactionDetection'.
     */
    public void setInteractionDetection(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType interactionDetection)
    {
        this._interactionDetection = interactionDetection;
    } //-- void setInteractionDetection(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType)

    /**
     * Method setNamesSets the value of field 'names'.
     * 
     * @param names the value of field 'names'.
     */
    public void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType names)
    {
        this._names = names;
    } //-- void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType)

    /**
     * Method setParticipantDetectionSets the value of field
     * 'participantDetection'. The field 'participantDetection' has
     * the following description: Experimental method to determine
     * the interactors involved in the interaction. External
     * controlled vocabulary.
     * 
     * @param participantDetection the value of field
     * 'participantDetection'.
     */
    public void setParticipantDetection(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType participantDetection)
    {
        this._participantDetection = participantDetection;
    } //-- void setParticipantDetection(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType)

    /**
     * Method setXrefSets the value of field 'xref'. The field
     * 'xref' has the following description: Refers to external
     * database description of the experiment.
     * 
     * @param xref the value of field 'xref'.
     */
    public void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType xref)
    {
        this._xref = xref;
    } //-- void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType)

    /**
     * Method unmarshalExperimentType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentType unmarshalExperimentType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ExperimentType unmarshalExperimentType(java.io.Reader)

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
