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
 * Describes one set of experimental parameters.
 * 
 * @version $Revision$ $Date$
 */
public class ExperimentType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * All major objects have a numerical id which must be unique
     * to that object within an entry. The object may be repeated,
     * though, e.g. in the denormalised representation.
     */
    private int _id;

    /**
     * keeps track of state for field: _id
     */
    private boolean _has_id;

    /**
     * Field _names
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType _names;

    /**
     * Publication describing the experiment.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.BibrefType _bibref;

    /**
     * Refers to external database description of the experiment.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType _xref;

    /**
     * The host organism(s) in which the experiment has been
     * performed.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList _hostOrganismList;

    /**
     * Experimental method to determine the interaction. This
     * element is controlled by the PSI-MI controlled vocabulary
     * "interaction detection method", root term id MI:0001.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType _interactionDetectionMethod;

    /**
     * Experimental method to determine the interactors involved in
     * the interaction. This element is controlled by the PSI-MI
     * controlled vocabulary "participant identification method",
     * root term id MI:0002.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType _participantIdentificationMethod;

    /**
     * Experimental method to determine the features of
     * interactors. If this element is filled it is assumed to
     * apply to all features described in the experiment. But can
     * be overridden by the featureDetectionMethod given in the
     * individual feature. This element is controlled by the PSI-MI
     * controlled vocabulary "feature detection method", root term
     * id MI:0003.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType _featureDetectionMethod;

    /**
     * Confidence in this experiment. Usually a statistical measure.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType _confidenceList;

    /**
     * Semi-structured additional description of the experiment.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType _attributeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ExperimentType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType()


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
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType getAttributeList()
    {
        return this._attributeList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType getAttributeList()

    /**
     * Method getBibrefReturns the value of field 'bibref'. The
     * field 'bibref' has the following description: Publication
     * describing the experiment.
     * 
     * @return the value of field 'bibref'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.BibrefType getBibref()
    {
        return this._bibref;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BibrefType getBibref()

    /**
     * Method getConfidenceListReturns the value of field
     * 'confidenceList'. The field 'confidenceList' has the
     * following description: Confidence in this experiment.
     * Usually a statistical measure.
     * 
     * @return the value of field 'confidenceList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType getConfidenceList()
    {
        return this._confidenceList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType getConfidenceList()

    /**
     * Method getFeatureDetectionMethodReturns the value of field
     * 'featureDetectionMethod'. The field 'featureDetectionMethod'
     * has the following description: Experimental method to
     * determine the features of interactors. If this element is
     * filled it is assumed to apply to all features described in
     * the experiment. But can be overridden by the
     * featureDetectionMethod given in the individual feature. This
     * element is controlled by the PSI-MI controlled vocabulary
     * "feature detection method", root term id MI:0003.
     * 
     * @return the value of field 'featureDetectionMethod'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getFeatureDetectionMethod()
    {
        return this._featureDetectionMethod;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getFeatureDetectionMethod()

    /**
     * Method getHostOrganismListReturns the value of field
     * 'hostOrganismList'. The field 'hostOrganismList' has the
     * following description: The host organism(s) in which the
     * experiment has been performed.
     * 
     * @return the value of field 'hostOrganismList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList getHostOrganismList()
    {
        return this._hostOrganismList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList getHostOrganismList()

    /**
     * Method getIdReturns the value of field 'id'. The field 'id'
     * has the following description: All major objects have a
     * numerical id which must be unique to that object within an
     * entry. The object may be repeated, though, e.g. in the
     * denormalised representation.
     * 
     * @return the value of field 'id'.
     */
    public int getId()
    {
        return this._id;
    } //-- int getId() 

    /**
     * Method getInteractionDetectionMethodReturns the value of
     * field 'interactionDetectionMethod'. The field
     * 'interactionDetectionMethod' has the following description:
     * Experimental method to determine the interaction. This
     * element is controlled by the PSI-MI controlled vocabulary
     * "interaction detection method", root term id MI:0001.
     * 
     * @return the value of field 'interactionDetectionMethod'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getInteractionDetectionMethod()
    {
        return this._interactionDetectionMethod;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getInteractionDetectionMethod()

    /**
     * Method getNamesReturns the value of field 'names'.
     * 
     * @return the value of field 'names'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType getNames()
    {
        return this._names;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType getNames()

    /**
     * Method getParticipantIdentificationMethodReturns the value
     * of field 'participantIdentificationMethod'. The field
     * 'participantIdentificationMethod' has the following
     * description: Experimental method to determine the
     * interactors involved in the interaction. This element is
     * controlled by the PSI-MI controlled vocabulary "participant
     * identification method", root term id MI:0002.
     * 
     * @return the value of field 'participantIdentificationMethod'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getParticipantIdentificationMethod()
    {
        return this._participantIdentificationMethod;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getParticipantIdentificationMethod()

    /**
     * Method getXrefReturns the value of field 'xref'. The field
     * 'xref' has the following description: Refers to external
     * database description of the experiment.
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
     * experiment.
     * 
     * @param attributeList the value of field 'attributeList'.
     */
    public void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType attributeList)
    {
        this._attributeList = attributeList;
    } //-- void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType)

    /**
     * Method setBibrefSets the value of field 'bibref'. The field
     * 'bibref' has the following description: Publication
     * describing the experiment.
     * 
     * @param bibref the value of field 'bibref'.
     */
    public void setBibref(org.cytoscape.coreplugin.psi_mi.schema.mi25.BibrefType bibref)
    {
        this._bibref = bibref;
    } //-- void setBibref(org.cytoscape.coreplugin.psi_mi.schema.mi25.BibrefType)

    /**
     * Method setConfidenceListSets the value of field
     * 'confidenceList'. The field 'confidenceList' has the
     * following description: Confidence in this experiment.
     * Usually a statistical measure.
     * 
     * @param confidenceList the value of field 'confidenceList'.
     */
    public void setConfidenceList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType confidenceList)
    {
        this._confidenceList = confidenceList;
    } //-- void setConfidenceList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceListType)

    /**
     * Method setFeatureDetectionMethodSets the value of field
     * 'featureDetectionMethod'. The field 'featureDetectionMethod'
     * has the following description: Experimental method to
     * determine the features of interactors. If this element is
     * filled it is assumed to apply to all features described in
     * the experiment. But can be overridden by the
     * featureDetectionMethod given in the individual feature. This
     * element is controlled by the PSI-MI controlled vocabulary
     * "feature detection method", root term id MI:0003.
     * 
     * @param featureDetectionMethod the value of field
     * 'featureDetectionMethod'.
     */
    public void setFeatureDetectionMethod(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType featureDetectionMethod)
    {
        this._featureDetectionMethod = featureDetectionMethod;
    } //-- void setFeatureDetectionMethod(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType)

    /**
     * Method setHostOrganismListSets the value of field
     * 'hostOrganismList'. The field 'hostOrganismList' has the
     * following description: The host organism(s) in which the
     * experiment has been performed.
     * 
     * @param hostOrganismList the value of field 'hostOrganismList'
     */
    public void setHostOrganismList(org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList hostOrganismList)
    {
        this._hostOrganismList = hostOrganismList;
    } //-- void setHostOrganismList(org.cytoscape.coreplugin.psi_mi.schema.mi25.HostOrganismList)

    /**
     * Method setIdSets the value of field 'id'. The field 'id' has
     * the following description: All major objects have a
     * numerical id which must be unique to that object within an
     * entry. The object may be repeated, though, e.g. in the
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
     * Method setInteractionDetectionMethodSets the value of field
     * 'interactionDetectionMethod'. The field
     * 'interactionDetectionMethod' has the following description:
     * Experimental method to determine the interaction. This
     * element is controlled by the PSI-MI controlled vocabulary
     * "interaction detection method", root term id MI:0001.
     * 
     * @param interactionDetectionMethod the value of field
     * 'interactionDetectionMethod'.
     */
    public void setInteractionDetectionMethod(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType interactionDetectionMethod)
    {
        this._interactionDetectionMethod = interactionDetectionMethod;
    } //-- void setInteractionDetectionMethod(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType)

    /**
     * Method setNamesSets the value of field 'names'.
     * 
     * @param names the value of field 'names'.
     */
    public void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType names)
    {
        this._names = names;
    } //-- void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType)

    /**
     * Method setParticipantIdentificationMethodSets the value of
     * field 'participantIdentificationMethod'. The field
     * 'participantIdentificationMethod' has the following
     * description: Experimental method to determine the
     * interactors involved in the interaction. This element is
     * controlled by the PSI-MI controlled vocabulary "participant
     * identification method", root term id MI:0002.
     * 
     * @param participantIdentificationMethod the value of field
     * 'participantIdentificationMethod'.
     */
    public void setParticipantIdentificationMethod(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType participantIdentificationMethod)
    {
        this._participantIdentificationMethod = participantIdentificationMethod;
    } //-- void setParticipantIdentificationMethod(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType)

    /**
     * Method setXrefSets the value of field 'xref'. The field
     * 'xref' has the following description: Refers to external
     * database description of the experiment.
     * 
     * @param xref the value of field 'xref'.
     */
    public void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType xref)
    {
        this._xref = xref;
    } //-- void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType)

    /**
     * Method unmarshalExperimentType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType unmarshalExperimentType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType unmarshalExperimentType(java.io.Reader)

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
