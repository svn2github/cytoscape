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
 * A feature, e.g. domain, on a sequence.
 * 
 * @version $Revision$ $Date$
 */
public class FeatureType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Reference to an external feature description, for example
     * InterPro entry.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType _xref;

    /**
     * Description and classification of the feature
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType _featureDescription;

    /**
     * Location of the feature on the sequence of the interactor
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationType _location;

    /**
     * Experimental method used to identify the feature.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType _featureDetection;


      //----------------/
     //- Constructors -/
    //----------------/

    public FeatureType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.FeatureType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getFeatureDescriptionReturns the value of field
     * 'featureDescription'. The field 'featureDescription' has the
     * following description: Description and classification of the
     * feature
     * 
     * @return the value of field 'featureDescription'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType getFeatureDescription()
    {
        return this._featureDescription;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType getFeatureDescription()

    /**
     * Method getFeatureDetectionReturns the value of field
     * 'featureDetection'. The field 'featureDetection' has the
     * following description: Experimental method used to identify
     * the feature.
     * 
     * @return the value of field 'featureDetection'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType getFeatureDetection()
    {
        return this._featureDetection;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType getFeatureDetection()

    /**
     * Method getLocationReturns the value of field 'location'. The
     * field 'location' has the following description: Location of
     * the feature on the sequence of the interactor
     * 
     * @return the value of field 'location'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationType getLocation()
    {
        return this._location;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationType getLocation()

    /**
     * Method getXrefReturns the value of field 'xref'. The field
     * 'xref' has the following description: Reference to an
     * external feature description, for example InterPro entry.
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
     * Method setFeatureDescriptionSets the value of field
     * 'featureDescription'. The field 'featureDescription' has the
     * following description: Description and classification of the
     * feature
     * 
     * @param featureDescription the value of field
     * 'featureDescription'.
     */
    public void setFeatureDescription(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType featureDescription)
    {
        this._featureDescription = featureDescription;
    } //-- void setFeatureDescription(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType)

    /**
     * Method setFeatureDetectionSets the value of field
     * 'featureDetection'. The field 'featureDetection' has the
     * following description: Experimental method used to identify
     * the feature.
     * 
     * @param featureDetection the value of field 'featureDetection'
     */
    public void setFeatureDetection(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType featureDetection)
    {
        this._featureDetection = featureDetection;
    } //-- void setFeatureDetection(org.cytoscape.coreplugin.psi_mi.schema.mi1.CvType)

    /**
     * Method setLocationSets the value of field 'location'. The
     * field 'location' has the following description: Location of
     * the feature on the sequence of the interactor
     * 
     * @param location the value of field 'location'.
     */
    public void setLocation(org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationType location)
    {
        this._location = location;
    } //-- void setLocation(org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationType)

    /**
     * Method setXrefSets the value of field 'xref'. The field
     * 'xref' has the following description: Reference to an
     * external feature description, for example InterPro entry.
     * 
     * @param xref the value of field 'xref'.
     */
    public void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType xref)
    {
        this._xref = xref;
    } //-- void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType)

    /**
     * Method unmarshalFeatureType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.FeatureType unmarshalFeatureType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.FeatureType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.FeatureType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.FeatureType unmarshalFeatureType(java.io.Reader)

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
