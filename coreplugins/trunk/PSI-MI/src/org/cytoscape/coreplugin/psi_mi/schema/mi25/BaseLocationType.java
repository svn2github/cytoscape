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
 * A location on a sequence. Both begin and end can be a defined
 * position, a fuzzy position, or undetermined.
 * 
 * @version $Revision$ $Date$
 */
public class BaseLocationType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _baseLocationTypeSequence
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence _baseLocationTypeSequence;

    /**
     * Field _baseLocationTypeSequence2
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2 _baseLocationTypeSequence2;

    /**
     * True if the described feature is a linking feature
     * connecting two amino acids rather than extending along the
     * sequence. 'begin' references the first amino acid, 'end' the
     * second. Standard example is a disulfide bridge. Does not
     * reference another feature, therefore is only suitable for
     * linking features on the same amino acid chain. 
     */
    private boolean _isLink = false;

    /**
     * keeps track of state for field: _isLink
     */
    private boolean _has_isLink;


      //----------------/
     //- Constructors -/
    //----------------/

    public BaseLocationType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteIsLink
     */
    public void deleteIsLink()
    {
        this._has_isLink= false;
    } //-- void deleteIsLink() 

    /**
     * Method getBaseLocationTypeSequenceReturns the value of field
     * 'baseLocationTypeSequence'.
     * 
     * @return the value of field 'baseLocationTypeSequence'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence getBaseLocationTypeSequence()
    {
        return this._baseLocationTypeSequence;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence getBaseLocationTypeSequence()

    /**
     * Method getBaseLocationTypeSequence2Returns the value of
     * field 'baseLocationTypeSequence2'.
     * 
     * @return the value of field 'baseLocationTypeSequence2'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2 getBaseLocationTypeSequence2()
    {
        return this._baseLocationTypeSequence2;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2 getBaseLocationTypeSequence2()

    /**
     * Method getIsLinkReturns the value of field 'isLink'. The
     * field 'isLink' has the following description: True if the
     * described feature is a linking feature connecting two amino
     * acids rather than extending along the sequence. 'begin'
     * references the first amino acid, 'end' the second. Standard
     * example is a disulfide bridge. Does not reference another
     * feature, therefore is only suitable for linking features on
     * the same amino acid chain. 
     * 
     * @return the value of field 'isLink'.
     */
    public boolean getIsLink()
    {
        return this._isLink;
    } //-- boolean getIsLink() 

    /**
     * Method hasIsLink
     */
    public boolean hasIsLink()
    {
        return this._has_isLink;
    } //-- boolean hasIsLink() 

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
     * Method setBaseLocationTypeSequenceSets the value of field
     * 'baseLocationTypeSequence'.
     * 
     * @param baseLocationTypeSequence the value of field
     * 'baseLocationTypeSequence'.
     */
    public void setBaseLocationTypeSequence(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence baseLocationTypeSequence)
    {
        this._baseLocationTypeSequence = baseLocationTypeSequence;
    } //-- void setBaseLocationTypeSequence(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence)

    /**
     * Method setBaseLocationTypeSequence2Sets the value of field
     * 'baseLocationTypeSequence2'.
     * 
     * @param baseLocationTypeSequence2 the value of field
     * 'baseLocationTypeSequence2'.
     */
    public void setBaseLocationTypeSequence2(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2 baseLocationTypeSequence2)
    {
        this._baseLocationTypeSequence2 = baseLocationTypeSequence2;
    } //-- void setBaseLocationTypeSequence2(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2)

    /**
     * Method setIsLinkSets the value of field 'isLink'. The field
     * 'isLink' has the following description: True if the
     * described feature is a linking feature connecting two amino
     * acids rather than extending along the sequence. 'begin'
     * references the first amino acid, 'end' the second. Standard
     * example is a disulfide bridge. Does not reference another
     * feature, therefore is only suitable for linking features on
     * the same amino acid chain. 
     * 
     * @param isLink the value of field 'isLink'.
     */
    public void setIsLink(boolean isLink)
    {
        this._isLink = isLink;
        this._has_isLink = true;
    } //-- void setIsLink(boolean) 

    /**
     * Method unmarshalBaseLocationType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType unmarshalBaseLocationType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationType unmarshalBaseLocationType(java.io.Reader)

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
