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
 * A feature, e.g. domain, on a sequence.
 * 
 * @version $Revision$ $Date$
 */
public class FeatureElementType implements java.io.Serializable {


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
     * Names for the feature, e.g. SH3 domain.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType _names;

    /**
     * Reference to an external feature description, for example
     * InterPro entry.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType _xref;

    /**
     * Description and classification of the feature. This element
     * is controlled by the PSI-MI controlled vocabulary
     * "featureType", root term id MI:0116.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType _featureType;

    /**
     * Experimental method used to identify the feature. A setting
     * here overrides the global setting given in the
     * experimentDescription. External controlled vocabulary. 
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType _featureDetectionMethod;

    /**
     * If no experimentRef is given, it is assumed this refers to
     * all experiments linked to the interaction.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType _experimentRefList;

    /**
     * Field _featureRangeList
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureRangeList _featureRangeList;

    /**
     * Semi-structured additional description of the data contained
     * in the entry.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType _attributeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public FeatureElementType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType()


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
     * Method getExperimentRefListReturns the value of field
     * 'experimentRefList'. The field 'experimentRefList' has the
     * following description: If no experimentRef is given, it is
     * assumed this refers to all experiments linked to the
     * interaction.
     * 
     * @return the value of field 'experimentRefList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType getExperimentRefList()
    {
        return this._experimentRefList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType getExperimentRefList()

    /**
     * Method getFeatureDetectionMethodReturns the value of field
     * 'featureDetectionMethod'. The field 'featureDetectionMethod'
     * has the following description: Experimental method used to
     * identify the feature. A setting here overrides the global
     * setting given in the experimentDescription. External
     * controlled vocabulary. 
     * 
     * @return the value of field 'featureDetectionMethod'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getFeatureDetectionMethod()
    {
        return this._featureDetectionMethod;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getFeatureDetectionMethod()

    /**
     * Method getFeatureRangeListReturns the value of field
     * 'featureRangeList'.
     * 
     * @return the value of field 'featureRangeList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureRangeList getFeatureRangeList()
    {
        return this._featureRangeList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureRangeList getFeatureRangeList()

    /**
     * Method getFeatureTypeReturns the value of field
     * 'featureType'. The field 'featureType' has the following
     * description: Description and classification of the feature.
     * This element is controlled by the PSI-MI controlled
     * vocabulary "featureType", root term id MI:0116.
     * 
     * @return the value of field 'featureType'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getFeatureType()
    {
        return this._featureType;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType getFeatureType()

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
     * 'names' has the following description: Names for the
     * feature, e.g. SH3 domain.
     * 
     * @return the value of field 'names'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType getNames()
    {
        return this._names;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType getNames()

    /**
     * Method getXrefReturns the value of field 'xref'. The field
     * 'xref' has the following description: Reference to an
     * external feature description, for example InterPro entry.
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
     * Method setExperimentRefListSets the value of field
     * 'experimentRefList'. The field 'experimentRefList' has the
     * following description: If no experimentRef is given, it is
     * assumed this refers to all experiments linked to the
     * interaction.
     * 
     * @param experimentRefList the value of field
     * 'experimentRefList'.
     */
    public void setExperimentRefList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType experimentRefList)
    {
        this._experimentRefList = experimentRefList;
    } //-- void setExperimentRefList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType)

    /**
     * Method setFeatureDetectionMethodSets the value of field
     * 'featureDetectionMethod'. The field 'featureDetectionMethod'
     * has the following description: Experimental method used to
     * identify the feature. A setting here overrides the global
     * setting given in the experimentDescription. External
     * controlled vocabulary. 
     * 
     * @param featureDetectionMethod the value of field
     * 'featureDetectionMethod'.
     */
    public void setFeatureDetectionMethod(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType featureDetectionMethod)
    {
        this._featureDetectionMethod = featureDetectionMethod;
    } //-- void setFeatureDetectionMethod(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType)

    /**
     * Method setFeatureRangeListSets the value of field
     * 'featureRangeList'.
     * 
     * @param featureRangeList the value of field 'featureRangeList'
     */
    public void setFeatureRangeList(org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureRangeList featureRangeList)
    {
        this._featureRangeList = featureRangeList;
    } //-- void setFeatureRangeList(org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureRangeList)

    /**
     * Method setFeatureTypeSets the value of field 'featureType'.
     * The field 'featureType' has the following description:
     * Description and classification of the feature. This element
     * is controlled by the PSI-MI controlled vocabulary
     * "featureType", root term id MI:0116.
     * 
     * @param featureType the value of field 'featureType'.
     */
    public void setFeatureType(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType featureType)
    {
        this._featureType = featureType;
    } //-- void setFeatureType(org.cytoscape.coreplugin.psi_mi.schema.mi25.CvType)

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
     * 'names' has the following description: Names for the
     * feature, e.g. SH3 domain.
     * 
     * @param names the value of field 'names'.
     */
    public void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType names)
    {
        this._names = names;
    } //-- void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType)

    /**
     * Method setXrefSets the value of field 'xref'. The field
     * 'xref' has the following description: Reference to an
     * external feature description, for example InterPro entry.
     * 
     * @param xref the value of field 'xref'.
     */
    public void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType xref)
    {
        this._xref = xref;
    } //-- void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType)

    /**
     * Method unmarshalFeatureElementType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType unmarshalFeatureElementType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.FeatureElementType unmarshalFeatureElementType(java.io.Reader)

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
